import http from 'k6/http';
import { check, sleep } from 'k6';

// Configuração do Ataque Simulado
export const options = {
  stages: [
    { duration: '10s', target: 20 }, // Sobe para 20 usuários simultâneos (Ataque leve)
    { duration: '30s', target: 100 }, // Sobe para 100 usuários (Tenta derrubar)
    { duration: '10s', target: 0 },   // Acalma
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'], // Erros devem ser baixos (exceto os 429)
  },
};

export default function () {
  // Tenta logar repetidamente
  const url = 'http://localhost:8080/auth/login';
  const payload = JSON.stringify({
    login: 'admin',
    senha: '123'
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const res = http.post(url, payload, params);

  // Verificação Sênior:
  // Se recebermos 200 (OK) -> Passou
  // Se recebermos 429 (Too Many Requests) -> SUCESSO! A defesa funcionou.
  check(res, {
    'status is 200 or 429': (r) => r.status === 200 || r.status === 429,
  });

  sleep(0.1);
}
