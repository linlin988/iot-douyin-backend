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
        // 调用真实API获取视频列表
        const response = await api.video.getList({ page, size: 10 })
        const apiData = response.data
        
        if (apiData.code === 200) {
          // 转换API数据为视频列表格式
          const videos = apiData.data.records.map((record, index) => ({
            id: record.id,
            videoUrl: record.videoUrl.replace(/`/g, ''), // 去除反引号
            coverUrl: record.coverUrl.replace(/`/g, ''), // 去除反引号
            author: {
              id: record.userId,
              name: `User${record.userId}`,
              avatar: `https://example.com/avatar${record.userId}.jpg`,
              isFollowing: false
            },
            description: record.description || record.title || `视频 ${index + 1}`,
            likes: record.likeCount,
            comments: record.commentCount,
            shares: Math.floor(Math.random() * 500),
            isLiked: false
          }))
          
          // 如果是加载更多，就追加视频列表
          if (loadMore) {
            this.videoList = [...this.videoList, ...videos]
          } else {
            this.videoList = videos
          }
          
          this.hasMore = apiData.data.current < apiData.data.pages
        }
      } catch (error) {
        console.error('Failed to fetch videos:', error)
        // 降级使用模拟数据
        if (!loadMore) {
          const mockData = [
            {
              id: 1,
              videoUrl: 'https://iot-enky.oss-cn-hangzhou.aliyuncs.com/2026/03/85d45e8e-502b-4edb-a3a5-a52a6d9c90b7.mp4',
              coverUrl: 'https://iot-enky.oss-cn-hangzhou.aliyuncs.com/2026/03/cce18d47-6cb3-4855-a773-f5f462d31859.jpg',
              author: {
                id: 1,
                name: 'User1',
                avatar: 'https://example.com/avatar1.jpg',
                isFollowing: false
              },
              description: '测试视频 1',
              likes: 123,
              comments: 45,
              shares: 67,
              isLiked: false
            },
            {
              id: 2,
              videoUrl: 'https://iot-enky.oss-cn-hangzhou.aliyuncs.com/2026/03/8a824d7e-f178-4947-99b1-5fc8d4189d7f.mp4',
              coverUrl: 'https://iot-enky.oss-cn-hangzhou.aliyuncs.com/2026/03/f6eff800-7d9d-4b69-a984-863b91fde37f.jpg',
              author: {
                id: 1,
                name: 'User1',
                avatar: 'https://example.com/avatar1.jpg',
                isFollowing: false
              },
              description: '测试视频 2',
              likes: 567,
              comments: 89,
              shares: 123,
              isLiked: false
            },
            {
              id: 3,
              videoUrl: 'https://iot-enky.oss-cn-hangzhou.aliyuncs.com/2026/03/dc14e649-0a9f-4af7-9f72-a51a2dd246dc.gif',
              coverUrl: 'https://iot-enky.oss-cn-hangzhou.aliyuncs.com/2026/03/a1e283f3-0e65-4f52-a37c-f3a5d0f327b5.jpg',
              author: {
                id: 1,
                name: 'User1',
                avatar: 'https://example.com/avatar1.jpg',
                isFollowing: false
              },
              description: '测试视频 3',
              likes: 789,
              comments: 123,
              shares: 456,
              isLiked: false
            },
            {
              id: 4,
              videoUrl: 'https://iot-douying.oss-cn-beijing.aliyuncs.com/2026/03/db814ccd-e02c-46b6-8256-3c33f89ea309.gif',
              coverUrl: 'https://iot-douying.oss-cn-beijing.aliyuncs.com/2026/03/e6717490-3a1b-4d96-b41f-88b30b088190.jpg',
              author: {
                id: 1,
                name: 'User1',
                avatar: 'https://example.com/avatar1.jpg',
                isFollowing: false
              },
              description: '测试视频 4',
              likes: 321,
              comments: 67,
              shares: 98,
              isLiked: false
            }
          ]
          this.videoList = mockData
          this.hasMore = false
        }
      } finally {
        this.isLoading = false
      }
    },
    setCurrentVideoIndex(index) {
      this.currentVideoIndex = index
    },
    async toggleLike(videoId) {
      const video = this.videoList.find(v => v.id === videoId)
      if (video) {
        try {
          if (video.isLiked) {
            await api.like.delete(videoId)
          } else {
            await api.like.create(videoId)
          }
          video.isLiked = !video.isLiked
          video.likes += video.isLiked ? 1 : -1
        } catch (error) {
          console.error('Failed to toggle like:', error)
        }
      }
    },
    async toggleFollow(userId) {
      const video = this.videoList.find(v => v.author.id === userId)
      if (video) {
        try {
          if (video.author.isFollowing) {
            await api.follow.delete(userId)
          } else {
            await api.follow.create(userId)
          }
          video.author.isFollowing = !video.author.isFollowing
        } catch (error) {
          console.error('Failed to toggle follow:', error)
        }
      }
    }
  }
})

export const useUserStore = defineStore('user', {
  state: () => ({
    currentUser: {
      id: 1,
      name: 'Current User',
      avatar: 'https://example.com/avatar.jpg',
      bio: 'This is my bio'
    },
    userProfile: null
  }),
  actions: {
    async fetchUserProfile(userId) {
      try {
        // 调用真实API获取用户信息
        const userInfoResponse = await api.user.getInfo(userId)
        const userVideoResponse = await api.user.getVideoList(userId)
        
        if (userInfoResponse.data.code === 200 && userVideoResponse.data.code === 200) {
          const userInfo = userInfoResponse.data.data
          const userVideos = userVideoResponse.data.data.records
          
          this.userProfile = {
            id: userInfo.id,
            name: userInfo.username,
            avatar: userInfo.avatar || `https://example.com/avatar${userId}.jpg`,
            bio: userInfo.bio || `This is user ${userId}'s bio`,
            followers: userInfo.followerCount || 0,
            following: userInfo.followingCount || 0,
            isFollowing: false,
            videos: userVideos.map(video => ({
              id: video.id,
              coverUrl: video.coverUrl.replace(/`/g, ''),
              likes: video.likeCount,
              comments: video.commentCount
            }))
          }
        }
      } catch (error) {
        console.error('Failed to fetch user profile:', error)
        // 降级使用模拟数据
        this.userProfile = {
          id: userId,
          name: `User${userId}`,
          avatar: `https://example.com/avatar${userId}.jpg`,
          bio: `This is user ${userId}'s bio`,
          followers: Math.floor(Math.random() * 10000),
          following: Math.floor(Math.random() * 1000),
          isFollowing: false,
          videos: Array.from({ length: 12 }, (_, index) => ({
            id: index + 1000,
            coverUrl: `https://example.com/cover${index + 1}.jpg`,
            likes: Math.floor(Math.random() * 10000),
            comments: Math.floor(Math.random() * 1000)
          }))
        }
      }
    },
    async toggleFollow() {
      if (this.userProfile) {
        try {
          if (this.userProfile.isFollowing) {
            await api.follow.delete(this.userProfile.id)
          } else {
            await api.follow.create(this.userProfile.id)
          }
          this.userProfile.isFollowing = !this.userProfile.isFollowing
        } catch (error) {
          console.error('Failed to toggle follow:', error)
        }
      }
    }
  }
})