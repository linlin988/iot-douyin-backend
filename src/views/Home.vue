<template>
  <div class="home-container">
    <VideoPlayer :videoList="videoList" @videoChange="handleVideoChange" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import VideoPlayer from '../components/VideoPlayer.vue'
import { useVideoStore } from '../store'

const videoStore = useVideoStore()
const videoList = ref([])

// 处理视频切换
const handleVideoChange = (index) => {
  console.log('Video changed to index:', index)
  // 可以在这里添加视频预加载逻辑
  preloadNextVideo(index)
}

// 预加载下一个视频
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

onMounted(async () => {
  // 获取视频列表
  await videoStore.fetchVideoList()
  videoList.value = videoStore.videoList
})
</script>

<style scoped>
.home-container {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}
</style>