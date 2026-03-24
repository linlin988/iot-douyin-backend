<template>
  <div class="home-container">
    <VideoPlayer :videoList="videoList" @videoChange="handleVideoChange" />

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
  </div>
</template>

<script setup>
import { onMounted, ref, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import VideoPlayer from '../components/VideoPlayer.vue'
import { useVideoStore, useUserStore } from '../store'

const router = useRouter()
const videoStore = useVideoStore()
const userStore = useUserStore()

const videoList = ref([])

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

const handleVideoChange = (index) => {
  console.log('Video changed to index:', index)
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
  videoList.value = newList
}, { deep: true })

onMounted(async () => {
  await videoStore.fetchVideoList()
  videoList.value = videoStore.videoList

  // 若已登录则拉取当前用户信息（更新头像）
  if (isLoggedIn.value && !userStore.currentUser) {
    await userStore.fetchCurrentUser()
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
</style>