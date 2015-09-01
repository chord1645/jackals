package jackals.web.filters;

import com.alibaba.fastjson.JSON;
import jackals.web.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


public class CookieInterceptor extends HandlerInterceptorAdapter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {
        Cookie cookie = getCookieByName(request, "chord1645.subscr");
        logger.info("cookie {} {} {} ", LogUtil.getIpAddr(request), JSON.toJSONString(cookie),cookie!=null?cookie.getDomain():null);
        if (cookie == null)
            cookie = addCookie(request,response, "chord1645.subscr", new Date().getTime() + "", 365*24*60*60);
//        if (cookie != null)
//        logger.info("cookie {}",cookie.getValue());
        request.setAttribute("user", cookie.getValue());
        return true;
    }

    /**
     * 设置cookie
     *
     * @param request
     * @param response
     * @param name     cookie名字
     * @param value    cookie值
     * @param maxAge   cookie生命周期  以秒为单位
     */
    public static Cookie addCookie(HttpServletRequest request, HttpServletResponse response, String name, String value, int maxAge) throws MalformedURLException {
        String domain= new URL(request.getRequestURL().toString()).getHost();

        Cookie cookie = new Cookie(name, value);
//        cookie.setPath("/");
        cookie.setDomain(domain);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
        return cookie;
    }

    /**
     * 根据名字获取cookie
     *
     * @param request
     * @param name    cookie名字
     * @return
     */
    public static Cookie getCookieByName(HttpServletRequest request, String name) {
        Map<String, Cookie> cookieMap = cookieMap(request);
        if (cookieMap.containsKey(name)) {
            Cookie cookie = (Cookie) cookieMap.get(name);
            return cookie;
        } else {
            return null;
        }
    }

    /**
     * 将cookie封装到Map里面
     *
     * @param request
     * @return
     */
    private static Map<String, Cookie> cookieMap(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }


}
