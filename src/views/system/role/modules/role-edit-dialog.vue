<template>
  <ElDialog
    v-model="visible"
    :title="dialogType === 'add' ? '新增角色' : '编辑角色'"
    width="460px"
    align-center
    @close="handleClose"
  >
    <ElForm ref="formRef" :model="form" :rules="rules" label-width="96px">
      <ElFormItem label="角色名称" prop="roleName">
        <ElInput v-model="form.roleName" placeholder="请输入角色名称" />
      </ElFormItem>
      <ElFormItem label="角色编码" prop="roleCode">
        <ElInput v-model="form.roleCode" placeholder="请输入角色编码，如 R_ADMIN" />
      </ElFormItem>
      <ElFormItem label="描述" prop="description">
        <ElInput
          v-model="form.description"
          type="textarea"
          :rows="3"
          placeholder="请输入角色描述"
        />
      </ElFormItem>
      <ElFormItem label="启用">
        <ElSwitch v-model="form.enabled" />
      </ElFormItem>
    </ElForm>
    <template #footer>
      <ElButton @click="handleClose">取消</ElButton>
      <ElButton type="primary" :loading="submitLoading" @click="handleSubmit">提交</ElButton>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import type { FormInstance, FormRules } from 'element-plus'
  import { fetchCreateRole, fetchUpdateRole } from '@/api/system-manage'

  type RoleListItem = Api.SystemManage.RoleListItem

  interface Props {
    modelValue: boolean
    dialogType: 'add' | 'edit'
    roleData?: RoleListItem
  }

  interface Emits {
    (e: 'update:modelValue', value: boolean): void
    (e: 'success'): void
  }

  const props = withDefaults(defineProps<Props>(), {
    modelValue: false,
    dialogType: 'add',
    roleData: undefined
  })

  const emit = defineEmits<Emits>()
  const formRef = ref<FormInstance>()
  const submitLoading = ref(false)

  const visible = computed({
    get: () => props.modelValue,
    set: (value) => emit('update:modelValue', value)
  })

  const rules = reactive<FormRules>({
    roleName: [
      { required: true, message: '请输入角色名称', trigger: 'blur' },
      { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
    ],
    roleCode: [
      { required: true, message: '请输入角色编码', trigger: 'blur' },
      { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' },
      {
        pattern: /^R_[A-Z0-9_]+$/,
        message: '角色编码建议使用 R_ADMIN 这样的格式',
        trigger: 'blur'
      }
    ]
  })

  const form = reactive<Api.SystemManage.RoleSaveParams>({
    roleId: 0,
    roleName: '',
    roleCode: '',
    description: '',
    enabled: true
  })

  watch(
    () => props.modelValue,
    (newVal) => {
      if (newVal) initForm()
    }
  )

  const initForm = () => {
    if (props.dialogType === 'edit' && props.roleData) {
      Object.assign(form, {
        roleId: props.roleData.roleId,
        roleName: props.roleData.roleName,
        roleCode: props.roleData.roleCode,
        description: props.roleData.description,
        enabled: props.roleData.enabled
      })
    } else {
      Object.assign(form, {
        roleId: 0,
        roleName: '',
        roleCode: '',
        description: '',
        enabled: true
      })
    }
  }

  const handleClose = () => {
    visible.value = false
    formRef.value?.resetFields()
  }

  const handleSubmit = async () => {
    if (!formRef.value) return

    try {
      await formRef.value.validate()
      submitLoading.value = true

      const payload = {
        roleName: form.roleName,
        roleCode: form.roleCode,
        description: form.description || '',
        enabled: form.enabled
      }

      if (props.dialogType === 'add') {
        await fetchCreateRole(payload)
      } else {
        await fetchUpdateRole(form.roleId || 0, payload)
      }

      emit('success')
      handleClose()
    } finally {
      submitLoading.value = false
    }
  }
</script>
