import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend } from 'k6/metrics';

// 1. Define 3 separate trends to track performance of each step
const registerTrend = new Trend('duration_register');
const loginTrend = new Trend('duration_login');
const accessTrend = new Trend('duration_access');

export const options = {
  stages: [
    { duration: '5s', target: 5 },
    { duration: '60s', target: 100 },
    { duration: '5s', target: 0 },
  ],
};

const BASE_URL = 'http://localhost:8080';

// Helper: Generates random string to ensure unique emails
function randomString(length) {
  const charset = 'abcdefghijklmnopqrstuvwxyz0123456789';
  let res = '';
  for (let i = 0; i < length; i++) {
    res += charset[Math.floor(Math.random() * charset.length)];
  }
  return res;
}

export default function () {
  // Generate unique credentials for this specific run
  const uniqueId = randomString(6);
  const email = `user_${uniqueId}@test.com`; 
  const password = 'password123';
  
  const headers = { 'Content-Type': 'application/json' };

  const registerPayload = JSON.stringify({
    username: `User${uniqueId}`,
    email: email,
    password: password
  });

  const registerRes = http.post(`${BASE_URL}/api/v1/auth/register`, registerPayload, { headers: headers });
  
  // Track timing
  registerTrend.add(registerRes.timings.duration);

  const isRegistered = check(registerRes, {
    'register successful': (r) => r.status === 200 || r.status === 201,
  });

  if (!isRegistered) {
    console.error(`Register failed for ${email}: ${registerRes.status} ${registerRes.body}`);
    return; // Stop execution if register failed
  }

  const loginPayload = JSON.stringify({
    email: email,
    password: password,
  });

  const loginRes = http.post(`${BASE_URL}/api/v1/auth/login`, loginPayload, { headers: headers });

  loginTrend.add(loginRes.timings.duration);

  const isLoggedIn = check(loginRes, {
    'login successful': (r) => r.status === 200,
    'has cookie': (r) => r.headers['Set-Cookie'] !== undefined,
  });

  if (!isLoggedIn) {
    console.error(`Login failed for ${email}: ${loginRes.status}`);
    return; 
  }

  const protectedRes = http.get(`${BASE_URL}/api/v1/user/`);

  accessTrend.add(protectedRes.timings.duration);

  check(protectedRes, {
    'access granted': (r) => r.status === 200,
  });

  sleep(1);
}