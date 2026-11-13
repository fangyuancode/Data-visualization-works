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
            <div class="auth-badge">创建账号</div>

            <h2 class="auth-hero__title">
              建立个人账号，<br />
              开启数据管理与分析流程
            </h2>

            <p class="auth-hero__text">
              注册后可进入系统工作台，对消费者购物、NIPT检测、房屋租赁、学生考试、
              青少年心理健康、电视剧播放量、世界幸福报告等数据进行统一管理和可视化分析。
            </p>

            <div class="auth-feature-list">
              <div class="auth-feature-item">
                <div class="auth-feature-item__icon">
                  <ArtSvgIcon icon="ri:user-add-line" />
                </div>
                <div>
                  <h3>账号快速开通</h3>
                  <p>填写基础账号信息即可注册，默认创建普通用户身份，便于后续进行角色分配。</p>
                </div>
              </div>

              <div class="auth-feature-item">
                <div class="auth-feature-item__icon">
                  <ArtSvgIcon icon="ri:database-2-line" />
                </div>
                <div>
                  <h3>数据权限衔接</h3>
                  <p>注册信息会写入用户表，后续可在系统管理模块中维护状态、角色和用户资料。</p>
                </div>
              </div>

              <div class="auth-feature-item">
                <div class="auth-feature-item__icon">
                  <ArtSvgIcon icon="ri:shield-check-line" />
                </div>
                <div>
                  <h3>表单校验完善</h3>
                  <p>前端完成账号、邮箱、手机号、密码一致性和用户协议等基础校验。</p>
                </div>
              </div>
            </div>
          </div>

          <div class="auth-hero__stats">
            <div class="auth-stat-card">
              <span class="auth-stat-card__value">User</span>
              <span class="auth-stat-card__label">普通用户注册</span>
            </div>
            <div class="auth-stat-card">
              <span class="auth-stat-card__value">Role</span>
              <span class="auth-stat-card__label">后续角色分配</span>
            </div>
            <div class="auth-stat-card">
              <span class="auth-stat-card__value">Data</span>
              <span class="auth-stat-card__label">进入数据工作台</span>
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
          <div class="auth-card register-card">
            <div class="auth-card__header">
              <h3 class="auth-card__title">{{ $t('register.title') }}</h3>
              <p class="auth-card__subtitle">{{ $t('register.subTitle') }}</p>
            </div>

            <ElForm
              ref="formRef"
              :model="formData"
              :rules="rules"
              :key="formKey"
              label-position="top"
              class="auth-form register-form"
              @keyup.enter="handleRegister"
            >
              <ElFormItem prop="username">
                <ElInput
                  class="auth-input"
                  v-model.trim="formData.username"
                  :placeholder="$t('register.placeholder.username')"
                  maxlength="20"
                  clearable
                >
                  <template #prefix>
                    <ArtSvgIcon icon="ri:user-3-line" />
                  </template>
                </ElInput>
              </ElFormItem>

              <ElFormItem prop="name">
                <ElInput
                  class="auth-input"
                  v-model.trim="formData.name"
                  :placeholder="$t('register.placeholder.name')"
                  maxlength="20"
                  clearable
                >
                  <template #prefix>
                    <ArtSvgIcon icon="ri:profile-line" />
                  </template>
                </ElInput>
              </ElFormItem>

              <ElFormItem prop="email">
                <ElInput
                  class="auth-input"
                  v-model.trim="formData.email"
                  :placeholder="$t('register.placeholder.email')"
                  maxlength="60"
                  clearable
                >
                  <template #prefix>
                    <ArtSvgIcon icon="ri:mail-line" />
                  </template>
                </ElInput>
              </ElFormItem>

              <ElFormItem prop="phone">
                <ElInput
                  class="auth-input"
                  v-model.trim="formData.phone"
                  :placeholder="$t('register.placeholder.phone')"
                  maxlength="11"
                  clearable
                >
                  <template #prefix>
                    <ArtSvgIcon icon="ri:phone-line" />
                  </template>
                </ElInput>
              </ElFormItem>

              <ElFormItem prop="password">
                <ElInput
                  class="auth-input"
                  v-model.trim="formData.password"
                  :placeholder="$t('register.placeholder.password')"
                  type="password"
                  autocomplete="new-password"
                  maxlength="20"
                  show-password
                >
                  <template #prefix>
                    <ArtSvgIcon icon="ri:lock-2-line" />
                  </template>
                </ElInput>
              </ElFormItem>

              <ElFormItem prop="confirmPassword">
                <ElInput
                  class="auth-input"
                  v-model.trim="formData.confirmPassword"
                  :placeholder="$t('register.placeholder.confirmPassword')"
                  type="password"
                  autocomplete="new-password"
                  maxlength="20"
                  show-password
                >
                  <template #prefix>
                    <ArtSvgIcon icon="ri:shield-keyhole-line" />
                  </template>
                </ElInput>
              </ElFormItem>

              <div class="register-tip">
                <ArtSvgIcon icon="ri:information-line" />
                <span>密码建议 6-20 位，注册成功后默认分配为普通用户，可在角色管理中调整权限。</span>
              </div>

              <ElFormItem prop="agreement" class="auth-agreement">
                <ElCheckbox v-model="formData.agreement">
                  {{ $t('register.agreeText') }}
                  <ElLink type="primary" :underline="false" @click.prevent="showAgreement = true">
                    {{ $t('register.privacyPolicy') }}
                  </ElLink>
                </ElCheckbox>
              </ElFormItem>

              <ElButton
                class="auth-submit"
                type="primary"
                @click="handleRegister"
                :loading="loading"
                v-ripple
              >
                {{ $t('register.submitBtnText') }}
              </ElButton>

              <div class="auth-bottom-tip">
                <span>{{ $t('register.hasAccount') }}</span>
                <RouterLink class="auth-link" :to="{ name: 'Login' }">
                  {{ $t('register.toLogin') }}
                </RouterLink>
              </div>
            </ElForm>
          </div>
        </div>
      </section>
    </div>

    <ElDialog v-model="showAgreement" title="用户协议与隐私说明" width="520px" align-center>
      <div class="agreement-content">
        <p>本系统用于多源数据管理、数据挖掘与可视化分析演示。</p>
        <p>注册账号时填写的用户名、昵称、邮箱、手机号等信息，仅用于系统登录、用户识别和权限管理。</p>
        <p>请妥善保管账号密码，不要使用与其他重要平台完全相同的密码。</p>
      </div>
      <template #footer>
        <ElButton type="primary" @click="showAgreement = false">我知道了</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
  import { useI18n } from 'vue-i18n'
  import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
  import AppConfig from '@/config'
  import { fetchRegister } from '@/api/auth'
  import { HttpError } from '@/utils/http/error'

  defineOptions({ name: 'Register' })

  interface RegisterForm {
    username: string
    name: string
    email: string
    phone: string
    password: string
    confirmPassword: string
    agreement: boolean
  }

  const USERNAME_MIN_LENGTH = 3
  const USERNAME_MAX_LENGTH = 20
  const PASSWORD_MIN_LENGTH = 6
  const PASSWORD_MAX_LENGTH = 20
  const DEFAULT_REGISTER_ROLE_ID = 3
  const ENABLED_STATUS = '1'

  const USERNAME_PATTERN = /^[A-Za-z0-9_]+$/
  const PHONE_PATTERN = /^1[3-9]\d{9}$/

  const { t, locale } = useI18n()
  const router = useRouter()
  const formRef = ref<FormInstance>()
  const systemName = AppConfig.systemInfo.name

  const loading = ref(false)
  const formKey = ref(0)
  const showAgreement = ref(false)

  watch(locale, () => {
    formKey.value++
  })

  const formData = reactive<RegisterForm>({
    username: '',
    name: '',
    email: '',
    phone: '',
    password: '',
    confirmPassword: '',
    agreement: false
  })

  const validateUsername = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
    if (!value) {
      callback(new Error(t('register.placeholder.username')))
      return
    }

    if (value.length < USERNAME_MIN_LENGTH || value.length > USERNAME_MAX_LENGTH) {
      callback(new Error(t('register.rule.usernameLength')))
      return
    }

    if (!USERNAME_PATTERN.test(value)) {
      callback(new Error(t('register.rule.usernamePattern')))
      return
    }

    callback()
  }

  const validatePassword = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
    if (!value) {
      callback(new Error(t('register.placeholder.password')))
      return
    }

    if (value.length < PASSWORD_MIN_LENGTH || value.length > PASSWORD_MAX_LENGTH) {
      callback(new Error(t('register.rule.passwordLength')))
      return
    }

    if (formData.confirmPassword) {
      formRef.value?.validateField('confirmPassword')
    }

    callback()
  }

  const validateConfirmPassword = (
    _rule: unknown,
    value: string,
    callback: (error?: Error) => void
  ) => {
    if (!value) {
      callback(new Error(t('register.rule.confirmPasswordRequired')))
      return
    }

    if (value !== formData.password) {
      callback(new Error(t('register.rule.passwordMismatch')))
      return
    }

    callback()
  }

  const validatePhone = (_rule: unknown, value: string, callback: (error?: Error) => void) => {
    if (!value) {
      callback()
      return
    }

    if (!PHONE_PATTERN.test(value)) {
      callback(new Error(t('register.rule.phoneInvalid')))
      return
    }

    callback()
  }

  const validateAgreement = (_rule: unknown, value: boolean, callback: (error?: Error) => void) => {
    if (!value) {
      callback(new Error(t('register.rule.agreementRequired')))
      return
    }

    callback()
  }

  const rules = computed<FormRules<RegisterForm>>(() => ({
    username: [{ required: true, validator: validateUsername, trigger: 'blur' }],
    name: [
      {
        min: 0,
        max: 20,
        message: t('register.rule.nameLength'),
        trigger: 'blur'
      }
    ],
    email: [
      { required: true, message: t('register.rule.emailRequired'), trigger: 'blur' },
      { type: 'email', message: t('register.rule.emailInvalid'), trigger: ['blur', 'change'] }
    ],
    phone: [{ validator: validatePhone, trigger: 'blur' }],
    password: [{ required: true, validator: validatePassword, trigger: 'blur' }],
    confirmPassword: [{ required: true, validator: validateConfirmPassword, trigger: 'blur' }],
    agreement: [{ validator: validateAgreement, trigger: 'change' }]
  }))

  const buildRegisterPayload = (): Api.Auth.RegisterParams => {
    const username = formData.username.trim()

    return {
      username,
      password: formData.password,
      name: formData.name.trim() || username,
      email: formData.email.trim(),
      phone: formData.phone.trim() || undefined,
      roleId: DEFAULT_REGISTER_ROLE_ID,
      status: ENABLED_STATUS
    }
  }

  const handleRegister = async () => {
    if (!formRef.value || loading.value) return

    try {
      const valid = await formRef.value.validate()
      if (!valid) return

      loading.value = true
      await fetchRegister(buildRegisterPayload())

      ElMessage.success(t('register.successText'))
      router.push({ name: 'Login' })
    } catch (error) {
      if (!(error instanceof HttpError)) {
        console.error('[Register] Unexpected error:', error)
      }
    } finally {
      loading.value = false
    }
  }
</script>

<style scoped>
  @import '../login/style.css';
</style>

<style lang="scss" scoped>
  .register-card {
    max-width: 500px;
  }

  .register-form {
    :deep(.el-form-item) {
      margin-bottom: 16px;
    }
  }

  .register-tip {
    display: flex;
    gap: 8px;
    align-items: flex-start;
    padding: 10px 12px;
    margin: -2px 0 14px;
    font-size: 12px;
    line-height: 1.6;
    color: var(--el-text-color-secondary);
    background: rgb(64 158 255 / 8%);
    border: 1px solid rgb(64 158 255 / 12%);
    border-radius: 14px;
  }

  .register-tip .art-svg-icon {
    flex-shrink: 0;
    margin-top: 2px;
    color: var(--el-color-primary);
  }

  .agreement-content {
    p {
      margin: 0 0 10px;
      line-height: 1.8;
      color: var(--el-text-color-regular);
    }

    p:last-child {
      margin-bottom: 0;
    }
  }

  :deep(.el-input__wrapper) {
    min-height: 46px !important;
    border-radius: 14px !important;
  }

  :deep(.el-checkbox) {
    align-items: flex-start;
  }

  :deep(.el-checkbox__input) {
    margin-top: 2px;
  }
</style>
