package org.example.productcatalog.util.cache;

import org.example.productcatalog.entity.Product;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

@Component
public class ProductCacheManager implements CacheManager<String, Product> {
    private final Map<Map.Entry<Instant, Supplier<String>>, Product> cache;

    public ProductCacheManager() {
        cache = Collections.synchronizedMap(
                new TreeMap<>(Map.Entry.<Instant, Supplier<String>>comparingByKey(Instant::compareTo).reversed()));
    }

    @Override
    public Product put(Product value) {
        Optional.ofNullable(value).map(Product::getItem).ifPresent(this::clear);
        Optional.ofNullable(value)
                .ifPresent(x -> cache.put(new AbstractMap.SimpleEntry<>(Instant.now(), x::getItem), x));
        return value;
    }

    @Override
    public Optional<Product> get(String item) {
        return cache.entrySet().stream().filter(entry -> entry.getKey().getValue().get().equals(item))
                .map(Map.Entry::getValue).findFirst();
    }

    @Override
    public void clear(String item) {
        cache.keySet().stream().filter(key -> key.getValue().get().equals(item)).findFirst()
                .ifPresent(cache::remove);
    }
}
