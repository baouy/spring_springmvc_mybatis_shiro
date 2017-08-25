<%--
  Created by IntelliJ IDEA.
  User: pysasuke
  Date: 2017/8/21
  Time: 15:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>权限不足跳转页面</title>
</head>
<body>
认证未通过，或者权限不足<br/>
<a href="${pageContext.request.contextPath }/shiro/user/logout">退出</a>
</body>
</html>
