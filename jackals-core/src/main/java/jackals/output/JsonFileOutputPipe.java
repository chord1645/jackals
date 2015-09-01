package jackals.output;

import jackals.job.pojo.JobInfo;
import jackals.model.PageObj;
import jackals.utils.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 */
public class JsonFileOutputPipe implements OutputPipe {
    Logger logger = LoggerFactory.getLogger(JsonFileOutputPipe.class);

    String root = "D:\\work\\crawler\\data\\";
    public static void main(String[] args){
        System.out.println(new File("http://www.cnblogs.com/zhuyaguang/archive/2015/08/04.html").getName());
    }
    public JsonFileOutputPipe() {
        Properties properties = SpringContextHolder.getBean("jackalsConfig");
        root = properties.getProperty("output.file");
    }

    @Override
    public void save(JobInfo spiderJob, PageObj page, Object obj) {
        try {
//            JSONObject jsonObject = (JSONObject) obj;
            File path = new File(root + spiderJob.getId(),new File(page.getRequest().getUrl()).getName());
//        File path = new File(root + spiderJob.getId(), (String) jsonObject.get("title"));

            BufferedWriter printWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    getFile(path.getPath() + ".json")), "UTF-8"));
            printWriter.write(obj.toString());
            printWriter.close();
        } catch (IOException e) {
            logger.warn("write file error", e);
        }
    }

    public File getFile(String fullName) {
        checkAndMakeParentDirecotry(fullName);
        return new File(fullName);
    }

    public void checkAndMakeParentDirecotry(String fullName) {
        File file = new File(fullName).getParentFile();
        if (!file.exists()) {
            file.mkdirs();
        }
    }


}
