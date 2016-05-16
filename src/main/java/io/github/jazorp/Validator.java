package io.github.jazorp;

@FunctionalInterface
public interface Validator<T> {

    Aggregation collect(T t);

    default Result validate(T t) {
        return validate(t, Env.empty());
    }

    default Result validate(T t, Env env) {
        return collect(t).validate(env);
    }

    default Aggregation aggregate(ValidationThunk... thunks) {
        return Aggregation.of(thunks);
    }

}
