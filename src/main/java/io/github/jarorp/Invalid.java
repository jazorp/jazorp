package io.github.jarorp;

public class Invalid implements Validation {

    private final Error error;

    public Invalid(Error error) {
	this.error = error;
    }

    public static Invalid of(String field, String error) {
	return new Invalid(Error.of(field, error));
    }

    @Override
    public boolean isValid() {
	return false;
    }

    @Override
    public Error getError() {
	return error;
    }

    @Override
    public String toString() {
	return "Invalid{" +
	    "error=" + error +
	    '}';
    }
}
