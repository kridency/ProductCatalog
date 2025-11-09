package org.example.productcatalog.util.specification;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public record Specification<T>(Map<String, ? extends Comparable<?>> criteria)
        implements Function<Collection<T>, Collection<T>> {
    private static final BiFunction<String, Object, Object> getPath = new BiFunction<>() {
        @Override
        public Object apply(String path, Object obj) {
            var ind = path.indexOf('.');
            try {
                var fieldObj = obj.getClass().getDeclaredField(ind == -1 ? path : path.substring(0, ind)).get(obj);
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
                    .filter(entry -> Optional.ofNullable(entry.getValue()).isPresent())
                    .filter(entry -> Objects.equals(Optional.ofNullable(value)
                            .map(x -> getPath.apply(entry.getKey(), x)).orElse(null), entry.getValue()))
                    .count()
        ).toList();
    }
}
