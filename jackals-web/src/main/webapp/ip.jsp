<%@ page  pageEncoding="utf-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
    <style type="text/css">
        body, html,#allmap {width: 100%;height: 100%;overflow: hidden;margin:0;font-family:"微软雅黑";}
    </style>
    <%--<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=aGN7rgC2f9Gori3QmkON0AOf"></script>--%>
    <title>浏览器定位</title>
</head>
<body>
<%=jackals.web.util.LogUtil.getIpAddr(request)%>
<div id="position"></div>
</body>
</html>
<script type="text/javascript">
    console.info("getCurrentPosition1");
    navigator.geolocation.getCurrentPosition(function(geo){
        geo.coords.latitude  //纬度
        geo.coords.longitude //经度
        geo.coords.accuracy  //经纬度以米为单位的精确度
        //另外还有海拔的信息，在支持陀螺仪的移动设备上，还有正北偏角和对地速度等信息
        document.getElementById("position").innerHTML = geo.coords.longitude +","+  geo.coords.latitude
    })
    console.info("getCurrentPosition2");
</script>
