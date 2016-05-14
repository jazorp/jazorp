package io.github.jazorp;

@FunctionalInterface
public interface Thunk {

    Validation eval(Env env);
}
