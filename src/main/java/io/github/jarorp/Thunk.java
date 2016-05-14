package io.github.jarorp;

@FunctionalInterface
public interface Thunk {

    Validation eval(Env env);
}
