spring-springmvc
===
## 项目介绍
在之前的mybatis整合项目之后，新增日志、简单集成shiro，之前的代码不予展示与介绍，想了解的请参考mybatis整合项目

## 项目结构
### main
- controller:控制层，ShiroUserController，主要包含登录及几个页面跳转
```
   @RequestMapping("/login")
    public String login(ShiroUser shiroUser, HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(shiroUser.getUsername(), shiroUser.getPassword());
        try {
            subject.login(token);//会跳到我们自定义的realm中
            request.getSession().setAttribute("user", shiroUser);
            log.info(shiroUser.getUsername() + "登录");
            return "success";
        } catch (UnknownAccountException e) {
            request.getSession().setAttribute("user", shiroUser);
            return "login";
        } catch (IncorrectCredentialsException e) {
            request.getSession().setAttribute("user", shiroUser);
            request.setAttribute("error", "用户名或密码错误！");
            return "login";
        }
    }
```    
- service:业务处理层，包含一个impl包，Service以接口类型存在，impl包下存放Service接口的实现类,ShiroUserServiceImpl包含用户、角色、权限相关操作
 ```
 @Service("shiroUserService")
public class ShiroUserServiceImpl implements ShiroUserService {
    @Resource
    private ShiroUserMapper shiroUserMapper;

    public ShiroUser getByUsername(String username) {
        return shiroUserMapper.getByUsername(username);
    }

    public Set<String> getRoles(String username) {
        return shiroUserMapper.getRoles(username);
    }

    public Set<String> getPermissions(String username) {
        return shiroUserMapper.getPermissions(username);
    }
}
 ```
- dao:数据库交互层
- model:实体对象层
- realm: 自定义Realm(shiro相关)
```
public class MyRealm extends AuthorizingRealm {

    @Resource
    private ShiroUserService shiroUserService;

    // 为当前登陆成功的用户授予权限和角色，已经登陆成功了
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(
            PrincipalCollection principals) {

        String username = (String) principals.getPrimaryPrincipal(); //获取用户名
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setRoles(shiroUserService.getRoles(username));
        authorizationInfo.setStringPermissions(shiroUserService.getPermissions(username));
        return authorizationInfo;
    }

    // 验证当前登录的用户，获取认证信息
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal(); // 获取用户名
        ShiroUser shiroUser = shiroUserService.getByUsername(username);
        if (shiroUser != null) {
            AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(shiroUser.getUsername(), shiroUser.getPassword(), "myRealm");
            return authcInfo;
        } else {
            return null;
        }
    }
}
```

### resources
- application.xml:spring配置文件入口，加载spring-config.xml
- spring-mvc.xml:springmvc配置相关文件
- spring-config.xml:加载其他集成的配置文件，这里加载spring-mybatis.xml、spring-shiro.xml和db.properties
- spring-mybatis.xml：mybatis相关配置文件
- spring-shiro.xml:shiro配置相关文件
```
   <!-- 自定义Realm -->
    <bean id="myRealm" class="com.py.realm.MyRealm"/>

    <!-- 安全管理器 -->
    <bean id="securityManager" class="org.apache.shiro.web.mgt.DefaultWebSecurityManager">
        <property name="realm" ref="myRealm"/>
    </bean>

    <!--自定义退出路径-->
    <bean id="logout1" class="org.apache.shiro.web.filter.authc.LogoutFilter">
        <property name="redirectUrl" value="/shiro/user/index"/>
    </bean>

    <!-- Shiro过滤器 -->
    <bean id="shiroFilter" class="org.apache.shiro.spring.web.ShiroFilterFactoryBean">
        <!-- Shiro的核心安全接口,这个属性是必须的 -->
        <property name="securityManager" ref="securityManager"/>
        <!-- 身份认证失败，则跳转到登录页面的配置 -->
        <property name="loginUrl" value="/shiro/user/login"/>
        <!-- 权限认证失败，则跳转到指定页面 -->
        <property name="unauthorizedUrl" value="/shiro/user/unauthorized"/>
        <!-- Shiro连接约束配置,即过滤链的定义 -->
        <property name="filterChainDefinitions">
            <value>
                /shiro/user/logout = logout <!--与操作指令key(logout)对应-->
                /shiro/user/login=anon  <!--登录不拦截-->
                /shiro/user/person*=authc  <!--表示需认证才能使用-->
                <!--注意URL Pattern里用到的是两颗星,这样才能实现任意层次的全匹配-->
                /shiro/user/student*/**=roles[student]  <!--访问需要student角色-->
                <!--多参时必须加上引号,且参数之间用逗号分割-->
                /shiro/user/teacher*/**=perms["user:create"] <!--访问需要user:create权限-->
            </value>
        </property>
        <property name="filters">
            <map>
                <entry key="logout" value-ref="logout1"/> <!--操作指令(logout)与过滤器(LogoutFilter拦截器id)对应-->
            </map>
        </property>
    </bean>

    <!-- 保证实现了Shiro内部lifecycle函数的bean执行 -->
    <bean id="lifecycleBeanPostProcessor" class="org.apache.shiro.spring.LifecycleBeanPostProcessor"/>

    <!-- 开启Shiro注解 -->
    <bean class="org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator"
          depends-on="lifecycleBeanPostProcessor"/>
    <bean class="org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor">
        <property name="securityManager" ref="securityManager"/>
    </bean>
```
- db.properties：数据库相关参数
- log4j.properties：日志相关配置
```
###Log4j建议只使用四个级别，优先级从高到低分别是ERROR、WARN、INFO、DEBUG
log4j.rootLogger=info, console, log, error

###Console ###
#输出到控制台
log4j.appender.console = org.apache.log4j.ConsoleAppender
log4j.appender.console.Target = System.out
log4j.appender.console.layout = org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern = %d %p[%C:%L]- %m%n

### log ###
#输出到文件
log4j.appender.log = org.apache.log4j.DailyRollingFileAppender
#日志编码设置
log4j.appender.log.Encoding=UTF-8
#文件路径(绝对路径)
log4j.appender.log.File = E:/my_project/spring-springmvc-mybatis/logs/log.log
#true为追加,false为覆盖，默认为true
log4j.appender.log.Append = true

#针对DEBUG级别以上的日志,低于DEBUG级别的日志不显示，这里设置为DEBUG没有意义
log4j.appender.log.Threshold = DEBUG
log4j.appender.log.DatePattern='.'yyyy-MM-dd

#指定布局模式
log4j.appender.log.layout = org.apache.log4j.PatternLayout
log4j.appender.log.layout.ConversionPattern = %d %p[%c:%L] - %m%n


### Error ###
log4j.appender.error = org.apache.log4j.DailyRollingFileAppender
log4j.appender.error.File = E:/my_project/spring-springmvc-mybatis/logs/error.log
log4j.appender.error.Append = true
log4j.appender.error.Threshold = ERROR 
log4j.appender.error.DatePattern='.'yyyy-MM-dd
log4j.appender.error.layout = org.apache.log4j.PatternLayout
log4j.appender.error.layout.ConversionPattern =%d %p[%c:%L] - %m%n

###控制台打印sql配置
log4j.logger.com.ibatis=DEBUG
log4j.logger.com.ibatis.common.jdbc.SimpleDataSource=DEBUG
log4j.logger.com.ibatis.common.jdbc.ScriptRunner=DEBUG
log4j.logger.com.ibatis.sqlmap.engine.impl.SqlMapClientDelegate=DEBUG
log4j.logger.java.sql.Connection=DEBUG
log4j.logger.java.sql.Statement=DEBUG
log4j.logger.java.sql.PreparedStatement=DEBUG
```
- mapping:存放mybatis映射文件，以UserMapper.xml为例
```
<!--与dao中的接口类对应-->
<mapper namespace="com.py.dao.UserMapper">

    <select id="getById" resultType="com.py.model.User">
        select id,username,password,email from user where id=#{id,jdbcType=BIGINT}
    </select>

</mapper>
```
### webapp
- web.xml
```
 <!-- shiro过滤器定义 -->
    <filter>
        <filter-name>shiroFilter</filter-name>
        <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
        <init-param>
            <!-- 该值缺省为false,表示生命周期由SpringApplicationContext管理,设置为true则表示由ServletContainer管理 -->
            <param-name>targetFilterLifecycle</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>shiroFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
 ```
