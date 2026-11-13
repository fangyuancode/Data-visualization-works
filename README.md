# 数据可视化平台

基于 Vue 3、TypeScript、Vite 和 Element Plus 的多源数据管理与可视化分析前端。项目围绕七类主题数据，提供数据查询、维护、统计图表和规律解读，并包含工作台、汇总大屏、用户与角色管理、登录日志等页面。

本文按当前源码整理。项目目录名为 `FontBaseProject`，`package.json` 包名为 `data-analysis`；界面名称在 `src/config/index.ts` 中配置为“数据可视化平台”。

## 1. 功能与页面

### 七类主题数据

每类主题均包含数据列表页 `index.vue`、分析页 `analysis.vue` 和对应的 API 模块。列表页提供条件筛选、分页和受权限控制的新增、编辑、删除；分析页调用后端统计接口，展示 ECharts 图表及接口返回的 `insights` 解读文本。

| 主题           | 数据列表路由       | 分析页路由                  | 主要分析内容                                              | API 文件                       |
| -------------- | ------------------ | --------------------------- | --------------------------------------------------------- | ------------------------------ |
| 消费者购物趋势 | `/source/comsumer` | `/source/comsumer-analysis` | 购物偏好、年龄与收入分层、城市等级、配送与售后体验        | `src/api/consumer-shopping.ts` |
| 孕妇 NIPT 检测 | `/source/nipt`     | `/source/nipt-analysis`     | 年龄、BMI、孕周、测序质量、染色体指标及数据集中的健康标签 | `src/api/nipt.ts`              |
| 房屋租赁       | `/source/rental`   | `/source/rental-analysis`   | 北京区域租金地图、户型、面积、地铁、楼层与供暖差异        | `src/api/rental.ts`            |
| 学生考试表现   | `/source/exam`     | `/source/exam-analysis`     | 学科成绩、学习时长、家庭背景、学习环境与通过状态          | `src/api/exam.ts`              |
| 青少年心理健康 | `/source/teen`     | `/source/teen-analysis`     | 社媒使用、睡眠、焦虑、压力及数据集中的风险标签            | `src/api/teen.ts`              |
| 电视剧播放量   | `/source/tvplay`   | `/source/tvplay-analysis`   | 题材、播放量、评分、互动反馈、月份趋势与热门剧集          | `src/api/tvplay.ts`            |
| 世界幸福报告   | `/source/world`    | `/source/world-analysis`    | 国家与地区排名、年度趋势、幸福分数及解释性指标            | `src/api/world-happiness.ts`   |

> `comsumer` 是当前目录与路由中的实际拼写。新增链接时应沿用现有路径；若要更名，需要同步修改路由、页面引用和跳转入口。

分析页在侧边栏中隐藏，通过列表页或工作台入口进入。图表覆盖柱状图、折线图、饼图、雷达图、散点图、热力矩阵和地图等形式。

### 平台与管理页面

| 页面           | 路由                                                         | 当前实现                                                     |
| -------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 登录 / 注册    | `/auth/login`、`/auth/register`                              | 对接后端认证接口；注册请求默认携带 `roleId: 3`               |
| 工作台         | `/dashboard/console`                                         | 七类数据入口、功能介绍与预设的分析覆盖度展示；其中能力评分等为页面配置值 |
| 数据总览大屏   | `/screen`                                                    | 并行请求七类分析接口，汇总数据量、主题指标和接口成功情况；部分能力维度为预设值 |
| 用户管理       | `/system/user`                                               | 用户查询、新增、修改、删除、状态与角色管理接口               |
| 角色管理       | `/system/role`                                               | 角色列表与增删改接口                                         |
| 菜单管理       | `/system/menu`                                               | 页面内的菜单树维护演示，尚未对接菜单写入接口                 |
| 个人中心       | `/system/user-center`                                        | 保留资料与密码表单，当前编辑按钮仅切换编辑状态               |
| 用户登录管理   | `/seting/usermanger`                                         | 登录日志查询、统计、详情、删除和当前页 CSV 导出              |
| 找回密码       | `/auth/forget-password`                                      | 页面已存在，提交处理函数尚未实现                             |
| 异常与外部页面 | `/exception/403`、`/exception/404`、`/exception/500`、`/outside/iframe/:path` | 异常提示与 iframe 容器                                       |

`seting`、`usermanger` 也是当前代码中的实际拼写。业务页面的菜单可见性取决于用户角色。

### 通用界面能力

- 明亮、暗黑及跟随系统主题，多种菜单布局和主题色配置。
- 标签页、面包屑、全局搜索、锁屏、返回顶部等后台界面组件。
- 中英文语言包与 Element Plus 本地化配置；业务页仍有直接编写的中文文案。
- 通用表格、搜索表单、图表、卡片、Excel 导入导出、富文本与媒体组件，集中在 `src/components/core/`。组件存在不代表每个业务页均已接入。

## 2. 技术栈

以下版本为 `package.json` 中声明的版本范围，实际解析结果以 `pnpm-lock.yaml` 为准。

| 类别       | 技术与声明版本                                               |
| ---------- | ------------------------------------------------------------ |
| 前端框架   | Vue `^3.5.21`、TypeScript `~5.6.3`                           |
| 构建工具   | Vite `^7.1.5`、Vue SFC 插件                                  |
| UI 与图标  | Element Plus `^2.11.2`、Element Plus Icons、Iconify          |
| 路由与状态 | Vue Router `^4.5.1`、Pinia `^3.0.3`、Pinia 持久化插件        |
| 可视化     | ECharts `^6.0.0`、本地地图数据                               |
| 请求与工具 | Axios `^1.12.2`、VueUse、Mitt                                |
| 样式与语言 | SCSS、Tailwind CSS `^4.1.14`、Vue I18n `^9.14.0`             |
| 工程规范   | ESLint、Prettier、Stylelint、Husky、lint-staged、Commitlint、Commitizen |
| 自动导入   | `unplugin-auto-import`、`unplugin-vue-components`、Element Plus 按需样式 |

## 3. 目录结构

```text
FontBaseProject/
├── public/                       # 直接复制到构建产物的静态资源
├── scripts/
│   └── clean-dev.ts               # 模板演示内容清理脚本，会删除及重写文件
├── src/
│   ├── api/                      # 认证、系统管理和七类主题接口及类型
│   ├── assets/                   # 图片、SVG、全局样式和主题样式
│   ├── components/core/          # 布局、表格、表单、图表等通用组件
│   ├── config/                   # 系统名称、主题、快捷入口、顶栏等配置
│   ├── directives/               # 角色、按钮权限、水波纹、高亮等指令
│   ├── echarts/                  # 世界、中国及省级地图 JS / JSON / GeoJSON
│   ├── enums/                    # 应用与表单枚举
│   ├── hooks/core/               # 表格、图表、主题、页面权限等组合式逻辑
│   ├── locales/                  # 国际化入口及中英文语言包
│   ├── mock/                     # 遗留演示数据，不是完整的业务 Mock 服务
│   ├── plugins/                  # 插件及 ECharts 配置
│   ├── router/                   # 静态路由、模块化菜单、守卫及动态注册逻辑
│   ├── store/                    # 用户、菜单、标签页、设置、表格状态
│   ├── types/                    # 公共类型、API 类型及自动生成的导入声明
│   ├── utils/                    # 请求、缓存、导航、地图、表单与 UI 工具
│   ├── views/
│   │   ├── auth/                 # 登录、注册、找回密码
│   │   ├── dashboard/console/    # 工作台
│   │   ├── index/                # 后台主布局
│   │   ├── screen/               # 数据总览大屏
│   │   ├── source/               # 七类数据列表与分析页
│   │   ├── system/               # 用户、角色、菜单、个人中心
│   │   ├── seting/usermanger/     # 用户登录日志管理
│   │   └── ...                   # 异常页、结果页、外链及保留页面
│   ├── main/                     # 混放的 Java 后端源码与 resources
│   ├── src/main/                 # 另一组租赁相关 Java / MyBatis 文件
│   ├── App.vue                   # 根组件、主题与语言初始化
│   └── main.ts                   # 前端入口，与 main/ 目录不同
├── .env                          # 通用环境配置
├── .env.development              # 开发环境接口配置
├── .env.production               # 生产环境接口配置
├── vite.config.ts                # 代理、别名、自动导入、压缩与构建配置
├── package.json                  # 依赖与命令
├── pnpm-lock.yaml                # 依赖锁文件
├── tsconfig.json                 # TypeScript 配置
├── LICENSE                       # MIT 许可证
└── dist/                         # 构建输出，已被 Git 忽略
```

**前后端边界：**此目录以 `package.json` 和 Vite 为前端工程入口。虽然包含 Spring Boot、MyBatis 相关 Java 文件、数据库配置和权限 SQL，但未发现 `pom.xml` 或 Gradle 构建文件，不能仅凭该目录认定后端可独立启动。Java 文件不由 Vite 编译；完整后端工程、数据库结构和业务数据需另行准备。`src/main/` 与 `src/src/main/` 中存在同类租赁文件，维护后端时需先确认实际使用的源码位置。

## 4. 本地启动

### 环境要求

- `package.json` 声明 Node.js `>=20.19.0`、pnpm `>=8.8.0`。
- 本次类型检查使用 Node.js `22.12.0`、pnpm `10.33.0`。可使用同一工具版本复现；仓库锁文件格式为 `9.0`，安装时需使用兼容该格式的 pnpm。
- 登录、注册、业务数据与统计图表依赖可访问的后端服务。

### 安装和运行

在 PowerShell 中进入项目目录：

```powershell
pnpm install --frozen-lockfile
```



```dotenv
VITE_API_URL=/
# 示例：实际后端使用 9091 时
VITE_API_PROXY_URL=http://localhost:9091
```

```powershell
pnpm dev
```



首次启动 Vite 会生成 `.auto-import.json`、`src/types/import/auto-imports.d.ts` 和 `src/types/import/components.d.ts`。这些文件被 Git 忽略；全新检出时应先启动一次开发服务，再执行依赖这些声明的类型或规范检查。

## 5. 环境变量与部署

Vite 加载通用 `.env` 和对应模式的环境文件；本机差异可放在 `.env.development.local` 或 `.env.production.local`。修改开发环境配置后需重启服务，修改生产配置后需重新构建。

| 变量                    | 当前配置 / 用途                                              |
| ----------------------- | ------------------------------------------------------------ |
| `VITE_VERSION`          | `3.0.2`，应用版本标识；与 `package.json` 的 `0.0.0` 不同     |
| `VITE_PORT`             | `3006`，开发服务端口                                         |
| `VITE_BASE_URL`         | `/`，静态资源部署基础路径；子目录部署时可设置为 `/admin/` 等路径 |
| `VITE_ACCESS_MODE`      | `frontend`，按本地路由配置生成菜单；`backend` 模式读取菜单接口 |
| `VITE_WITH_CREDENTIALS` | `false`，跨域请求是否携带 Cookie                             |
| `VITE_OPEN_ROUTE_INFO`  | `false`，路由信息调试开关                                    |
| `VITE_LOCK_ENCRYPT_KEY` | 锁屏功能使用的前端加密配置；不应作为后端认证密钥             |
| `VITE_API_URL`          | Axios 基础地址；开发配置为 `/`，生产文件当前包含固定 HTTP 后端地址 |
| `VITE_API_PROXY_URL`    | 开发代理的目标服务，当前为 `http://localhost:9090`           |
| `VITE_DROP_CONSOLE`     | 环境文件中有声明，但当前 Vite 构建配置直接设置 `drop_console: true`，未按此变量切换 |

`VITE_` 变量会暴露给前端构建代码，不用于保存服务端密码或私密凭证。

### 构建与预览

```powershell
pnpm build
pnpm serve
```

`build` 先执行 `vue-tsc --noEmit`，通过后再执行 `vite build`，产物输出至 `dist/`。`serve` 使用 Vite 预览构建产物，访问地址以终端输出为准。构建会移除 `console` / `debugger`，并为大于 10 KB 的资源额外生成 `.gz` 文件；线上是否使用这些压缩文件取决于服务器配置。

生产部署时，将 `dist/` 交由静态 Web 服务器托管，并按实际环境替换 `.env.production` 中的固定接口地址。可采用以下任一方式：

- **同域代理：**设置 `VITE_API_URL=/`，在生产 Web 服务器上配置 `/api` 到后端的反向代理，并保留 `/api` 路径。
- **独立 API 域名：**设置 `VITE_API_URL=https://api.example.com`，由后端配置对应的跨域访问规则。

`api.example.com` 仅为占位示例。接口模块自身已包含 `/api/...`，不要再次给基础地址追加 `/api`。`VITE_API_PROXY_URL` 不会随静态构建产物成为线上代理，生产环境需单独配置服务转发。

项目使用 **Hash 路由**（`createWebHashHistory()`），业务地址形式为 `/#/source/exam`。部署到子目录时同步调整 `VITE_BASE_URL`，例如 `/admin/#/source/exam`。

## 6. 接口约定与权限

### 请求流程

```text
页面组件 → src/api/* → src/utils/http/index.ts → 后端接口
                                                   ↓
页面图表 / 表格 ← 已解包的 data ← { code, msg, data }
```

统一请求封装位于 `src/utils/http/index.ts`：超时时间为 15 秒，当前默认重试次数为 0。登录后同时发送 `Authorization: Bearer <token>` 和 `token: <token>` 请求头；遇到 HTTP 或业务状态码 `401` 时执行未授权处理。

后端成功响应应符合以下结构，`code` 支持可转为数字的 `200`：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "records": [],
    "current": 1,
    "size": 10,
    "total": 0
  }
}
```

API 调用返回的是已解包的 `data`，业务页无需再次读取 `res.data.data`。分页参数使用 `current`、`size`，结果包含 `records`、`current`、`size`、`total`。

| 接口范围             | 路径                                                         |
| -------------------- | ------------------------------------------------------------ |
| 登录、注册、用户信息 | `POST /api/auth/login`、`POST /api/auth/register`、`GET /api/user/info` |
| 用户、角色、登录日志 | `/api/user/...`、`/api/role/...`、`/api/login-log/...`       |
| 后端菜单模式         | `GET /api/v3/system/menus/simple`                            |
| 七类数据             | `/api/common/{resource}`                                     |

其中 `{resource}` 为 `consumer-shopping`、`nipt`、`rental`、`exam`、`teen`、`tvplay` 或 `world-happiness`。各主题接口采用共同形式：

| 操作     | 方法与路径                            |
| -------- | ------------------------------------- |
| 分页查询 | `GET /api/common/{resource}/page`     |
| 筛选项   | `GET /api/common/{resource}/options`  |
| 统计分析 | `GET /api/common/{resource}/analysis` |
| 新增     | `POST /api/common/{resource}`         |
| 更新     | `PUT /api/common/{resource}/{id}`     |
| 删除     | `DELETE /api/common/{resource}/{id}`  |

具体筛选字段与统计结构以 `src/api/` 中各主题类型定义为准。当前 HTTP 封装会把未提供 `data` 的 POST / PUT 请求中的 `params` 移入 JSON 请求体；新增接口或联调角色分配等功能时，需要核对后端参数接收方式。

### 登录和权限流程

1. 登录页调用 `fetchLogin`，将令牌及登录状态写入用户 Store。
2. 路由守卫获取用户信息，根据菜单模式构造可访问菜单并动态注册路由。
3. 当前 `frontend` 模式读取 `src/router/modules/index.ts` 导出的路由，再按 `meta.roles` 过滤；它仍然需要真实后端完成认证与业务查询。
4. 切换为 `backend` 模式前，需提供 `/api/v3/system/menus/simple` 并确保返回的组件路径与现有页面匹配。

| 角色 / 账号   | 当前前端规则                                           |
| ------------- | ------------------------------------------------------ |
| `R_SUPER`     | 工作台、数据源、用户与角色管理、菜单管理、登录日志管理 |
| `R_ADMIN`     | 工作台、数据源和用户管理；数据页允许写操作             |
| `R_USER`      | 工作台和数据源；数据页只读                             |
| 用户名 `test` | `usePagePermission` 强制只读，即使角色中包含管理员角色 |

页面写权限集中在 `src/hooks/core/usePagePermission.ts`；按钮权限与角色指令在 `src/hooks/core/useAuth.ts`、`src/directives/core/`。前端规则负责界面展示与交互拦截，后端仍需独立校验写权限。

`/screen` 是静态路由，前端允许未登录访问；实际图表能否加载仍由部署后的后端接口权限和可用性决定。

## 7. 开发维护

### 常用命令

| 命令                         | 作用                                       |
| ---------------------------- | ------------------------------------------ |
| `pnpm dev`                   | 启动开发服务并打开浏览器                   |
| `pnpm build`                 | 类型检查后执行生产构建                     |
| `pnpm serve`                 | 预览 `dist/`                               |
| `pnpm exec vue-tsc --noEmit` | 单独执行类型检查                           |
| `pnpm lint`                  | ESLint 检查                                |
| `pnpm fix`                   | ESLint 自动修复，会修改文件                |
| `pnpm lint:prettier`         | 全项目匹配文件的格式化写入，并非只检查     |
| `pnpm lint:stylelint`        | 样式检查并自动修复                         |
| `pnpm lint:lint-staged`      | 检查并修复暂存区匹配的文件                 |
| `pnpm commit`                | 交互式填写提交信息                         |
| `pnpm clean:dev`             | 删除模板演示资源并重写部分配置，见下方说明 |

安装依赖时 `prepare` 脚本初始化 Husky。提交前钩子运行 lint-staged，提交信息钩子运行 Commitlint。当前未定义 `test` 脚本。

**不要将 `pnpm clean:dev` 当作缓存清理命令。**它会删除文件，并重写 `src/router/modules/index.ts`、部分路由、语言配置等。当前脚本生成的路由入口仅保留 `dashboard`、`system`、`result`、`exception`，会移除现有 `source` 和 `seting` 的注册。使用前应备份，并按当前业务调整脚本。

### 新增业务模块

1. 在 `src/api/` 新建接口和数据类型，复用 `@/utils/http`。
2. 在 `src/views/` 新建页面；数据维护页可复用 `usePagePermission`，图表可参考现有分析页或 `useChart`。
3. 在 `src/router/modules/` 定义路由，并加入 `src/router/modules/index.ts` 的 `routeModules`。组件标识如 `/source/exam` 对应 `src/views/source/exam/index.vue`，`/source/exam/analysis` 对应同目录 `analysis.vue`。
4. 在 `src/locales/langs/zh.json`、`en.json` 添加菜单文案；按需要配置 `roles`、`isHide`、`activePath`、`keepAlive`。
5. 完成后进行类型检查，并通过实际账号验证页面、接口、图表和权限。

系统名称和主题默认值主要在 `src/config/index.ts`、`src/config/setting.ts`；请求处理在 `src/utils/http/`；地图加载逻辑在 `src/utils/echarts-map.ts`。

## 8. 当前边界与排查提示

- **保留页面不等于已开放功能。**动态路由入口目前仅注册工作台、系统管理、数据源和登录管理。`toolbox.ts`、`result.ts`、`exception.ts` 虽被导入，但未加入 `routeModules`；异常页另有静态路由。`src/views/home/index.vue`、NIPT 的 `test.vue` 等页面没有当前业务路由入口。
- **部分管理功能仍是模板实现。**菜单编辑只操作页面内存数据，不会保存到服务端或自动修改实际导航；个人中心尚无保存接口；角色权限弹窗文件中的保存方法仍有 TODO。找回密码提交逻辑为空。
- **前端中的“智能分析”主要体现为统计图表与返回文本。**当前七类分析页没有直接调用大模型的逻辑。整页 PDF / Word 分析报告导出、自然语言问答等出现在工作台的后续方向中，不能仅凭已安装相关依赖认定功能完成。
- **请求失败时先查地址和响应。**确认后端端口、`/api` 转发、登录状态，以及响应是否为 `{ code, msg, data }`。当前开发代理 `9090` 与随附 Java 配置 `9091` 的差异尤其需要核对。
- **有页面但无菜单时检查路由注册和角色。**重点核对 `routeModules`、`meta.roles`、用户接口返回的 `roles` 及 `VITE_ACCESS_MODE`；菜单管理演示页中的修改不会改变本地路由配置。
- **全新检出后检查工具报声明缺失时，先运行一次 `pnpm dev`。**自动导入文件未入库，ESLint 配置还直接读取 `.auto-import.json`。
- **开发正常、部署后接口失败时检查生产配置。**生产 API 地址在构建阶段写入，开发代理不会随 `dist/` 自动部署；HTTPS 页面也需使用兼容的 HTTPS API 或同域代理。

