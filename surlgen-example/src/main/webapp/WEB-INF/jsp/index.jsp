<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="url" uri="http://de.neofonie.surlgen.uri/" %>
<html>
<body>

<div>Hello World!</div>

<h2>URL's</h2>
<c:forEach items="${urls}" var="url">
    <div><a href="${url}">${url}</a></div>
</c:forEach>
<div><a href="/doWithModel?id=123&caption=1233">http://localhost:8080/doWithModel?id=123&caption=1233</a></div>

<h2>By TLD</h2>
<a href="${url:helloWorldController2GetBooking("fooo")}">${url:helloWorldController2GetBooking("fooo")}</a>

<h2>requestScope</h2>
<c:forEach items="${requestScope}" var="r">
    <div><b>${r.key}</b>: ${r.value}</div>
</c:forEach>

</body>
</html>
