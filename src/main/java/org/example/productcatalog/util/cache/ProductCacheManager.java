package org.example.productcatalog.util.cache;

import org.example.productcatalog.entity.Product;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ProductCacheManager {
    private static ProductCacheManager INSTANCE;
    private final Map<String, Product> cache;

    private ProductCacheManager() { cache = Collections.synchronizedMap(new HashMap<>()); }

    public static ProductCacheManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ProductCacheManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ProductCacheManager();
                }
            }
        }
        return INSTANCE;
    }

    public void put(String cacheKey, Product value) {
        cache.put(cacheKey, value);
    }

    public Optional<Product> get(String cacheKey) {
        return cache.entrySet().stream().filter(entry -> entry.getKey().equals(cacheKey))
                .map(Map.Entry::getValue).findFirst();
    }

    public void clear(String cacheKey) { cache.put(cacheKey, null); }

    public void clear() { cache.clear(); }
}
