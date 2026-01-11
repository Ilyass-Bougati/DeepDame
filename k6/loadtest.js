import ws from 'k6/ws';
import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';
import { Counter, Trend } from 'k6/metrics';

const BASE_URL = `https://ubuntu-server.tail2081a0.ts.net`;
const WS_URL = `wss://ubuntu-server.tail2081a0.ts.net/ws/websocket`;

const registrationDuration = new Trend('auth_register_duration'); // Track time to register
const loginDuration = new Trend('auth_login_duration');           // Track time to login
const authFailures = new Counter('auth_failures');                // specific login/reg errors

const successfulGames = new Counter('successful_games');
const wsErrors = new Counter('ws_errors');
const moveLatency = new Trend('move_latency'); 

const MOVE_WHITE = JSON.stringify({ from: { row: 5, col: 0 }, to: { row: 4, col: 1 } });
const MOVE_BLACK = JSON.stringify({ from: { row: 2, col: 1 }, to: { row: 3, col: 0 } });

const NULL_BYTE = '\u0000';

function formatFrame(command, headers, body = '') {
    let frame = command + '\n';
    for (const [key, value] of Object.entries(headers)) {
        frame += `${key}:${value}\n`;
    }
    frame += '\n' + body + NULL_BYTE;
    return frame;
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
        if (k && v) headers[k] = v;
    });
    return { command: headerPart.split('\n')[0], headers, body };
}

export default function () {
    const username = `k6_${randomString(8)}`;
    const email = `${username}@loadtest.com`;
    const password = 'Password123!';

    // --- STEP 1: REGISTER ---
    let resReg = http.post(`${BASE_URL}/api/v1/auth/register`, JSON.stringify({ username, email, password }), { headers: { 'Content-Type': 'application/json' } });
    
    registrationDuration.add(resReg.timings.duration);

    if (resReg.status !== 200 && resReg.status !== 201) {
        authFailures.add(1);
        console.error(`Registration failed: ${resReg.status}`);
        return; 
    }
    
    sleep(0.5);

    // --- STEP 2: LOGIN ---
    let resLogin = http.post(`${BASE_URL}/api/v1/auth/login`, JSON.stringify({ email, password }), { headers: { 'Content-Type': 'application/json' } });
    
    loginDuration.add(resLogin.timings.duration);

    const isLoggedIn = check(resLogin, { 'Logged In': (r) => r.status === 200 });
    
    if (!isLoggedIn) {
        authFailures.add(1);
        console.error(`Login failed: ${resLogin.status}`);
        return;
    }

    // --- STEP 3: WEBSOCKET ---
    const resWs = ws.connect(WS_URL, {}, function (socket) {
        let myGameId = null;
        let myColor = null; 

        socket.on('open', () => {
            socket.send(formatFrame('CONNECT', { 'accept-version': '1.1,1.2', 'host': 'localhost', 'heart-beat': '10000,10000' }));
        });

        socket.on('message', (msg) => {
            const stompMsg = parseStompMessage(msg);
            if (!stompMsg) return;

            if (stompMsg.command === 'CONNECTED') {
                socket.send(formatFrame('SUBSCRIBE', { id: 'sub-created', destination: '/user/queue/game/created' }));
                socket.send(formatFrame('SUBSCRIBE', { id: 'sub-joined', destination: '/user/queue/game/joined' }));

                const rand = Math.random();
                if (rand < 0.2) {
                    socket.send(formatFrame('SUBSCRIBE', { id: 'sub-chat', destination: '/topic/general-chat' }));
                    socket.send(formatFrame('SEND', { destination: '/app/message' }, JSON.stringify({ message: "Load Test " + username, createdAt: new Date().toISOString() })));
                    sleep(2);
                    socket.close();
                } else if (rand < 0.6) {
                    socket.send(formatFrame('SEND', { destination: '/app/game/create' }, '"PVE"'));
                } else {
                    socket.send(formatFrame('SEND', { destination: '/app/game/matchmaking' }));
                }
            }

            if (stompMsg.headers['destination']?.includes('game/created')) {
                const body = JSON.parse(stompMsg.body);
                myGameId = body.gameId;
                socket.send(formatFrame('SUBSCRIBE', { id: `sub-g-${myGameId}`, destination: `/topic/game/${myGameId}` }));
                
                sleep(Math.random() * 2 + 1);
                
                const start = Date.now();
                socket.send(formatFrame('SEND', { destination: `/app/game/${myGameId}/move` }, MOVE_WHITE));
                moveLatency.add(Date.now() - start);

                sleep(2);
                socket.send(formatFrame('SEND', { destination: `/app/game/${myGameId}/surrender` }));
                successfulGames.add(1);
                socket.close();
            }

            if (stompMsg.headers['destination']?.includes('game/joined')) {
                const body = JSON.parse(stompMsg.body);
                myGameId = body.gameId;
                myColor = body.yourColor;
                socket.send(formatFrame('SUBSCRIBE', { id: `sub-g-${myGameId}`, destination: `/topic/game/${myGameId}` }));

                if (myColor === 'WHITE') {
                    sleep(Math.random() * 2 + 1);
                    socket.send(formatFrame('SEND', { destination: `/app/game/${myGameId}/move` }, MOVE_WHITE));
                    sleep(2);
                    socket.send(formatFrame('SEND', { destination: `/app/game/${myGameId}/surrender` }));
                    successfulGames.add(1);
                    socket.close();
                }
            }

            if (stompMsg.headers['destination']?.includes(`/topic/game/`)) {
                if (myColor === 'BLACK' && myGameId) {
                    sleep(Math.random() * 2 + 1);
                    socket.send(formatFrame('SEND', { destination: `/app/game/${myGameId}/move` }, MOVE_BLACK));
                    sleep(1);
                    socket.send(formatFrame('SEND', { destination: `/app/game/${myGameId}/surrender` }));
                    successfulGames.add(1);
                    socket.close();
                }
            }
        });

        socket.on('error', (e) => {
            if (e.error() !== 'websocket: close 1000 (normal)') {
                wsErrors.add(1);
                console.log('WS Error: ' + e.error());
            }
        });
    });

    check(resWs, { 'WS Session Finished': (r) => r && r.status === 101 });
}