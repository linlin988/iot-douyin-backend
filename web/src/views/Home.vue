<template>
  <div class="home-container">
    <!-- 返回个人主页按钮 -->
    <div class="back-button" v-if="fromProfile" @click="goBackToProfile">
      <span class="back-icon">←</span>
      <span class="back-text">返回个人主页</span>
    </div>
    
    <VideoPlayer :videoList="videoList" :currentIndex="currentIndex" @videoChange="handleVideoChange" />

    <!-- 右上角悬浮头像入口 -->
    <div class="avatar-entry" v-show="videoList.length > 0" @click="goToProfile">
      <img
        :src="avatarSrc"
        alt="我的"
        class="entry-avatar"
        @error="onAvatarError"
      />
      <span v-if="!isLoggedIn" class="avatar-label">登录</span>
    </div>
    
    <!-- 右侧滑动模块 -->
    <div 
      class="scroll-module"
      @touchstart="handleScrollModuleTouchStart"
      @touchmove="handleScrollModuleTouchMove"
      @touchend="handleScrollModuleTouchEnd"
    >
      <div class="scroll-track">
        <div class="scroll-thumb" :style="{ transform: `translateY(${scrollThumbPosition}%)` }"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref, watch, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import VideoPlayer from '../components/VideoPlayer.vue'
import { useVideoStore, useUserStore } from '../store'

const router = useRouter()
const route = useRoute()
const videoStore = useVideoStore()
const userStore = useUserStore()

const videoList = ref([])
const fromProfile = ref(false)
const currentIndex = ref(0)
const scrollThumbPosition = ref(0)
const touchStartY = ref(0)
const touchStartPosition = ref(0)

const DEFAULT_AVATAR = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><circle cx="50" cy="50" r="50" fill="%23444"/><circle cx="50" cy="38" r="18" fill="%23777"/><ellipse cx="50" cy="85" rx="28" ry="20" fill="%23777"/></svg>'

const isLoggedIn = computed(() => userStore.isLoggedIn)
const avatarSrc = ref(DEFAULT_AVATAR)

const onAvatarError = () => { avatarSrc.value = DEFAULT_AVATAR }

watch(() => userStore.currentUser, (user) => {
  avatarSrc.value = user?.avatar || DEFAULT_AVATAR
}, { immediate: true })

const goToProfile = () => {
  if (isLoggedIn.value) {
    router.push('/me')
  } else {
    router.push('/login')
  }
}

const goBackToProfile = () => {
  router.push('/me')
}

// 滑动模块触摸开始
const handleScrollModuleTouchStart = (e) => {
  touchStartY.value = e.touches[0].clientY
  touchStartPosition.value = scrollThumbPosition.value
}

// 滑动模块触摸移动
const handleScrollModuleTouchMove = (e) => {
  const touchY = e.touches[0].clientY
  const deltaY = touchY - touchStartY.value
  const deltaPosition = (deltaY / window.innerHeight) * 100
  
  // 计算新的位置，限制在0-100%之间
  let newPosition = touchStartPosition.value + deltaPosition
  newPosition = Math.max(0, Math.min(100, newPosition))
  
  scrollThumbPosition.value = newPosition
  
  // 根据滑动位置计算视频索引
  if (videoList.value.length > 0) {
    const videoIndex = Math.floor((newPosition / 100) * (videoList.value.length - 1))
    if (videoIndex !== currentIndex.value) {
      currentIndex.value = videoIndex
    }
  }
}

// 滑动模块触摸结束
const handleScrollModuleTouchEnd = () => {
  // 触摸结束时的处理
}

const handleVideoChange = (index) => {
  console.log('Video changed to index:', index)
  currentIndex.value = index
  // 更新滑动模块的位置
  if (videoList.value.length > 1) {
    scrollThumbPosition.value = (index / (videoList.value.length - 1)) * 100
  }
  preloadNextVideo(index)
}

const preloadNextVideo = (currentIndex) => {
  if (currentIndex < videoList.value.length - 1) {
    const nextVideo = videoList.value[currentIndex + 1]
    if (nextVideo) {
      const video = document.createElement('video')
      video.src = nextVideo.videoUrl
      video.preload = 'metadata'
    }
  }
}

watch(() => videoStore.videoList, (newList) => {
  if (!fromProfile.value) {
    videoList.value = newList
  }
}, { deep: true })

onMounted(async () => {
  // 检查是否从个人主页跳转过来
  const fromProfileParam = route.query.fromProfile
  const videoListParam = route.query.videoList
  const currentIndexParam = route.query.currentIndex
  
  if (fromProfileParam === 'true' && videoListParam) {
    try {
      // 使用从个人主页传递过来的视频列表
      fromProfile.value = true
      const parsedVideoList = JSON.parse(videoListParam)
      // 转换视频数据格式，确保与VideoPlayer组件期望的格式一致
      videoList.value = parsedVideoList.map((video, index) => ({
        id: video.id,
        videoUrl: video.videoUrl,
        coverUrl: video.coverUrl,
        title: video.title || video.description || `视频 ${index + 1}`,
        description: video.description || video.title || '',
        likes: Number(video.likeCount) || 0,
        comments: Number(video.commentCount) || 0,
        shares: 0,
        isLiked: false,
        author: {
          id: userStore.currentUser?.id || 0,
          name: userStore.currentUser?.username || '我',
          avatar: userStore.currentUser?.avatar || '',
          isFollowing: false
        }
      }))
      
      // 若已登录则拉取当前用户信息（更新头像）
      if (isLoggedIn.value && !userStore.currentUser) {
        await userStore.fetchCurrentUser()
      }
    } catch (error) {
      console.error('Failed to parse video list from profile:', error)
      // 解析失败时，使用默认视频列表
      await videoStore.fetchVideoList()
      videoList.value = videoStore.videoList
    }
  } else {
    // 正常加载视频列表
    await videoStore.fetchVideoList()
    videoList.value = videoStore.videoList

    // 若已登录则拉取当前用户信息（更新头像）
    if (isLoggedIn.value && !userStore.currentUser) {
      await userStore.fetchCurrentUser()
    }
  }
})
</script>

<style scoped>
.home-container {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  position: relative;
}

/* 返回个人主页按钮 */
.back-button {
  position: fixed;
  top: 16px;
  left: 16px;
  z-index: 100;
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(0,0,0,0.6);
  backdrop-filter: blur(10px);
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s;
  filter: drop-shadow(0 2px 8px rgba(0,0,0,0.6));
}

.back-button:hover {
  background: rgba(0,0,0,0.8);
  transform: translateX(-4px);
}

.back-icon {
  font-size: 16px;
  color: #fff;
  font-weight: bold;
}

.back-text {
  font-size: 14px;
  color: #fff;
  font-weight: 500;
}

/* 右上角悬浮头像 */
.avatar-entry {
  position: fixed;
  top: 16px;
  right: 16px;
  z-index: 100;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  gap: 4px;
  filter: drop-shadow(0 2px 8px rgba(0,0,0,0.6));
}

.entry-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid rgba(255,255,255,0.8);
  background: #333;
  transition: transform 0.2s, border-color 0.2s;
}
.avatar-entry:hover .entry-avatar {
  transform: scale(1.08);
  border-color: #ff0050;
}

.avatar-label {
  font-size: 11px;
  color: rgba(255,255,255,0.8);
  white-space: nowrap;
  text-shadow: 0 1px 4px rgba(0,0,0,0.8);
}

/* 右侧滑动模块 */
.scroll-module {
  position: fixed;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 99;
  width: 40px;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.scroll-track {
  width: 4px;
  height: 100%;
  background: rgba(255,255,255,0.2);
  border-radius: 2px;
  position: relative;
  overflow: hidden;
}

.scroll-thumb {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 30px;
  background: rgba(255,255,255,0.6);
  border-radius: 2px;
  cursor: pointer;
  transition: background 0.2s;
}

.scroll-thumb:hover {
  background: rgba(255,255,255,0.8);
}
</style>