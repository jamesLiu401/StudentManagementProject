小菜鸡一个，不会打包，现在就npm run+ide里跑后端
技术栈：
Spring boot
maven
react
spring JPA
Postsql
jjwt
electron

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
