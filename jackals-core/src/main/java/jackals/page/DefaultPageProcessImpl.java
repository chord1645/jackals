package jackals.page;

import com.alibaba.fastjson.JSONObject;
import jackals.downloader.HttpDownloader;
import jackals.downloader.ProxyHttpDownloader;
import jackals.downloader.ReqCfg;
import jackals.job.pojo.JobInfo;
import jackals.job.pojo.Orders;
import jackals.model.PageObj;
import jackals.model.RequestOjb;
import jackals.output.OutputPipe;
import jackals.output.solr.SolrOutputPipe;
import jackals.utils.LinkUtil;
import jackals.utils.SpringContextHolder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 */
public class DefaultPageProcessImpl implements PageProcess {
    Logger logger = LoggerFactory.getLogger(PageProcess.class);

    //    OutputPipe outputPipe = new PageFileOutputPipe<JSONObject>();
    OutputPipe outputPipe;
    HttpDownloader downloader;
    HtmlExtrator extrator;

    public OutputPipe getOutputPipe() {
        return outputPipe;
    }

    public void setOutputPipe(OutputPipe outputPipe) {
        this.outputPipe = outputPipe;
    }

    public DefaultPageProcessImpl(int size) {
        outputPipe = new SolrOutputPipe();
        downloader = SpringContextHolder.getBean(ProxyHttpDownloader.class, size);
//        downloader = new HttpDownloader(size);
        extrator = new HtmlExtratorImpl();
    }

    @Override
    public ArrayList<RequestOjb> process(RequestOjb link, JobInfo job) throws Exception {
        ArrayList<RequestOjb> list = new ArrayList<RequestOjb>();
        logger.info(job.getId() + " >>>>>>>> {}", link);
        PageObj page = downloader.download(new RequestOjb(link.getUrl()),
                ReqCfg.deft().setTimeOut(10000).setJobInfo(job));
//        logger.debug("show html {} {} ", link.getUrl(), page.getRawText());
//        logger.info("rejected {}", page.getRawText().contains("换一张图"));
        Pattern target = Pattern.compile(job.getOrders().getTargetRegx());
        Pattern path = Pattern.compile(job.getOrders().getPathRegx());
        if (page.isSuccess()) {
            if (target.matcher(link.getUrl()).find()) {
                JSONObject jsonObject = (JSONObject) extrator.extrat(page, job.getOrders());
                outputPipe.save(job, page, jsonObject);
            }
            if (isMaxDepth(link, job)) { //到达最大深度,不再提取url
                return new ArrayList<RequestOjb>();
            }
            logger.info("extratLinks {}", link.getUrl());
//        if (link.isSeed() || path.matcher(link.getUrl()).find())
            list = extratLinks(page, link, job.getOrders());
            logger.info("process done {} {} links: {}", job.getId(), list.size(), link.getUrl());
        } else {
            outputPipe.error(job, page);
        }
        return list;
    }

    private boolean isMaxDepth(RequestOjb link, JobInfo job) {
        logger.info("isMaxDepth  {}>={} {}", link.getDepth(), job.getMaxDepth(), link.getDepth() >= job.getMaxDepth());
        return link.getDepth() >= job.getMaxDepth();
    }

    private ArrayList<RequestOjb> extratLinks(PageObj page, RequestOjb refer, Orders job) {
        Pattern path = Pattern.compile(job.getPathRegx());
        Pattern target = Pattern.compile(job.getTargetRegx());
        Set<RequestOjb> out = new HashSet<RequestOjb>();
        Document doc = Jsoup.parse(page.getRawText(), page.getRequest().getUrl());
        if (doc != null) {
            Elements links = doc.getElementsByTag("a");

            for (Element a : links) {
                String href = LinkUtil.clean(a.attr("abs:href"));
//                System.out.println(href+","+path.matcher(href).find()+","+target.matcher(href).find()+"");
                if (!StringUtils.hasText(href))
                    continue;
                if (path.matcher(href).find() || target.matcher(href).find()) {
                    out.add(new RequestOjb(LinkUtil.clean(href), refer.getDepth() + 1));
                }
            }
//            logger.info("pushed__{}[{}]", count, link.getUrl());
        }
//        ArrayList<Request> list = new ArrayList<Request>();
//        for (int i = 1; i <= 3; i++) {
//            list.add(new Request(link.getUrl() + i));
//        }
//        return list;
        return new ArrayList<RequestOjb>(out);
    }
}
