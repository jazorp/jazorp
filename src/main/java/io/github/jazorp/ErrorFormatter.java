package io.github.jazorp;

import java.util.HashMap;
import java.util.Map;

public class ErrorFormatter {

    private static final Map<ErrorType, String> STOCK_ERROR_MESSAGES = new HashMap<>();

    static {
        STOCK_ERROR_MESSAGES.put(ErrorType.NOT_NULL, "%s cannot be null");
        STOCK_ERROR_MESSAGES.put(ErrorType.NOT_BLANK, "%s cannot be blank");
        STOCK_ERROR_MESSAGES.put(ErrorType.POSITIVE, "%s must be positive");
        STOCK_ERROR_MESSAGES.put(ErrorType.MIN_LENGTH, "%s must have at least %3$s characters");
        STOCK_ERROR_MESSAGES.put(ErrorType.MAX_LENGTH, "%s must have at most %3$s characters");
        STOCK_ERROR_MESSAGES.put(ErrorType.LENGTH, "%s must be exactly %3$s characters long");
        STOCK_ERROR_MESSAGES.put(ErrorType.EMAIL, "%s is not a valid e-mail address");
        STOCK_ERROR_MESSAGES.put(ErrorType.MEMBER_OF, "%s is not a member of %3$s");
    }

    private static ErrorFormatter instance;

    private ErrorFormatter() {
        reset();
    }

    public static ErrorFormatter getInstance() {
        if (instance == null) {
            instance = new ErrorFormatter();
        }
        return instance;
    }

    private Map<ErrorType, String> errorMap = new HashMap<>();
    private ErrorFormattingFunc fmtFunc;

    public void reset() {
        errorMap = new HashMap<>(STOCK_ERROR_MESSAGES);
        fmtFunc = (error, env, args) -> String.format(errorMap.get(error), args);
    }

    public void override(Map<ErrorType, String> customErrors) {
        for(Map.Entry<ErrorType, String> error : customErrors.entrySet()) {
            errorMap.put(error.getKey(), error.getValue());
        }

    }

    public void override(ErrorFormattingFunc fmtFunc) {
        this.fmtFunc = fmtFunc;
    }

    public String format(ErrorType error, Env env, Object... args) {
        return fmtFunc.format(error, env, args);
    }
}
