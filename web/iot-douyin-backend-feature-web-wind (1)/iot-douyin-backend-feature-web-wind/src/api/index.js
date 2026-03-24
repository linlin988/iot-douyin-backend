import axios from 'axios'

const apiClient = axios.create({
    baseURL: 'http://localhost:9999', //  网关地址
    headers: {
        'Content-Type': 'application/json'
    }
})

// 请求拦截器
apiClient.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers.token = token
    }
    return config
})

export const api = {

    // ================= 视频 =================
    video: {
        getList: (params) => apiClient.get('/content/video/list', { params }),

        getDetail: (id) => apiClient.get(`/content/video/detail/${id}`),

        delete: (id) => apiClient.delete(`/content/video/delete/${id}`)
    },

    // ================= 评论 =================
    comment: {
        // POST
        getList: (videoId, pageQuery) =>
            apiClient.post(`/content/comment/list/${videoId}`, pageQuery),

        create: (data) =>
            apiClient.post('/content/comment/add', data),

        delete: (id) =>
            apiClient.delete(`/content/comment/delete/${id}`)
    },

    // ================= 点赞 =================
    like: {
        // PUT（切换点赞）
        toggle: (videoId) =>
            apiClient.put(`/content/like/${videoId}`),

        // 是否点赞
        isLike: (videoId) =>
            apiClient.get(`/content/like/isLike/${videoId}`),

        // 点赞数
        count: (videoId) =>
            apiClient.get(`/content/like/likeCount/${videoId}`)
    },

    // ================= 关注 =================
    follow: {
        // follow = true/false
        toggle: (userId, follow) =>
            apiClient.put(`/content/follow/${userId}/${follow}`),

        isFollow: (userId) =>
            apiClient.get(`/content/follow/queryFollow/${userId}`),

        followList: (params) =>
            apiClient.get('/content/follow/followList', { params }),

        fansList: (params) =>
            apiClient.get('/content/follow/fansList', { params })
    },

    // ================= 用户 =================
    user: {
        login: (data) =>
            apiClient.post('/user/login', data),

        register: (data) =>
            apiClient.post('/user/register', data),

        // 当前用户（推荐用这个）
        getCurrent: () =>
            apiClient.get('/user/info'),

        // 按ID查
        getInfo: (id) =>
            apiClient.get(`/user/info/${id}`),

        update: (data) =>
            apiClient.put('/user/info', data)
    }
}

export default api