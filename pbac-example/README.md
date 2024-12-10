# 分布式场景下落地实践 

## 背景

* **权限管理**：网络安全、数据安全日益受到重视，权限管理组件能够有效防范越权访问、数据泄露等问题
* **多维属性**：集团现有应用开发平台方案不足：（多租户、生态场景支持）无法兼容条线、机构、岗位、职务、资质等多维属性进行资源（菜单、数据等）权限控制。
* **企业级**：行内及公司各系统使用的权限管理模式不一，通过配置、代码等方式各自实现资源权限管理，系统开发、改造、运营成本较大。





## PBAC 实现原理
>分布式场景下,一般会在网关进行鉴权。按照PBAC执行时序图如下:

![pbac-flow](docs/img/pbac.png)
分布式环境中一般由网关承载整体认证鉴权流程,而用户权限数据则作为 `resource-server`的形式为网关提供用户的资源信息，以如下登陆和认证鉴权为例

###  登陆/认证流程
> 微服务架构下,为了解耦程序,避免认证鉴权代码对业务系统的入侵，会在业务网关中统一处理登陆逻辑,使用jwt 在业务系统中传递，jwt 具有不可篡改，无状态，自动过期的特性，保证了认证授权的逻辑高度内聚。

![登陆流程](docs/img/authenciation.png)
>如图所示: 网关只做转发，实际登陆逻辑由认证鉴权服务完成,当确认了用户信息合法之后(用户名密码正确，用户状态合法，图形验证码，短信等),返回带用户属性的 jwt。用户登陆成功

![鉴权流程](docs/img/authorization.png)
> 基于用户登陆成功的基础 网关获取到jwt 校验jwt合法性，获得当前用户信息。并根据访问的资源(uri)获得对应的策略执行链路，以用户，资源，环境信息为变量，通过策略链的执行来判断是否允许访问对应业务系统中的资源。当校验通过之后放行


## 技术实践
> 我们充分考虑了现有技术框架选型,为了充分实现松耦合，我们选择使用webFlux版本的spring-security 作为底座。该项目优点如下：高性能,松耦合,功能完备,符合安全设计,易于扩展。缺点是: reactor 模型复杂,可能出现回调地狱。基于此我们设计改善了整体多维权限的设计。

### spring security 原理
>以图为例子,spring security 本质上利用了web用户自身提供的过滤器链来完善整体的权限设计。通过插入过滤器链代理能得到spring security 自带的过滤器链。如下是spring-security 的过滤器链示意图（使用了servlet 的图，官网没有webflux架构图，处理流程相似）。

![自带过滤器链](docs/img/filterchain.png)

![插入后的过滤器链](docs/img/filterchainproxy.png)

>我们通过spring security 提供的配置类来配置SecurityFilterChain的整体执行逻辑代码如下

```java
@Bean
public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
      PbacProperties pbacProperties, JwtService jwtService, SecurityExector securityExector) {
        // 此处通过配置的方式提供了jwt 的解析方式,将header 上面的 JWT 串
        // 解析成spring security可识别的 Jwt 对象 
    return http.oauth2ResourceServer(oAuth2ResourceServerSpec ->
            oAuth2ResourceServerSpec
                .bearerTokenConverter(serverWebExchange->bareTokenConvert(serverWebExchange,pbacProperties))
                .jwt(jwtSpec -> jwtSpec.jwtDecoder(token -> Mono.just(jwtService.decode(token))))
                
        ).csrf(csrf -> csrf.disable())
        .logout(logout -> logout.disable())
        .cors(cors -> cors.disable())
        .httpBasic(httpBasic -> httpBasic.disable())
        // 此处配置了permitAll 请求范围
        // 以及通用的鉴权管理器 security 来识别策略
        .authorizeExchange(authorizeExchangeSpec ->
            authorizeExchangeSpec
                .pathMatchers(pbacProperties.getPermitAllPattern()).permitAll()
                .anyExchange()
                .access(securityExector)
        // 此处配置了异常处理
        // 登陆失败处理
        // 无权限访问处理
        ).exceptionHandling(exceptionHandlingSpec ->
            exceptionHandlingSpec.accessDeniedHandler(this::accessDenied)
                .authenticationEntryPoint(this::onLoginFailure)
        )
        .build();
}
```
### 基于策略的权限控制

#### 数据库表分析
![pbac-engine](docs/img/pbac_engine.png) 

- **resource**: 资源表每个资源对应系统的一个uri (http接口),每个资源绑定一个执行策略,因为资源之间访问策略大部分是相似的可以分组,而对于资源的访问方式最好是规划到不同的策略组里

- **policy**: 策略是一组规则的集合,一组规则之间通过排列成有序的顺序,通过运行计算最终得到一个确定的结果（通过或不通过）

- **rule**: 规则是策略的具体表现形式,通过自身条件确定自己是充分的或者是必要的规则,策略和规则是多对多的关系,如两个不同的策略可以都和时间维度的规则相关,取决于不同的访问时间,以及该规则是否是充分的需要有 **policy_rule作为中间表存储**
- **account**: 用户表

- **account-role**: 用户角色表 pbac 并非完全抛弃rbac,而是可以以配置的形式配置出pbac的执行逻辑
- **role**: 角色表
- **role-resource**: 角色资源表

***注： account-role,role,role-resource 表不一定要存在,为适配原系统rbac可以引入,pbac讲究的是对于任意与account相关的业务如 用户岗位,或者无关的如时间节点,地点节点,都可以作为权限设计的维度。***

#### 执行过程
> 上文提到基于securityExecutor 对需要鉴权的资源(uri)进行分析,核心代码变转移到如何实现SecurityExecutor中了,securityExecutor 通过从缓存或认证鉴权服务中获得资源对应的策略链进行执行,从而决定当前用户是否有访问对应资源的权限,示例过程遵守一下规则

![策略链](docs/img/policychain.png)

1. 如果某个规则通过且是充分条件,则跳出规则链允许访问资源
2. 如果某个规则不通过且是必要条件,则跳出规则链拒绝访问资源
3. 如果不符合上述两点记录结果和前结果的与操作并进入下一规则
4. 重复上述过程直至流程结束
5. 如果走到最后拦截器仍然无法判断,则取记录结果（即要全部通过才算通过,有一个不通过为拒绝访问）

#### 执行逻辑
```java
public Mono<AuthorizationDecision> check(Mono<Authentication> authentication,
AuthorizationContext authorizationContext) {
    ServerWebExchange exchange = authorizationContext.getExchange();
    // 将jwt转化为用户信息,并传递要给ruleChain Factory判断
    return authentication.filter(Authentication::isAuthenticated)
        .map(Authentication::getPrincipal)
        .cast(Jwt.class)
        .map(jwtService::jwt2PbacUser)
        // 由规则链工厂出发规则链执行
        .flatMap(pbacUser -> ruleChainFactory.decide(uriReactiveExecutorPoint, authorizationContext, pbacUser))
        .map(AuthorizationDecision::new);
}

// 异步转同步
public <T> Mono<Boolean> decide(ReactiveExecutorPoint<T> executorPoint, T pattern,
      PbacUser pbacUser) {
    return executorPoint.getPolicyRuleParam(pattern, pbacUser)
        .map(pbacRuleResult -> executor(pbacRuleResult, pattern, pbacUser));

  }

private <T> boolean executor(PbacRuleResult pbacRuleResult, Object pattern, PbacUser pbacUser) {
    RuleChain ruleChain = createRuleChain(pbacRuleResult.getPolicyRuleParams());
    // 构造context
    PbacContext pbacContext = PbacContext.builder()
        .pattern(pattern)
        .pbacUser(pbacUser)
        .resourceId(pbacRuleResult.getResourceId())
        .policyId(pbacRuleResult.getPolicyId())
        .result(false)
        .build();
        // 触发责任链内部迭代
    ruleChain.executeRule(pbacContext);
    return pbacContext.getResult();
}
// 规则链迭代
public void executeRule(PbacContext ruleContext) {
    internalExecuteRule(ruleContext);
}
private void internalExecuteRule(PbacContext ruleContext) {
    // 获取规则链迭代的指针,确定是否往后走
    if (pos < policyRuleParams.size()) {
      PbacPolicyRule policyRuleParam = policyRuleParams.get(pos++);
      IPbacRule pbacRule = StringUtils.isNotBlank(policyRuleParam.getScripts()) ?
          new GrooovyRule(policyRuleParam.getScripts())
          : factory.get(policyRuleParam.getHandlerName());
      Assert.notNull(pbacRule, "规则名称不存在");
      ruleContext.setCurrentRuleConditionType(policyRuleParam.getConditionType());
      ruleContext.setCurrentRule(pbacRule);
      // 触发链里面的规则执行
      pbacRule.executeRule(ruleContext, this, policyRuleParam.getValue());
    }
}

default void executeRule(PbacContext pbacContext, SimpleRuleChain ruleChain, String configuration) {
    boolean decide = decide(pbacContext,configuration);
    // 结果需要与上一结果与操作
    pbacContext.setResult(pbacContext.getResult() && decide);
    // 如果规则满足且连接是AND说明要接着判断(必要条件为true)
    // 同理规则不满足是OR,则说明要接着判断(充分条件为false)
    boolean needNext =
        (decide && pbacContext.getCurrentRuleConditionType().equals(ConditionType.AND))
            || (!decide && pbacContext.getCurrentRuleConditionType().equals(ConditionType.OR));
    if (needNext) {
      ruleChain.executeRule(pbacContext);
    }
}
```

#### 示例规则

```java
/**
 * 基于开始时间结束时间的规则,该信息从数据库policyRule中获得
 */
public class DailyStartEndRule extends AbstractGenericRule<DailyParam> {

  @Override
  public boolean decideWithType(PbacContext ruleContext, DailyParam configuration) {
    LocalTime now = LocalTime.now();
    // 当前时间必须在配置的开始时间和结束时间中间
    return  Objects.nonNull(configuration)
        && now.isAfter(configuration.getStartTime())
        && now.isBefore(configuration.getEndTime());
  }

  @Data
  public static final class DailyParam {
    private LocalTime startTime;
    private LocalTime endTime;
  }
```
## 案例实践
```bash 
# 1. 启动docker-compose.yml 要求的中间件mysql,redis,nacos
docker-compose up -d
# 2. 初始化数据库,初始化nacos
cat docs/sql/pbac-engine.sql |docker exec -i pbac_db mysql -uroot -proot
cat docs/sql/pbac-biz.sql |docker exec -i pbac_db mysql -uroot -proot
# 3. 初始化nacos 密码(必要，)
curl 'http://localhost:8848/nacos/v1/auth/users/admin' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  --data-raw 'password=nacos'
# 4. 启动所有应用
nohup ./gradlew pbac-gateway:bootRun > pbac_gateway.log 2>&1 &
nohup ./gradlew pbac-engine:bootRun > pbac_engine.log 2>&1 &
nohup ./gradlew pbac-biz:bootRun > pbac_biz.log 2>&1 &
# 5.测试
# 5.1 测试登陆 mario, 账号,需要等待服务发现成功后请求,大概需要一分钟时间
+ curl -X POST localhost/pbac-engine/account/login -d "username=mario&password=123456"
{"data":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtYXJpbyIsImFjY291bnRJZCI6IjEiLCJhdWQiOiJ3ZWIiLCJpc3MiOiJodHRwOi8vcGJhYy5odXlpeXUuY29tIiwicm9sZUNvZGVzIjpbImN1c3RvbWVyIl0sImV4cCI6MTczMzgyMzc3MiwiaWF0IjoxNzMzODIwMTcyLCJqdGkiOiIxIiwidXNlcm5hbWUiOiJtYXJpbyJ9.VElwrB2S2jdqVpdcbDjum1GyjtGwIXs6Fs888XfzGLdSk57GlrnhoF2PywPcBcja5HvMv2dG25s-lEpP1VpKjw","msg":"success","code":0}
+ curl -X POST localhost/pbac-engine/account/login -d "username=bowser&password=123456"
{"data":"eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJib3dzZXIiLCJhY2NvdW50SWQiOiI1IiwiYXVkIjoid2ViIiwiaXNzIjoiaHR0cDovL3BiYWMuaHV5aXl1LmNvbSIsInJvbGVDb2RlcyI6WyJjdXN0b21lciJdLCJleHAiOjE3MzM4MjM4MDQsImlhdCI6MTczMzgyMDIwNCwianRpIjoiNSIsInVzZXJuYW1lIjoiYm93c2VyIn0.i968zfw7oC5tRUv5cjs-vfMbs-kw1rGoBZVuhS9NfI1QhtGBAl2SqPkmbjX80ZCkOFg92PflcJoe_6GA0rMcFA","msg":"success","code":0}
```
```sql 
-- 5.2 通过查询可知,访问billPage需要满足三个维度 1. 时间在9:00 -18:00 工作时间 2. 身份为customer 3 拥有对应的角色
select resource.name                                                       as '资源名称',
       policy.name                                                         as '策略名称',
       rule.name                                                           as '规则名称',
       pr.param_value                                                      as '参数',
       pr.pirority                                                         as '排序',
       case (pr.condition_type) when 1 then '必要条件' else '充分条件' end as 条件
from resource
         join policy on resource.policy_id = policy.id
         join policy_rule pr on policy.id = pr.policy_id
         join rule on pr.rule_id = rule.id
where pattern = '/bill/page'
order by pirority asc
-- 5.3 通过下列sql 查询 在规定时间内只有bowser 有权限访问 mario 没权限访问
select account.username
from account
left join account_role ar on account.id = ar.account_id
left join role_resource rr on ar.role_id=rr.role_id
left join resource on rr.resource_id = resource.id
join pbac_biz.customer pbc  on account.id = pbc.account_id
where resource.pattern='/bill/page'
```
```bash
# 5.4 分别使用5.1 获得的 mario 和 bowser 访问 /bill/page接口 可以得到 mario 没有访问权限
curl -X POST localhost/pbac-biz//bill/page -H 'JWT:eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtYXJpbyIsImFjY291bnRJZCI6IjEiLCJhdWQiOiJ3ZWIiLCJpc3MiOiJodHRwOi8vcGJhYy5odXlpeXUuY29tIiwicm9sZUNvZGVzIjpbImN1c3RvbWVyIl0sImV4cCI6MTczMzgyMzc3MiwiaWF0IjoxNzMzODIwMTcyLCJqdGkiOiIxIiwidXNlcm5hbWUiOiJtYXJpbyJ9.VElwrB2S2jdqVpdcbDjum1GyjtGwIXs6Fs888XfzGLdSk57GlrnhoF2PywPcBcja5HvMv2dG25s-lEpP1VpKjw'
{"msg":"Access Denied","code":-1}

curl localhost/pbac-biz/bill/page -H "JWT:eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJib3dzZXIiLCJhY2NvdW50SWQiOiI1IiwiYXVkIjoid2ViIiwiaXNzIjoiaHR0cDovL3BiYWMuaHV5aXl1LmNvbSIsInJvbGVDb2RlcyI6WyJjdXN0b21lciJdLCJleHAiOjE3MzM4Mjc0ODYsImlhdCI6MTczMzgyMzg4NiwianRpIjoiNSIsInVzZXJuYW1lIjoiYm93c2VyIn0.N59fcWsmLHrFCYYOj0nxuvp7csShBG2Nw4L-rjrG_SnXUSKAhNJImFmIwgj7LVjfxIt9K0rSjsW8ilAyheIvPw"


{"data":{"records":[{"id":1,"descriptions":"万科金域榕郡","price":2500000.00,"firstPrice":750000.00,"salesmanId":1,"customerId":1,"areaId":1,"areaName":"福州市","billStatus":1,"createTime":"2024-08-01T01:33:24","updateTime":"2024-08-01T01:33:24","deletedTime":"1000-01-01T00:00:00"},{"id":2,"descriptions":"世茂云锦","price":3000000.00,"firstPrice":900000.00,"salesmanId":2,"customerId":3,"areaId":2,"areaName":"厦门市","billStatus":2,"createTime":"2024-08-01T01:33:24","updateTime":"2024-08-01T01:33:24","deletedTime":"1000-01-01T00:00:00"},{"id":3,"descriptions":"融信白金湾","price":3200000.00,"firstPrice":960000.00,"salesmanId":3,"customerId":5,"areaId":3,"areaName":"泉州市","billStatus":3,"createTime":"2024-08-01T01:33:24","updateTime":"2024-08-01T01:33:24","deletedTime":"1000-01-01T00:00:00"},{"id":4,"descriptions":"恒大山水城","price":4000000.00,"firstPrice":1200000.00,"salesmanId":4,"customerId":7,"areaId":4,"areaName":"宁德市","billStatus":1,"createTime":"2024-08-01T01:33:24","updateTime":"2024-08-01T01:33:24","deletedTime":"1000-01-01T00:00:00"},{"id":5,"descriptions":"融信江南","price":4200000.00,"firstPrice":1260000.00,"salesmanId":5,"customerId":9,"areaId":5,"areaName":"莆田市","billStatus":2,"createTime":"2024-08-01T01:33:24","updateTime":"2024-08-01T01:33:24","deletedTime":"1000-01-01T00:00:00"},{"id":6,"descriptions":"正荣润城","price":2100000.00,"firstPrice":630000.00,"salesmanId":6,"customerId":11,"areaId":6,"areaName":"龙岩市","billStatus":3,"createTime":"2024-08-01T01:33:24","updateTime":"2024-08-01T01:33:24","deletedTime":"1000-01-01T00:00:00"},{"id":7,"descriptions":"新城璟悦城","price":2200000.00,"firstPrice":660000.00,"salesmanId":7,"customerId":13,"areaId":7,"areaName":"三明市","billStatus":1,"createTime":"2024-08-01T01:33:24","updateTime":"2024-08-01T01:33:24","deletedTime":"1000-01-01T00:00:00"},{"id":8,"descriptions":"万科金域榕郡","price":2300000.00,"firstPrice":690000.00,"salesmanId":8,"customerId":15,"areaId":8,"areaName":"南平市","billStatus":2,"createTime":"2024-08-01T01:33:24","updateTime":"2024-08-01T01:33:24","deletedTime":"1000-01-01T00:00:00"},{"id":9,"descriptions":"世茂云锦","price":2800000.00,"firstPrice":840000.00,"salesmanId":9,"customerId":17,"areaId":9,"areaName":"漳州市","billStatus":3,"createTime":"2024-08-01T01:33:24","updateTime":"2024-08-01T01:33:24","deletedTime":"1000-01-01T00:00:00"},{"id":10,"descriptions":"融信白金湾","price":2600000.00,"firstPrice":780000.00,"salesmanId":10,"customerId":19,"areaId":10,"areaName":"福州市","billStatus":1,"createTime":"2024-08-01T01:33:24","updateTime":"2024-08-01T01:33:24","deletedTime":"1000-01-01T00:00:00"}],"total":21,"size":10,"current":1,"pages":3},"msg":"success","code":0}

# 6. 结束所有应用
jps|grep GradleWrapperMain |awk '{print $1}'|xargs -I {} kill -9 {}
```
## 展望

1. 对于权限设计,当前仅是demo,具体设计应贴合实际业务场景,如一般时间约定会采用 cron表达式进行解析而非简单的`startTime`,`endTime`。
2. 该思路可以扩展到数据权限,此时访问的资源则变成数据库中的表,或者表中的记录，通过限制不同用户对整个表的数据的访问来做到整体数据权限的维度。目前已实现了基于jsqlparser 方案的数据权限的限制

