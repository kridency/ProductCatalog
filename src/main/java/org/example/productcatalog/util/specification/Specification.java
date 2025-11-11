package org.example.productcatalog.util.specification;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public record Specification<T>(Map<String, Optional<?>> criteria)
        implements Function<Collection<T>, Collection<T>> {
    private static final BiFunction<String, Object, Object> getPath = new BiFunction<>() {
        @Override
        public Object apply(String path, Object obj) {
            var ind = path.indexOf('.');
            try {
                var field = obj.getClass().getDeclaredField(ind == -1 ? path : path.substring(0, ind));
                field.setAccessible(true);
                var fieldObj = field.get(obj);
                return ind == -1 ? fieldObj : getPath.apply(path.substring(ind + 1), fieldObj);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    };

    @Override
    public Collection<T> apply(Collection<T> target) {
        return target.stream().filter(value ->
            criteria.size() == criteria.entrySet().stream()
                    .filter(entry -> entry.getValue().isEmpty()
                            || Objects.equals(getPath.apply(entry.getKey(), value), entry.getValue().get()))
                    .count()
        ).toList();
    }
}
