package io.github.jazorp;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Validators {

    private static Object[] concat(Object[] a, Object[] b) {
        int length = a.length + b.length;
        Object[] result = new Object[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private static <T> ValidationThunk validateImpl(Predicate<T> assertFunc, ErrorType error,
                                                    String field, T value, Object... args) {
        return ValidationThunk.of((env, optional) -> {
            if (optional && value == null) {
                return Valid.valid();
            } else {
                if (value == null || !assertFunc.test(value)) {
                    return Invalid.of(field,
                            ErrorFormatter.getInstance().format(error, env,
                                    concat(new Object[] { field, value }, args)));
                } else {
                    return Valid.valid();
                }
            }
        });
    }

    // =====================
    // Validators

    public static ValidationThunk notNull(String field, Object value) {
        return validateImpl(v -> v != null, ErrorType.NOT_NULL, field, value);
    }

    public static ValidationThunk notBlank(String field, String value) {
        return validateImpl(v -> v.trim().length() > 0, ErrorType.NOT_BLANK, field, value);
    }

    public static ValidationThunk positive(String field, Number value) {
        return validateImpl(v -> v.doubleValue() > 0, ErrorType.POSITIVE, field, value);
    }

    public static ValidationThunk minLength(String field, String value, Integer min) {
        return validateImpl(v -> v.length() >= min, ErrorType.MIN_LENGTH, field, value, min);
    }

    public static ValidationThunk maxLength(String field, String value, Integer max) {
        return validateImpl(v -> v.length()  <= max, ErrorType.MAX_LENGTH, field, value, max);
    }

    public static ValidationThunk length(String field, String value, int length) {
        return validateImpl(v -> v.length() == length, ErrorType.LENGTH, field, value, length);
    }

    private static final String EMAIL_REGEX = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static ValidationThunk email(String field, String value) {
        return validateImpl(v -> EMAIL_PATTERN.matcher(value).matches(), ErrorType.EMAIL, field, value);
    }
}

