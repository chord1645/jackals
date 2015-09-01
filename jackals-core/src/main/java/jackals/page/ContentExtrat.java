package jackals.page;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class ContentExtrat {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    boolean debug;

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    //    public void process(List<String> list) {
    class NodeX {
        int level;
        int index;
        double all;
        double txt;
        double tagCount;
        Double score;
        Element e;
        String text;
        String innerHtml;
        String innerText;
        NodeX parent;

        public NodeX(Element e, int level, int index, NodeX parent) {
            this.e = e;
            this.innerHtml = e.html();
            this.innerText = e.html().replaceAll("<.+?>", "").replaceAll("[\\s　 ]", "");
            this.text = e.text();
            this.level = level;
            this.index = index;
            this.all = e.toString().length();
            this.txt = innerText.length();
            this.tagCount = e.getAllElements().size();
            this.score = txt / tagCount;
            this.parent = parent;


        }

//        public NodeX(double all, double text, double tagCount, Element e, int level, int index) {
//            this.all = all;
//            this.txt = text;
//            this.tagCount = tagCount;
//            this.e = e;
//            this.text = e.text();
//            this.score = txt / tagCount;
//            this.level = level;
//            this.index = index;
//        }


        @Override
        public boolean equals(Object obj) {
            NodeX tmp = (NodeX) obj;
            return this.text.equals(tmp.text);
        }
    }

    public String process(String html, String url) {
        Document doc = clean1(html, url);
//        List<Element> nodeList = foo();
        List<NodeX> result = new ArrayList<NodeX>();
        digui(new NodeX(doc, 0, 0, null), null, result);
        Collections.sort(result, new Comparator<NodeX>() {
            @Override
            public int compare(NodeX o1, NodeX o2) {
//                System.out.println((int) (o1.score - o2.score));
                return o2.score.compareTo(o1.score);
            }
        });
        if (debug) {
            int i = 0;
            for (NodeX nodeX : result) {
                System.out.println(i++ + ") " + nodeX.level + "_" + nodeX.index
                        + "   innerText=" + nodeX.innerText.length() + " all=" + nodeX.all + " tagCount=" + nodeX.tagCount + " score=" + nodeX.score + " " +
                        nodeX.e.html().replaceAll("\n", ""));

            }
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>");
//        Element content = digui(doc, doc,result);
//        System.out.println("======================================");
//        System.out.println(pickResult(result).e.text());
        }

        return pickResult(result).e.toString();
//        }
    }

    private NodeX pickResult(List<NodeX> result) {
//        Set<NodeX> set = new LinkedHashSet<NodeX>();
        int maxWords = 0;
        int level = 0;
        NodeX out = null;
        double mid = 0;
        double max = 0;
        double min = 0;
        for (NodeX nodeX : result) {
            max = Math.max(max, nodeX.score);
            min = Math.min(min, nodeX.score);
        }
        mid = (max + min) / 2;
        for (int i = 0; i < 10; i++) {
            NodeX current = result.get(i);
            NodeX parent = result.get(i).parent;
            if (current.score<mid)
                break;
            if (parent.innerText.length() > maxWords) {
                out = parent;
                maxWords = parent.innerText.length();
                level = parent.level;
            }
        }

        return out;
    }

    /**
     * public double getSd() {
     * if (weightList.size() == 1)
     * return 5;
     * double[] arr = new double[weightList.size()];
     * int i = 0;
     * for (Double d : weightList) {
     * arr[i++] = d;
     * }
     * double sd = smile.math.Math.sd(arr);
     * return sd == 0 ? 0.001 : sd;
     * }
     *
     * @param current
     * @param parent
     * @param result
     * @return
     */
    private void digui(NodeX current, NodeX parent, List<NodeX> result) {
        if (CollectionUtils.isEmpty(current.e.children())) {
//            double x = parent.e.toString().length();
//            double y = parent.e.text().length();
//            double z = parent.getAllElements().size();
            if (current.innerText.length() < 1)
                result.add(parent);
            else
                result.add(current);
            return;
        }
        List<NodeX> xlist = new ArrayList<NodeX>();
        int index = 0;
        for (Element e : current.e.children()) {
            double x = e.toString().length();
            double y = e.html().length();
            double z = e.getAllElements().size();

//            double z = e.getElementsByTag("a").size();
//            System.out.println(e.text());
//            System.out.println(e.toString());
//            System.out.println(e.html());
//            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>");
            xlist.add(new NodeX(e, current.level + 1, index++, current));
        }
//        Collections.sort(xlist, new Comparator<NodeX>() {
//            @Override
//            public int compare(NodeX o1, NodeX o2) {
//                return (o2.score - o1.score) > 0 ? 1 : -1;
//            }
//        });
//        for (NodeX nodeX : xlist) {
//            System.out.println("txt=" + nodeX.txt + " all=" + nodeX.all + " tagCount=" + nodeX.tagCount + " score=" + nodeX.score + " " +
//                    nodeX.e.toString());
//            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>");
//        }

//        System.out.println("======================================");
        for (NodeX nodeX : xlist) {
            digui(nodeX, current, result);
        }
    }


    private static Document clean1(String html, String url) {
        html = html.replaceAll("(?is)<!--.*?-->", "");
        html = html.replaceAll("<a.+?>.+?</a>", "");
        Document doc = Jsoup.parse(html, url);
        doc.select("script").remove();
        doc.select("title").remove();
        doc.select("meta").remove();
        doc.select("head").remove();
        doc.select("link").remove();
        doc.select("style").remove();
        doc.select("form").remove();
        doc.select("input").remove();
        doc.getElementsByAttributeValue("style", "display:none; width:0px; height:0px; overflow:hidden;").remove();
        return doc;
    }
}
