package org.example.productcatalog.util.cache;

import java.util.Optional;

public interface CacheManager<K, V> {
    V put(V value);
    Optional<V> get(K objKey);
    V clear(K objKey);
}
