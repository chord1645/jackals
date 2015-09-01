package jackals.web.controller;

import com.alibaba.fastjson.JSON;
import jackals.model.WordGroup;
import jackals.solr.IndexDao;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;


@Controller
@RequestMapping("/*")
public class TestController {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    //    String key = "jackals_web_subscribe";
    @Autowired
    Properties crawlerConfig;
    @Autowired
    IndexDao indexDao;
    @Autowired
    StringRedisTemplate redisTemplate;

    @RequestMapping("test.do")
    public ModelAndView test(HttpServletRequest request,
                             @RequestParam(required = false) String word,
                             @RequestParam(required = false) String page
    ) {
        String user = request.getAttribute("user") + "";

        if (!StringUtils.isNumeric(page)) {
            page = "1";
        }
//        String query = "title:(" + word.replaceAll("\\s","") + ")";
        String query = buildQueyr(word);
        log.info(query);
        List list = indexDao.sortList(query, Integer.valueOf(page), 30, "infoTime_dt desc");
        request.setAttribute("words", redisTemplate.opsForZSet().range(user, 0, Long.MAX_VALUE));
        request.setAttribute("list", list);
        return new ModelAndView("/index");
    }

    private String buildQueyr(String word) {
        if (StringUtils.isEmpty(word)) {
            return  "*:*";
        }
        return word.trim().replaceAll(" ", " AND title:").replaceAll("^([^ ]+)", "title:$1");
    }

    public static void main(String[] args) {
        System.out.println(new TestController().buildQueyr("训练 阅兵 "));
    }

    @RequestMapping("subscribe.do")
    public ModelAndView subscribe(HttpServletRequest request,
                                  @RequestParam(required = true) String word) throws UnsupportedEncodingException {
        String user = request.getAttribute("user") + "";
        Long size = redisTemplate.opsForZSet().size(user);
        if (size < 20)
            redisTemplate.opsForZSet().add(user, word, size);
        return new ModelAndView("redirect:/test.do?word=" + URLEncoder.encode(word, "utf-8"));
    }

    @RequestMapping("del.do")
    public ModelAndView del(HttpServletRequest request,
                            @RequestParam(required = true) String word) {
        String user = request.getAttribute("user") + "";
        redisTemplate.opsForZSet().remove(user, word);
        return new ModelAndView("redirect:/test.do?word=" + word);
    }

    @RequestMapping("topic.do")
    public ModelAndView topic(HttpServletRequest request) {
        Set<String> set = redisTemplate.opsForZSet().reverseRange("fpgrowth_key", 0, 30);
        List<WordGroup> list = new ArrayList<WordGroup>();
        for (String s : set) {
            list.add(JSON.parseObject(s, WordGroup.class));
        }
        request.setAttribute("list", list);
        return new ModelAndView("/topic");
    }
}
