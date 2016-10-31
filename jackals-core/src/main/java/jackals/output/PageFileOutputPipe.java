package jackals.output;

import jackals.job.pojo.JobInfo;
import jackals.model.PageObj;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 */
public class PageFileOutputPipe implements OutputPipe {
    Logger logger = LoggerFactory.getLogger(PageFileOutputPipe.class);

    String root = "D:\\work\\crawler\\data\\";

    @Override
    public void save(JobInfo spiderJob, PageObj page, Object obj) {
        File path = new File(root, DigestUtils.md5Hex(page.getRequest().getUrl()));
        try {
            BufferedWriter printWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    getFile(path.getPath() + ".html")), "UTF-8"));
            printWriter.write(page.getRawText());
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
    @Override
    public void error(JobInfo job, PageObj page) {

    }

}
