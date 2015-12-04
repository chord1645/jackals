package jackals.lab.shares;

import jackals.downloader.HttpDownloader;
import jackals.downloader.ReqCfg;
import jackals.lab.FileUtil;
import jackals.model.PageObj;
import jackals.model.RequestOjb;
import jackals.utils.BlockExecutorPool;
import org.apache.commons.collections.CollectionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//@Ignore
public class HistoryDownloader {
    static HttpDownloader downloader = new HttpDownloader(10);
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void downloadOne() throws Exception {
        downloadData("600750", 2015, new File(CodeUtil.origFileName("600750")));
    }

    @Test
    public void run() throws Exception {
        BlockExecutorPool executor = new BlockExecutorPool(10);
        String[] txt = FileUtil.read(new File("D:\\tmp\\codes.txt")).split("\n");
        for (final String s : txt) {
            if (CodeUtil.blackList.contains(s))
                continue;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        logger.info("download {}", s);
                        downloadData(s, 2015, new File(CodeUtil.origFileName(s)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    int retry = 3;

    public void downloadData(String code, int year, File output) throws Exception {
        if (output.exists())
            return;
        List<DataDay> codes = new ArrayList<DataDay>();
        for (int y = year; y <= year; y++) {  //循环年
            for (int z = 1; z <= 4; z++) {                //循环季度
                int retryCnt = 0;
                do {
                    String url = "http://money.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/" + code + ".phtml?year=" + y + "&jidu=" + z;
                    List<DataDay> tmp = onePage(url);
                    if (!CollectionUtils.isEmpty(tmp)) {
                        codes.addAll(tmp);
                        break;
                    }
                } while (retryCnt++ > retry);
            }
        }

        Collections.sort(codes, new Comparator<DataDay>() {
            @Override
            public int compare(DataDay o1, DataDay o2) {
                return o1.date.compareTo(o2.date);
            }
        });
        for (DataDay c : codes) {
            FileUtil.write(output, c.toString() + "\n", true);
        }

        if (codes.size() < Shares.checkData)
            throw new LackDataException("");
    }

    //http://money.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/000063.phtml?year=2006&jidu=3
    public static List<DataDay> onePage(String url) {
        List<DataDay> codes = new ArrayList<DataDay>();
        try {
            PageObj page = downloader.download(new RequestOjb(url),
                    ReqCfg.deft().setTimeOut(10000));
            Document doc = Jsoup.parse(page.getRawText());
            Elements elements = doc.select("table#FundHoldSharesTable>tbody>tr");
            elements.remove(0);
            for (Element e : elements) {
                DataDay code = new DataDay();
                code.date = e.child(0).text();//            日期
                code.start = Double.valueOf(e.child(1).text());//            开盘价
                code.highest = Double.valueOf(e.child(2).text());//            最高价
                code.end = Double.valueOf(e.child(3).text());//            收盘价
                code.lowest = Double.valueOf(e.child(4).text());//            最低价
                code.quantity = Double.valueOf(e.child(5).text());//            交易量(股)
                code.money = Double.valueOf(e.child(6).text());//            交易金额(元)
//                logger.info("{}", JSON.toJSONString(code));
                codes.add(code);
            }
        } catch (Throwable e) {
        }

        return codes;

    }

}
