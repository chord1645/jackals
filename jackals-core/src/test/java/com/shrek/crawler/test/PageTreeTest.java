package com.shrek.crawler.test;

import jackals.downloader.HttpDownloader;
import jackals.downloader.ReqCfg;
import jackals.model.PageObj;
import jackals.model.RequestOjb;
import jackals.page.ContentExtrat;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageTreeTest extends BaseTest {
    static int depth = 4;
    static int miniMean = 50;
    static int textLimit = 0;
    HttpDownloader downloader;
    private static Logger logger = LoggerFactory.getLogger(PageTreeTest.class);


    ContentExtrat contentExtrat = new ContentExtrat();

    @Test
    public void contentExtrat() {
        downloader = new HttpDownloader(1);
        String url = "http://money.163.com/15/0831/18/B2C9FS050025335P.html";
//        String url = "http://money.163.com/15/0831/18/B2CAITC300254IU4.html";
        PageObj page = downloader.download(new RequestOjb(url),
                ReqCfg.deft().setTimeOut(10000));
        contentExtrat.setDebug(true);
        System.out.println(contentExtrat.process(page.getRawText(), url));
    }


}
