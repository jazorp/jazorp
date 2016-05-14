package io.github.jarorp;


public class Valid implements Validation {

    public static Valid valid() {
	return new Valid();
    }

    @Override
    public boolean isValid() {
	return true;
    }

    @Override
    public Error getError() {
	throw new IllegalStateException("Valid cannot have an error");
    }
}
