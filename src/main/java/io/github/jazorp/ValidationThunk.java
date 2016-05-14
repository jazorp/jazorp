package io.github.jazorp;

public class ValidationThunk implements Comparable<ValidationThunk> {


    private Thunk supplier;

    private int priority;
    public int getPriority() { return priority; }

    private boolean blocking;
    public boolean isBlocking() { return blocking; }

    @Override
    public int compareTo(ValidationThunk o) {
        return priority < o.priority ? -1 : 1;
    }

    private ValidationThunk(Thunk supplier, int priority, boolean blocking) {
        this.supplier = supplier;
        this.priority = priority;
        this.blocking = blocking;
    }

    public static ValidationThunk of(Thunk supplier) {
        return new ValidationThunk(supplier, 100, false);
    }

    public static ValidationThunk blocking(Thunk supplier) {
        return new ValidationThunk(supplier, 10, true);
    }

    public ValidationThunk blocking() {
        return ValidationThunk.blocking(supplier);
    }

    public Validation validate(Env env) {
        return supplier.eval(env);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ValidationThunk that = (ValidationThunk) o;

        return supplier.equals(that.supplier);
    }

    @Override
    public int hashCode() {
        return supplier.hashCode();
    }
}
