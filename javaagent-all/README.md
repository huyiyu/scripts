# 通过premain 和 agentMain 动态替换类
> 开发人员方便测试人员测试环境不校验某些条件产生的工具,通过javaagent机制动态替换字节码,应用人员开发时只需将想替换的包copy到
`javaagent-core` ***并对想替换的java类打上@Replace 注解***
## 项目结构说明
```bash
├── README.md  # 文档说明
├── javaagent-core # 核心项目
│   ├── pom.xml
│   └── src
│       ├── main
│       │   └── java
│       │       └── com
│       │           └── huyiyu
│       │               ├── Replace.java # 需要替换的代码带上此注解 定义注解是为了agentLib不扫描到应用的代码
│       │               ├── agent
│       │               │   └── PreMainReplaceClassAgent.java # 核心文件
│       │               └── worktest
│       │                   └── service
│       │                       └── TestService.java # 示例替换文件
│       └── test
│           └── java
│               └── Main.java # 使用agent模式时运行,不兼容 OPENJDK
├── pom.xml    # 配置打包建议不修改
└── spring-boot-demo # 示例项目 
```
## 开始使用

```bash
# 1. 下载当前项目 
git clone ***.git
# 2. 启动maven构建
mvn package; cd spring-boot-demo/target
# 3. 通过agent 启动spring boot 项目
java -javaagent:../../javaagent-core/target/agentCore.jar -jar springBootDemo.jar 
# 4. 测试preMain 接口返回 {"testField":"12345678"} 有内容生效
curl localhost:8080/preMain

# agentMain 测试
# 5. 正常启动项目
java -jar springBootDemo.jar
# 6. 执行javaagentCore 的test 将agentCore 相关信息 attach 到jvm中
java javaagent-core/target/test-classes/Main
# 返回 {"testField":""}
curl localhost:8080/preMain 
# 返回 {"testField":"12345678"} 生效
curl localhost:8080/agentMain 
```
## 参考 
1. https://blogs.oracle.com/ouchina/post/javaagent
2. https://www.cnblogs.com/rickiyang/p/11368932.html