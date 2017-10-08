package io.github.jazorp;

import org.junit.Test;

import static org.junit.Assert.*;

public class ValidatorsTest {

    private Env env = Env.empty();

    private String getEvalError(ValidationThunk vt) {
        return vt.validate(env).getError().getError();
    }

    private boolean isValid(ValidationThunk vt) {
        return vt.validate(env).isValid();
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

    @Test
    public void maxLength() {
        String error = getEvalError(Validators.maxLength("foo", "asdfggh", 5));
        assertEquals("foo must have at most 5 characters", error);
    }

    @Test
    public void length() {
        String error = getEvalError(Validators.length("foo", "asd", 5));
        assertEquals("foo must be exactly 5 characters long", error);
    }

    @Test
    public void email() {
        String error = getEvalError(Validators.email("foo", ".@mail,com"));
        assertEquals("foo is not a valid e-mail address", error);

        assertFalse(isValid(Validators.email("foo", "username@domain.com.")));
        assertFalse(isValid(Validators.email("foo", ".username@domain.com")));
        assertFalse(isValid(Validators.email("foo", "username@domaincom")));
        assertFalse(isValid(Validators.email("foo", "username_AT_domain_DOT_com")));

        assertTrue(isValid(Validators.email("foo", "user.name@domain.com")));
        assertTrue(isValid(Validators.email("foo", "user.name@domain.co.uk")));
    }

    @Test
    public void lt() {
        String error = getEvalError(Validators.lt("foo", 8, 5));
        assertEquals("foo must be less than 5", error);
    }

    @Test
    public void lte() {
        String error = getEvalError(Validators.lte("foo", 8, 5));
        assertEquals("foo must be less or equals than 5", error);

        error = getEvalError(Validators.lte("foo", 5.1, 5));
        assertEquals("foo must be less or equals than 5", error);

        assertTrue(isValid(Validators.lte("foo", 5, 5)));
        assertTrue(isValid(Validators.lte("foo", 4.9, 5)));
    }

    @Test
    public void gt() {
        String error = getEvalError(Validators.gt("foo", 3, 5));
        assertEquals("foo must be grater than 5", error);

        error = getEvalError(Validators.gt("foo", 5, 5));
        assertEquals("foo must be grater than 5", error);
    }

    @Test
    public void gte() {
        String error = getEvalError(Validators.gte("foo", 3, 5));
        assertEquals("foo must be grater or equals than 5", error);

        assertTrue(isValid(Validators.gte("foo", 5, 5)));
    }
}