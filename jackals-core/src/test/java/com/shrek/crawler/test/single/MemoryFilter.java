package com.shrek.crawler.test.single;

import jackals.URLFilter;
import jackals.job.pojo.JobInfo;
import jackals.utils.SpringContextHolder;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashSet;
import java.util.Set;

/**
 */
public class MemoryFilter implements URLFilter {
//    @Autowired
//    StringRedisTemplate redisTemplate;
    Set<String> set = new HashSet<>();
    public MemoryFilter(){
    }
    public boolean exist(JobInfo jobInfo, String url) {
        return set.contains(url);
    }

    public void clean(JobInfo jobInfo) {
       set.clear();
    }

    public void add(JobInfo jobInfo, String url) {
        set.add(url);
    }
}
