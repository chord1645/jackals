package com.shrek.crawler.test.redis;

import com.google.common.collect.ImmutableList;
import jackals.filter.URLRedisFilter;
import jackals.job.pojo.JobInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

//@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/jar/config/test/spring/test.xml"})
public class BaseTest {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    public void initData() throws InterruptedException {

        final URLRedisFilter urlRedisFilter = new URLRedisFilter();
        final JobInfo jobInfo = new JobInfo("news.163.com");
        for (int i = 0; i <10 ; i++) {
            new Thread(){
                @Override
                public void run() {
                    long s = System.currentTimeMillis();
                    urlRedisFilter.add(jobInfo, "http://www.douyutv.com/directory/game/FTG");
                    System.out.println(System.currentTimeMillis() - s);
                    System.out.println(redisTemplate.opsForSet().size("urlnews.163.com")
                    );
                }
            }.start();
        }
        TimeUnit.SECONDS.sleep(100000);

    }
    @Test
    public void diff() throws InterruptedException {
        redisTemplate.delete("test_diff");
        redisTemplate.opsForSet().add("test_diff","1");
        System.out.println(redisTemplate.opsForSet().union("test_diff", ImmutableList.of("2","3")));

    }

}
