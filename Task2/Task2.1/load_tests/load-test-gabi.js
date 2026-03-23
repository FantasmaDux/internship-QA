import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 3,          
  iterations: 30,  
};

export default function () {
    const itemId = 'a93d5c57-2fe2-4d4f-8094-b5812ffc52d0';

    const params = {
    headers: {
      'Content-Type': 'application/json', 
    },
  };

  http.get(`https://qa-internship.avito.com/api/1/item/${itemId}`, params);
  sleep(1);
}