package jackals.output;

import com.alibaba.fastjson.JSONObject;
import jackals.job.pojo.JobInfo;
import jackals.model.PageObj;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 */
public class OneFileOutputPipe implements OutputPipe {
    Logger logger = LoggerFactory.getLogger(OneFileOutputPipe.class);

    String root = "D:\\Work\\tmp\\jackals\\haodaifu.txt";

    @Override
    public void save(JobInfo spiderJob, PageObj page, Object obj) {
        try {
            JSONObject jsonObject = (JSONObject) obj;
            System.out.println(jsonObject.get("title"));
            BufferedWriter printWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(getFile(root),true), "UTF-8"));
            printWriter.write(jsonObject.get("title").toString()+"\n");
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
