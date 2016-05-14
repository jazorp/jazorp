package io.github.jarorp;

public class Error {

    private String field;
    public String getField() { return field; }
    public void setField(String field) { this.field = field; }

    private String error;
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public Error(String field, String error) {
	this.field = field;
	this.error = error;
    }

    public static Error of(String field, String error) {
	return new Error(field, error);
    }

    @Override
    public String toString() {
	return "Error{" +
	    "field='" + field + '\'' +
	    ", error='" + error + '\'' +
	    '}';
    }
}
