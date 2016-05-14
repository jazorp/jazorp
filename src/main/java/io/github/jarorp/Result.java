package io.github.jarorp;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class Result {

    private Map<String, Object> errors;
    private boolean blocked;

    public Result(Map<String, Object> errors) {
        this.errors = errors;
    }

    public Result(Map<String, Object> errors, boolean blocked) {
        this.errors = errors;
        this.blocked = blocked;
    }

    public static Result empty() {
        return new Result(new HashMap<>());
    }

    public Map<String, Object> getErrors() { return errors; }

    public Result put(String field, Result nested) {
        Map<String, Object> errors = nested.getErrors();
        if (this.errors != null && !this.errors.isEmpty()) {
            this.errors.put(field, errors);
        }
        return this;
    }

    public Result put(Result result) {
        for (Map.Entry<String, Object> entry : result.getErrors().entrySet()) {
            errors.put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public boolean isValid() {
        return errors == null || errors.isEmpty();
    }

    public boolean wasBlocked() { return blocked; }

    public <R> R getOrElse(Supplier<? extends R> successSup, Function<Map<String, Object>, ? extends R> errorFunc) {
        return isValid() ? successSup.get() : errorFunc.apply(errors);
    }

    @Override
    public String toString() {
        return "Result{" +
                "errors=" + errors +
                '}';
    }
}
