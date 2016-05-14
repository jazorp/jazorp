package io.github.jarorp;

@FunctionalInterface
public interface ErrorFormattingFunc {

    String format(ErrorType error, Env env, Object[] args);
}
