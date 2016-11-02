package jackals.downloader;

import jackals.model.PageObj;

/**
 * 对下载后的页面验证正确性
 */
public interface Valid {
    boolean success(PageObj page);
}
