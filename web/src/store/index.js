import { defineStore } from 'pinia'
import { api } from '../api'

export const useVideoStore = defineStore('video', {
    state: () => ({
        videoList: [],
        currentVideoIndex: 0,
        isLoading: false,
        hasMore: true
    }),

    actions: {
        async fetchVideoList(page = 1, loadMore = false) {
            this.isLoading = true
            try {
                const res = await api.video.getList({ page, size: 10 })
                const apiData = res.data

                if (apiData.code === 200) {
                    const records = apiData.data.records || []
                    const userIds = [...new Set(records.map(record => record.userId).filter(Boolean))]
                    const userInfoMap = {}

                    await Promise.all(
                        userIds.map(async (userId) => {
                            try {
                                const userRes = await api.user.getInfo(userId)
                                if (userRes?.data?.code === 200 && userRes?.data?.data) {
                                    const userInfo = userRes.data.data
                                    userInfoMap[userId] = {
                                        username: userInfo.username || '',
                                        avatar: userInfo.avatar || ''
                                    }
                                }
                            } catch (e) {
                                console.error('fetch user info error:', e)
                            }
                        })
                    )

                    const videos = records.map((record, index) => ({
                        id: record.id,
                        videoUrl: record.videoUrl?.replace(/`/g, ''),
                        coverUrl: record.coverUrl?.replace(/`/g, ''),
                        author: {
                            id: record.userId,
                            name: userInfoMap[record.userId]?.username || String(record.userId),
                            avatar: userInfoMap[record.userId]?.avatar || '',
                            isFollowing: false
                        },
                        title: record.title || `视频 ${index + 1}`,
                        description: record.description || '',
                        likes: Number(record.likeCount) || 0,
                        comments: Number(record.commentCount) || 0,
                        shares: 0,
                        isLiked: false
                    }))

                    this.videoList = loadMore
                        ? [...this.videoList, ...videos]
                        : videos

                    this.hasMore = apiData.data.current < apiData.data.pages
                }
            } catch (e) {
                console.error('fetchVideoList error:', e)
            } finally {
                this.isLoading = false
            }
        },

        setCurrentVideoIndex(index) {
            this.currentVideoIndex = index
        },

        //  点赞
        async toggleLike(videoId) {
            const video = this.videoList.find(v => v.id === videoId)
            if (!video) return

            try {
                await api.like.toggle(videoId)

                video.isLiked = !video.isLiked
                // 确保 likes 是数字类型
                video.likes = Number(video.likes) + (video.isLiked ? 1 : -1)
            } catch (e) {
                console.error('toggleLike error:', e)
            }
        },

        //  关注
        async toggleFollow(userId) {
            const videos = this.videoList.filter(v => v.author.id === userId)
            if (!videos.length) return

            const currentState = videos[0].author.isFollowing

            try {
                await api.follow.toggle(userId, !currentState)

                // 同步所有视频作者状态
                videos.forEach(v => {
                    v.author.isFollowing = !currentState
                })
            } catch (e) {
                console.error('toggleFollow error:', e)
            }
        }
    }
})

/* ================= 用户 ================= */

export const useUserStore = defineStore('user', {
    state: () => ({
        token: localStorage.getItem('token') || '',
        currentUser: null,
        userProfile: null,
        publishedVideos: [],
        likedVideos: []
    }),

    getters: {
        isLoggedIn: (state) => !!state.token
    },

    actions: {
        // 登录
        async login(data) {
            const res = await api.user.login(data)
            if (res.data.code === 200) {
                const loginData = res.data.data || {}
                const token = loginData.token || ''
                if (!token) {
                    return { success: false, message: '登录返回缺少token' }
                }
                this.token = token
                localStorage.setItem('token', token)
                await this.fetchCurrentUser()
                return { success: true }
            }
            return { success: false, message: res.data.message || '登录失败' }
        },

        // 注册
        async register(data) {
            const res = await api.user.register(data)
            if (res.data.code === 200) {
                return { success: true }
            }
            return { success: false, message: res.data.message || '注册失败' }
        },

        // 退出登录
        logout() {
            this.token = ''
            this.currentUser = null
            this.publishedVideos = []
            this.likedVideos = []
            localStorage.removeItem('token')
        },

        // 当前登录用户
        async fetchCurrentUser() {
            try {
                const res = await api.user.getCurrent()
                if (res.data.code === 200) {
                    this.currentUser = res.data.data
                }
            } catch (e) {
                console.error('fetchCurrentUser error:', e)
            }
        },

        // 用户主页
        async fetchUserProfile(userId) {
            try {
                const userRes = await api.user.getInfo(userId)

                if (userRes.data.code === 200) {
                    const user = userRes.data.data

                    this.userProfile = {
                        id: user.id,
                        name: user.username,
                        avatar: user.avatar || '',
                        bio: user.bio || '',
                        followers: user.fanCount || 0,
                        following: user.followCount || 0,
                        isFollowing: false,
                        videos: []
                    }
                } else {
                    // 查无此用户时也要结束页面加载态
                    this.userProfile = {
                        id: Number(userId),
                        name: String(userId),
                        avatar: '',
                        bio: '用户不存在或已注销',
                        followers: 0,
                        following: 0,
                        isFollowing: false,
                        videos: []
                    }
                }
            } catch (e) {
                console.error('fetchUserProfile error:', e)
                // 接口异常时同样提供兜底，避免页面一直骨架屏
                this.userProfile = {
                    id: Number(userId),
                    name: String(userId),
                    avatar: '',
                    bio: '用户信息加载失败',
                    followers: 0,
                    following: 0,
                    isFollowing: false,
                    videos: []
                }
            }
        },

        // 用户页关注
        async toggleFollow() {
            if (!this.userProfile) return

            try {
                const newState = !this.userProfile.isFollowing

                await api.follow.toggle(this.userProfile.id, newState)

                this.userProfile.isFollowing = newState
            } catch (e) {
                console.error('user toggleFollow error:', e)
            }
        },

        // 获取我发布的视频
        async fetchPublishedVideos(userId) {
            try {
                const res = await api.video.getUserVideos(userId)
                if (res.data.code === 200) {
                    const videos = res.data.data || []
                    // 处理视频数据，确保字段格式正确
                    this.publishedVideos = videos.map(video => ({
                        id: video.id,
                        coverUrl: video.coverUrl?.replace(/`/g, '') || '',
                        likeCount: Number(video.likeCount) || 0,
                        commentCount: Number(video.commentCount) || 0,
                        title: video.title || '',
                        description: video.description || '',
                        videoUrl: video.videoUrl?.replace(/`/g, '') || ''
                    }))
                }
            } catch (e) {
                console.error('fetchPublishedVideos error:', e)
                this.publishedVideos = []
            }
        },

        // 获取我点赞的视频
        async fetchLikedVideos(userId) {
            try {
                const res = await api.like.getLikedVideos(userId)
                if (res.data.code === 200) {
                    const videos = res.data.data || []
                    // 处理视频数据，确保字段格式正确
                    this.likedVideos = videos.map(video => ({
                        id: video.id,
                        coverUrl: video.coverUrl?.replace(/`/g, '') || '',
                        likeCount: Number(video.likeCount) || 0,
                        commentCount: Number(video.commentCount) || 0,
                        title: video.title || '',
                        description: video.description || '',
                        videoUrl: video.videoUrl?.replace(/`/g, '') || ''
                    }))
                }
            } catch (e) {
                console.error('fetchLikedVideos error:', e)
                this.likedVideos = []
            }
        }
    }
})