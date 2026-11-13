<template>
  <div class="auth-page">
    <div class="auth-shell">
      <section class="auth-hero">
        <div class="auth-hero__inner">
          <div class="auth-brand">
            <div>
              <h1 class="auth-brand__title">{{ systemName }}</h1>
              <p class="auth-brand__desc">数据可视化项目 · 多源数据分析平台</p>
            </div>
          </div>

          <div class="auth-hero__content">
            <div class="auth-badge">数据分析工作台</div>&nbsp;&nbsp;
            <div class="auth-badge" @click="backHome">系统主页</div>

            <h2 class="auth-hero__title">
              统一管理多源数据，<br />
              清晰呈现分析结果
            </h2>

            <p class="auth-hero__text">
              系统围绕消费者购物、NIPT检测、北京房屋租赁、学生考试、青少年心理健康、
              电视剧播放量、世界幸福报告等主题数据，提供数据管理、统计挖掘、图表展示和规律解读能力。
            </p>

            <div class="auth-feature-list">
              <div class="auth-feature-item">
                <div class="auth-feature-item__icon">
                  <ArtSvgIcon icon="ri:database-2-line" />
                </div>
                <div>
                  <h3>数据高效管理</h3>
                  <p>支持多主题数据表的查询、新增、编辑、删除与条件筛选，方便维护基础数据。</p>
                </div>
              </div>

              <div class="auth-feature-item">
                <div class="auth-feature-item__icon">
                  <ArtSvgIcon icon="ri:bar-chart-box-line" />
                </div>
                <div>
                  <h3>图表可视化分析</h3>
                  <p>结合柱状图、饼图、雷达图、地图、散点图、热力图等方式展示数据规律。</p>
                </div>
              </div>

              <div class="auth-feature-item">
                <div class="auth-feature-item__icon">
                  <ArtSvgIcon icon="ri:file-chart-line" />
                </div>
                <div>
                  <h3>规律价值解读</h3>
                  <p>围绕分布结构、分层差异、趋势变化和相关关系，形成清晰的数据分析说明。</p>
                </div>
              </div>

              <div class="auth-feature-item">
                <div class="auth-feature-item__icon">
                  <ArtSvgIcon icon="ri:code-box-line" />
                </div>
                <div>
                  <h3>项目技术栈</h3>
                  <p>
                    前端采用 Vue3、TypeScript、Vite、Element Plus 与 ECharts，
                    后端结合 Spring Boot、MyBatis 与 MySQL，支撑多源数据管理和可视化分析。
                  </p>
                </div>
              </div>
            </div>
          </div>

          <div class="auth-hero__stats">
            <div class="auth-stat-card">
              <span class="auth-stat-card__value">7+</span>
              <span class="auth-stat-card__label">主题数据集</span>
            </div>
            <div class="auth-stat-card">
              <span class="auth-stat-card__value">30+</span>
              <span class="auth-stat-card__label">分析图表</span>
            </div>
            <div class="auth-stat-card">
              <span class="auth-stat-card__value">One</span>
              <span class="auth-stat-card__label">统一工作台</span>
            </div>
          </div>
        </div>

        <div class="auth-hero__decoration auth-hero__decoration--one"></div>
        <div class="auth-hero__decoration auth-hero__decoration--two"></div>
        <div class="auth-hero__grid"></div>
      </section>

      <section class="auth-panel">
        <AuthTopBar />

        <div class="auth-panel__body">
          <div class="auth-card">
            <div class="auth-card__header">
              <h3 class="auth-card__title">{{ $t('login.title') }}</h3>
              <p class="auth-card__subtitle">登录后进入数据可视化工作台，继续管理数据与查看分析结果</p>
            </div>

            <ElForm ref="formRef" :model="formData" :rules="rules" :key="formKey" class="auth-form" @keyup.enter="handleSubmit">
              <!--
              <ElFormItem prop="account">
                <ElSelect v-model="formData.account" @change="setupAccount">
                  <ElOption
                    v-for="account in accounts"
                    :key="account.key"
                    :label="account.label"
                    :value="account.key"
                  >
                    <span>{{ account.label }}</span>
                  </ElOption>
                </ElSelect>
              </ElFormItem>
              -->

              <ElFormItem prop="username">
                <ElInput class="auth-input" :placeholder="$t('login.placeholder.username')" v-model.trim="formData.username">
                  <template #prefix>
                    <ArtSvgIcon icon="ri:user-3-line" />
                  </template>
                </ElInput>
              </ElFormItem>

              <ElFormItem prop="password">
                <ElInput class="auth-input" :placeholder="$t('login.placeholder.password')" v-model.trim="formData.password" type="password" autocomplete="off" show-password>
                  <template #prefix>
                    <ArtSvgIcon icon="ri:lock-2-line" />
                  </template>
                </ElInput>
              </ElFormItem>

              <div class="auth-verify">
                <div class="auth-verify__box" :class="{ 'is-error': !isPassing && isClickPass }">
                  <ArtDragVerify ref="dragVerify" v-model:value="isPassing" :text="$t('login.sliderText')" textColor="var(--art-gray-700)" :successText="$t('login.sliderSuccessText')" progressBarBg="var(--main-color)" :background="isDark ? '#26272F' : '#F5F7FA'" handlerBg="var(--default-box-color)" />
                </div>

                <p class="auth-verify__error" :class="{ visible: !isPassing && isClickPass }">
                  {{ $t('login.placeholder.slider') }}
                </p>
              </div>

              <div class="auth-form__meta">
                <ElCheckbox v-model="formData.rememberPassword">
                  {{ $t('login.rememberPwd') }}
                </ElCheckbox>

                <RouterLink class="auth-link" :to="{ name: 'ForgetPassword' }">
                  {{ $t('login.forgetPwd') }}
                </RouterLink>
              </div>

              <ElButton class="auth-submit" type="primary" @click="handleSubmit" :loading="loading" v-ripple>
                {{ $t('login.btnText') }}
              </ElButton>

              <div class="auth-bottom-tip">
                <span>{{ $t('login.noAccount') }}</span>
                <RouterLink class="auth-link" :to="{ name: 'Register' }">
                  {{ $t('login.register') }}
                </RouterLink>
              </div>
            </ElForm>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import AppConfig from '@/config'
import { useUserStore } from '@/store/modules/user'
import { useI18n } from 'vue-i18n'
import { HttpError } from '@/utils/http/error'
import { fetchLogin } from '@/api/auth'
import { ElNotification, type FormInstance, type FormRules } from 'element-plus'
import { useSettingStore } from '@/store/modules/setting'

defineOptions({ name: 'Login' })

const settingStore = useSettingStore()
const { isDark } = storeToRefs(settingStore)
const { t, locale } = useI18n()
const formKey = ref(0)

watch(locale, () => {
  formKey.value++
})

const dragVerify = ref()
const userStore = useUserStore()
const router = useRouter()
const isPassing = ref(false)
const isClickPass = ref(false)

const systemName = AppConfig.systemInfo.name
const formRef = ref<FormInstance>()

const formData = reactive({
  username: 'manager',
  password: '123456',
  rememberPassword: false
})

const rules = computed<FormRules>(() => ({
  username: [{ required: true, message: t('login.placeholder.username'), trigger: 'blur' }],
  password: [{ required: true, message: t('login.placeholder.password'), trigger: 'blur' }]
}))

const loading = ref(false)

const handleSubmit = async () => {
  if (!formRef.value) return

  try {
    const valid = await formRef.value.validate()
    if (!valid) return

    if (!isPassing.value) {
      isClickPass.value = true
      return
    }

    loading.value = true

    const username = formData.username.trim()
    const password = formData.password

    const { token, refreshToken } = await fetchLogin({
      username,
      password,
      ...collectLoginClientMeta()
    })

    if (!token) {
      throw new Error('Login failed - no token received')
    }

    userStore.setToken(token, refreshToken)
    userStore.setLoginStatus(true)

    showLoginSuccessNotice()

    const redirect =
      typeof router.currentRoute.value.query.redirect === 'string'
        ? router.currentRoute.value.query.redirect
        : '/dashboard/console'

    router.push(redirect)
  } catch (error) {
    if (!(error instanceof HttpError)) {
      console.error('[Login] Unexpected error:', error)
    }
  } finally {
    loading.value = false
    resetDragVerify()
  }
}

const backHome = () => {
  router.push('/home')
}

const collectLoginClientMeta = () => {
  const timezone = Intl.DateTimeFormat().resolvedOptions().timeZone || ''
  const width = window.screen?.width || 0
  const height = window.screen?.height || 0

  return {
    loginType: '用户名密码登录',
    clientTimezone: timezone,
    clientLanguage: navigator.language || '',
    screenSize: width && height ? `${width}x${height}` : '',
    platform: navigator.platform || ''
  }
}

const resetDragVerify = () => {
  dragVerify.value?.reset?.()
}

const showLoginSuccessNotice = () => {
  setTimeout(() => {
    ElNotification({
      title: t('login.success.title'),
      type: 'success',
      duration: 2500,
      zIndex: 10000,
      message: `${t('login.success.message')}, ${systemName}!`
    })
  }, 1000)
}
</script>
<style scoped>
@import './style.css';
</style>

<style lang="scss" scoped>
:deep(.el-select__wrapper) {
  min-height: 46px !important;
  border-radius: 14px !important;
}

:deep(.el-input__wrapper) {
  min-height: 46px !important;
  border-radius: 14px !important;
}
</style>