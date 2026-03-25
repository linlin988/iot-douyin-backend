<template>
  <div class="upload-container">
    <!-- 顶部导航栏 -->
    <div class="upload-header">
      <van-icon name="arrow-left" @click="goBack" />
      <h1 class="upload-title">上传视频</h1>
      <button class="upload-btn" @click="handleUpload" :disabled="!isReady">
        发布
      </button>
    </div>
    
    <!-- 视频选择区域 -->
    <div class="video-select-area" v-if="!videoFile">
      <van-icon name="plus" size="64" />
      <p>点击选择视频</p>
      <input 
        type="file" 
        ref="fileInput" 
        accept="video/*" 
        @change="handleFileSelect" 
        style="display: none"
      />
      <button class="select-btn" @click="fileInput.click()">
        选择视频
      </button>
    </div>
    
    <!-- 视频预览区域 -->
    <div class="video-preview-area" v-else>
      <video 
        ref="videoRef" 
        :src="videoUrl" 
        controls 
        class="video-preview"
      ></video>
      
      <!-- 封面上传 -->
      <div class="cover-selector">
        <h3>上传封面</h3>
        <input
          type="file"
          ref="coverInput"
          accept="image/*"
          @change="handleCoverSelect"
          style="display: none"
        />
        <button class="select-btn cover-btn" @click="coverInput.click()">
          选择封面
        </button>
        <div class="cover-preview" v-if="coverUrl">
          <img :src="coverUrl" alt="封面预览" />
        </div>
      </div>
      
      <!-- 视频信息填写 -->
      <div class="video-info">
        <div class="form-group">
          <label>标题</label>
          <input 
            type="text" 
            v-model="videoTitle" 
            placeholder="请输入视频标题"
            class="form-input"
          />
        </div>
        <div class="form-group">
          <label>描述</label>
          <textarea 
            v-model="videoDescription" 
            placeholder="请输入视频描述"
            class="form-textarea"
            rows="4"
          ></textarea>
        </div>
      </div>
      
      <!-- 上传进度 -->
      <div class="upload-progress" v-if="isUploading">
        <van-progress :percentage="uploadProgress" color="#ff0050" />
        <p>{{ uploadProgress }}%</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../api'

const router = useRouter()

// 文件输入
const fileInput = ref(null)
const coverInput = ref(null)
const videoRef = ref(null)

// 视频相关
const videoFile = ref(null)
const videoUrl = ref('')
const coverFile = ref(null)
const coverUrl = ref('')

// 视频信息
const videoTitle = ref('')
const videoDescription = ref('')

// 上传状态
const isUploading = ref(false)
const uploadProgress = ref(0)

// 计算属性：是否准备就绪
const isReady = computed(() => {
  return videoFile.value && coverFile.value && videoTitle.value.trim()
})

// 处理文件选择
const handleFileSelect = (e) => {
  const file = e.target.files[0]
  if (file) {
    videoFile.value = file
    videoUrl.value = URL.createObjectURL(file)
  }
}

// 处理封面选择
const handleCoverSelect = (e) => {
  const file = e.target.files[0]
  if (file) {
    coverFile.value = file
    coverUrl.value = URL.createObjectURL(file)
  }
}

// 处理上传
const handleUpload = async () => {
  if (!isReady.value) return
  
  isUploading.value = true
  uploadProgress.value = 0
  
  try {
    // 创建FormData对象
    const formData = new FormData()
    formData.append('file', videoFile.value)
    formData.append('coverfile', coverFile.value)
    formData.append('title', videoTitle.value)
    formData.append('description', videoDescription.value)
    
    // 调用API上传视频
    const response = await api.video.upload(formData, {
      onUploadProgress: (progressEvent) => {
        uploadProgress.value = Math.round((progressEvent.loaded / progressEvent.total) * 100)
      }
    })
    
    if (response.data.code === 200) {
      // 上传成功，跳转到主页
      setTimeout(() => {
        isUploading.value = false
        router.push('/')
      }, 500)
    } else {
      throw new Error('上传失败')
    }
  } catch (error) {
    console.error('Upload failed:', error)
    isUploading.value = false
    // 显示错误提示
    alert('上传失败，请重试')
  }
}

// 返回上一页
const goBack = () => {
  router.back()
}
</script>

<style scoped>
.upload-container {
  width: 100vw;
  height: 100vh;
  background-color: #000;
  color: #fff;
  padding-bottom: 20px;
  box-sizing: border-box;
  overflow-y: auto;
  -webkit-overflow-scrolling: touch;
}

.upload-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px;
  background-color: #000;
  position: sticky;
  top: 0;
  z-index: 10;
  border-bottom: 1px solid #333;
}

.upload-title {
  font-size: 18px;
  font-weight: bold;
}

.upload-btn {
  background-color: #ff0050;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 5px 20px;
  font-size: 14px;
  cursor: pointer;
}

.upload-btn:disabled {
  background-color: #666;
  cursor: not-allowed;
}

.video-select-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 80vh;
  gap: 20px;
}

.select-btn {
  background-color: #ff0050;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 10px 40px;
  font-size: 16px;
  cursor: pointer;
}

.video-preview-area {
  padding: 20px;
}

.video-preview {
  width: 100%;
  max-height: 40vh;
  object-fit: contain;
  background-color: #1a1a1a;
  border-radius: 10px;
  margin-bottom: 20px;
}

.cover-selector {
  margin-bottom: 20px;
}

.cover-selector h3 {
  margin-bottom: 10px;
  font-size: 16px;
}

.cover-btn {
  margin-bottom: 12px;
}

.cover-preview {
  width: 120px;
  height: 180px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #444;
}

.cover-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-info {
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-size: 14px;
  color: #999;
}

.form-input,
.form-textarea {
  width: 100%;
  padding: 10px;
  background-color: #333;
  border: none;
  border-radius: 8px;
  color: #fff;
  font-size: 14px;
}

.form-textarea {
  resize: none;
}

.upload-progress {
  margin-top: 20px;
  text-align: center;
}

.upload-progress p {
  margin-top: 10px;
  font-size: 14px;
}
</style>