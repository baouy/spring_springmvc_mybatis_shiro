<%--
  Created by IntelliJ IDEA.
  User: pysasuke
  Date: 2017/8/21
  Time: 15:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>登录成功页面</title>
</head>
欢迎你${user.username } <br/>
<a href="${pageContext.request.contextPath }/shiro/user/logout">退出</a><br/>
<a href="${pageContext.request.contextPath }/shiro/user/student">访问student相关</a><br/>
<a href="${pageContext.request.contextPath }/shiro/user/teacher">访问teacher相关</a>
</body>
</html>
