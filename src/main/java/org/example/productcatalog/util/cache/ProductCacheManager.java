package org.example.productcatalog.util.cache;

import org.example.productcatalog.entity.Product;

import java.time.Instant;
import java.util.*;

public class ProductCacheManager extends AbstractCacheManager<String, Product> {
    private static final ProductCacheManager INSTANCE = new ProductCacheManager();


    private ProductCacheManager() {}

    public static ProductCacheManager getInstance() {
        return INSTANCE;
    }

    @Override
    public Product put(Product value) {
        get(value.getItem()).ifPresent(x -> clear(x.getItem()));
        cache.put(new AbstractMap.SimpleEntry<>(Instant.now(), value::getItem), value);
        return value;
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
