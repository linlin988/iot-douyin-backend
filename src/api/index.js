import axios from 'axios';

const API_BASE_URL = 'http://localhost:3000';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

apiClient.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

export const api = {
  // 视频相关
  video: {
    getList: (params) => apiClient.get('/api/video/list', { params }),
    getDetail: (id) => apiClient.get(`/api/video/detail/${id}`),
    create: (data, config) => apiClient.post('/api/video/create', data, config),
    update: (id, data) => apiClient.put(`/api/video/update/${id}`, data),
    delete: (id) => apiClient.delete(`/api/video/delete/${id}`)
  },
  
  // 评论相关
  comment: {
    getList: (videoId) => apiClient.get(`/api/comment/list/${videoId}`),
    create: (data) => apiClient.post('/api/comment/create', data),
    delete: (id) => apiClient.delete(`/api/comment/delete/${id}`)
  },
  
  // 点赞相关
  like: {
    create: (videoId) => apiClient.post('/api/like/create', { videoId }),
    delete: (videoId) => apiClient.delete(`/api/like/delete/${videoId}`)
  },
  
  // 关注相关
  follow: {
    create: (userId) => apiClient.post('/api/follow/create', { userId }),
    delete: (userId) => apiClient.delete(`/api/follow/delete/${userId}`)
  },
  
  // 用户相关
  user: {
    login: (data) => apiClient.post('/api/user/login', data),
    register: (data) => apiClient.post('/api/user/register', data),
    getInfo: (id) => apiClient.get(`/api/user/info/${id}`),
    update: (data) => apiClient.put('/api/user/update', data),
    getVideoList: (userId) => apiClient.get(`/api/user/video/list/${userId}`),
    getLikeList: (userId) => apiClient.get(`/api/user/like/list/${userId}`),
    getFollowList: (userId) => apiClient.get(`/api/user/follow/list/${userId}`),
    getFollowerList: (userId) => apiClient.get(`/api/user/follower/list/${userId}`)
  }
};

export default api;