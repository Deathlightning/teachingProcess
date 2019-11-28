package xyz.kingsword.course.config;

import org.springframework.cache.Cache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.kingsword.course.dao.BookMapper;
import xyz.kingsword.course.pojo.Book;

import javax.annotation.Resource;
import java.util.List;

/**
 * 缓存初始化
 */
@Configuration
public class EhCacheConfig {
    @Resource
    private org.springframework.cache.CacheManager springCacheManager;
    @Resource
    private net.sf.ehcache.CacheManager ehcacheCacheManager;
    @Resource
    private BookMapper bookMapper;
    @Bean(name = "config")
    public Cache config() {
        return springCacheManager.getCache("config");
    }

    @Bean(name = "book")
    public Cache book() {
        Cache cache = springCacheManager.getCache("book");
        int size = ehcacheCacheManager.getCache("book").getSize();
        if (size == 0) {
            List<Book> bookList = bookMapper.selectAll();
            for (Book book : bookList) {
                cache.put(book.getId(), book);
            }
        }
        return cache;
    }
}
