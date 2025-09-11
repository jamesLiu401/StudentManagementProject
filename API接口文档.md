# 学生管理系统 API 接口文档

## 📋 目录
- [接口概述](#接口概述)
- [认证接口](#认证接口)
- [学生管理接口](#学生管理接口)
- [教师管理接口](#教师管理接口)
- [缴费管理接口](#缴费管理接口)
- [错误码说明](#错误码说明)
- [使用示例](#使用示例)

---

## 🔌 接口概述

### 基础信息
- **Base URL**: `http://localhost:8080`
- **认证方式**: JWT Bearer Token
- **数据格式**: JSON
- **字符编码**: UTF-8

### 请求头格式
```http
Content-Type: application/json
Authorization: Bearer {jwt_token}
```

### 响应格式
```json
{
  "status": 200,
  "message": "操作成功",
  "data": {
    // 响应数据
  }
}
```

---

## 🔐 认证接口

### 1. 用户登录

**接口地址**: `POST /auth/login`

**权限要求**: 无

**请求参数**:
```json
{
  "username": "admin",
  "password": "password123"
}
```

**响应示例**:
```json
{
  "status": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTYzODk2NzIwMCwiZXhwIjoxNjM5MDUzNjAwfQ...",
    "username": "admin",
    "role": "ROLE_ADMIN",
    "userId": 1
  }
}
```

**错误响应**:
```json
{
  "status": 401,
  "message": "用户名或密码错误"
}
```

### 2. 用户注册

**接口地址**: `POST /auth/register`

**权限要求**: ADMIN 或 TEACHER

**请求参数**:
```json
{
  "username": "teacher1",
  "password": "password123",
  "role": "TEACHER"
}
```

**响应示例**:
```json
{
  "status": 200,
  "message": "操作成功",
  "data": {
    "userId": 2,
    "username": "teacher1",
    "role": "ROLE_TEACHER",
    "teacherId": 1
  }
}
```

**权限规则**:
- ADMIN: 可以注册ADMIN和TEACHER角色
- TEACHER: 不能注册任何新用户

### 3. 用户登出

**接口地址**: `GET /auth/logout`

**权限要求**: 已认证用户

**响应示例**:
```json
{
  "status": 200,
  "message": "登出成功"
}
```

---

## 👨‍🎓 学生管理接口

### 1. 添加学生

**接口地址**: `POST /students`

**权限要求**: ADMIN

**请求参数**:
```json
{
  "stuName": "张三",
  "stuGender": true,
  "stuMajor": "计算机科学",
  "grade": 2023,
  "stuTel": "13800138000",
  "stuAddress": "北京市海淀区"
}
```

**智能特性**: 自动创建专业、班级等关联数据

**响应示例**:
```json
{
  "status": 200,
  "message": "操作成功",
  "data": {
    "stuId": 1,
    "stuName": "张三",
    "stuGender": true,
    "stuMajor": "计算机科学",
    "grade": 2023,
    "stuTel": "13800138000",
    "stuAddress": "北京市海淀区",
    "stuClass": {
      "subClassId": 1,
      "subClassName": "计科2301"
    }
  }
}
```

### 2. 获取学生列表

**接口地址**: `GET /students`

**权限要求**: ADMIN 或 TEACHER

**查询参数**:
- `page`: 页码，默认0
- `size`: 每页条数，默认10
- `sort`: 排序字段，如"stuName,asc"

**响应示例**:
```json
{
  "status": 200,
  "message": "操作成功",
  "data": [
    {
      "stuId": 1,
      "stuName": "张三",
      "stuGender": true,
      "stuMajor": "计算机科学",
      "grade": 2023,
      "stuTel": "13800138000",
      "stuClass": {
        "subClassId": 1,
        "subClassName": "计科2301"
      }
    }
  ]
}
```

### 3. 分页查询学生

**接口地址**: `GET /students/page`

**权限要求**: ADMIN 或 TEACHER

**查询参数**:
- `page`: 页码，默认0
- `size`: 每页条数，默认10
- `sortBy`: 排序字段，默认"stuId"
- `sortDir`: 排序方向，默认"asc"

**响应示例**:
```json
{
  "status": 200,
  "message": "操作成功",
  "data": {
    "content": [
      {
        "stuId": 1,
        "stuName": "张三",
        "stuGender": true,
        "stuMajor": "计算机科学",
        "grade": 2023
      }
    ],
    "totalPages": 5,
    "totalElements": 48,
    "size": 10,
    "number": 0
  }
}
```

### 4. 根据ID获取学生

**接口地址**: `GET /students/{id}`

**权限要求**: ADMIN 或 TEACHER

**路径参数**:
- `id`: 学生ID

**响应示例**:
```json
{
  "status": 200,
  "message": "操作成功",
  "data": {
    "stuId": 1,
    "stuName": "张三",
    "stuGender": true,
    "stuMajor": "计算机科学",
    "grade": 2023,
    "stuTel": "13800138000",
    "stuAddress": "北京市海淀区",
    "stuClass": {
      "subClassId": 1,
      "subClassName": "计科2301",
      "totalClass": {
        "totalClassId": 1,
        "totalClassName": "计算机科学2023级",
        "major": {
          "majorId": 1,
          "majorName": "计算机科学",
          "academy": "计算机学院"
        }
      }
    }
  }
}
```

### 5. 更新学生信息

**接口地址**: `PUT /students/{id}`

**权限要求**: ADMIN

**路径参数**:
- `id`: 学生ID

**请求参数**: 同添加学生接口

**响应示例**:
```json
{
  "status": 200,
  "message": "操作成功",
  "data": {
    "stuId": 1,
    "stuName": "张三（更新）",
    "stuGender": true,
    "stuMajor": "计算机科学",
    "grade": 2023
  }
}
```

### 6. 删除学生

**接口地址**: `DELETE /students/{id}`

**权限要求**: ADMIN

**路径参数**:
- `id`: 学生ID

**响应示例**:
```json
{
  "status": 200,
  "message": "学生删除成功"
}
```

### 7. 搜索学生

**接口地址**: `GET /students/search`

**权限要求**: ADMIN 或 TEACHER

**查询参数**:
- `name`: 学生姓名（支持模糊搜索）

**响应示例**:
```json
{
  "status": 200,
  "message": "操作成功",
  "data": [
    {
      "stuId": 1,
      "stuName": "张三",
      "stuGender": true,
      "stuMajor": "计算机科学",
      "grade": 2023
    }
  ]
}
```

### 8. 批量导入学生

**接口地址**: `POST /students/batch/import`

**权限要求**: ADMIN

**请求类型**: `multipart/form-data`

**请求参数**:
- `file`: CSV文件

**CSV文件格式**:
```csv
姓名,性别,专业,年级,电话,地址,班级ID
张三,true,计算机科学,2023,13800138000,北京市海淀区,1
李四,false,软件工程,2023,13900139000,北京市朝阳区,2
```

**响应示例**:
```json
{
  "status": 200,
  "message": "批量导入成功，共导入 20 条记录",
  "data": [
    {
      "stuId": 1,
      "stuName": "张三",
      "stuGender": true,
      "stuMajor": "计算机科学",
      "grade": 2023
    }
  ]
}
```

---

## 👨‍🏫 教师管理接口

### 1. 获取教师列表

**接口地址**: `GET /teachers`

**权限要求**: ADMIN 或 TEACHER

**响应示例**:
```json
{
  "status": 200,
  "message": "操作成功",
  "data": [
    {
      "teacherId": 1,
      "teacherName": "张老师",
      "teacherNo": "T2024001",
      "department": "计算机学院",
      "title": "副教授"
    }
  ]
}
```

### 2. 分页查询教师

**接口地址**: `GET /teachers/page`

**权限要求**: ADMIN 或 TEACHER

**查询参数**:
- `page`: 页码，默认0
- `size`: 每页条数，默认10
- `sortBy`: 排序字段，默认"teacherId"
- `sortDir`: 排序方向，默认"asc"

**响应示例**:
```json
{
  "status": 200,
  "message": "操作成功",
  "data": {
    "content": [
      {
        "teacherId": 1,
        "teacherName": "张老师",
        "teacherNo": "T2024001",
        "department": "计算机学院",
        "title": "副教授"
      }
    ],
    "totalPages": 2,
    "totalElements": 15,
    "size": 10,
    "number": 0
  }
}
```

### 3. 根据ID获取教师

**接口地址**: `GET /teachers/{id}`

**权限要求**: ADMIN 或 TEACHER

**路径参数**:
- `id`: 教师ID

**响应示例**:
```json
{
  "status": 200,
  "message": "操作成功",
  "data": {
    "teacherId": 1,
    "teacherName": "张老师",
    "teacherNo": "T2024001",
    "department": "计算机学院",
    "title": "副教授",
    "majors": [
      {
        "majorId": 1,
        "majorName": "计算机科学",
        "academy": "计算机学院"
      }
    ]
  }
}
```

### 4. 添加教师

**接口地址**: `POST /teachers`

**权限要求**: ADMIN

**请求参数**:
```json
{
  "teacherName": "李老师",
  "teacherNo": "T2024002",
  "department": "软件学院",
  "title": "讲师"
}
```

**响应示例**:
```json
{
  "status": 200,
  "message": "操作成功",
  "data": {
    "teacherId": 2,
    "teacherName": "李老师",
    "teacherNo": "T2024002",
    "department": "软件学院",
    "title": "讲师"
  }
}
```

### 5. 更新教师信息

**接口地址**: `PUT /teachers/{id}`

**权限要求**: ADMIN

**路径参数**:
- `id`: 教师ID

**请求参数**: 同添加教师接口

**响应示例**:
```json
{
  "status": 200,
  "message": "操作成功",
  "data": {
    "teacherId": 1,
    "teacherName": "张老师（更新）",
    "teacherNo": "T2024001",
    "department": "计算机学院",
    "title": "教授"
  }
}
```

### 6. 删除教师

**接口地址**: `DELETE /teachers/{id}`

**权限要求**: ADMIN

**路径参数**:
- `id`: 教师ID

**响应示例**:
```json
{
  "status": 200,
  "message": "教师删除成功"
}
```

### 7. 搜索教师

**接口地址**: `GET /teachers/search`

**权限要求**: ADMIN 或 TEACHER

**查询参数**:
- `name`: 教师姓名（支持模糊搜索）

**响应示例**:
```json
{
  "status": 200,
  "message": "操作成功",
  "data": [
    {
      "teacherId": 1,
      "teacherName": "张老师",
      "teacherNo": "T2024001",
      "department": "计算机学院",
      "title": "副教授"
    }
  ]
}
```

### 8. 按部门查询教师

**接口地址**: `GET /teachers/department/{department}`

**权限要求**: ADMIN 或 TEACHER

**路径参数**:
- `department`: 部门名称

**响应示例**:
```json
{
  "status": 200,
  "message": "操作成功",
  "data": [
    {
      "teacherId": 1,
      "teacherName": "张老师",
      "teacherNo": "T2024001",
      "department": "计算机学院",
      "title": "副教授"
    }
  ]
}
```

---

## 💰 缴费管理接口

### 1. 创建缴费记录

**接口地址**: `POST /payments`

**权限要求**: ADMIN

**请求参数**:
```json
{
  "student": {
    "stuId": 1
  },
  "paymentItem": "学费",
  "amount": 5000.00,
  "isCompleted": false
}
```

**智能特性**: 自动验证学生是否存在

**响应示例**:
```json
{
  "status": 200,
  "message": "操作成功",
  "data": {
    "paymentId": 1,
    "student": {
      "stuId": 1,
      "stuName": "张三"
    },
    "paymentItem": "学费",
    "amount": 5000.00,
    "paymentDate": "2024-01-15",
    "isCompleted": false
  }
}
```

### 2. 获取缴费记录列表

**接口地址**: `GET /payments`

**权限要求**: ADMIN 或 TEACHER

**响应示例**:
```json
{
  "status": 200,
  "message": "操作成功",
  "data": [
    {
      "paymentId": 1,
      "student": {
        "stuId": 1,
        "stuName": "张三"
      },
      "paymentItem": "学费",
      "amount": 5000.00,
      "paymentDate": "2024-01-15",
      "isCompleted": false
    }
  ]
}
```

### 3. 分页查询缴费记录

**接口地址**: `GET /payments/page`

**权限要求**: ADMIN 或 TEACHER

**查询参数**:
- `page`: 页码，默认0
- `size`: 每页条数，默认10
- `sortBy`: 排序字段，默认"paymentId"
- `sortDir`: 排序方向，默认"asc"

**响应示例**:
```json
{
  "status": 200,
  "message": "操作成功",
  "data": {
    "content": [
      {
        "paymentId": 1,
        "student": {
          "stuId": 1,
          "stuName": "张三"
        },
        "paymentItem": "学费",
        "amount": 5000.00,
        "paymentDate": "2024-01-15",
        "isCompleted": false
      }
    ],
    "totalPages": 3,
    "totalElements": 25,
    "size": 10,
    "number": 0
  }
}
```

### 4. 根据ID获取缴费记录

**接口地址**: `GET /payments/{id}`

**权限要求**: ADMIN 或 TEACHER

**路径参数**:
- `id`: 缴费记录ID

**响应示例**:
```json
{
  "status": 200,
  "message": "操作成功",
  "data": {
    "paymentId": 1,
    "student": {
      "stuId": 1,
      "stuName": "张三",
      "stuMajor": "计算机科学",
      "grade": 2023
    },
    "paymentItem": "学费",
    "amount": 5000.00,
    "paymentDate": "2024-01-15",
    "isCompleted": false
  }
}
```

### 5. 更新缴费记录

**接口地址**: `PUT /payments/{id}`

**权限要求**: ADMIN

**路径参数**:
- `id`: 缴费记录ID

**请求参数**:
```json
{
  "paymentItem": "学费",
  "amount": 5000.00,
  "paymentDate": "2024-01-15",
  "isCompleted": true
}
```

**响应示例**:
```json
{
  "status": 200,
  "message": "操作成功",
  "data": {
    "paymentId": 1,
    "student": {
      "stuId": 1,
      "stuName": "张三"
    },
    "paymentItem": "学费",
    "amount": 5000.00,
    "paymentDate": "2024-01-15",
    "isCompleted": true
  }
}
```

### 6. 更新缴费状态

**接口地址**: `PATCH /payments/{id}/status`

**权限要求**: ADMIN

**路径参数**:
- `id`: 缴费记录ID

**请求参数**:
```json
{
  "completed": true
}
```

**响应示例**:
```json
{
  "status": 200,
  "message": "操作成功",
  "data": {
    "paymentId": 1,
    "student": {
      "stuId": 1,
      "stuName": "张三"
    },
    "paymentItem": "学费",
    "amount": 5000.00,
    "paymentDate": "2024-01-15",
    "isCompleted": true
  }
}
```

### 7. 删除缴费记录

**接口地址**: `DELETE /payments/{id}`

**权限要求**: ADMIN

**路径参数**:
- `id`: 缴费记录ID

**响应示例**:
```json
{
  "status": 200,
  "message": "缴费记录删除成功"
}
```

### 8. 根据学生查询缴费记录

**接口地址**: `GET /payments/student/{studentId}`

**权限要求**: ADMIN 或 TEACHER

**路径参数**:
- `studentId`: 学生ID

**响应示例**:
```json
{
  "status": 200,
  "message": "操作成功",
  "data": [
    {
      "paymentId": 1,
      "student": {
        "stuId": 1,
        "stuName": "张三"
      },
      "paymentItem": "学费",
      "amount": 5000.00,
      "paymentDate": "2024-01-15",
      "isCompleted": false
    },
    {
      "paymentId": 2,
      "student": {
        "stuId": 1,
        "stuName": "张三"
      },
      "paymentItem": "住宿费",
      "amount": 1200.00,
      "paymentDate": "2024-01-15",
      "isCompleted": true
    }
  ]
}
```

### 9. 根据状态查询缴费记录

**接口地址**: `GET /payments/status/{completed}`

**权限要求**: ADMIN 或 TEACHER

**路径参数**:
- `completed`: 缴费状态（true/false）

**响应示例**:
```json
{
  "status": 200,
  "message": "操作成功",
  "data": [
    {
      "paymentId": 1,
      "student": {
        "stuId": 1,
        "stuName": "张三"
      },
      "paymentItem": "学费",
      "amount": 5000.00,
      "paymentDate": "2024-01-15",
      "isCompleted": false
    }
  ]
}
```

---

## ❌ 错误码说明

### HTTP状态码
- `200`: 操作成功
- `201`: 资源创建成功
- `400`: 请求参数错误
- `401`: 未认证或令牌过期
- `403`: 权限不足
- `404`: 资源不存在
- `500`: 服务器内部错误

### 业务错误码
| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| `USER_NOT_FOUND` | 用户不存在 | 检查用户名是否正确 |
| `INVALID_CREDENTIALS` | 用户名或密码错误 | 检查登录凭据 |
| `USERNAME_EXISTS` | 用户名已存在 | 使用其他用户名 |
| `INSUFFICIENT_PERMISSION` | 权限不足 | 联系管理员分配权限 |
| `STUDENT_NOT_FOUND` | 学生不存在 | 检查学生ID是否正确 |
| `TEACHER_NOT_FOUND` | 教师不存在 | 检查教师ID是否正确 |
| `PAYMENT_NOT_FOUND` | 缴费记录不存在 | 检查缴费记录ID是否正确 |
| `INVALID_FILE_FORMAT` | 文件格式错误 | 使用CSV格式文件 |
| `EMPTY_FILE` | 文件为空 | 上传非空文件 |

### 错误响应示例
```json
{
  "status": 400,
  "message": "请求参数错误",
  "errors": [
    "学生姓名不能为空",
    "电话号码格式不正确"
  ]
}
```

---

## 📝 使用示例

### 1. 完整的登录流程

```bash
# 1. 登录获取令牌
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }'

# 响应
{
  "status": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "username": "admin",
    "role": "ROLE_ADMIN",
    "userId": 1
  }
}
```

### 2. 添加学生（智能创建关联数据）

```bash
# 2. 使用令牌添加学生
curl -X POST http://localhost:8080/students \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -d '{
    "stuName": "张三",
    "stuGender": true,
    "stuMajor": "计算机科学",
    "grade": 2023,
    "stuTel": "13800138000",
    "stuAddress": "北京市海淀区"
  }'

# 响应（自动创建了专业、班级等关联数据）
{
  "status": 200,
  "message": "操作成功",
  "data": {
    "stuId": 1,
    "stuName": "张三",
    "stuGender": true,
    "stuMajor": "计算机科学",
    "grade": 2023,
    "stuTel": "13800138000",
    "stuAddress": "北京市海淀区",
    "stuClass": {
      "subClassId": 1,
      "subClassName": "计科2301",
      "totalClass": {
        "totalClassId": 1,
        "totalClassName": "计算机科学2023级",
        "major": {
          "majorId": 1,
          "majorName": "计算机科学",
          "academy": "默认学院"
        }
      }
    }
  }
}
```

### 3. 创建缴费记录

```bash
# 3. 为学生创建缴费记录
curl -X POST http://localhost:8080/payments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -d '{
    "student": {"stuId": 1},
    "paymentItem": "学费",
    "amount": 5000.00,
    "isCompleted": false
  }'

# 响应
{
  "status": 200,
  "message": "操作成功",
  "data": {
    "paymentId": 1,
    "student": {
      "stuId": 1,
      "stuName": "张三"
    },
    "paymentItem": "学费",
    "amount": 5000.00,
    "paymentDate": "2024-01-15",
    "isCompleted": false
  }
}
```

### 4. 批量导入学生

```bash
# 4. 批量导入学生（需要先准备CSV文件）
curl -X POST http://localhost:8080/students/batch/import \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -F "file=@students.csv"

# CSV文件内容示例：
# 姓名,性别,专业,年级,电话,地址,班级ID
# 张三,true,计算机科学,2023,13800138000,北京市海淀区,1
# 李四,false,软件工程,2023,13900139000,北京市朝阳区,2
```

### 5. 注册教师用户（自动创建Teacher实体）

```bash
# 5. 注册教师用户
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -d '{
    "username": "zhang_teacher",
    "password": "password123",
    "role": "TEACHER"
  }'

# 响应（自动创建了Teacher实体）
{
  "status": 200,
  "message": "操作成功",
  "data": {
    "userId": 2,
    "username": "zhang_teacher",
    "role": "ROLE_TEACHER",
    "teacherId": 1
  }
}
```

---

## 🔧 注意事项

### 1. 令牌管理
- JWT令牌有效期为24小时
- 令牌过期后需要重新登录
- 请妥善保管令牌，不要泄露给他人

### 2. 权限控制
- 不同角色有不同的操作权限
- 教师用户只能查看数据，不能进行增删改操作
- 批量导入功能仅限管理员使用

### 3. 数据验证
- 所有必填字段都需要提供
- 电话号码格式需要正确
- 文件上传仅支持CSV格式

### 4. 智能特性
- 添加学生时会自动创建关联的班级、专业数据
- 注册教师用户时会自动创建Teacher实体
- 创建缴费记录时会自动验证学生是否存在

---

*本文档会随着API的更新持续维护，请关注最新版本。*
