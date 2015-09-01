<%@ page import="java.text.SimpleDateFormat" pageEncoding="utf-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
    <title>默认浏览器客户端IP定位</title>
    <style type="text/css">
        *{
            margin:0px;
            padding:0px;
        }
        body, button, input, select, textarea {
            font: 12px/16px Verdana, Helvetica, Arial, sans-serif;
        }
        p{
            width:603px;
            padding-top:3px;
            overflow:hidden;
        }
    </style>
    <script charset="utf-8" src="http://map.qq.com/api/js?v=2.exp"></script>
    <script>
        var citylocation,map,marker = null;
        var init = function() {

            citylocation = new qq.maps.CityService({
                complete : function(result){
                    console.log(1);
                    console.log(result.detail.latLng);
                    alert(result.detail.latLng)
                    // map.setCenter(result.detail.latLng);
                }
            });
            citylocation.searchLocalCity();
        }
    </script>
</head>
<body onload="init()">
<span style="height:30px;display:none" id="city"></span>
<div style="width:603px;height:300px" id="container"></div>
<p>根据客户端IP定位地图中心位置。</p>
</body>
</html>
