<template>
  <div class="my-profile-container">
    <!-- 顶部导航 -->
    <div class="profile-nav">
      <button class="back-btn" @click="goToHome">←</button>
      <h1 class="nav-title">我的</h1>
      <button class="logout-btn" @click="handleLogout">退出</button>
    </div>

    <!-- 用户信息卡片 -->
    <div class="user-card">
      <div class="avatar-wrap" @click="triggerAvatarUpload">
        <img
          :src="avatarSrc"
          alt="头像"
          class="user-avatar"
          @error="onAvatarError"
        />
        <div class="avatar-mask">
          <span>更换</span>
        </div>
        <input
          ref="fileInput"
          type="file"
          accept="image/*"
          style="display:none"
          @change="handleAvatarChange"
        />
      </div>
      <h2 class="user-name">{{ currentUser?.username || '未知用户' }}</h2>
      <p class="user-bio">{{ currentUser?.bio || '这个人很懒，什么都没留下~' }}</p>

      <div class="stats-row">
        <div class="stat-item">
          <span class="stat-num">{{ currentUser?.followingCount || 0 }}</span>
          <span class="stat-label">关注</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-num">{{ currentUser?.followerCount || 0 }}</span>
          <span class="stat-label">粉丝</span>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <span class="stat-num">{{ publishedVideos.length }}</span>
          <span class="stat-label">获赞</span>
        </div>
      </div>
    </div>

    <!-- Tab 切换 -->
    <div class="tab-bar">
      <button
        class="tab-btn"
        :class="{ active: activeTab === 'published' }"
        @click="activeTab = 'published'"
      >
        <span class="tab-icon">▶</span> 已发布
      </button>
      <button
        class="tab-btn"
        :class="{ active: activeTab === 'liked' }"
        @click="activeTab = 'liked'"
      >
        <span class="tab-icon">♥</span> 我的点赞
      </button>
    </div>

    <!-- 视频网格 -->
    <div class="video-grid" v-if="currentList.length > 0">
      <div
        v-for="video in currentList"
        :key="video.id"
        class="video-card"
        @click="playVideo(video)"
      >
        <div class="video-thumb">
          <img
            :src="video.coverUrl || video.cover || ''"
            alt="封面"
            class="thumb-img"
            @error="(e) => e.target.style.display='none'"
          />
          <div class="thumb-overlay">
            <span class="play-icon">▶</span>
            <span class="like-count">♥ {{ formatNum(video.likeCount || video.likes || 0) }}</span>
          </div>
        </div>
        <p class="video-title">{{ video.description || video.title || '视频' }}</p>
      </div>
    </div>

    <!-- 空状态 -->
    <div class="empty-state" v-else-if="!loading">
      <div class="empty-icon">{{ activeTab === 'published' ? '🎬' : '💔' }}</div>
      <p class="empty-text">{{ activeTab === 'published' ? '还没有发布视频' : '还没有点赞的视频' }}</p>
      <router-link v-if="activeTab === 'published'" to="/upload" class="upload-link">
        去发布
      </router-link>
    </div>

    <!-- 加载中 -->
    <div class="loading-state" v-if="loading">
      <div class="loading-spinner"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store'
import { api } from '../api'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('published')
const loading = ref(false)
const fileInput = ref(null)

const DEFAULT_AVATAR = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><circle cx="50" cy="50" r="50" fill="%23333"/><circle cx="50" cy="38" r="18" fill="%23666"/><ellipse cx="50" cy="85" rx="28" ry="20" fill="%23666"/></svg>'

const currentUser = computed(() => userStore.currentUser)
const publishedVideos = computed(() => userStore.publishedVideos)
const likedVideos = computed(() => userStore.likedVideos)

const avatarSrc = ref(DEFAULT_AVATAR)
const onAvatarError = () => { avatarSrc.value = DEFAULT_AVATAR }

watch(currentUser, (user) => {
  if (user?.avatar) avatarSrc.value = user.avatar
  else avatarSrc.value = DEFAULT_AVATAR
}, { immediate: true })

const currentList = computed(() =>
  activeTab.value === 'published' ? publishedVideos.value : likedVideos.value
)

const formatNum = (n) => {
  if (n >= 10000) return (n / 10000).toFixed(1) + 'w'
  return String(n)
}

const playVideo = (video) => {
  // 确定要播放的视频列表
  const targetList = activeTab.value === 'published' ? publishedVideos.value : likedVideos.value
  // 找到视频在列表中的索引
  const videoIndex = targetList.findIndex(v => v.id === video.id)
  // 跳转到首页，并传递视频列表和当前索引
  router.push({
    path: '/',
    query: {
      fromProfile: 'true',
      videoList: JSON.stringify(targetList),
      currentIndex: videoIndex.toString()
    }
  })
}

const triggerAvatarUpload = () => {
  fileInput.value?.click()
}

const handleAvatarChange = async (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await api.user.uploadAvatar(formData)
    if (res.data.code === 200) {
      const newAvatarUrl = res.data?.data
      if (newAvatarUrl) {
        avatarSrc.value = newAvatarUrl
        await api.user.update({ avatar: newAvatarUrl })
      }
      await userStore.fetchCurrentUser()
    } else {
      alert(res.data?.message || '头像上传失败，请重试')
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    alert('头像上传失败，请重试')
  } finally {
    e.target.value = ''
  }
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

const goToHome = () => {
  router.push('/')
}

const loadData = async () => {
  if (!currentUser.value) {
    await userStore.fetchCurrentUser()
  }
  const userId = currentUser.value?.id
  if (!userId) return

  loading.value = true
  try {
    await Promise.all([
      userStore.fetchPublishedVideos(userId),
      userStore.fetchLikedVideos(userId)
    ])
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@400;700&display=swap');

.my-profile-container {
  width: 100vw;
  min-height: 100vh;
  background: #0a0a0f;
  color: #fff;
  font-family: 'Noto Sans SC', sans-serif;
  overflow-y: auto;
  padding-bottom: 40px;
  -webkit-overflow-scrolling: touch;
}

/* 顶部导航 */
.profile-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  position: sticky;
  top: 0;
  background: rgba(10,10,15,0.9);
  backdrop-filter: blur(16px);
  z-index: 10;
  border-bottom: 1px solid rgba(255,255,255,0.06);
}
.back-btn, .logout-btn {
  background: transparent;
  border: 1px solid rgba(255,255,255,0.15);
  color: rgba(255,255,255,0.7);
  border-radius: 20px;
  padding: 6px 14px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}
.back-btn:hover, .logout-btn:hover {
  background: rgba(255,255,255,0.08);
  color: #fff;
}
.logout-btn {
  color: #ff6b88;
  border-color: rgba(255,0,80,0.3);
}
.logout-btn:hover {
  background: rgba(255,0,80,0.1);
  color: #ff0050;
}
.nav-title {
  font-size: 18px;
  font-weight: 700;
}

/* 用户信息卡片 */
.user-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 32px 24px 24px;
  background: linear-gradient(180deg, rgba(255,0,80,0.08) 0%, transparent 100%);
  border-bottom: 1px solid rgba(255,255,255,0.06);
}

.avatar-wrap {
  position: relative;
  width: 88px;
  height: 88px;
  border-radius: 50%;
  cursor: pointer;
  margin-bottom: 14px;
  flex-shrink: 0;
}
.user-avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: 2.5px solid rgba(255,0,80,0.6);
  background: #222;
}
.avatar-mask {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
  font-size: 12px;
  color: #fff;
}
.avatar-wrap:hover .avatar-mask {
  opacity: 1;
}

.user-name {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 6px;
}
.user-bio {
  font-size: 13px;
  color: rgba(255,255,255,0.45);
  margin-bottom: 20px;
  text-align: center;
  max-width: 260px;
  line-height: 1.5;
}

.stats-row {
  display: flex;
  align-items: center;
  gap: 0;
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 16px;
  padding: 16px 32px;
  gap: 24px;
}
.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3px;
}
.stat-num {
  font-size: 20px;
  font-weight: 700;
  color: #fff;
}
.stat-label {
  font-size: 12px;
  color: rgba(255,255,255,0.4);
}
.stat-divider {
  width: 1px;
  height: 28px;
  background: rgba(255,255,255,0.1);
}

/* Tab */
.tab-bar {
  display: flex;
  padding: 0 16px;
  gap: 8px;
  margin: 20px 0 12px;
}
.tab-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  background: rgba(255,255,255,0.05);
  border: 1px solid rgba(255,255,255,0.08);
  color: rgba(255,255,255,0.5);
  border-radius: 12px;
  padding: 12px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  font-family: inherit;
}
.tab-btn.active {
  background: rgba(255,0,80,0.15);
  border-color: rgba(255,0,80,0.4);
  color: #ff0050;
  font-weight: 600;
}
.tab-icon {
  font-size: 13px;
}

/* 视频网格 */
.video-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 3px;
  padding: 0 3px;
}

.video-card {
  cursor: pointer;
  border-radius: 4px;
  overflow: hidden;
}
.video-thumb {
  position: relative;
  padding-top: 150%;
  background: #1a1a1a;
}
.thumb-img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.thumb-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.6) 0%, transparent 50%);
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  padding: 6px 6px;
  opacity: 0;
  transition: opacity 0.2s;
}
.video-card:hover .thumb-overlay {
  opacity: 1;
}
.play-icon { font-size: 16px; color: #fff; }
.like-count { font-size: 11px; color: rgba(255,255,255,0.9); }

.video-title {
  font-size: 11px;
  color: rgba(255,255,255,0.5);
  padding: 4px 4px 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  background: #111;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 24px;
  gap: 12px;
}
.empty-icon { font-size: 48px; opacity: 0.5; }
.empty-text { font-size: 14px; color: rgba(255,255,255,0.35); }
.upload-link {
  background: linear-gradient(135deg, #ff0050, #ff4d94);
  color: #fff;
  text-decoration: none;
  border-radius: 20px;
  padding: 8px 24px;
  font-size: 14px;
  font-weight: 600;
  margin-top: 4px;
  transition: opacity 0.2s;
}
.upload-link:hover { opacity: 0.85; }

/* 加载 */
.loading-state {
  display: flex;
  justify-content: center;
  padding: 60px;
}
.loading-spinner {
  width: 32px;
  height: 32px;
  border: 3px solid rgba(255,255,255,0.1);
  border-top-color: #ff0050;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }
</style>
