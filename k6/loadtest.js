import ws from 'k6/ws';
import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';


const BASE_URL = `http://ubuntu-server:30080`;
const WS_URL = `ws://ubuntu-server:30080/ws/websocket`;

// const BASE_URL = 'http://localhost:8080';
// const WS_URL = 'ws://localhost:8080/ws/websocket';

const MOVE_WHITE = JSON.stringify({ 
    from: { row: 5, col: 0 }, 
    to:   { row: 4, col: 1 } 
});

const MOVE_BLACK = JSON.stringify({ 
    from: { row: 2, col: 1 }, 
    to:   { row: 3, col: 0 } 
});

export const options = {

stages: [

        { duration: '30s', target: 5 },

        { duration: '30s', target: 10 },

        { duration: '15s', target: 1 },
    ],

    thresholds: {
        'http_req_failed': ['rate<0.15'],
        'http_req_duration': ['p(95)<10000'],
    }
};


const NULL_BYTE = '\u0000';

function formatFrame(command, headers, body = '') {
    let frame = command + '\n';
    for (const [key, value] of Object.entries(headers)) {
        frame += `${key}:${value}\n`;
    }
    frame += '\n' + body + NULL_BYTE;
    return frame;
}

function getJavaTimestamp() {
    const now = new Date();
    return now.toISOString().replace('T', ' ').replace('Z', '');
}

function parseStompMessage(raw) {
    if (!raw || raw === '\n') return null;
    
    const parts = raw.split(/\n\n(.*)/s); 
    if (parts.length < 2) return null;

    const headerPart = parts[0];
    const body = parts[1] ? parts[1].replace(/\u0000$/, '') : '';
    
    const headers = {};
    headerPart.split('\n').slice(1).forEach(line => {
        const [k, v] = line.split(':');
        if(k && v) headers[k] = v;
    });

    return { command: headerPart.split('\n')[0], headers, body };
}

export default function () {
    const username = `k6_${randomString(5)}`;
    const email = `${username}@test.com`;
    const password = 'Password123!';

    let res = http.post(`${BASE_URL}/api/v1/auth/register`, JSON.stringify({
        username, email, password
    }), { headers: { 'Content-Type': 'application/json' } });

    res = http.post(`${BASE_URL}/api/v1/auth/login`, JSON.stringify({
        email, password
    }), { headers: { 'Content-Type': 'application/json' } });

    const isLoggedIn = check(res, { 'Logged In': (r) => r.status === 200 });
    if (!isLoggedIn) {
        console.log(`Login failed: ${res.status} ${res.body}`);
        sleep(1);
        return;
    }

    const resWs = ws.connect(WS_URL, {}, function (socket) {
        
        let myGameId = null;
        let myColor = null; 

        socket.on('open', () => {
            socket.send(formatFrame('CONNECT', { 
                'accept-version': '1.1,1.2', 
                'host': 'localhost',
                'heart-beat': '0,0' 
            }));
        });

        socket.on('message', (msg) => {
            const stompMsg = parseStompMessage(msg);
            if (!stompMsg) return;

            if (stompMsg.command === 'CONNECTED') {
                socket.send(formatFrame('SUBSCRIBE', { id: 'sub-created', destination: '/sender/queue/game/created' }));
                socket.send(formatFrame('SUBSCRIBE', { id: 'sub-joined', destination: '/sender/queue/game/joined' }));
                
                const rand = Math.random();
                if (rand < 0.3) {
                    socket.send(formatFrame('SUBSCRIBE', { id: 'sub-chat', destination: '/topic/general-chat' }));
                    const chatPayload = JSON.stringify({ 
                        message: "Load Test Hello",
                        createdAt: getJavaTimestamp()
                    });

                    socket.send(formatFrame('SEND', { destination: '/app/message' }, chatPayload));
                    sleep(1);
                    socket.close();
                } 
                else if (rand < 0.6) {
                     socket.send(formatFrame('SEND', { destination: '/app/game/create' }, '"PVE"'));
                } 
                else {
                    socket.send(formatFrame('SEND', { destination: '/app/game/matchmaking' }));
                }
            }

            if (stompMsg.headers['destination'] && stompMsg.headers['destination'].includes('game/created')) {
                const body = JSON.parse(stompMsg.body);
                myGameId = body.gameId;
                
                socket.send(formatFrame('SUBSCRIBE', { id: `sub-g-${myGameId}`, destination: `/topic/game/${myGameId}` }));

                sleep(1);
                socket.send(formatFrame('SEND', { destination: `/app/game/${myGameId}/move` }, MOVE_WHITE));
                
                sleep(2); 
                socket.send(formatFrame('SEND', { destination: `/app/game/${myGameId}/surrender` }));
                sleep(1);
                socket.close();
            }

            if (stompMsg.headers['destination'] && stompMsg.headers['destination'].includes('game/joined')) {
                const body = JSON.parse(stompMsg.body);
                myGameId = body.gameId;
                myColor = body.yourColor;

                socket.send(formatFrame('SUBSCRIBE', { id: `sub-g-${myGameId}`, destination: `/topic/game/${myGameId}` }));

                if (myColor === 'WHITE') {
                    sleep(1);
                    socket.send(formatFrame('SEND', { destination: `/app/game/${myGameId}/move` }, MOVE_WHITE));
                    sleep(2);
                    socket.send(formatFrame('SEND', { destination: `/app/game/${myGameId}/surrender` }));
                    sleep(1);
                    socket.close();
                }
            }

            if (stompMsg.headers['destination'] && stompMsg.headers['destination'].includes('/topic/game/')) {
                if (myColor === 'BLACK' && myGameId) {
                    sleep(1); 
                    socket.send(formatFrame('SEND', { destination: `/app/game/${myGameId}/move` }, MOVE_BLACK));
                    sleep(1);
                    socket.send(formatFrame('SEND', { destination: `/app/game/${myGameId}/surrender` }));
                    sleep(1);
                    socket.close();
                }
            }
        });

        socket.on('error', (e) => {
             if (e.error() !== 'websocket: close 1000 (normal)') {
                 console.log('WS Error: ' + e.error());
             }
        });
    });

    check(resWs, { 'WS Session Finished': (r) => r && r.status === 101 });
}