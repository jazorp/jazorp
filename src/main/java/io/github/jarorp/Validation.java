package io.github.jarorp;

public interface Validation {

    boolean isValid();

    Error getError();
}
