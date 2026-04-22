<template>
    <div class="profile-container animate-enter">
        <div class="glass-panel profile-card">
            <div class="profile-bg"></div>
            <div class="profile-header">
                <div class="avatar-wrapper">
                    <el-avatar :size="100" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"
                        class="profile-avatar" />
                    <el-button circle class="edit-avatar-btn"><el-icon>
                            <Camera />
                        </el-icon></el-button>
                </div>
                <div class="user-info">
                    <div class="u-name">{{ userStore.realName }} <el-tag size="small" effect="dark" round>{{ userStore.roles.join(', ') }}</el-tag></div>
                    <div class="u-email">{{ userStore.email || '未设置邮箱' }}</div>
                    <div class="u-meta">
                        <span><el-icon>
                                <Location />
                            </el-icon> 医院办公大楼 4F</span>
                        <span class="ml-4"><el-icon>
                                <Calendar />
                            </el-icon> 加入于 2024-01-10</span>
                    </div>
                </div>
                <div class="header-actions">
                    <el-button type="primary" round icon="Edit" @click="handleEdit">编辑资料</el-button>
                </div>
            </div>

            <div class="profile-body">
                <el-row :gutter="24">
                    <el-col :span="16">
                        <div class="section-box">
                            <h3 class="section-title">基本信息</h3>
                            <el-descriptions :column="2" border>
                                <el-descriptions-item label="真实姓名">{{ userStore.realName }}</el-descriptions-item>
                                <el-descriptions-item label="登录账号">{{ userStore.username }}</el-descriptions-item>
                                <el-descriptions-item label="工号">{{ userStore.empNo || '无' }}</el-descriptions-item>
                                <el-descriptions-item label="手机号码">{{ userStore.mobile || '未绑定' }}</el-descriptions-item>
                                <el-descriptions-item label="账号状态">
                                    <el-tag size="small" type="success">正常可用</el-tag>
                                </el-descriptions-item>
                                <el-descriptions-item label="电子邮箱">{{ userStore.email || '未设置' }}</el-descriptions-item>
                            </el-descriptions>
                        </div>

                        <div class="section-box mt-6">
                            <h3 class="section-title">系统权限</h3>
                            <div class="perms-list">
                                <div class="perm-card" v-for="p in perms" :key="p.title">
                                    <div class="p-icon" :style="{ background: p.bg }">
                                        <el-icon>
                                            <component :is="p.icon" />
                                        </el-icon>
                                    </div>
                                    <div class="p-info">
                                        <div class="p-title">{{ p.title }}</div>
                                        <div class="p-desc">{{ p.desc }}</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </el-col>

                    <el-col :span="8">
                        <div class="section-box h-full">
                            <h3 class="section-title">安全设置</h3>
                            <div class="security-list">
                                <div class="s-item">
                                    <div class="s-info">
                                        <div class="s-label">重置密码</div>
                                        <div class="s-tip">定期修改密码可增强账号安全性</div>
                                    </div>
                                    <el-button link type="primary" @click="showPassModal = true">修改</el-button>
                                </div>
                                <div class="s-item">
                                    <div class="s-info">
                                        <div class="s-label">双重认证</div>
                                        <div class="s-tip">暂未启用企业微信二次验证</div>
                                    </div>
                                    <el-switch size="small" />
                                </div>
                                <div class="s-item">
                                    <div class="s-info">
                                        <div class="s-label">敏感数据权限</div>
                                        <div class="s-tip">您拥有查看财务类敏感数据的权限</div>
                                    </div>
                                    <el-icon color="#eab308" size="20">
                                        <Lock />
                                    </el-icon>
                                </div>
                            </div>

                            <div class="stats-mini mt-6">
                                <h3 class="section-title">我的操作量 (本月)</h3>
                                <div class="chart-box" style="height: 120px">
                                    <!-- Simple mock chart visualization -->
                                    <div class="bars">
                                        <div class="bar" v-for="i in 12" :key="i"
                                            :style="{ height: Math.random() * 80 + 20 + '%' }"></div>
                                    </div>
                                </div>
                                <p class="text-center text-xs text-gray-400 mt-2">系统活跃度排名 Top 5%</p>
                            </div>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </div>

        <!-- 修改密码弹窗 -->
        <el-dialog v-model="showPassModal" title="修改登录密码" width="400px" align-center>
            <el-form label-position="top">
                <el-form-item label="当前密码" required>
                    <el-input v-model="passForm.oldPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="新密码" required>
                    <el-input v-model="passForm.newPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="确认新密码" required>
                    <el-input v-model="confirmPassword" type="password" show-password />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="showPassModal = false">取消</el-button>
                <el-button type="primary" @click="savePassword">提交修改</el-button>
            </template>
        </el-dialog>

        <!-- 编辑资料弹窗 -->
        <el-dialog v-model="showEditModal" title="编辑基本资料" width="500px" align-center>
            <el-form :model="profileForm" label-position="top">
                <el-form-item label="真实姓名">
                    <el-input v-model="profileForm.realName" />
                </el-form-item>
                <el-form-item label="手机号码">
                    <el-input v-model="profileForm.mobile" />
                </el-form-item>
                <el-form-item label="电子邮箱">
                    <el-input v-model="profileForm.email" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="showEditModal = false">取消</el-button>
                <el-button type="primary" @click="saveProfile">保存修改</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Camera, Location, Calendar, Monitor, Lock, DataAnalysis, Key } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { updateProfile, updatePassword } from '@/api/user'

const userStore = useUserStore()
const showPassModal = ref(false)
const showEditModal = ref(false)

const passForm = ref({
    oldPassword: '',
    newPassword: ''
})
const confirmPassword = ref('')
const profileForm = ref({
    realName: '',
    mobile: '',
    email: ''
})

const perms = [
    { title: '全院运营监控', desc: '全模块读写权限', icon: Monitor, bg: 'rgba(13, 189, 168, 0.15)' },
    { title: '敏感数据准入', desc: '核心财务指标查看', icon: Key, bg: 'rgba(234, 179, 8, 0.15)' },
    { title: '预警规则管理', desc: '全院触发条件配置', icon: Lock, bg: 'rgba(239, 68, 68, 0.15)' },
    { title: '决策驾驶舱', desc: '高管视图访问权限', icon: DataAnalysis, bg: 'rgba(99, 102, 241, 0.15)' },
]

const handleEdit = () => {
    profileForm.value = {
        realName: userStore.realName,
        mobile: userStore.mobile,
        email: userStore.email
    }
    showEditModal.value = true
}

const saveProfile = async () => {
    try {
        await updateProfile(profileForm.value)
        ElMessage.success('资料修改成功')
        showEditModal.value = false
        // 重新拉取用户信息
        await userStore.getInfo()
    } catch (e) {
        console.error(e)
    }
}

const savePassword = async () => {
    if (passForm.value.newPassword !== confirmPassword.value) {
        ElMessage.error('两次输入的密码不一致')
        return
    }
    try {
        await updatePassword(passForm.value)
        ElMessage.success('密码修改成功，请谨记新密码')
        showPassModal.value = false
        passForm.value = { oldPassword: '', newPassword: '' }
        confirmPassword.value = ''
    } catch (e) {
        console.error(e)
    }
}

onMounted(async () => {
    await userStore.getInfo()
})
</script>

<style scoped lang="scss">
.profile-container {
    padding-bottom: 20px;
    height: 100%;
}

.profile-card {
    background: #fff;
    border-radius: 24px;
    border: 1px solid #e2e8f0;
    overflow: hidden;
    position: relative;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02);
}

.profile-bg {
    height: 120px;
    background: linear-gradient(135deg, #0dbda8, #3b82f6);
    opacity: 0.1;
}

.profile-header {
    padding: 0 40px;
    margin-top: -50px;
    display: flex;
    align-items: flex-end;
    gap: 30px;
    padding-bottom: 30px;
    border-bottom: 1px solid #f1f5f9;

    .avatar-wrapper {
        position: relative;

        .profile-avatar {
            border: 4px solid #fff;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        .edit-avatar-btn {
            position: absolute;
            bottom: 0;
            right: 0;
            background: #fff;
            color: #64748b;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
    }

    .user-info {
        flex: 1;

        .u-name {
            font-size: 24px;
            font-weight: 700;
            color: #1e293b;
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 8px;
        }

        .u-email {
            color: #64748b;
            font-size: 14px;
            margin-bottom: 8px;
        }

        .u-meta {
            font-size: 13px;
            color: #94a3b8;
            display: flex;
            align-items: center;
        }
    }
}

.profile-body {
    padding: 40px;
}

.section-box {
    .section-title {
        font-size: 16px;
        font-weight: 700;
        color: #1e293b;
        margin-bottom: 20px;
        position: relative;
        padding-left: 12px;

        &::after {
            content: '';
            position: absolute;
            left: 0;
            top: 50%;
            transform: translateY(-50%);
            width: 4px;
            height: 14px;
            background: #0dbda8;
            border-radius: 2px;
        }
    }
}

.perms-list {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;

    .perm-card {
        padding: 16px;
        background: #f8fafc;
        border: 1px solid #e2e8f0;
        border-radius: 12px;
        display: flex;
        align-items: center;
        gap: 12px;
        transition: all 0.2s;

        &:hover {
            border-color: #0dbda8;
            transform: translateY(-2px);
        }

        .p-icon {
            width: 40px;
            height: 40px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #1e293b;
            font-size: 20px;
        }

        .p-info {
            .p-title {
                font-size: 14px;
                font-weight: 600;
                color: #1e293b;
            }

            .p-desc {
                font-size: 12px;
                color: #64748b;
                margin-top: 2px;
            }
        }
    }
}

.security-list {
    .s-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16px 0;
        border-bottom: 1px dashed #e2e8f0;

        &:last-child {
            border-bottom: none;
        }

        .s-label {
            font-size: 14px;
            font-weight: 600;
            color: #1e293b;
            margin-bottom: 4px;
        }

        .s-tip {
            font-size: 12px;
            color: #94a3b8;
        }
    }
}

.chart-box {
    .bars {
        height: 100%;
        display: flex;
        align-items: flex-end;
        gap: 8px;
        padding: 0 10px;

        .bar {
            flex: 1;
            background: #0dbda8;
            border-radius: 4px 4px 0 0;
            opacity: 0.6;
            transition: opacity 0.3s;

            &:hover {
                opacity: 1;
            }
        }
    }
}

.mt-6 {
    margin-top: 24px;
}

.ml-4 {
    margin-left: 16px;
}

.h-full {
    height: 100%;
}

.text-xs {
    font-size: 11px;
}

.animate-enter {
    animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(20px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}
</style>
