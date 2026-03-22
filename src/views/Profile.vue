<template>
  <div class="profile-container">
    <!-- 顶部导航栏 -->
    <div class="profile-header">
      <van-icon name="arrow-left" @click="goBack" />
      <h1 class="profile-title">{{ userProfile?.name || 'Loading...' }}</h1>
      <van-icon name="ellipsis" />
    </div>
    
    <!-- 用户信息 -->
    <div class="user-info" v-if="userProfile">
      <div class="avatar-container">
        <img :src="userProfile.avatar" alt="Avatar" class="avatar" />
      </div>
      <div class="stats-container">
        <div class="stat-item">
          <span class="stat-number">{{ userProfile.videos.length }}</span>
          <span class="stat-label">作品</span>
        </div>
        <div class="stat-item">
          <span class="stat-number">{{ formatNumber(userProfile.followers) }}</span>
          <span class="stat-label">粉丝</span>
        </div>
        <div class="stat-item">
          <span class="stat-number">{{ formatNumber(userProfile.following) }}</span>
          <span class="stat-label">关注</span>
        </div>
      </div>
      <div class="user-details">
        <h2 class="user-name">{{ userProfile.name }}</h2>
        <p class="user-bio">{{ userProfile.bio }}</p>
      </div>
      <button 
        class="follow-btn" 
        :class="{ 'following': userProfile.isFollowing }"
        @click="handleFollow"
      >
        {{ userProfile.isFollowing ? '已关注' : '关注' }}
      </button>
    </div>
    
    <!-- Tab切换 -->
    <div class="tab-container">
      <div 
        class="tab-item" 
        :class="{ 'active': activeTab === 'works' }"
        @click="activeTab = 'works'"
      >
        <van-icon name="play-circle-o" />
        <span>作品</span>
      </div>
      <div 
        class="tab-item" 
        :class="{ 'active': activeTab === 'dynamic' }"
        @click="activeTab = 'dynamic'"
      >
        <van-icon name="time-o" />
        <span>动态</span>
      </div>
      <div 
        class="tab-item" 
        :class="{ 'active': activeTab === 'likes' }"
        @click="activeTab = 'likes'"
      >
        <van-icon name="heart-o" />
        <span>喜欢</span>
      </div>
    </div>
    
    <!-- 视频列表 -->
    <div class="video-grid" v-if="userProfile">
      <div 
        v-for="(video, index) in userProfile.videos" 
        :key="video.id" 
        class="video-grid-item"
      >
        <img :src="video.coverUrl" alt="Video cover" class="video-cover" />
        <div class="video-overlay">
          <van-icon name="play-circle-o" />
          <span class="video-stats">{{ formatNumber(video.likes) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../store'
import { formatNumber } from '../utils/format'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('works')

// 获取用户ID
const userId = route.params.userId

// 处理关注
const handleFollow = () => {
  userStore.toggleFollow()
}

// 返回上一页
const goBack = () => {
  router.back()
}

onMounted(async () => {
  // 获取用户资料
  await userStore.fetchUserProfile(userId)
})

// 计算属性：用户资料
const userProfile = computed(() => userStore.userProfile)
</script>

<style scoped>
.profile-container {
  width: 100vw;
  min-height: 100vh;
  background-color: #000;
  color: #fff;
}

.profile-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px;
  background-color: #000;
  position: sticky;
  top: 0;
  z-index: 10;
}

.profile-title {
  font-size: 18px;
  font-weight: bold;
}

.user-info {
  padding: 20px;
  text-align: center;
}

.avatar-container {
  margin-bottom: 20px;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  border: 2px solid #fff;
}

.stats-container {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

.user-details {
  margin-bottom: 20px;
}

.user-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
}

.user-bio {
  font-size: 14px;
  color: #999;
  line-height: 1.5;
}

.follow-btn {
  background-color: #ff0050;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 8px 40px;
  font-size: 14px;
  cursor: pointer;
}

.follow-btn.following {
  background-color: rgba(255,255,255,0.3);
}

.tab-container {
  display: flex;
  justify-content: space-around;
  padding: 15px 0;
  border-top: 1px solid #333;
  border-bottom: 1px solid #333;
  margin-bottom: 10px;
}

.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  color: #999;
}

.tab-item.active {
  color: #fff;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 2px;
}

.video-grid-item {
  position: relative;
  padding-top: 150%; /* 3:2 宽高比 */
}

.video-cover {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-overlay {
  position: absolute;
  bottom: 5px;
  right: 5px;
  display: flex;
  align-items: center;
  gap: 5px;
  color: #fff;
  font-size: 12px;
  text-shadow: 0 0 2px rgba(0,0,0,0.8);
}
</style>