package org.example.productcatalog.util.specification;

import java.util.function.BiPredicate;
import java.util.function.Function;

public class Filter<T extends Comparable<? super T>> implements BiPredicate<Object, Object> {
    private final Condition condition;
    private final Function<Object, T> left;
    private final Function<Object, T> right;

    public Filter(Function<Object, T> left, Function<Object, T> right, Condition condition) {
        this.left = left;
        this.right = right;
        this.condition = condition;
    }

    @Override
    public boolean test(Object objLeft, Object objRight) {
        int comparison = Condition.valueOf(condition.toString()).ordinal();
        int expression = left.apply(objLeft).compareTo(right.apply(objRight));
        return (expression != 0 ?  expression / Math.abs(expression) : expression)  == --comparison;
    }
}
