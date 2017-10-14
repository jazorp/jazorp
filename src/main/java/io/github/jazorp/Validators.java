package io.github.jazorp;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
                                    concat(new Object[]{field, value}, args)));
                } else {
                    return Valid.valid();
                }
            }
        });
    }

    // =====================
    // Validators

	public static ValidationThunk memberOf(String field, final Object value, Iterable<Object> values) {
		Collection<Object> collection = null;
		if (values instanceof Collection) {
			collection = (Collection<Object>) values;
		} else {
			collection = new ArrayList<>();
			values.forEach(collection::add);
		}
		return memberOf(field, value, collection);
	}

	public static ValidationThunk memberOf(String field, final Object value, Object... values) {
		return memberOf(field, value, Arrays.asList(values));
	}

	public static ValidationThunk memberOf(String field, final Object value, Collection<Object> values) {
		return validateImpl(values::contains, ErrorType.MEMBER_OF, field, value, values);
	}

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
        return validateImpl(v -> v.length() <= max, ErrorType.MAX_LENGTH, field, value, max);
    }

    public static ValidationThunk length(String field, String value, int length) {
        return validateImpl(v -> v.length() == length, ErrorType.LENGTH, field, value, length);
    }

    private static final String EMAIL_REGEX = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static ValidationThunk email(String field, String value) {
        return validateImpl(v -> EMAIL_PATTERN.matcher(value).matches(), ErrorType.EMAIL, field, value);
    }

    /**
     * see https://en.wikipedia.org/wiki/Machine_epsilon#Values_for_standard_hardware_floating_point_arithmetics
     */
    public static double EPSILON = 5.96e-08;

    public static ValidationThunk equal(String field, Number value, Number ref) {
        return equal(field, value, ref, EPSILON);
    }

    public static ValidationThunk equal(String field, Number value, Number ref, double epsilon) {
        return validateImpl(doubleEquals(ref.doubleValue(), epsilon), ErrorType.EQUAL, field, value, ref);
    }

    public static ValidationThunk less(String field, Number value, Number max) {
        return less(field, value, max, EPSILON);
    }

    public static ValidationThunk less(String field, Number value, Number max, double epsilon) {
        return validateImpl(doubleEquals(max.doubleValue(), epsilon).negate().
                        and(doubleLessThan(max.doubleValue(), epsilon)),
                ErrorType.LESS, field, value, max);
    }

    public static ValidationThunk lessEqual(String field, Number value, Number max) {
        return lessEqual(field, value, max, EPSILON);
    }

    public static ValidationThunk lessEqual(String field, Number value, Number max, double epsilon) {
        return validateImpl(doubleEquals(max.doubleValue(), epsilon).
                        or(doubleLessThan(max.doubleValue(), epsilon)),
                ErrorType.LESS_EQUAL, field, value, max);
    }

    public static ValidationThunk greater(String field, Number value, Number min) {
        return greater(field, value, min, EPSILON);
    }

    public static ValidationThunk greater(String field, Number value, Number min, double epsilon) {
        return validateImpl(doubleEquals(min.doubleValue(), epsilon).negate().
                        and(doubleGreaterThan(min.doubleValue(), epsilon)),
                ErrorType.GREATER, field, value, min);
    }

    public static ValidationThunk greaterEqual(String field, Number value, Number min) {
        return greaterEqual(field, value, min, EPSILON);
    }

    public static ValidationThunk greaterEqual(String field, Number value, Number min, double epsilon) {
        return validateImpl(doubleEquals(min.doubleValue(), epsilon).
                        or(doubleGreaterThan(min.doubleValue(), epsilon))
                , ErrorType.GREATER_EQUAL, field, value, min);
    }


    private static Predicate<Number> doubleEquals(double b, double epsilon) {
        return v -> v.doubleValue() == b || Math.abs(v.doubleValue() - b) < epsilon;
    }

    private static Predicate<Number> doubleLessThan(double b, double epsilon) {
        return v -> v.doubleValue() - b < epsilon;
    }

    private static Predicate<Number> doubleGreaterThan(double b, double epsilon) {
        return v -> v.doubleValue() - b > epsilon;
    }

}

