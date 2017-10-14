package io.github.jazorp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class ValidatorsTest {

    private Env env = Env.empty();

    private String getEvalError(ValidationThunk vt) {
        return vt.validate(env).getError().getError();
    }

    private boolean isValid(ValidationThunk vt) {
        return vt.validate(env).isValid();
    }

	@Test
	public void memberOf_iterable() {
		String error = getEvalError(Validators.memberOf("foo", "1", Arrays.asList("a", "b", "c")));
		assertEquals("foo is not a member of [a, b, c]", error);
		assertTrue(isValid(Validators.memberOf("foo", "1", Arrays.asList("1", "b", "c"))));
	}

	@Test
	public void memberOf_varArgs() {
		String error = getEvalError(Validators.memberOf("foo", "1", "a", "b", "c"));
		assertEquals("foo is not a member of [a, b, c]", error);
		assertTrue(isValid(Validators.memberOf("foo", "1", "1", "a", "b")));
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
}