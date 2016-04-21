<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="url" uri="http://de.neofonie.surlgen.uri/" %>
<%--
  ~ The MIT License (MIT)
  ~
  ~ Copyright (c) 2016 Neofonie GmbH
  ~
  ~ Permission is hereby granted, free of charge, to any person obtaining a copy
  ~ of this software and associated documentation files (the "Software"), to deal
  ~ in the Software without restriction, including without limitation the rights
  ~ to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  ~ copies of the Software, and to permit persons to whom the Software is
  ~ furnished to do so, subject to the following conditions:
  ~
  ~ The above copyright notice and this permission notice shall be included in all
  ~ copies or substantial portions of the Software.
  ~
  ~ THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  ~ IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  ~ FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  ~ AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  ~ LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  ~ OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  ~ SOFTWARE.
  --%>

<html>
<body>

<div>Hello World!</div>

<h2>URL's</h2>
<c:forEach items="${urls}" var="url">
    <div><a href="${url}">${url}</a></div>
</c:forEach>

<h2>By TLD</h2>
<a href="${url:helloWorldController2GetBooking("fooo")}">${url:helloWorldController2GetBooking("fooo")}</a>

<h2>requestScope</h2>
<c:forEach items="${requestScope}" var="r">
    <div><b>${r.key}</b>: ${r.value}</div>
</c:forEach>

</body>
</html>
