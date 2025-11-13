package org.example.productcatalog.util.cache;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Supplier;

public abstract class AbstractCacheManager<K, V> {
    protected final Map<Map.Entry<Instant, Supplier<K>>, V> cache = Collections.synchronizedMap(
            new TreeMap<>(Map.Entry.<Instant, Supplier<K>>comparingByKey(Instant::compareTo).reversed())
    );

    public abstract V put(V value);
    public abstract Optional<V> get(K objKey);
    public abstract Optional<V> clear(K objKey);
}
