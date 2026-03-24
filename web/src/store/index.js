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
                    const videos = apiData.data.records.map((record, index) => ({
                        id: record.id,
                        videoUrl: record.videoUrl?.replace(/`/g, ''),
                        coverUrl: record.coverUrl?.replace(/`/g, ''),
                        author: {
                            id: record.userId,
                            name: `User${record.userId}`,
                            avatar: `https://example.com/avatar${record.userId}.jpg`,
                            isFollowing: false
                        },
                        description: record.description || record.title || `视频 ${index + 1}`,
                        likes: record.likeCount,
                        comments: record.commentCount,
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
                video.likes += video.isLiked ? 1 : -1
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
                const token = res.data.data
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
                        followers: user.followerCount || 0,
                        following: user.followingCount || 0,
                        isFollowing: false,
                        videos: []
                    }
                }
            } catch (e) {
                console.error('fetchUserProfile error:', e)
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
        // TODO: 依赖后端接口 GET /content/video/userList/{userId}，若接口未实现则返回空数组
        async fetchPublishedVideos(userId) {
            try {
                const res = await api.video.getUserVideos(userId)
                if (res.data.code === 200) {
                    this.publishedVideos = res.data.data?.records || res.data.data || []
                }
            } catch (e) {
                console.error('fetchPublishedVideos error:', e)
                this.publishedVideos = []
            }
        },

        // 获取我点赞的视频
        // TODO: 依赖后端接口 GET /content/like/likedList/{userId}，若接口未实现则返回空数组
        async fetchLikedVideos(userId) {
            try {
                const res = await api.like.getLikedVideos(userId)
                if (res.data.code === 200) {
                    this.likedVideos = res.data.data?.records || res.data.data || []
                }
            } catch (e) {
                console.error('fetchLikedVideos error:', e)
                this.likedVideos = []
            }
        }
    }
})