package jackals.solr;

import java.util.List;
import java.util.Map;

/**
 * 搜索接口
 */
public interface IndexDao {
    public void addIndex(List<Map<String, Object>> list);

    public List<? extends Map> sortList(
            String queryStr, int pageNum, int size, String orderBy);

    public void delete(String q) ;
}
