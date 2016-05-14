package io.github.jarorp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ValidatorsTest {

    private Env env = new Env();

    private String getEvalError(ValidationThunk vt) {
        return vt.validate(env).getError().getError();
    }

    @Test
    public void notNull() {
        String error = getEvalError(Validators.notNull("foo", null));
        assertEquals("foo cannot be null", error);
    }

    @Test
    public void notBlank() {
        String error = getEvalError(Validators.notBlank("foo", ""));
        assertEquals("foo cannot be blank", error);
    }

    @Test
    public void positive() {
        String error = getEvalError(Validators.positive("foo", -5));
        assertEquals("foo must be positive", error);
    }

    @Test
    public void minLength() {
        String error = getEvalError(Validators.minLength("foo", "asd", 5));
        assertEquals("foo must have at least 5 characters", error);

    }
}