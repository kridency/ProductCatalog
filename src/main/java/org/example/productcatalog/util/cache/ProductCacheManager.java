package org.example.productcatalog.util.cache;

import org.example.productcatalog.entity.Product;

import java.time.Instant;
import java.util.*;

public class ProductCacheManager extends AbstractCacheManager<String, Product> {
    private static ProductCacheManager INSTANCE;


    private ProductCacheManager() {}

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

    @Override
    public void put(Product value) {
        cache.put(new AbstractMap.SimpleEntry<>(Instant.now(), value::getItem), value);
    }

    @Override
    public Optional<Product> get(String item) {
        return cache.entrySet().stream().filter(entry -> entry.getKey().getValue().get().equals(item))
                .map(Map.Entry::getValue).findFirst();
    }

    @Override
    public Optional<Product> clear(String item) {
        return cache.keySet().stream().filter(key -> key.getValue().get().equals(item)).findFirst().map(cache::remove);
    }
}
