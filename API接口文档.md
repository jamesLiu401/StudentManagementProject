# 学生管理系统 API 接口文档

## 📋 系统概述

**基础URL**: `http://localhost:8080`  
**认证方式**: JWT Token  
**权限角色**: ADMIN(管理员), TEACHER(教师), STUDENT(学生)

## 🔐 认证与权限

### 请求头格式
```
Authorization: Bearer <your-jwt-token>
```

### 通用响应格式
```json
{
  "success": true,
  "message": "操作成功",
  "data": { ... },
  "timestamp": "2025-09-10T10:30:00"
}
```

---

## 📚 API接口总览

| 模块 | 基础路径 | 接口数量 | 主要功能 |
|------|----------|----------|----------|
| 学生管理 | `/students` | 8 | 学生CRUD、批量导入、搜索 |
| 学院管理 | `/academies` | 18 | 学院CRUD、分页查询、统计、院长查询 |
| 专业管理 | `/majors` | 20 | 专业CRUD、多条件查询、统计、辅导员管理 |
| 教师管理 | `/teachers` | 15 | 教师CRUD、部门职称查询、统计 |
| 课程管理 | `/subjects` | 35 | 课程CRUD、学分管理、多条件查询、聚合统计 |
| 班级管理 | `/classes` | 25 | 总班级/子班级CRUD、层级管理、业务逻辑 |
| 成绩管理 | `/scores` | 22 | 成绩CRUD、统计分析、批量操作、等级管理 |
| 缴费管理 | `/payments` | 30 | 缴费CRUD、统计查询、金额计算、状态管理 |
| 认证管理 | `/auth` | 3 | 登录注册、用户管理 |
| 级联管理 | `/cascade` | 7 | 级联操作、数据迁移、一致性检查 |

**总计**: **183个API接口**

---

## 🎯 核心功能模块

### 1. 学生管理 API (`/students`)

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | `/students` | 添加学生 | ADMIN |
| PUT | `/students/{id}` | 更新学生 | ADMIN |
| DELETE | `/students/{id}` | 删除学生 | ADMIN |
| GET | `/students/{id}` | 根据ID查询学生 | ADMIN, TEACHER |
| GET | `/students` | 查询所有学生 | ADMIN, TEACHER |
| GET | `/students/page` | 分页查询学生 | ADMIN, TEACHER |
| GET | `/students/search` | 按姓名搜索学生 | ADMIN, TEACHER |
| POST | `/students/batch/import` | 批量导入学生(CSV) | ADMIN |

**示例请求**:
```http
POST /students
Content-Type: application/json
Authorization: Bearer <token>

{
  "stuName": "张三",
  "gender": true,
  "phone": "13800138000",
  "address": "北京市海淀区",
  "major": {"majorId": 1},
  "grade": 2023
}
```

### 2. 学院管理 API (`/academies`)

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | `/academies` | 添加学院 | ADMIN |
| PUT | `/academies/{id}` | 更新学院 | ADMIN |
| DELETE | `/academies/{id}` | 删除学院 | ADMIN |
| GET | `/academies/{id}` | 根据ID查询学院 | ADMIN, TEACHER |
| GET | `/academies` | 查询所有学院 | ADMIN, TEACHER |
| GET | `/academies/page` | 分页查询学院 | ADMIN, TEACHER |
| GET | `/academies/search/name` | 按学院名称模糊查询 | ADMIN, TEACHER |
| GET | `/academies/search/name/page` | 按学院名称模糊分页查询 | ADMIN, TEACHER |
| GET | `/academies/search/name/count` | 统计学院名称数量 | ADMIN, TEACHER |
| GET | `/academies/dean/{deanName}` | 根据院长姓名查询 | ADMIN, TEACHER |
| GET | `/academies/search/dean` | 根据院长姓名模糊查询 | ADMIN, TEACHER |
| GET | `/academies/search/dean/page` | 根据院长姓名模糊分页查询 | ADMIN, TEACHER |
| GET | `/academies/search/dean/count` | 统计院长姓名数量 | ADMIN, TEACHER |
| GET | `/academies/name/{name}` | 根据学院名称精确查询 | ADMIN, TEACHER |
| GET | `/academies/code/{code}` | 根据学院代码查询 | ADMIN, TEACHER |

### 3. 专业管理 API (`/majors`)

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | `/majors` | 添加专业 | ADMIN |
| PUT | `/majors/{id}` | 更新专业 | ADMIN |
| DELETE | `/majors/{id}` | 删除专业 | ADMIN |
| GET | `/majors/{id}` | 根据ID查询专业 | ADMIN, TEACHER |
| GET | `/majors` | 查询所有专业 | ADMIN, TEACHER |
| GET | `/majors/page` | 分页查询专业 | ADMIN, TEACHER |
| GET | `/majors/academy/{academyId}` | 根据学院查询专业 | ADMIN, TEACHER |
| GET | `/majors/academy/{academyId}/page` | 根据学院分页查询专业 | ADMIN, TEACHER |
| GET | `/majors/academy/{academyId}/count` | 统计学院专业数量 | ADMIN, TEACHER |
| GET | `/majors/grade/{grade}` | 根据年级查询专业 | ADMIN, TEACHER |
| GET | `/majors/grade/{grade}/page` | 根据年级分页查询专业 | ADMIN, TEACHER |
| GET | `/majors/grade/{grade}/count` | 统计年级专业数量 | ADMIN, TEACHER |
| GET | `/majors/name/{majorName}` | 根据专业名称查询 | ADMIN, TEACHER |
| GET | `/majors/name/{majorName}/page` | 根据专业名称分页查询 | ADMIN, TEACHER |
| GET | `/majors/counselor/{counselorId}` | 根据辅导员查询专业 | ADMIN, TEACHER |
| GET | `/majors/counselor/{counselorId}/page` | 根据辅导员分页查询专业 | ADMIN, TEACHER |
| GET | `/majors/counselor/{counselorId}/count` | 统计辅导员管理的专业数量 | ADMIN, TEACHER |
| GET | `/majors/academy/{academyId}/grade/{grade}` | 根据学院和年级查询专业 | ADMIN, TEACHER |
| GET | `/majors/academy/{academyId}/grade/{grade}/count` | 统计学院年级专业数量 | ADMIN, TEACHER |

### 4. 教师管理 API (`/teachers`)

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | `/teachers` | 添加教师 | ADMIN |
| PUT | `/teachers/{id}` | 更新教师 | ADMIN |
| DELETE | `/teachers/{id}` | 删除教师 | ADMIN |
| GET | `/teachers/{id}` | 根据ID查询教师 | ADMIN, TEACHER |
| GET | `/teachers` | 查询所有教师 | ADMIN, TEACHER |
| GET | `/teachers/page` | 分页查询教师 | ADMIN, TEACHER |
| GET | `/teachers/search` | 按姓名搜索教师 | ADMIN, TEACHER |
| GET | `/teachers/search/page` | 按姓名分页搜索教师 | ADMIN, TEACHER |
| GET | `/teachers/department/{department}` | 根据部门查询教师 | ADMIN, TEACHER |
| GET | `/teachers/title/{title}` | 根据职称查询教师 | ADMIN, TEACHER |
| GET | `/teachers/department/{department}/title/{title}` | 根据部门和职称查询教师 | ADMIN, TEACHER |
| GET | `/teachers/department/{department}/count` | 统计部门教师数量 | ADMIN, TEACHER |
| GET | `/teachers/title/{title}/count` | 统计职称教师数量 | ADMIN, TEACHER |
| GET | `/teachers/department/{department}/title/{title}/count` | 统计部门职称教师数量 | ADMIN, TEACHER |

### 5. 课程管理 API (`/subjects`)

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | `/subjects` | 添加课程 | ADMIN |
| POST | `/subjects/create` | 创建课程(通过参数) | ADMIN |
| PUT | `/subjects/{id}` | 更新课程 | ADMIN |
| DELETE | `/subjects/{id}` | 删除课程 | ADMIN |
| GET | `/subjects/{id}` | 根据ID查询课程 | ADMIN, TEACHER |
| GET | `/subjects` | 查询所有课程 | ADMIN, TEACHER |
| GET | `/subjects/page` | 分页查询课程 | ADMIN, TEACHER |
| GET | `/subjects/academy/{academy}` | 根据学院查询课程 | ADMIN, TEACHER |
| GET | `/subjects/academy/{academy}/page` | 根据学院分页查询课程 | ADMIN, TEACHER |
| GET | `/subjects/academy/{academy}/count` | 统计学院课程数量 | ADMIN, TEACHER |
| GET | `/subjects/search/name` | 按课程名称模糊查询 | ADMIN, TEACHER |
| GET | `/subjects/search/name/page` | 按课程名称模糊分页查询 | ADMIN, TEACHER |
| GET | `/subjects/name/{subjectName}` | 根据课程名称精确查询 | ADMIN, TEACHER |
| GET | `/subjects/academy/{academy}/name/{subjectName}` | 根据学院和课程名称查询 | ADMIN, TEACHER |
| GET | `/subjects/credit/{credit}` | 根据学分查询课程 | ADMIN, TEACHER |
| GET | `/subjects/credit/{credit}/page` | 根据学分分页查询课程 | ADMIN, TEACHER |
| GET | `/subjects/credit/{credit}/count` | 统计学分课程数量 | ADMIN, TEACHER |
| GET | `/subjects/credit-range` | 根据学分范围查询课程 | ADMIN, TEACHER |
| GET | `/subjects/credit-range/page` | 根据学分范围分页查询课程 | ADMIN, TEACHER |
| GET | `/subjects/credit-range/count` | 统计学分范围课程数量 | ADMIN, TEACHER |
| GET | `/subjects/academy/{academy}/credit/{credit}` | 根据学院和学分查询课程 | ADMIN, TEACHER |
| GET | `/subjects/academy/{academy}/credit-range` | 根据学院和学分范围查询课程 | ADMIN, TEACHER |
| GET | `/subjects/academy/{academy}/credit-range/count` | 统计学院学分范围课程数量 | ADMIN, TEACHER |
| GET | `/subjects/search/multiple` | 多条件查询课程 | ADMIN, TEACHER |
| GET | `/subjects/search/multiple/page` | 多条件分页查询课程 | ADMIN, TEACHER |
| GET | `/subjects/search` | 通用搜索课程 | ADMIN, TEACHER |
| GET | `/subjects/academy/{academy}/total-credit` | 获取学院总学分 | ADMIN, TEACHER |
| GET | `/subjects/academies` | 获取所有学院列表 | ADMIN, TEACHER |
| GET | `/subjects/academy/{academy}/credits` | 获取学院所有学分 | ADMIN, TEACHER |
| GET | `/subjects/exists` | 检查课程是否存在 | ADMIN, TEACHER |

**示例请求**:
```http
POST /subjects/create?subjectName=数据结构&academy=计算机学院&credit=3.0
Authorization: Bearer <token>
```

### 6. 班级管理 API (`/classes`)

#### 总班级管理
| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | `/classes/total` | 添加总班级 | ADMIN |
| POST | `/classes/total/create` | 创建总班级(通过参数) | ADMIN |
| PUT | `/classes/total/{id}` | 更新总班级 | ADMIN |
| DELETE | `/classes/total/{id}` | 删除总班级 | ADMIN |
| GET | `/classes/total/{id}` | 根据ID查询总班级 | ADMIN, TEACHER |
| GET | `/classes/total` | 查询所有总班级 | ADMIN, TEACHER |
| GET | `/classes/total/page` | 分页查询总班级 | ADMIN, TEACHER |
| GET | `/classes/total/major/{majorId}` | 根据专业查询总班级 | ADMIN, TEACHER |
| GET | `/classes/total/search` | 按名称搜索总班级 | ADMIN, TEACHER |
| GET | `/classes/total/name/{totalClassName}/major/{majorId}` | 根据名称和专业查询总班级 | ADMIN, TEACHER |
| GET | `/classes/total/exists` | 检查总班级是否存在 | ADMIN, TEACHER |
| GET | `/classes/total/search/general` | 通用搜索总班级 | ADMIN, TEACHER |

#### 子班级管理
| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | `/classes/sub` | 添加子班级 | ADMIN |
| POST | `/classes/sub/create` | 创建子班级(通过参数) | ADMIN |
| PUT | `/classes/sub/{id}` | 更新子班级 | ADMIN |
| DELETE | `/classes/sub/{id}` | 删除子班级 | ADMIN |
| GET | `/classes/sub/{id}` | 根据ID查询子班级 | ADMIN, TEACHER |
| GET | `/classes/sub` | 查询所有子班级 | ADMIN, TEACHER |
| GET | `/classes/sub/page` | 分页查询子班级 | ADMIN, TEACHER |
| GET | `/classes/sub/total/{totalClassId}` | 根据总班级查询子班级 | ADMIN, TEACHER |
| GET | `/classes/sub/search` | 按名称搜索子班级 | ADMIN, TEACHER |
| GET | `/classes/sub/name/{subClassName}/total/{totalClassId}` | 根据名称和总班级查询子班级 | ADMIN, TEACHER |
| GET | `/classes/sub/exists` | 检查子班级是否存在 | ADMIN, TEACHER |
| GET | `/classes/sub/search/general` | 通用搜索子班级 | ADMIN, TEACHER |

### 7. 成绩管理 API (`/scores`)

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | `/scores` | 添加成绩 | ADMIN, TEACHER |
| POST | `/scores/create` | 创建成绩(通过学生ID和课程ID) | ADMIN, TEACHER |
| PUT | `/scores/{id}` | 更新成绩 | ADMIN, TEACHER |
| PUT | `/scores/update` | 通过学生ID和课程ID更新成绩 | ADMIN, TEACHER |
| DELETE | `/scores/{id}` | 删除成绩 | ADMIN |
| GET | `/scores/{id}` | 根据ID查询成绩 | ADMIN, TEACHER |
| GET | `/scores` | 查询所有成绩 | ADMIN, TEACHER |
| GET | `/scores/page` | 分页查询成绩 | ADMIN, TEACHER |
| GET | `/scores/student/{studentId}` | 根据学生ID查询成绩 | ADMIN, TEACHER |
| GET | `/scores/subject/{subjectId}` | 根据课程ID查询成绩 | ADMIN, TEACHER |
| GET | `/scores/student/{studentId}/subject/{subjectId}` | 根据学生ID和课程ID查询成绩 | ADMIN, TEACHER |
| GET | `/scores/student/{studentId}/average` | 获取学生平均分 | ADMIN, TEACHER |
| GET | `/scores/subject/{subjectId}/average` | 获取课程平均分 | ADMIN, TEACHER |
| GET | `/scores/student/{studentId}/max` | 获取学生最高分 | ADMIN, TEACHER |
| GET | `/scores/student/{studentId}/min` | 获取学生最低分 | ADMIN, TEACHER |
| GET | `/scores/student/{studentId}/passing` | 获取学生及格成绩 | ADMIN, TEACHER |
| GET | `/scores/student/{studentId}/failing` | 获取学生不及格成绩 | ADMIN, TEACHER |
| GET | `/scores/student/{studentId}/passing/count` | 统计学生及格成绩数量 | ADMIN, TEACHER |
| GET | `/scores/student/{studentId}/failing/count` | 统计学生不及格成绩数量 | ADMIN, TEACHER |
| POST | `/scores/batch` | 批量创建成绩 | ADMIN, TEACHER |
| GET | `/scores/grade` | 获取成绩等级 | ADMIN, TEACHER |

### 8. 缴费管理 API (`/payments`)

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | `/payments` | 添加缴费记录 | ADMIN |
| PUT | `/payments/{id}` | 更新缴费记录 | ADMIN |
| PATCH | `/payments/{id}/status` | 更新缴费状态 | ADMIN |
| DELETE | `/payments/{id}` | 删除缴费记录 | ADMIN |
| GET | `/payments/{id}` | 根据ID查询缴费记录 | ADMIN, TEACHER |
| GET | `/payments` | 查询所有缴费记录 | ADMIN, TEACHER |
| GET | `/payments/page` | 分页查询缴费记录 | ADMIN, TEACHER |
| GET | `/payments/student/{studentId}` | 根据学生ID查询缴费记录 | ADMIN, TEACHER |
| GET | `/payments/type/{type}` | 根据缴费类型查询缴费记录 | ADMIN, TEACHER |
| GET | `/payments/type/{type}/page` | 根据缴费类型分页查询缴费记录 | ADMIN, TEACHER |
| GET | `/payments/status/{status}` | 根据缴费状态查询缴费记录 | ADMIN, TEACHER |
| GET | `/payments/status/{status}/page` | 根据缴费状态分页查询缴费记录 | ADMIN, TEACHER |
| GET | `/payments/date-range` | 根据日期范围查询缴费记录 | ADMIN, TEACHER |
| GET | `/payments/amount-range` | 根据金额范围查询缴费记录 | ADMIN, TEACHER |
| GET | `/payments/student/{studentId}/type/{type}` | 根据学生和类型查询缴费记录 | ADMIN, TEACHER |
| GET | `/payments/student/{studentId}/status/{status}` | 根据学生和状态查询缴费记录 | ADMIN, TEACHER |
| GET | `/payments/type/{type}/status/{status}` | 根据类型和状态查询缴费记录 | ADMIN, TEACHER |
| GET | `/payments/student/{studentId}/date-range` | 根据学生和日期范围查询缴费记录 | ADMIN, TEACHER |
| GET | `/payments/student/{studentId}/amount-range` | 根据学生和金额范围查询缴费记录 | ADMIN, TEACHER |
| GET | `/payments/student/{studentId}/count` | 统计学生缴费记录数量 | ADMIN, TEACHER |
| GET | `/payments/type/{type}/count` | 统计缴费类型数量 | ADMIN, TEACHER |
| GET | `/payments/status/{status}/count` | 统计缴费状态数量 | ADMIN, TEACHER |
| GET | `/payments/student/{studentId}/status/{status}/count` | 统计学生缴费状态数量 | ADMIN, TEACHER |
| GET | `/payments/student/{studentId}/total-amount` | 获取学生总缴费金额 | ADMIN, TEACHER |
| GET | `/payments/student/{studentId}/total-amount-by-id` | 根据学生ID获取总缴费金额 | ADMIN, TEACHER |
| GET | `/payments/type/{type}/total-amount` | 获取缴费类型总金额 | ADMIN, TEACHER |
| GET | `/payments/status/{status}/total-amount` | 获取缴费状态总金额 | ADMIN, TEACHER |
| GET | `/payments/student/{studentId}/type/{type}/total-amount` | 获取学生缴费类型总金额 | ADMIN, TEACHER |
| GET | `/payments/search` | 搜索缴费记录 | ADMIN, TEACHER |

**示例请求**:
```http
GET /payments/date-range?startDate=2023-09-01&endDate=2023-12-31
Authorization: Bearer <token>
```

### 9. 认证管理 API (`/auth`)

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | `/auth/login` | 用户登录 | 无 |
| POST | `/auth/register` | 用户注册 | ADMIN, TEACHER |
| GET | `/auth/logout` | 用户登出 | 已认证用户 |

**示例请求**:
```http
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password123"
}
```

**登录响应示例**:
```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "username": "admin",
    "role": "ROLE_ADMIN",
    "userId": 1
  },
  "timestamp": "2025-09-10T10:30:00"
}
```

### 10. 级联管理 API (`/cascade`)

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| POST | `/cascade/delete` | 级联删除操作 | ADMIN |
| POST | `/cascade/batch-create` | 批量创建专业和班级 | ADMIN |
| POST | `/cascade/migrate-students` | 学生迁移 | ADMIN |
| GET | `/cascade/consistency-check` | 数据一致性检查 | ADMIN |
| GET | `/cascade/hierarchy` | 获取完整层级结构 | ADMIN |
| GET | `/cascade/delete-preview` | 获取级联删除预览 | ADMIN |
| POST | `/cascade/batch-update` | 批量更新操作 | ADMIN |

---

## 🔧 通用功能

### 分页查询参数
- `page`: 页码 (默认: 0)
- `size`: 每页大小 (默认: 10)
- `sortBy`: 排序字段
- `sortDir`: 排序方向 (asc/desc)

### 错误码说明
| 错误码 | 说明 | 常见场景 |
|--------|------|----------|
| 400 | 请求参数错误 | 参数格式不正确、必填参数缺失、参数值超出范围 |
| 401 | 未授权访问 | JWT Token缺失、Token格式错误、Token已过期 |
| 403 | 权限不足 | 角色权限不够、操作被禁止 |
| 404 | 资源不存在 | 实体ID不存在、路径错误 |
| 409 | 资源冲突 | 用户名已存在、数据重复 |
| 422 | 数据验证失败 | 业务规则验证失败、数据格式不符合要求 |
| 500 | 服务器内部错误 | 数据库连接失败、系统异常 |

### 错误响应格式
```json
{
  "success": false,
  "message": "具体错误信息",
  "data": null,
  "timestamp": "2025-09-10T10:30:00",
  "errorCode": "ERROR_CODE",
  "details": "详细错误描述"
}
```

### 常见错误场景
1. **认证失败**: Token无效或过期
2. **权限不足**: 用户角色无法执行该操作
3. **数据不存在**: 查询的实体ID不存在
4. **数据重复**: 创建时违反唯一性约束
5. **参数错误**: 请求参数格式或值不正确
6. **业务规则**: 违反业务逻辑规则

---

## 📝 使用示例

### 1. 获取JWT Token
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'
```

### 2. 查询学生列表（分页）
```bash
curl -X GET "http://localhost:8080/students/page?page=0&size=10" \
  -H "Authorization: Bearer <your-jwt-token>"
```

### 3. 添加新学生
```bash
curl -X POST http://localhost:8080/students \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d '{
    "stuName": "张三",
    "gender": true,
    "phone": "13800138000",
    "address": "北京市海淀区",
    "major": {"majorId": 1},
    "grade": 2023
  }'
```

### 4. 查询缴费统计
```bash
curl -X GET "http://localhost:8080/payments/student/1/total-amount" \
  -H "Authorization: Bearer <your-jwt-token>"
```

---

## ⚠️ 注意事项

1. **认证要求**: 除登录和注册接口外，所有接口都需要JWT Token认证
2. **权限控制**: 不同角色有不同的访问权限，请确保使用正确的角色
3. **分页参数**: 分页查询默认page=0, size=10，可根据需要调整
4. **日期格式**: 日期参数使用ISO 8601格式 (yyyy-MM-dd)
5. **金额格式**: 金额使用BigDecimal类型，支持精确计算
6. **批量操作**: 批量操作接口支持事务，要么全部成功，要么全部失败

---

## 📊 系统特性

- ✅ **完整的CRUD操作** - 所有实体都支持增删改查
- ✅ **强大的查询功能** - 支持单条件、多条件、模糊查询、精确查询
- ✅ **灵活的分页系统** - 所有列表查询都支持分页和排序
- ✅ **丰富的统计功能** - 支持各种维度的数据统计和聚合计算
- ✅ **完善的搜索功能** - 支持关键字搜索、组合搜索、通用搜索
- ✅ **业务逻辑支持** - 支持复杂业务场景的API调用
- ✅ **权限控制** - 基于角色的访问控制(ADMIN/TEACHER/STUDENT)
- ✅ **事务支持** - 保证数据一致性
- ✅ **智能数据关联** - 自动创建关联数据，支持级联操作
- ✅ **批量操作** - 支持批量导入、批量创建、批量更新
- ✅ **数据迁移** - 支持学生和班级的迁移操作
- ✅ **一致性检查** - 检查数据关联的完整性
- ✅ **层级结构管理** - 支持学院-专业-班级的层级管理
- ✅ **状态管理** - 支持缴费状态、成绩等级等状态管理
- ✅ **金额计算** - 支持精确的金额统计和计算
- ✅ **文件上传** - 支持CSV文件批量导入

---

## 📈 更新日志

- **v1.1.0** (2025-09-10): 完善版
  - 更新API接口总览，修正接口数量统计(183个接口)
  - 完善所有模块的API端点文档
  - 添加缺失的CRUD操作接口
  - 补充分页查询、统计查询、搜索功能
  - 完善认证管理、级联管理功能
  - 更新系统特性描述
  - 添加详细的请求响应示例

- **v1.0.0** (2025-09-10): 初始版