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

        delete: (id) => apiClient.delete(`/content/video/delete/${id}`),

        // 获取指定用户发布的视频列表
        // 接口：GET /content/video/userList/{userId}
        // TODO: 后端还没有该接口
        getUserVideos: (userId) => apiClient.get(`/content/video/userList/${userId}`),

        // 上传视频（包括封面）
        // 接口：POST /content/video/upload
        // 参数：file(MultipartFile), coverfile(MultipartFile), title, description
        upload: (formData, config) => apiClient.post('/content/video/upload', formData, {
            headers: { 'Content-Type': 'multipart/form-data' },
            ...config
        })
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
            apiClient.get(`/content/like/likeCount/${videoId}`),

        // 获取用户点赞的视频列表
        // 接口：GET /content/like/likedList/{userId}
        // TODO: 后端还没有该接口
        getLikedVideos: (userId) =>
            apiClient.get(`/content/like/likedList/${userId}`)
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

        // 获取图形验证码图片流
        // 接口：GET /user/captcha
        getCaptcha: () =>
            apiClient.get('/user/captcha', {
                params: { t: Date.now() },
                responseType: 'blob'
            }),

        // 当前用户
        getCurrent: () =>
            apiClient.get('/user/info'),

        // 按ID查
        getInfo: (id) =>
            apiClient.get(`/user/info/${id}`),

        update: (data) =>
            apiClient.put('/user/info', data),

        // 头像上传 (假定接口路径，需后端实现配合)
        // 接口：POST /user/avatar
        // 参数：file (MultipartFile)
        uploadAvatar: (formData) =>
            apiClient.post('/user/avatar', formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            })
    },

    // ================= 私信 =================
    // TODO: 以下接口均需要后端实现后接入
    message: {
        // 获取和指定用户的私信列表
        // 接口：GET /message/list/{targetUserId}
        getList: (targetUserId, params) =>
            apiClient.get(`/message/list/${targetUserId}`, { params }),

        // 发送私信
        // 接口：POST /message/send
        // 请求体：{ targetUserId, content }
        send: (data) =>
            apiClient.post('/message/send', data),

        // 获取未读消息数
        // 接口：GET /message/unread/count
        getUnreadCount: () =>
            apiClient.get('/message/unread/count')
    }
}

export default api