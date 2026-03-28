<template>
  <div class="video-player-container" ref="containerRef" @touchstart="handleTouchStart" @touchmove="handleTouchMove" @touchend="handleTouchEnd" @keydown="handleKeydown" @wheel="handleWheel">
    <div class="video-list" :style="{ transform: `translateY(-${currentIndex * 100}vh)` }">
      <div v-for="(video, index) in (videoList.length > 0 ? videoList : [{}])" :key="video.id || index" class="video-item" ref="videoItems">
        <video
          v-if="video.videoUrl"
          :ref="el => { if (el) videoRefs[index] = el }"
          :src="video.videoUrl"
          :poster="video.coverUrl"
          :muted="isMuted"
          loop
          playsinline
          @canplay="handleCanPlay(index)"
        ></video>
        <div v-else class="empty-video">
          <div class="empty-icon">🎬</div>
          <div class="empty-text">视频加载中...登录以享受全部功能</div>
        </div>
        
        <!-- 视频内容覆盖层 -->
        <div class="video-overlay" @click="handleVideoClick(index)">
          <!-- 底部信息 -->
          <div class="bottom-info">
            <div class="author-info">
              <img v-if="video.author?.avatar" :src="video.author.avatar" alt="Avatar" class="avatar" @click.stop="goToAuthorProfile(video)" />
              <span v-if="video.author?.name" class="author-name">{{ video.author.name }}</span>
              <button v-if="video.author?.id"
                class="follow-btn" 
                :class="{ 'following': video.author.isFollowing }"
                @click.stop="handleFollow(video.author.id)"
              >
                {{ video.author.isFollowing ? '已关注' : '+ 关注' }}
              </button>
            </div>
            <h3 v-if="video.title" class="video-title">{{ video.title }}</h3>
            <p class="description">{{ video.description || '暂无描述' }}</p>
          </div>
          
          <!-- 右侧操作栏 -->
          <div v-if="video.id" class="right-actions">
            <div class="action-item" @click.stop="handleLike(video.id)">
              <div class="action-icon" :class="{ 'liked': video.isLiked }">
                <van-icon name="like" size="32" />
              </div>
              <span class="action-text">{{ formatNumber(video.likes) }}</span>
            </div>
            <div class="action-item" @click.stop="showComments(video.id)">
              <div class="action-icon">
                <van-icon name="chat-o" size="32" />
              </div>
              <span class="action-text">{{ formatNumber(video.comments) }}</span>
            </div>
            <div class="action-item" @click.stop="toggleMute">
              <div class="action-icon">
                <van-icon :name="isMuted ? 'volume-o' : 'volume'" size="32" />
              </div>
              <span class="action-text">{{ isMuted ? '静音' : '声音' }}</span>
            </div>
            <div class="action-item" @click.stop="handleShare(video.id)">
              <div class="action-icon">
                <van-icon name="share-o" size="32" />
              </div>
              <span class="action-text">分享</span>
            </div>
            <div class="action-item" @click.stop="toggleFullscreen()">
              <div class="action-icon">
                <van-icon name="expand-o" size="32" />
              </div>
              <span class="action-text">全屏</span>
            </div>
          </div>
          
          <!-- 点赞动画 -->
          <div v-if="showLikeAnimation && currentIndex === index" class="like-animation">
            <van-icon name="like" size="100" />
          </div>
        </div>
      </div>
    </div>
    
    <!-- 评论弹窗 -->
    <van-popup v-model:show="showCommentsPopup" position="bottom" :style="{ height: '80vh' }">
      <div class="comments-container">
        <div class="comments-header">
          <h3>评论</h3>
          <van-icon name="cross" size="20" @click="showCommentsPopup = false" />
        </div>
        <div class="comments-list" @touchstart.stop @touchmove.stop @touchend.stop @wheel.stop>
          <div v-for="(comment, index) in comments" :key="index" class="comment-item">
            <img :src="comment.avatar" alt="Avatar" class="comment-avatar" />
            <div class="comment-content">
              <div class="comment-header">
                <span class="comment-author">{{ comment.author }}</span>
                <span class="comment-time">{{ comment.time }}</span>
              </div>
              <p class="comment-text">{{ comment.text }}</p>
              <div class="comment-footer">
                <van-icon name="like-o" @click="handleCommentLike(index)" />
                <span class="comment-likes">{{ comment.likes }}</span>
              </div>
            </div>
          </div>
          <div v-if="comments.length === 0" class="comments-empty">这里空空的~</div>
        </div>
        <div class="comment-input-container">
          <input 
            type="text" 
            v-model="commentInput" 
            placeholder="添加评论..." 
            class="comment-input"
          />
          <button class="send-comment-btn" @click="sendComment">发送</button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useVideoStore, useUserStore } from '../store'
import { formatNumber } from '../utils/format'
import { showToast } from 'vant'
import { Icon } from 'vant'
import { api } from '../api'

const props = defineProps({
  videoList: {
    type: Array,
    default: () => []
  },
  currentIndex: {
    type: Number,
    default: 0
  }
})

const emit = defineEmits(['videoChange'])

const router = useRouter()
const videoStore = useVideoStore()
const userStore = useUserStore()

const containerRef = ref(null)
const videoItems = ref([])
const videoRefs = ref({})
const currentIndex = ref(props.currentIndex)
const touchStartY = ref(0)
const touchEndY = ref(0)
const showLikeAnimation = ref(false)
const showCommentsPopup = ref(false)
const commentInput = ref('')
const page = ref(1)
const isMuted = ref(true)

const comments = ref([])

// 处理触摸开始
const handleTouchStart = (e) => {
  if (showCommentsPopup.value) return
  touchStartY.value = e.touches[0].clientY
}

// 处理触摸移动
const handleTouchMove = (e) => {
  if (showCommentsPopup.value) return
  touchEndY.value = e.touches[0].clientY
}

// 处理触摸结束
const handleTouchEnd = async () => {
  if (showCommentsPopup.value) return
  const diff = touchStartY.value - touchEndY.value
  if (diff > 50 && currentIndex.value < props.videoList.length - 1) {
    // 向上滑动，下一个视频
    currentIndex.value++
    videoStore.setCurrentVideoIndex(currentIndex.value)
    emit('videoChange', currentIndex.value)
    playCurrentVideo()
    
    // 检查是否需要加载更多视频
    if (currentIndex.value >= props.videoList.length - 2 && !videoStore.isLoading && videoStore.hasMore) {
      page.value++
      await videoStore.fetchVideoList(page.value, true)
    }
  } else if (diff < -50 && currentIndex.value > 0) {
    // 向下滑动，上一个视频
    currentIndex.value--
    videoStore.setCurrentVideoIndex(currentIndex.value)
    emit('videoChange', currentIndex.value)
    playCurrentVideo()
  }
  // 检查是否滑到了最后一个视频
  if (currentIndex.value === props.videoList.length - 1 && !videoStore.isLoading && videoStore.hasMore) {
    page.value++
    await videoStore.fetchVideoList(page.value, true)
  }
}

// 处理视频点击（暂停/播放）
const handleVideoClick = (index) => {
  if (index === currentIndex.value) {
    const video = videoRefs.value[index]
    if (video) {
      if (video.paused) {
        video.play()
        // 调用播放量接口
        const videoData = props.videoList[index]
        if (videoData?.id) {
          api.video.play(videoData.id).catch(error => {
            console.error('播放量统计失败:', error)
          })
        }
      } else {
        video.pause()
      }
    }
  }
}

// 处理双击点赞
const handleVideoDoubleClick = async (index) => {
  if (index === currentIndex.value) {
    const video = props.videoList[index]
    await videoStore.toggleLike(video.id)
    showLikeAnimation.value = true
    setTimeout(() => {
      showLikeAnimation.value = false
    }, 1000)
  }
}

// 处理点赞
const handleLike = async (videoId) => {
  await videoStore.toggleLike(videoId)
}

// 处理关注：未登录时跳转到登录页
const handleFollow = async (userId) => {
  if (!userStore.isLoggedIn) {
    router.push({ name: 'Login', query: { redirect: '/' } })
    return
  }
  await videoStore.toggleFollow(userId)
}

// 切换静音状态
const toggleMute = () => {
  isMuted.value = !isMuted.value
  const currentVideo = videoRefs.value[currentIndex.value]
  if (currentVideo) {
    currentVideo.muted = isMuted.value
  }
}

// 显示评论
const showComments = async (videoId) => {
  try {
    const response = await api.comment.getList(videoId, { page: 1, size: 20 })
    if (response.data.code === 200) {
      const records = response.data?.data?.list || []
      comments.value = records.map((comment, idx) => ({
        id: idx,
        author: comment.nickname || '未知用户',
        avatar: comment.avatar?.trim() || '',
        text: comment.content || '',
        time: formatCommentTime(comment.createTime || ''),
        likes: 0
      }))
    } else {
      comments.value = []
    }
  } catch (error) {
    console.error('Failed to fetch comments:', error)
    comments.value = []
  }
  showCommentsPopup.value = true
}

// 格式化评论时间
const formatCommentTime = (timeString) => {
  const now = new Date()
  const commentTime = new Date(timeString)
  const diff = now - commentTime
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 60) {
    return `${minutes}分钟前`
  } else if (hours < 24) {
    return `${hours}小时前`
  } else {
    return `${days}天前`
  }
}

// 处理评论点赞
const handleCommentLike = (index) => {
  comments.value[index].likes++
}

// 发送评论
const sendComment = async () => {
  if (commentInput.value.trim()) {
    try {
      const currentVideo = props.videoList[currentIndex.value]
      if (currentVideo) {
        const response = await api.comment.create({
          videoId: currentVideo.id,
          content: commentInput.value
        })
        
        if (response.data.code === 200) {
          comments.value.unshift({
            id: response.data.data?.id || Date.now(),
            author: userStore.currentUser?.username || '我',
            avatar: userStore.currentUser?.avatar || '',
            text: commentInput.value,
            time: '刚刚',
            likes: 0
          })
          // 更新视频评论数
          currentVideo.comments++
          commentInput.value = ''
        } else {
          showToast('发布评论失败: ' + response.data.message)
        }
      }
    } catch (error) {
      console.error('Failed to send comment:', error)
      showToast('发布接口异常')
    }
  }
}

// 导航到个人主页
const goToAuthorProfile = (video) => {
  const userId = video?.author?.id ?? video?.userId
  if (!userId) return
  router.push({
    name: 'Profile',
    params: { userId: String(userId) }
  })
}

// 验证初始状态 (已赞已关注)
const verifyStatus = async () => {
  const currentVideo = props.videoList[currentIndex.value]
  if (currentVideo && userStore.isLoggedIn) {
    try {
      const likeRes = await api.like.isLike(currentVideo.id)
      if (likeRes.data.code === 200) currentVideo.isLiked = likeRes.data.data
      
      const followRes = await api.follow.isFollow(currentVideo.author.id)
      if (followRes.data.code === 200) currentVideo.author.isFollowing = followRes.data.data
    } catch (e) {
      console.error('状态验证失败', e)
    }
  }
}

// 播放当前视频
const playCurrentVideo = () => {
  // 暂停所有视频
  Object.values(videoRefs.value).forEach((video, index) => {
    if (video && index !== currentIndex.value) {
      video.pause()
    }
  })
  
  // 播放当前视频
  const currentVideo = videoRefs.value[currentIndex.value]
  if (currentVideo) {
    currentVideo.muted = isMuted.value
    currentVideo.play()
    verifyStatus()
    // 调用播放量接口
    const video = props.videoList[currentIndex.value]
    if (video?.id) {
      api.video.play(video.id).catch(error => {
        console.error('播放量统计失败:', error)
      })
    }
  }
}

// 处理视频可播放
const handleCanPlay = (index) => {
  if (index === currentIndex.value) {
    const video = videoRefs.value[index]
    if (video) {
      video.muted = isMuted.value
      video.play()
      // 调用播放量接口
      const videoData = props.videoList[index]
      if (videoData?.id) {
        api.video.play(videoData.id).catch(error => {
          console.error('播放量统计失败:', error)
        })
      }
    }
  }
}

// 监听视频列表变化
watch(() => props.videoList, () => {
  nextTick(() => {
    // 重新初始化双击事件
    videoItems.value.forEach((item, index) => {
      if (item) {
        let lastClick = 0
        item.addEventListener('click', () => {
          const currentTime = new Date().getTime()
          if (currentTime - lastClick < 300) {
            handleVideoDoubleClick(index)
          }
          lastClick = currentTime
        })
      }
    })
    playCurrentVideo()
  })
}, { deep: true })

// 监听currentIndex变化
watch(() => props.currentIndex, (newIndex) => {
  if (newIndex !== currentIndex.value) {
    currentIndex.value = newIndex
    videoStore.setCurrentVideoIndex(newIndex)
    emit('videoChange', newIndex)
    playCurrentVideo()
  }
})

// 处理键盘事件
const handleKeydown = (e) => {
  if (e.key === 'ArrowUp' || e.key === 'ArrowLeft') {
    // 上一个视频
    if (currentIndex.value > 0) {
      currentIndex.value--
      videoStore.setCurrentVideoIndex(currentIndex.value)
      emit('videoChange', currentIndex.value)
      playCurrentVideo()
    }
  } else if (e.key === 'ArrowDown' || e.key === 'ArrowRight') {
    // 下一个视频
    if (currentIndex.value < props.videoList.length - 1) {
      currentIndex.value++
      videoStore.setCurrentVideoIndex(currentIndex.value)
      emit('videoChange', currentIndex.value)
      playCurrentVideo()
    }
  }
}

// 处理鼠标滚轮事件
const handleWheel = (e) => {
  if (showCommentsPopup.value) return
  e.preventDefault()
  if (e.deltaY > 0) {
    // 向下滚动，下一个视频
    if (currentIndex.value < props.videoList.length - 1) {
      currentIndex.value++
      videoStore.setCurrentVideoIndex(currentIndex.value)
      emit('videoChange', currentIndex.value)
      playCurrentVideo()
    }
  } else if (e.deltaY < 0) {
    // 向上滚动，上一个视频
    if (currentIndex.value > 0) {
      currentIndex.value--
      videoStore.setCurrentVideoIndex(currentIndex.value)
      emit('videoChange', currentIndex.value)
      playCurrentVideo()
    }
  }
}

// 处理分享
const handleShare = (videoId) => {
  const currentUrl = `${window.location.origin}/?video=${videoId}`
  if (navigator.clipboard && navigator.clipboard.writeText) {
    navigator.clipboard.writeText(currentUrl).then(() => {
      showToast('视频链接已复制到剪贴板')
    }).catch(() => {
      showToast('复制失败，请重试')
    })
  } else {
    // 降级方案
    const input = document.createElement('input')
    input.value = currentUrl
    document.body.appendChild(input)
    input.select()
    try {
      document.execCommand('copy')
      showToast('视频链接已复制到剪贴板')
    } catch (e) {
      showToast('复制失败，请重试')
    }
    document.body.removeChild(input)
  }
}

// 处理全屏
const toggleFullscreen = () => {
  const container = containerRef.value
  if (!container) return

  if (!document.fullscreenElement) {
    if (container.requestFullscreen) {
      container.requestFullscreen()
    } else if (container.webkitRequestFullscreen) {
      container.webkitRequestFullscreen()
    }
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen()
    } else if (document.webkitExitFullscreen) {
      document.webkitExitFullscreen()
    }
  }
}

onMounted(() => {
  // 初始化双击事件
  videoItems.value.forEach((item, index) => {
    if (item) {
      let lastClick = 0
      item.addEventListener('click', () => {
        const currentTime = new Date().getTime()
        if (currentTime - lastClick < 300) {
          handleVideoDoubleClick(index)
        }
        lastClick = currentTime
      })
    }
  })
  
  // 确保容器可以获取焦点
  if (containerRef.value) {
    containerRef.value.tabIndex = 0
    containerRef.value.focus()
  }
  
  playCurrentVideo()
})
</script>

<style scoped>
.video-player-container {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  position: relative;
}

.video-list {
  width: 100%;
  height: 100%;
  transition: transform 0.3s ease;
}

.video-item {
  width: 100%;
  height: 100vh;
  position: relative;
}

video {
  position: absolute;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  object-fit: contain;
  background-color: #000;
  z-index: -1;
}

.video-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 20px;
  background: linear-gradient(to top, rgba(0,0,0,0.7), transparent);
}

.bottom-info {
  margin-bottom: 100px;
}

.author-info {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.author-name {
  font-weight: bold;
  margin-right: 10px;
}

.follow-btn {
  background-color: #ff0050;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 5px 15px;
  font-size: 14px;
  cursor: pointer;
}

.follow-btn.following {
  background-color: rgba(255,255,255,0.3);
}

.video-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #fff;
}

.description {
  font-size: 14px;
  line-height: 1.5;
}

/* 空视频状态 */
.empty-video {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #111;
  z-index: -1;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-text {
  font-size: 16px;
  color: rgba(255, 255, 255, 0.5);
}

.right-actions {
  position: absolute;
  right: 20px;
  bottom: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}

.action-icon {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(0,0,0,0.3);
  border-radius: 50%;
  margin-bottom: 5px;
}

.action-icon.liked {
  color: #ff0050;
}

.action-text {
  font-size: 12px;
}

.like-animation {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #ff0050;
  animation: likeAnimation 1s ease-in-out;
}

@keyframes likeAnimation {
  0% {
    opacity: 0;
    transform: translate(-50%, -50%) scale(0.5);
  }
  50% {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1.2);
  }
  100% {
    opacity: 0;
    transform: translate(-50%, -50%) scale(1);
  }
}

.comments-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.comments-list {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
}

.comment-item {
  display: flex;
  margin-bottom: 20px;
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.comment-author {
  font-weight: bold;
  font-size: 14px;
  color: #333;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-text {
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 5px;
  color: #000;
}

.comment-footer {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: #999;
}

.comment-input-container {
  display: flex;
  padding: 15px;
  border-top: 1px solid #f0f0f0;
}

.comment-input {
  flex: 1;
  padding: 10px;
  border: 1px solid #f0f0f0;
  border-radius: 20px;
  margin-right: 10px;
  color: #000;
  background-color: #fff;
}

.send-comment-btn {
  background-color: #ff0050;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 0 20px;
  cursor: pointer;
}

.comments-empty {
  text-align: center;
  color: #999;
  padding: 24px 0;
}
</style>