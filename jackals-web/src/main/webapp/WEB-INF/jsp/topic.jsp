<%@ page import="java.text.SimpleDateFormat" pageEncoding="utf-8" %>
<%@ page import="java.util.LinkedHashSet" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.springframework.beans.factory.annotation.Autowired" %>
<%@ page import="org.springframework.data.redis.core.StringRedisTemplate" %>
<%@ page import="jackals.utils.SpringContextHolder" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.alibaba.fastjson.JSON" %>
<%@ page import="jackals.model.WordGroup" %>
<!DOCTYPE html>
<html lang="en" ng-app="offsetapp">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="pierre">

    <title>∑(っ °Д °;)っ</title>

    <!-- Bootstrap core CSS -->
    <link href="//netdna.bootstrapcdn.com/bootswatch/3.0.3/slate/bootstrap.min.css" rel="stylesheet">
    <link href="./style.css" rel="stylesheet">
    <link href="./css/cluster-viz.css" rel="stylesheet">

</head>

<body>

<nav class="navbar navbar-default" role="navigation">
    <h1> ∑(っ °Д °;)っ</h1>
    <!-- /.container-fluid -->
</nav>
<div class="container" style="width: 100%">

    <div ng-view>
        <table>
            <%
                List<WordGroup> list = (List<WordGroup>) request.getAttribute("list");
                for (WordGroup wordGroup : list) {
            %>
            <tr>
                <td><a href="test.do?word=<%=wordGroup.fmt()%>"><%=wordGroup.fmt()%>
                </a></td>
                <td><%=wordGroup.getTimes()%>
                </td>
            </tr>
            <tr>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
            <%
                }
            %>

        </table>
    </div>

</div>
<!-- /.container -->

<!-- Bootstrap core JavaScript
================================================== -->
</body>
</html>
