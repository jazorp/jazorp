package io.github.jarorp;

import java.util.HashMap;
import java.util.Map;

public class ErrorFormatter {

    private static ErrorFormatter instance;

    private ErrorFormatter() {
        init();
    }

    public static ErrorFormatter getInstance() {
        if (instance == null) {
            instance = new ErrorFormatter();
        }
        return instance;
    }

    private Map<ErrorType, String> errorMap = new HashMap<>();
    private ErrorFormattingFunc fmtFunc;

    private void init() {
        fmtFunc = (error, env, args) -> String.format(errorMap.get(error), args);

        errorMap.put(ErrorType.NOT_NULL, "%s cannot be null");
        errorMap.put(ErrorType.NOT_BLANK, "%s cannot be blank");
        errorMap.put(ErrorType.POSITIVE, "%s must be positive");
        errorMap.put(ErrorType.MIN_LENGTH, "%s must have at least %3$s characters");
        errorMap.put(ErrorType.LENGTH, "%s must be exactly %3$s characters long");
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
