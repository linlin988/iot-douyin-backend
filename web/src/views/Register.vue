<template>
  <div class="auth-container">
    <!-- 返回按钮 -->
    <button class="page-back-btn" @click="router.push('/')">← 返回</button>

    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <div class="auth-card">
      <!-- Logo区域 -->
      <div class="logo-area">
        <div class="logo-icon">▶</div>
        <h1 class="logo-text">创建账号</h1>
        <p class="logo-sub">加入抖音，记录美好生活</p>
      </div>

      <!-- 表单 -->
      <form class="auth-form" @submit.prevent="handleRegister">
        <div class="input-group">
          <span class="input-icon">👤</span>
          <input
            v-model="form.username"
            type="text"
            placeholder="请输入用户名（3-16位）"
            class="auth-input"
            autocomplete="username"
            maxlength="16"
          />
        </div>

        <div class="input-group">
          <span class="input-icon">🔒</span>
          <input
            v-model="form.password"
            :type="showPwd ? 'text' : 'password'"
            placeholder="请输入密码（6位以上）"
            class="auth-input"
            autocomplete="new-password"
          />
          <span class="eye-icon" @click="showPwd = !showPwd">
            {{ showPwd ? '🙈' : '👁️' }}
          </span>
        </div>

        <div class="input-group">
          <span class="input-icon">🔒</span>
          <input
            v-model="form.confirmPassword"
            :type="showPwd2 ? 'text' : 'password'"
            placeholder="再次确认密码"
            class="auth-input"
            autocomplete="new-password"
          />
          <span class="eye-icon" @click="showPwd2 = !showPwd2">
            {{ showPwd2 ? '🙈' : '👁️' }}
          </span>
        </div>

        <!-- 验证码 -->
        <div class="input-group captcha-group">
          <span class="input-icon">🛡️</span>
          <input
            v-model="form.captchaCode"
            type="text"
            placeholder="请输入验证码"
            class="auth-input captcha-input"
            maxlength="6"
          />
          <!-- 显示后端返回的验证码图片流 -->
          <div class="captcha-img-wrap" @click="refreshCaptcha" title="点击刷新">
            <img v-if="captchaImage" :src="captchaImage" alt="验证码" class="captcha-img" />
          </div>
        </div>

        <!-- 强度指示 -->
        <div v-if="form.password" class="strength-bar-wrap">
          <div class="strength-bar">
            <div
              class="strength-fill"
              :style="{ width: strengthPct + '%', background: strengthColor }"
            ></div>
          </div>
          <span class="strength-label" :style="{ color: strengthColor }">{{ strengthText }}</span>
        </div>

        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
        <p v-if="successMsg" class="success-msg">{{ successMsg }}</p>

        <button type="submit" class="auth-btn" :disabled="loading">
          <span v-if="loading" class="loading-spin">⟳</span>
          <span v-else>注 册</span>
        </button>
      </form>

      <p class="switch-link">
        已有账号？
        <router-link to="/login" class="link">立即登录</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../store'
import { api } from '../api'

const router = useRouter()
const userStore = useUserStore()

const form = ref({ username: '', password: '', confirmPassword: '', captchaCode: '' })
const showPwd = ref(false)
const showPwd2 = ref(false)
const loading = ref(false)
const errorMsg = ref('')
const successMsg = ref('')

// 验证码
const captchaImage = ref('') // 后端图片流 Object URL

// 密码强度
const strengthPct = computed(() => {
  const p = form.value.password
  if (!p) return 0
  let score = 0
  if (p.length >= 6) score += 30
  if (p.length >= 10) score += 20
  if (/[A-Z]/.test(p)) score += 15
  if (/[0-9]/.test(p)) score += 20
  if (/[^A-Za-z0-9]/.test(p)) score += 15
  return Math.min(score, 100)
})
const strengthColor = computed(() => {
  const s = strengthPct.value
  if (s < 40) return '#ff4d4f'
  if (s < 70) return '#faad14'
  return '#52c41a'
})
const strengthText = computed(() => {
  const s = strengthPct.value
  if (s < 40) return '弱'
  if (s < 70) return '中'
  return '强'
})

const refreshCaptcha = async () => {
  try {
    const res = await api.user.getCaptcha()
    if (captchaImage.value) {
      URL.revokeObjectURL(captchaImage.value)
    }
    captchaImage.value = URL.createObjectURL(res.data)
  } catch (error) {
    console.error('获取验证码图片失败', error)
  }
  form.value.captchaCode = ''
}

const handleRegister = async () => {
  if (form.value.username.length < 3) { errorMsg.value = '用户名至少3位'; return }
  if (form.value.password.length < 6) { errorMsg.value = '密码至少6位'; return }
  if (form.value.password !== form.value.confirmPassword) { errorMsg.value = '两次密码不一致'; return }
  if (!form.value.captchaCode) { errorMsg.value = '请输入验证码'; return }

  errorMsg.value = ''
  successMsg.value = ''
  loading.value = true

  try {
    const payload = {
      account: form.value.username,
      password: form.value.password
    }
    const result = await userStore.register(payload)
    if (result.success) {
      successMsg.value = '注册成功！即将跳转到登录页...'
      setTimeout(() => router.push('/login'), 1500)
    } else {
      errorMsg.value = result.message
      refreshCaptcha()
    }
  } catch (e) {
    errorMsg.value = '网络错误，请稍后重试'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  refreshCaptcha()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@400;700&display=swap');

.auth-container {
  min-height: 100vh;
  background: #0a0a0f;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  font-family: 'Noto Sans SC', sans-serif;
}

.bg-decoration { position: absolute; inset: 0; pointer-events: none; }
.circle { position: absolute; border-radius: 50%; filter: blur(80px); opacity: 0.15; }
.circle-1 { width: 400px; height: 400px; background: radial-gradient(circle, #ff0050, transparent); top: -100px; right: -100px; }
.circle-2 { width: 350px; height: 350px; background: radial-gradient(circle, #00f2ea, transparent); bottom: -80px; left: -80px; }
.circle-3 { width: 250px; height: 250px; background: radial-gradient(circle, #8b5cf6, transparent); top: 50%; left: 50%; transform: translate(-50%, -50%); }

.auth-card {
  position: relative;
  z-index: 1;
  width: 360px;
  background: rgba(255,255,255,0.04);
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 24px;
  padding: 36px 32px;
  backdrop-filter: blur(20px);
  box-shadow: 0 20px 60px rgba(0,0,0,0.5);
}

.logo-area { text-align: center; margin-bottom: 28px; }
.logo-icon { font-size: 40px; background: linear-gradient(135deg, #00f2ea, #00c8e0); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; line-height: 1; margin-bottom: 8px; }
.logo-text { font-size: 24px; font-weight: 700; color: #fff; margin-bottom: 4px; }
.logo-sub { font-size: 13px; color: rgba(255,255,255,0.4); }

.auth-form { display: flex; flex-direction: column; gap: 14px; }

.input-group {
  position: relative; display: flex; align-items: center;
  background: rgba(255,255,255,0.06);
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 12px;
  transition: border-color 0.2s;
}
.input-group:focus-within { border-color: #00f2ea; background: rgba(0,242,234,0.05); }

.input-icon { padding: 0 12px; font-size: 16px; flex-shrink: 0; }
.eye-icon { padding: 0 12px; cursor: pointer; font-size: 16px; flex-shrink: 0; }

.auth-input {
  flex: 1; background: transparent; border: none; outline: none;
  padding: 14px 0; color: #fff; font-size: 15px; font-family: inherit;
}
.auth-input::placeholder { color: rgba(255,255,255,0.3); }

.captcha-group { gap: 0; }
.captcha-input { min-width: 0; }
.captcha-img-wrap { flex-shrink: 0; cursor: pointer; border-left: 1px solid rgba(255,255,255,0.1); border-radius: 0 12px 12px 0; overflow: hidden; }
.captcha-img { display: block; height: 38px; width: 100px; object-fit: cover; }

.strength-bar-wrap {
  display: flex; align-items: center; gap: 10px; padding: 0 2px;
}
.strength-bar {
  flex: 1; height: 4px; background: rgba(255,255,255,0.1); border-radius: 4px; overflow: hidden;
}
.strength-fill { height: 100%; border-radius: 4px; transition: width 0.3s, background 0.3s; }
.strength-label { font-size: 12px; width: 16px; text-align: right; flex-shrink: 0; }

.error-msg { color: #ff4d4f; font-size: 13px; text-align: center; margin: -2px 0; }
.success-msg { color: #52c41a; font-size: 13px; text-align: center; margin: -2px 0; }

.auth-btn {
  background: linear-gradient(135deg, #00f2ea, #00c8e0);
  color: #000; border: none; border-radius: 12px;
  padding: 15px; font-size: 16px; font-weight: 700;
  cursor: pointer; transition: opacity 0.2s, transform 0.1s;
  margin-top: 4px; font-family: inherit;
}
.auth-btn:hover:not(:disabled) { opacity: 0.88; transform: translateY(-1px); }
.auth-btn:active:not(:disabled) { transform: translateY(0); }
.auth-btn:disabled { opacity: 0.5; cursor: not-allowed; }

.loading-spin { display: inline-block; animation: spin 0.8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

.switch-link { text-align: center; color: rgba(255,255,255,0.4); font-size: 13px; margin-top: 20px; }
.link { color: #00f2ea; text-decoration: none; font-weight: 600; }
.link:hover { text-decoration: underline; }

.page-back-btn {
  position: fixed;
  top: 16px;
  left: 16px;
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.15);
  color: rgba(255,255,255,0.7);
  border-radius: 20px;
  padding: 7px 14px;
  font-size: 14px;
  cursor: pointer;
  z-index: 10;
  transition: all 0.2s;
  font-family: inherit;
}
.page-back-btn:hover {
  background: rgba(255,255,255,0.15);
  color: #fff;
}
</style>
