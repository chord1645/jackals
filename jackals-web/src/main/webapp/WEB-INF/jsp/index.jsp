<%@ page import="java.text.SimpleDateFormat" pageEncoding="utf-8" %>
<%@ page import="java.util.LinkedHashSet" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="org.joda.time.DateTime" %>
<%@ page import="java.util.Date" %>
<%@ page import="jackals.web.pojo.News" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta charset="UTF-8">
    <meta content="width=device-width,user-scalable=no" name="viewport">
    <meta name="apple-itunes-app" content="app-id=425349261">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <title>∑(っ °Д °;)っ没想好叫啥名字哪！</title>
    <link rel="stylesheet" href="../../css/main.css#1">
    <link rel="stylesheet" href="../../css/touch_css_v2.css">
    <script src="../../js/zepto.js"></script>
</head>
<body>
<header class="index-p31 header-channel">
</header>
<div style="height: 698px; display: none;" id="mask"></div>
<script>
    var mask = document.getElementById('mask');
    mask.style.height = screen.availHeight - 40 + 'px';
    function showInput() {
        if ($("#sinput").css("display") == "none") {
            $("#sinput").show();
//            $("#showSwitch").innerText="-";
        } else {
            $("#sinput").hide();
//            $("#showSwitch").innerText="+";
        }
    }
</script>
<% String word = (String) request.getAttribute("word");%>
<article class="topNews">
    <div class="sBorder"></div>
    <nav class="channel-nav">
        <ul>
            <li page="0" tid="9ARIUJ61yswang" cname="要闻" curl="news">
                <a class="<%=StringUtils.isEmpty(word)?"link1_chos":"link1"%>"
                   href="test.do">最新</a>
                <a id="showSwitch" href="javascript:showInput();">+</a>
            </li>
            <%
                LinkedHashSet<String> words = (LinkedHashSet<String>) request.getAttribute("words");
                for (String s : words) {
            %>
            <li>
                <a href="test.do?word=<%=s%>"><%=s%>
                </a>
                <a href="del.do?word=<%=s%>">×</a>
            </li>
            <%
                }
            %>
        </ul>
    </nav>
    <section id="sinput" style="display:none;padding-left: 3.1%;">
        <form action="subscribe.do">
            <input name="word" value="">
            <button type="submit">+</button>
        </form>
    </section>

    <section style="overflow: hidden;" class="newsList">
        <ul class="newsPage" id="newsListContent">
            <%
                List<News> list = (List<News>) request.getAttribute("list");
                for (News obj : list) {
            %>
            <li class="newsHead">
                <% if (StringUtils.isNotEmpty(obj.getImg())) {%>
                <a href="<%=obj.getId()%>" target="_blank">
                    <img src="<%=obj.getImg()%>"/>
                </a>
                <%}%>
                <a href="<%=obj.getId()%>" target="_blank">
                    <div>
                        <p class="newsTitle">
                            <%=obj.getTitle().get(0)%>
                        </p>

                        <p>
                            <span style="float: left;color: #808080;font-size:12px;margin-top: 2px;">
                                <%=obj.getContent()%>
                            </span>
                            <%if (obj.getSim() > 1) {%>
                            <a style="color: cornflowerblue" href="sim.do?group=<%=obj.getGroup()%>">
                                <%=obj.getSim() > 1 ? "相似新闻" + obj.getSim() + "条" : " "%>
                            </a>
                            <%}%>
                        </p>

                        <span class="newsTips">
                            <%=obj.getTime() != null ? new DateTime(obj.getTime()).toString("yyyy-MM-dd HH:mm:ss") : ""%>
                        </span>

                    </div>
                </a>
            </li>
            <%
                }
            %>
        </ul>

    </section>
    <nav class="page">
        <ul>
            <li>
                <a href="test.do?word=<%=StringUtils.isEmpty(word)?"":word%>&page=${page-1<1?1:page-1}">上页</a>
            </li>
            <li>
                <a href="test.do?word=<%=StringUtils.isEmpty(word)?"":word%>&page=${page+1}">下页</a>
            </li>
        </ul>
    </nav>

</article>

</body>
</html>
