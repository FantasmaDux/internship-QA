import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 3,          
  iterations: 30,  
};

export default function () {
    const sellerId = '997999';

    const params = {
    headers: {
      'Content-Type': 'application/json', 
    },
  };

  http.get(`https://qa-internship.avito.com/api/1/${sellerId}/item`, params);
  sleep(1);
}