package io.github.jazorp;

@FunctionalInterface
public interface Validator<T> {

    Aggregation collect(T t);

    default Result validate(T t) {
        return collect(t).validate();
    }

    default Aggregation aggregate(ValidationThunk... thunks) {
        return Aggregation.of(thunks);
    }

}
