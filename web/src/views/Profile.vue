<template>
  <div class="profile-container" @touchstart="handleTouchStart" @touchmove="handleTouchMove" @touchend="handleTouchEnd">
    <!-- 下拉刷新指示器 -->
    <div class="pull-refresh" :style="{ transform: `translateY(${pullDistance}px)` }">
      <div class="refresh-icon" :class="{ 'refreshing': isRefreshing }">↻</div>
      <div class="refresh-text">{{ refreshText }}</div>
    </div>
    <!-- 顶部导航栏 -->
    <div class="profile-header">
      <button class="back-btn" @click="goBack">←</button>
      <h1 class="profile-title">{{ userProfile?.name || '主页' }}</h1>
      <div class="header-placeholder"></div>
    </div>

    <!-- 骨架屏加载 -->
    <div v-if="!userProfile" class="skeleton-wrap">
      <div class="skel skel-avatar"></div>
      <div class="skel skel-name"></div>
      <div class="skel skel-bio"></div>
    </div>

    <!-- 用户信息 -->
    <div class="user-info" v-if="userProfile">
      <!-- 封面背景（使用渐变代替封面图）-->
      <div class="cover-bg"></div>

      <div class="info-body">
        <div class="avatar-container">
          <img
            :src="userProfile.avatar || defaultAvatar"
            alt="Avatar"
            class="avatar"
            @error="(e) => e.target.src = defaultAvatar"
          />
        </div>

        <h2 class="user-name">@{{ userProfile.name }}</h2>
        <p class="user-bio">{{ userProfile.bio || '这个人很懒，什么都没留下~' }}</p>

        <!-- 数据统计 -->
        <div class="stats-container">
          <div class="stat-item">
            <span class="stat-number">{{ formatNumber(userProfile.following) }}</span>
            <span class="stat-label">关注</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-number">{{ formatNumber(userProfile.followers) }}</span>
            <span class="stat-label">粉丝</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-number">{{ formatNumber(userProfile.videos.length) }}</span>
            <span class="stat-label">作品</span>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-row">
          <button
            class="follow-btn"
            :class="{ 'following': userProfile.isFollowing }"
            @click="handleFollow"
          >
            {{ userProfile.isFollowing ? '✓ 已关注' : '+ 关注' }}
          </button>
          <button class="msg-btn" @click="handleMessage">私信</button>
        </div>
      </div>
    </div>

    <!-- Tab切换：只保留"作品"和"喜欢" -->
    <div class="tab-container" v-if="userProfile">
      <div
        class="tab-item"
        :class="{ 'active': activeTab === 'works' }"
        @click="activeTab = 'works'"
      >
        <span class="tab-icon">▶</span>
        <span>作品</span>
        <div class="tab-line"></div>
      </div>
      <div
        class="tab-item"
        :class="{ 'active': activeTab === 'likes' }"
        @click="activeTab = 'likes'"
      >
        <span class="tab-icon">♥</span>
        <span>喜欢</span>
        <div class="tab-line"></div>
      </div>
    </div>

    <!-- 视频网格 -->
    <div class="video-grid" v-if="userProfile">
      <template v-if="activeTab === 'works'">
        <div
          v-for="(video, index) in userProfile.videos"
          :key="video.id"
          class="video-grid-item"
        >
          <img :src="video.coverUrl" alt="Video cover" class="video-cover"
            @error="(e) => e.target.style.opacity='0'" />
          <div class="video-overlay">
            <span class="play-icon">▶</span>
            <span class="video-stats">♥ {{ formatNumber(video.likes) }}</span>
          </div>
        </div>
        <div v-if="!userProfile.videos.length" class="empty-state">
          <p class="empty-icon">🎬</p>
          <p class="empty-text">还没有发布作品</p>
        </div>
      </template>
      <template v-else>
        <!-- 喜欢列表（TODO：后端接口 GET /content/like/likedList/{userId}）-->
        <div class="empty-state">
          <p class="empty-icon">🔒</p>
          <p class="empty-text">该用户的喜欢列表已加密</p>
        </div>
      </template>
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

// 下拉刷新相关
const pullDistance = ref(0)
const isRefreshing = ref(false)
const refreshText = ref('下拉刷新')
const startY = ref(0)
const isPulling = ref(false)

const defaultAvatar = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><circle cx="50" cy="50" r="50" fill="%23333"/><circle cx="50" cy="38" r="18" fill="%23666"/><ellipse cx="50" cy="85" rx="28" ry="20" fill="%23666"/></svg>'

// 获取用户ID
const userId = route.params.userId

// 处理关注：未登录跳转到登录页
const handleFollow = async () => {
  if (!userStore.isLoggedIn) {
    router.push({ name: 'Login', query: { redirect: route.fullPath } })
    return
  }
  await userStore.toggleFollow()
}

// 私信（TODO：后端实现私信功能后接入）
const handleMessage = () => {
  console.log('私信功能：待后端实现')
}

// 处理触摸开始
const handleTouchStart = (e) => {
  if (window.scrollY === 0) {
    startY.value = e.touches[0].clientY
    isPulling.value = true
  }
}

// 处理触摸移动
const handleTouchMove = (e) => {
  if (!isPulling.value || isRefreshing.value) return
  
  const currentY = e.touches[0].clientY
  const distance = currentY - startY.value
  
  if (distance > 0) {
    e.preventDefault()
    pullDistance.value = Math.min(distance * 0.5, 80)
    
    if (pullDistance.value > 60) {
      refreshText.value = '松开刷新'
    } else {
      refreshText.value = '下拉刷新'
    }
  }
}

// 处理触摸结束
const handleTouchEnd = async () => {
  if (!isPulling.value || isRefreshing.value) return
  
  if (pullDistance.value > 60) {
    isRefreshing.value = true
    refreshText.value = '刷新中...'
    pullDistance.value = 60
    
    // 模拟刷新操作
    await new Promise(resolve => setTimeout(resolve, 1500))
    await userStore.fetchUserProfile(userId)
    
    isRefreshing.value = false
    refreshText.value = '刷新成功'
    
    // 动画结束后重置
    setTimeout(() => {
      pullDistance.value = 0
      refreshText.value = '下拉刷新'
    }, 500)
  } else {
    pullDistance.value = 0
  }
  
  isPulling.value = false
}

// 返回上一页
const goBack = () => {
  router.back()
}

onMounted(async () => {
  await userStore.fetchUserProfile(userId)
})

// 计算属性：用户资料
const userProfile = computed(() => userStore.userProfile)
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@400;700&display=swap');

.profile-container {
  width: 100vw;
  min-height: 100vh;
  background-color: #0a0a0f;
  color: #fff;
  font-family: 'Noto Sans SC', sans-serif;
  overflow-y: auto;
  scroll-behavior: smooth;
  -webkit-overflow-scrolling: touch;
}

/* 下拉刷新 */
.pull-refresh {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 80px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transform: translateY(-100%);
  transition: transform 0.3s ease;
  z-index: 10;
}

.refresh-icon {
  font-size: 24px;
  color: #ff0050;
  margin-bottom: 8px;
  transition: transform 0.3s ease;
}

.refresh-icon.refreshing {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.refresh-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
}

/* 顶部导航 */
.profile-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  position: sticky;
  top: 0;
  z-index: 20;
  background: rgba(10,10,15,0.85);
  backdrop-filter: blur(16px);
  border-bottom: 1px solid rgba(255,255,255,0.06);
}
.back-btn {
  background: transparent;
  border: 1px solid rgba(255,255,255,0.15);
  color: rgba(255,255,255,0.8);
  border-radius: 50%;
  width: 36px; height: 36px;
  font-size: 18px;
  cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.2s;
}
.back-btn:hover { background: rgba(255,255,255,0.1); }
.profile-title {
  font-size: 17px;
  font-weight: 700;
}
.header-placeholder { width: 36px; }

/* 骨架屏 */
.skeleton-wrap {
  display: flex; flex-direction: column; align-items: center;
  padding: 40px 24px;
  gap: 16px;
}
.skel {
  background: linear-gradient(90deg, #1a1a2e 25%, #252545 50%, #1a1a2e 75%);
  background-size: 200% 100%;
  animation: shimmer 1.4s infinite;
  border-radius: 8px;
}
.skel-avatar { width: 88px; height: 88px; border-radius: 50%; }
.skel-name { width: 140px; height: 20px; }
.skel-bio { width: 220px; height: 14px; }
@keyframes shimmer { to { background-position: -200% 0; } }

/* 用户信息 */
.user-info {
  position: relative;
}
.cover-bg {
  height: 140px;
  background: linear-gradient(135deg, #1a0010 0%, #0d001a 50%, #001a1a 100%);
}

.info-body {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 0 24px 24px;
  margin-top: -44px;
}

.avatar-container { margin-bottom: 12px; }
.avatar {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  border: 3px solid #0a0a0f;
  object-fit: cover;
  background: #222;
  box-shadow: 0 0 0 2px rgba(255,0,80,0.5);
}

.user-name {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 6px;
  color: #fff;
}
.user-bio {
  font-size: 13px;
  color: rgba(255,255,255,0.45);
  margin-bottom: 20px;
  text-align: center;
  line-height: 1.5;
  max-width: 280px;
}

/* 统计 */
.stats-container {
  display: flex;
  align-items: center;
  gap: 28px;
  margin-bottom: 20px;
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 16px;
  padding: 14px 28px;
}
.stat-item {
  display: flex; flex-direction: column; align-items: center; gap: 3px;
}
.stat-number {
  font-size: 20px; font-weight: 700; color: #fff;
}
.stat-label {
  font-size: 12px; color: rgba(255,255,255,0.4);
}
.stat-divider {
  width: 1px; height: 28px; background: rgba(255,255,255,0.1);
}

/* 操作按钮 */
.action-row {
  display: flex; gap: 12px;
}
.follow-btn {
  background: linear-gradient(135deg, #ff0050, #ff4d94);
  color: #fff;
  border: none;
  border-radius: 22px;
  padding: 10px 32px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: opacity 0.2s, transform 0.1s;
  font-family: inherit;
}
.follow-btn:hover { opacity: 0.88; transform: translateY(-1px); }
.follow-btn.following {
  background: rgba(255,255,255,0.1);
  border: 1px solid rgba(255,255,255,0.2);
  color: rgba(255,255,255,0.7);
}
.msg-btn {
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.15);
  color: rgba(255,255,255,0.8);
  border-radius: 22px;
  padding: 10px 24px;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
}
.msg-btn:hover { background: rgba(255,255,255,0.15); }

/* Tab */
.tab-container {
  display: flex;
  border-top: 1px solid rgba(255,255,255,0.06);
  border-bottom: 1px solid rgba(255,255,255,0.06);
  margin-top: 4px;
}
.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 14px 0 0;
  cursor: pointer;
  color: rgba(255,255,255,0.3);
  font-size: 14px;
  position: relative;
  transition: color 0.2s;
}
.tab-item.active { color: #fff; font-weight: 600; }
.tab-icon { font-size: 13px; }
.tab-line {
  height: 2px;
  width: 0;
  background: #ff0050;
  border-radius: 2px;
  margin-top: 4px;
  width: 100%;
  transform: scaleX(0);
  transition: transform 0.2s;
}
.tab-item.active .tab-line { transform: scaleX(1); }

/* 视频网格 */
.video-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 2px;
  padding: 2px;
}
.video-grid-item {
  position: relative;
  padding-top: 150%;
  background: #111;
  cursor: pointer;
  overflow: hidden;
}
.video-cover {
  position: absolute;
  inset: 0;
  width: 100%; height: 100%;
  object-fit: cover;
  transition: transform 0.2s;
}
.video-grid-item:hover .video-cover { transform: scale(1.04); }
.video-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.5) 0%, transparent 50%);
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  padding: 6px;
  opacity: 0;
  transition: opacity 0.2s;
}
.video-grid-item:hover .video-overlay { opacity: 1; }
.play-icon { font-size: 14px; color: #fff; }
.video-stats { font-size: 11px; color: rgba(255,255,255,0.9); }

/* 空状态 */
.empty-state {
  grid-column: 1 / -1;
  display: flex; flex-direction: column; align-items: center;
  padding: 60px 24px; gap: 12px;
}
.empty-icon { font-size: 40px; opacity: 0.5; }
.empty-text { font-size: 14px; color: rgba(255,255,255,0.35); }
</style>