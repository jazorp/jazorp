package io.github.jazorp;

public class ValidationThunk implements Comparable<ValidationThunk> {

    private Thunk thunk;

    private int priority;

    private boolean blocking;
    public boolean isBlocking() { return blocking; }

    private boolean optional;

    @Override
    public int compareTo(ValidationThunk o) {
        return priority < o.priority ? -1 : 1;
    }

    private ValidationThunk(Thunk thunk, int priority, boolean blocking) {
        this.thunk = thunk;
        this.priority = priority;
        this.blocking = blocking;
    }

    public static ValidationThunk of(Thunk thunk) {
        return new ValidationThunk(thunk, 100, false);
    }

    public static ValidationThunk blocking(Thunk thunk) {
        return new ValidationThunk(thunk, 10, true);
    }

    public ValidationThunk blocking() {
        return ValidationThunk.blocking(thunk);
    }

    public ValidationThunk optional() {
        optional = true;
        return this;
    }

    public Validation validate(Env env) {
        return thunk.eval(env, optional);
    }

}
