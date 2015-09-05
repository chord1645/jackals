<%@ page import="java.text.SimpleDateFormat" pageEncoding="utf-8" %>
<%@ page import="java.util.LinkedHashSet" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.joda.time.DateTime" %>
<%@ page import="java.util.Date" %>
<%@ page import="jackals.web.pojo.News" %>
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
    <style>
        .link1 {
            color: #fff !important;
            font-weight: bold;
            font-size: large;
            display: inline-block !important;
            padding-bottom: 5px !important;
            padding-top: 5px !important;
        }

        .link2 {
            color: #d8d8d8 !important;
            font-weight: bold;
            display: inline-block !important;
            padding-bottom: 5px !important;
            padding-top: 5px !important;
            font-size: medium;
        }

        .link3 {
            color: #c3c3c3 !important;
            font-weight: bold;
            display: inline-block !important;
            padding-bottom: 5px !important;
            padding-top: 5px !important;
            padding-left: 5px !important;
            padding-right: 5px !important;
        }
        .li_1 {
            display: inline !important;
        }
    </style>
</head>

<body>
<nav class="navbar navbar-default" role="navigation">
    <div class="container-fluid">
        <table style="width: 100%">
            <tr>
                <td>
                    <ul class="nav navbar-nav" style="display: inline;">
                        <li class="li_1"><a class="link1" href="test.do">最新</a></li>
                        <%
                            LinkedHashSet<String> words = (LinkedHashSet<String>) request.getAttribute("words");
                            for (String s : words) {
                        %>
                        <li class="li_1">
                            <a class="link1" href="test.do?word=<%=s%>"><%=s%>
                            </a>
                            <a class="link3" href="del.do?word=<%=s%>">×</a>
                        </li>
                        <%
                            }
                        %>


                    </ul>

                </td>
            </tr>
            <tr>
                <td>
                    <form action="subscribe.do">
                        &nbsp;&nbsp;&nbsp;&nbsp;<input name="word" value="">
                        <button type="submit">+</button>
                        <a style="display: inline-block;" href="topic.do">\("▔□▔)/ </a>
                    </form>

                </td>
            </tr>
        </table>

        <!-- /.navbar-collapse -->
    </div>
    <!-- /.container-fluid -->
</nav>
<div class="container" style="width: 100%">

    <div ng-view>
        <table>
            <%
                List<News>  list = (List<News> ) request.getAttribute("list");
                for (News obj : list) {
            %>
            <tr>
                <td><a class="link2" target="_blank" href="<%=obj.getId()%>"><%=obj.getTitle()%>
                </a></td>

            </tr>
            <tr>

                <td>
                    <%=obj.getTime()!=null?new DateTime(obj.getTime()).toString("yyyy-MM-dd HH:mm:ss"):""%>
                    <%--<%=sdf.format(obj.get("infoTime_dt"))%>--%>
                </td>
            </tr>
            <tr>
                <td>
                    <span style="color: #ababab"> <%=obj.getContent()%></span>

                </td>
            </tr>
            <%
                }
            %>
            <tr>
                <td style="text-align:justify"><a class="link1" href="test.do?page=${page-1}">上页</a>
                    &nbsp;
                    &nbsp;
                    &nbsp;
                    &nbsp;
                    <a class="link1" href="test.do?page=${page+1}">下页</a>  </td>
            </tr>
        </table>
    </div>

</div>
</body>
</html>