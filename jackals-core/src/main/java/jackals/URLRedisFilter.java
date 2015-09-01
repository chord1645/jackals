package jackals;

import jackals.job.pojo.JobInfo;
import jackals.utils.SpringContextHolder;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 */
public class URLRedisFilter implements URLFilter {
//    @Autowired
    StringRedisTemplate redisTemplate;
    public URLRedisFilter(){
        redisTemplate = SpringContextHolder.getBean(StringRedisTemplate.class);
    }
    @Override
    public boolean exist(JobInfo jobInfo, String url) {
        url = url.replaceAll("\\?.*","");
        return redisTemplate.opsForSet().isMember(getKey(jobInfo.getId()), url);
    }

    public void clean(JobInfo jobInfo) {
        redisTemplate.delete(getKey(jobInfo.getId()));
    }

    @Override
    public void add(JobInfo jobInfo, String url) {
        url = url.replaceAll("\\?.*","");
         redisTemplate.opsForSet().add(getKey(jobInfo.getId()), url);
    }



    public static String getKey(String s) {

        return "url" + s;
    }
}
