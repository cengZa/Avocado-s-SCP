import axios from 'axios';

export function request(config) {
  const instance = axios.create({
    baseURL: "/dev",
    timeout: 5000,
  });

  instance.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json'; // 设置 Content-Type 头
    return config;
  }, err => {
    console.log(err);
  });

  instance.interceptors.response.use(res => {
    return res.data;
  }, err => {
    console.log(err);
  });

  return instance(config);
}
