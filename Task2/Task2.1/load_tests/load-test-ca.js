import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 3,          
  iterations: 30,  
};

export default function () {
  const payload = JSON.stringify({
    sellerId: 111114,
    name: 'clock',
    price: 1000,
    statistics: {
    likes: 2,
    viewCount: 18,
    contacts: 1
  }
  });

    const params = {
    headers: {
      'Content-Type': 'application/json', 
    },
  };

http.post('https://qa-internship.avito.com/api/1/item', payload, params);
  sleep(1);
}