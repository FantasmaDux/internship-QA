import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 3,          
  iterations: 30,  
};

export default function () {
    const itemId = '7b32ebda-b357-4101-bd4c-869a0ed32201';

    const params = {
    headers: {
      'Content-Type': 'application/json', 
    },
  };

  http.get(`https://qa-internship.avito.com/api/1/statistic/${itemId}`, params);
  sleep(1);
}