import { defineStore } from 'pinia'

export const useVideoStore = defineStore('video', {
  state: () => ({
    videoList: [],
    currentVideoIndex: 0,
    isLoading: false,
    hasMore: true
  }),
  actions: {
    async fetchVideoList() {
      this.isLoading = true
      try {
        // 使用真实API数据
        const apiData = {
          "code": 200,
          "message": "222",
          "data": {
            "records": [
              {
                "id": 2035567570664845313,
                "userId": 1,
                "title": "视频",
                "description": "测试视频",
                "videoUrl": " `https://iot-enky.oss-cn-hangzhou.aliyuncs.com/2026/03/85d45e8e-502b-4edb-a3a5-a52a6d9c90b7.mp4` ",
                "coverUrl": " `https://iot-enky.oss-cn-hangzhou.aliyuncs.com/2026/03/cce18d47-6cb3-4855-a773-f5f462d31859.jpg` ",
                "likeCount": 0,
                "playCount": 0,
                "commentCount": 0,
                "createTime": "2026-03-22T12:01:43",
                "updateTime": "2026-03-22T04:01:43"
              },
              {
                "id": 2035566364508844033,
                "userId": 1,
                "title": "视频",
                "description": "测试视频",
                "videoUrl": " `https://iot-enky.oss-cn-hangzhou.aliyuncs.com/2026/03/8a824d7e-f178-4947-99b1-5fc8d4189d7f.mp4` ",
                "coverUrl": " `https://iot-enky.oss-cn-hangzhou.aliyuncs.com/2026/03/f6eff800-7d9d-4b69-a984-863b91fde37f.jpg` ",
                "likeCount": 0,
                "playCount": 0,
                "commentCount": 0,
                "createTime": "2026-03-22T11:56:55",
                "updateTime": "2026-03-22T03:56:57"
              },
              {
                "id": 2035556625128882177,
                "userId": 1,
                "title": "小恩",
                "description": "测试视频",
                "videoUrl": " `https://iot-enky.oss-cn-hangzhou.aliyuncs.com/2026/03/dc14e649-0a9f-4af7-9f72-a51a2dd246dc.gif` ",
                "coverUrl": " `https://iot-enky.oss-cn-hangzhou.aliyuncs.com/2026/03/a1e283f3-0e65-4f52-a37c-f3a5d0f327b5.jpg` ",
                "likeCount": 0,
                "playCount": 0,
                "commentCount": 0,
                "createTime": "2026-03-22T11:18:13",
                "updateTime": "2026-03-22T03:18:13"
              },
              {
                "id": 2035556283402158081,
                "userId": 1,
                "title": "",
                "description": "",
                "videoUrl": " `https://iot-enky.oss-cn-hangzhou.aliyuncs.com/2026/03/c6981fea-9d4c-40e6-a3b9-5eb7689f694c.gif` ",
                "coverUrl": " `https://iot-enky.oss-cn-hangzhou.aliyuncs.com/2026/03/8fa9a42e-e61c-46b4-8e88-86922b9f9f96.jpg` ",
                "likeCount": 0,
                "playCount": 0,
                "commentCount": 0,
                "createTime": "2026-03-22T11:16:51",
                "updateTime": "2026-03-22T03:16:52"
              },
              {
                "id": 2035350626413703170,
                "userId": 1,
                "title": "",
                "description": "",
                "videoUrl": " `https://iot-douying.oss-cn-beijing.aliyuncs.com/2026/03/db814ccd-e02c-46b6-8256-3c33f89ea309.gif` ",
                "coverUrl": " `https://iot-douying.oss-cn-beijing.aliyuncs.com/2026/03/e6717490-3a1b-4d96-b41f-88b30b088190.jpg` ",
                "likeCount": 0,
                "playCount": 0,
                "commentCount": 0,
                "createTime": "2026-03-21T21:39:39",
                "updateTime": "2026-03-21T13:39:39"
              },
              {
                "id": 2035281386759331841,
                "userId": 1,
                "title": "",
                "description": "",
                "videoUrl": " `https://iot-douying.oss-cn-beijing.aliyuncs.com/2026/03/d99b0983-a5f0-424c-86c2-d2d2503d9d53.mp4` ",
                "coverUrl": " `https://iot-douying.oss-cn-beijing.aliyuncs.com/2026/03/cf4d7888-8a52-4590-8346-2a8696ba1d25.jpg` ",
                "likeCount": 0,
                "playCount": 0,
                "commentCount": 0,
                "createTime": "2026-03-21T09:04:31",
                "updateTime": "2026-03-21T09:04:31"
              },
              {
                "id": 2034998153605910530,
                "userId": 1,
                "title": "",
                "description": "",
                "videoUrl": " `https://iot-douying.oss-cn-beijing.aliyuncs.com/2026/03/5be19fcb-f2a4-4302-ae7f-818b3d7388a6.mp4` ",
                "coverUrl": " `https://iot-douying.oss-cn-beijing.aliyuncs.com/2026/03/2806ecf6-451b-4396-85e2-6f35c9821859.jpg` ",
                "likeCount": 0,
                "playCount": 0,
                "commentCount": 0,
                "createTime": "2026-03-20T14:19:03",
                "updateTime": "2026-03-20T14:19:06"
              },
              {
                "id": 2034996448390545409,
                "userId": 1,
                "title": "",
                "description": "",
                "videoUrl": " `https://iot-maffile.oss-cn-beijing.aliyuncs.com/2026/03/60551c91-c47d-4701-a4fa-710e2c16ad45.mp4` ",
                "coverUrl": " `https://iot-maffile.oss-cn-beijing.aliyuncs.com/2026/03/696972e7-f0f0-487f-898e-b3e1ddf026d7.png` ",
                "likeCount": 0,
                "playCount": 0,
                "commentCount": 0,
                "createTime": "2026-03-20T14:12:16",
                "updateTime": "2026-03-20T14:12:18"
              },
              {
                "id": 2034995415069229057,
                "userId": 1,
                "title": "",
                "description": "",
                "videoUrl": " `https://iot-maffile.oss-cn-beijing.aliyuncs.com/2026/03/a842a31c-1aac-4168-80ce-d892a417ffea.mp4` ",
                "coverUrl": " `https://iot-maffile.oss-cn-beijing.aliyuncs.com/2026/03/83854790-7f21-4dd1-871d-f09da5934191.png` ",
                "likeCount": 2,
                "playCount": 0,
                "commentCount": 1,
                "createTime": "2026-03-20T14:08:10",
                "updateTime": "2026-03-21T08:44:42"
              }
            ],
            "total": 9,
            "size": 10,
            "current": 1,
            "pages": 1
          }
        }
        
        // 转换API数据为视频列表格式
        const mockData = apiData.data.records.map((record, index) => ({
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
        this.videoList = mockData
        this.hasMore = false
      } catch (error) {
        console.error('Failed to fetch videos:', error)
      } finally {
        this.isLoading = false
      }
    },
    setCurrentVideoIndex(index) {
      this.currentVideoIndex = index
    },
    toggleLike(videoId) {
      const video = this.videoList.find(v => v.id === videoId)
      if (video) {
        video.isLiked = !video.isLiked
        video.likes += video.isLiked ? 1 : -1
      }
    },
    toggleFollow(userId) {
      const video = this.videoList.find(v => v.author.id === userId)
      if (video) {
        video.author.isFollowing = !video.author.isFollowing
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
        // 模拟API请求
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
      } catch (error) {
        console.error('Failed to fetch user profile:', error)
      }
    },
    toggleFollow() {
      if (this.userProfile) {
        this.userProfile.isFollowing = !this.userProfile.isFollowing
      }
    }
  }
})