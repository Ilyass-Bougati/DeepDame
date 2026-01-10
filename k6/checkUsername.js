import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '20s', target: 10 },
    { duration: '100s', target: 200 },
    { duration: '20s', target: 0 },
  ],

  thresholds: {
    http_req_duration: ['p(95)<500'], 
  },
};

export default function () {
  const url = 'http://localhost:8080/api/v1/auth/checkUsername/random';

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const res = http.get(url, params);

  check(res, {
    'is status 200': (r) => r.status === 200,
  });

  sleep(1);
}