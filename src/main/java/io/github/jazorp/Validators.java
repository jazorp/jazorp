package io.github.jazorp;

public class Validators {

    private static ValidationThunk validateImpl(boolean isInvalid, ErrorType error, Object... args) {
        return ValidationThunk.of(env -> isInvalid ? Invalid.of((String) args[0],
                ErrorFormatter.getInstance().format(error, env, args)) :
                Valid.valid());
    }

    // =====================
    // Validators

    public static ValidationThunk notNull(String field, Object value) {
        return validateImpl(value == null, ErrorType.NOT_NULL, field, value);
    }

    public static ValidationThunk notBlank(String field, String value) {
        boolean isInvalid = value == null || value.trim().length() == 0;
        return validateImpl(isInvalid, ErrorType.NOT_BLANK, field, value);
    }

    public static ValidationThunk positive(String field, Number value) {
        boolean isInvalid = value == null || value.doubleValue() <= 0;
        return validateImpl(isInvalid, ErrorType.POSITIVE, field, value);
    }

    public static ValidationThunk minLength(String field, String value, Integer min) {
        boolean isInvalid = value == null || value.length() < min;
        return validateImpl(isInvalid, ErrorType.MIN_LENGTH, field, value, min);
    }

    public static ValidationThunk length(String field, String value, int length) {
        boolean isInvalid = value == null || value.length() != length;
        return validateImpl(isInvalid, ErrorType.LENGTH, field, value, length);
    }
}

