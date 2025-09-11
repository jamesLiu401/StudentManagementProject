# 学生管理系统 Java 开发文档

## 📋 目录
- [项目概述](#项目概述)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [数据库设计](#数据库设计)
- [API接口文档](#api接口文档)
- [核心功能实现](#核心功能实现)
- [安全认证](#安全认证)
- [部署指南](#部署指南)
- [开发规范](#开发规范)

---

## 🎯 项目概述

学生管理系统是一个基于Spring Boot框架开发的Web应用，旨在提供便捷的学生信息管理、课程管理、成绩管理和缴费管理功能。系统采用RESTful风格API设计，支持管理员和教师两种角色，通过JWT令牌进行身份验证和授权。

### 主要功能
- **用户认证与授权**：JWT令牌认证，角色权限控制
- **学生管理**：学生信息的增删改查，批量导入
- **教师管理**：教师信息管理，用户关联
- **缴费管理**：学生缴费记录管理
- **智能数据关联**：自动创建和维护关联数据

---

## 🛠 技术栈

### 后端技术
- **Java 17+**
- **Spring Boot 3.5.5**
- **Spring Security** - 安全框架
- **Spring Data JPA** - 数据访问层
- **PostgreSQL** - 数据库
- **JWT (jjwt)** - 令牌认证
- **Maven** - 项目管理

### 开发工具
- **IDE**: IntelliJ IDEA / Eclipse
- **数据库管理**: pgAdmin
- **API测试**: Postman / curl

---

## 📁 项目结构

```
src/main/java/com/jamesliu/stumanagement/student_management/
├── config/                          # 配置类
│   ├── SecurityConfig.java         # Spring Security配置
│   ├── JwtAuthenticationFilter.java # JWT认证过滤器
│   ├── UserDatabaseConfig.java     # 用户数据库配置
│   └── StuDatabaseConfig.java      # 学生数据库配置
├── controller/                      # 控制器层
│   ├── AuthController.java         # 认证控制器
│   ├── StudentController.java      # 学生管理控制器
│   ├── TeacherController.java      # 教师管理控制器
│   └── PaymentController.java      # 缴费管理控制器
├── Entity/                         # 实体类
│   ├── User/                       # 用户实体
│   ├── Student/                    # 学生相关实体
│   ├── Teacher/                    # 教师相关实体
│   └── Finance/                    # 财务相关实体
├── repository/                     # 数据访问层
│   ├── UserRepo/                   # 用户数据访问
│   ├── StuRepo/                    # 学生数据访问
│   └── TeacherRepo/                # 教师数据访问
├── Service/                        # 业务逻辑层
│   ├── StudentService/             # 学生业务逻辑
│   └── UserService/                # 用户业务逻辑
├── utils/                          # 工具类
│   ├── JwtUtils.java              # JWT工具类
│   └── StripEnc.java              # 加密工具
└── crypto/                         # 加密相关
    └── CredentialTools.java        # 凭据加密工具
```

---

## 🗄 数据库设计

### 核心表结构

#### 1. 用户认证表 (table_user)
```sql
CREATE TABLE table_user (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,        -- ROLE_ADMIN 或 ROLE_TEACHER
    teacher_id INTEGER               -- 关联教师ID，管理员可为null
);
```

#### 2. 学生表 (stu_table)
```sql
CREATE TABLE stu_table (
    stu_id SERIAL PRIMARY KEY,
    stu_name VARCHAR(50) NOT NULL,
    stu_gender BOOLEAN,
    stu_major VARCHAR(50),
    class_id INTEGER,                -- 关联小班ID
    stu_grade INTEGER,
    stu_telephone_no VARCHAR(20),
    stu_address VARCHAR(200)
);
```

#### 3. 教师表 (teacher_table)
```sql
CREATE TABLE teacher_table (
    teacher_id SERIAL PRIMARY KEY,
    teacher_name VARCHAR(50) NOT NULL,
    teacher_no VARCHAR(20) UNIQUE NOT NULL,
    department VARCHAR(50),
    title VARCHAR(20)                -- 职称
);
```

#### 4. 专业表 (major_table)
```sql
CREATE TABLE major_table (
    major_id SERIAL PRIMARY KEY,
    major_name VARCHAR(50) NOT NULL,
    academy VARCHAR(50) NOT NULL,
    grade INTEGER,
    counselor_id INTEGER             -- 关联教师ID（辅导员）
);
```

#### 5. 班级表结构
- **大班表** (total_class_table): 专业下的年级班级
- **小班表** (sub_class_table): 大班下的具体班级
- **缴费表** (payment_table): 学生缴费记录

### 数据库关系图
```
User (认证) ←→ Teacher (教师信息)
                ↓
            Major (专业) ←→ TotalClass (大班) ←→ SubClass (小班) ←→ Student (学生)
                ↓                                                      ↓
            SubjectClass (课程班级)                              Payment (缴费)
```

---

## 🔌 API接口文档

### 基础信息
- **Base URL**: `http://localhost:8080`
- **认证方式**: JWT Bearer Token
- **数据格式**: JSON

### 认证接口

#### 1. 用户登录
```http
POST /auth/login
Content-Type: application/json

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
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "username": "admin",
    "role": "ROLE_ADMIN",
    "userId": 1
  }
}
```

#### 2. 用户注册
```http
POST /auth/register
Authorization: Bearer {token}
Content-Type: application/json

{
  "username": "teacher1",
  "password": "password123",
  "role": "TEACHER"
}
```

**权限要求**: ADMIN 或 TEACHER

### 学生管理接口

#### 1. 添加学生
```http
POST /students
Authorization: Bearer {token}
Content-Type: application/json

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

#### 2. 获取学生列表
```http
GET /students?page=0&size=10&sort=stuName,asc
Authorization: Bearer {token}
```

#### 3. 批量导入学生
```http
POST /students/batch/import
Authorization: Bearer {token}
Content-Type: multipart/form-data

file: students.csv
```

**权限要求**: 仅ADMIN

### 教师管理接口

#### 1. 获取教师列表
```http
GET /teachers
Authorization: Bearer {token}
```

#### 2. 添加教师
```http
POST /teachers
Authorization: Bearer {token}
Content-Type: application/json

{
  "teacherName": "李老师",
  "teacherNo": "T2024001",
  "department": "计算机学院",
  "title": "副教授"
}
```

### 缴费管理接口

#### 1. 创建缴费记录
```http
POST /payments
Authorization: Bearer {token}
Content-Type: application/json

{
  "student": {"stuId": 1},
  "paymentItem": "学费",
  "amount": 5000.00,
  "isCompleted": false
}
```

#### 2. 更新缴费状态
```http
PATCH /payments/{id}/status
Authorization: Bearer {token}
Content-Type: application/json

{
  "completed": true
}
```

---

## ⚙️ 核心功能实现

### 1. 智能数据关联

#### 学生添加时的自动创建逻辑
```java
@Transactional
public Student addStudent(Student student) {
    // 智能创建关联数据
    SubClass subClass = ensureClassExists(student);
    student.setStuClass(subClass);
    
    return studentRepository.save(student);
}

private SubClass ensureClassExists(Student student) {
    // 1. 确保专业存在
    Major major = ensureMajorExists(majorName, grade);
    
    // 2. 确保大班存在
    TotalClass totalClass = ensureTotalClassExists(major, grade);
    
    // 3. 确保小班存在
    return ensureSubClassExists(totalClass, className);
}
```

#### 教师用户注册时的自动关联
```java
// 如果是教师角色，智能创建Teacher实体
if ("TEACHER".equals(requestedRole)) {
    Teacher teacher = createTeacherForUser(user);
    user.setTeacherId(teacher.getTeacherId());
}

private Teacher createTeacherForUser(User user) {
    Teacher teacher = new Teacher();
    teacher.setTeacherNo(generateTeacherNo()); // 自动生成编号
    teacher.setTeacherName(extractTeacherNameFromUsername(user.getUsername()));
    teacher.setDepartment("默认部门");
    teacher.setTitle("讲师");
    return teacherRepository.save(teacher);
}
```

### 2. JWT认证实现

#### JWT工具类
```java
@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    // 生成令牌
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }
    
    // 验证令牌
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
```

#### JWT过滤器
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) {
        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            String username = jwtUtils.getUsernameFromToken(jwt);
            
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                if (jwtUtils.validateToken(jwt, userDetails)) {
                    // 设置认证信息
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
```

### 3. 权限控制

#### 方法级权限注解
```java
@PostMapping("/students")
@PreAuthorize("hasRole('ADMIN')")  // 仅管理员可添加学生
public ResponseMessage<Student> addStudent(@RequestBody Student student) {
    // 实现逻辑
}

@PostMapping("/students/batch/import")
@PreAuthorize("hasRole('ADMIN')")  // 仅管理员可批量导入
public ResponseMessage<List<Student>> batchImportFromCsv(@RequestParam("file") MultipartFile file) {
    // 实现逻辑
}
```

#### 角色权限矩阵
| 操作 | ADMIN | TEACHER |
|------|-------|---------|
| 学生管理 | ✅ 全部权限 | ✅ 查看权限 |
| 教师管理 | ✅ 全部权限 | ✅ 查看权限 |
| 缴费管理 | ✅ 全部权限 | ✅ 查看权限 |
| 用户注册 | ✅ 可注册任何角色 | ❌ 不能注册 |
| 批量导入 | ✅ 允许 | ❌ 禁止 |

---

## 🔐 安全认证

### 1. 密码加密
- 使用BCrypt算法加密用户密码
- 密码不可逆，即使数据库泄露也无法还原

### 2. JWT令牌
- 令牌有效期：24小时
- 签名算法：HMAC-SHA512
- 自动刷新机制

### 3. 数据库连接加密
- 使用AES加密数据库连接信息
- 环境变量存储加密密钥

### 4. 权限验证
- 基于角色的访问控制（RBAC）
- 方法级权限注解
- 请求级权限过滤

---

## 🚀 部署指南

### 1. 环境要求
- Java 17+
- PostgreSQL 12+
- Maven 3.6+

### 2. 数据库配置
```properties
# 用户数据库
spring.datasource.usermanage.url=jdbc:postgresql://localhost:5432/stu_manage_user
spring.datasource.usermanage.username=ENC(加密用户名)
spring.datasource.usermanage.password=ENC(加密密码)

# 学生数据库
spring.datasource.stumanage.url=jdbc:postgresql://localhost:5432/student_management
spring.datasource.stumanage.username=ENC(加密用户名)
spring.datasource.stumanage.password=ENC(加密密码)
```

### 3. JWT配置
```properties
jwt.secret=mySecretKey123456789012345678901234567890123456789012345678901234567890
jwt.expiration=86400000
```

### 4. 环境变量
```bash
export STU_ACCOUNT_DB_ENCRYPT_KEY=your_encryption_key
```

### 5. 启动应用
```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run

# 或打包运行
mvn clean package
java -jar target/student_management-1.0.0.jar
```

---

## 📝 开发规范

### 1. 代码规范
- 使用驼峰命名法
- 类名使用大驼峰，方法名使用小驼峰
- 常量使用全大写，下划线分隔
- 包名全小写，点分隔

### 2. 注释规范
```java
/**
 * 学生管理控制器
 * 提供学生信息的增删改查功能
 * 
 * @author JamesLiu
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/students")
public class StudentController {
    
    /**
     * 添加学生信息
     * 支持智能创建关联的班级、专业数据
     * 
     * @param student 学生信息对象
     * @return 保存成功的学生信息
     * @throws RuntimeException 当数据验证失败时抛出
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseMessage<Student> addStudent(@RequestBody Student student) {
        // 实现逻辑
    }
}
```

### 3. 异常处理
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseMessage<String> handleUserNotFound(UsernameNotFoundException e) {
        return ResponseMessage.error("用户不存在: " + e.getMessage());
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseMessage<String> handleDataIntegrity(DataIntegrityViolationException e) {
        return ResponseMessage.error("数据完整性约束违反");
    }
}
```

### 4. 日志规范
```java
@Slf4j
public class StudentService {
    
    public Student addStudent(Student student) {
        log.info("开始添加学生: {}", student.getStuName());
        
        try {
            Student savedStudent = studentRepository.save(student);
            log.info("学生添加成功, ID: {}", savedStudent.getStuId());
            return savedStudent;
        } catch (Exception e) {
            log.error("学生添加失败: {}", e.getMessage(), e);
            throw new RuntimeException("学生添加失败", e);
        }
    }
}
```

### 5. 测试规范
```java
@SpringBootTest
@Transactional
class StudentServiceTest {
    
    @Autowired
    private StudentService studentService;
    
    @Test
    void testAddStudent() {
        // Given
        Student student = new Student();
        student.setStuName("测试学生");
        
        // When
        Student result = studentService.addStudent(student);
        
        // Then
        assertThat(result.getStuId()).isNotNull();
        assertThat(result.getStuName()).isEqualTo("测试学生");
    }
}
```

---

## 🔧 常见问题

### 1. 数据库连接问题
**问题**: 启动时提示数据库连接失败
**解决**: 检查数据库服务是否启动，连接信息是否正确

### 2. JWT令牌问题
**问题**: 接口返回401未授权
**解决**: 检查请求头格式是否为 `Authorization: Bearer {token}`

### 3. 权限问题
**问题**: 接口返回403权限不足
**解决**: 检查用户角色和接口权限要求是否匹配

### 4. 数据关联问题
**问题**: 添加学生时班级不存在
**解决**: 系统会自动创建关联数据，或手动指定存在的班级ID

---

## 📞 技术支持

- **开发者**: JamesLiu
- **版本**: 1.0.0
- **最后更新**: 2024年1月

---

*本文档会随着项目的发展持续更新，请关注最新版本。*
