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
        currentUser: null,
        userProfile: null
    }),

    actions: {
        //  当前登录用户
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

        //  用户主页
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
                        videos: [] // 后端暂时没有视频接口
                    }
                }
            } catch (e) {
                console.error('fetchUserProfile error:', e)
            }
        },

        //  用户页关注
        async toggleFollow() {
            if (!this.userProfile) return

            try {
                const newState = !this.userProfile.isFollowing

                await api.follow.toggle(this.userProfile.id, newState)

                this.userProfile.isFollowing = newState
            } catch (e) {
                console.error('user toggleFollow error:', e)
            }
        }
    }
})