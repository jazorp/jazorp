package io.github.jazorp;

@FunctionalInterface
public interface ErrorFormattingFunc {

    String format(ErrorType error, Env env, Object[] args);
}
