package io.github.jazorp;

import java.util.*;
import java.util.stream.Collectors;

public class Aggregation {

    private Set<ValidationThunk> validations;
    private Map<String, Aggregation> nested = new TreeMap<>();

    private Aggregation(Set<ValidationThunk> validations) {
        this.validations = validations;
    }

    private Aggregation(Set<ValidationThunk> validations, Map<String, Aggregation> nested) {
        this.validations = validations;
        this.nested = nested;
    }

    public static Aggregation of(ValidationThunk... thunks) {
        return new Aggregation(new TreeSet<>(Arrays.asList(thunks)));
    }

    public Aggregation nested(String field, Aggregation aggregation) {
        nested.put(field, aggregation);
        return this;
    }

    public <T> Aggregation nested(String field, Validator<T> validator, T t) {
        nested.put(field, validator.collect(t));
        return this;
    }

    public <T> Aggregation nestedList(String field, Validator<T> validator, List<T> ts) {
        for (int i=0; i<ts.size(); i++) {
            nested(field + "[" + i + "]", validator, ts.get(i));
        }
        return this;
    }

    public Aggregation compose(Aggregation other) {
        Set<ValidationThunk> composedValidations = new HashSet<>(validations);
        composedValidations.addAll(other.validations);
        Map<String, Aggregation> composedNested = new HashMap<>(nested);
        composedNested.putAll(other.nested);
        return new Aggregation(composedValidations, composedNested);
    }

    public Result validate(Env env) {

        Result result = validateImpl(this, env);

        if (!result.wasBlocked()) {
            for (Map.Entry<String, Aggregation> entry : nested.entrySet()) {
                String key = entry.getKey();
                Aggregation nestedAggr = entry.getValue();
                Result validate = nestedAggr.validate(env);
                if (!validate.isValid()) {
                    result.put(key, validate);
                }
            }
        }

        return result;
    }

    private Result validateImpl(Aggregation aggregation, Env env) {

        boolean proceed = true;

        Iterator<ValidationThunk> iterator = aggregation.validations.iterator();

        List<Validation> errors = new ArrayList<>();

        while(proceed && iterator.hasNext()) {
            ValidationThunk thunk = iterator.next();
            Validation validation = thunk.validate(env);
            if (!validation.isValid()) {
                errors.add(validation);
            }
            if (thunk.isBlocking()) {
                proceed = false;
            }
        }

        Map<String, List<Validation>> collect = errors.stream().collect(Collectors.groupingBy(v -> v.getError().getField()));

        Map<String, Object> collect1 = new TreeMap<>(collect.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().map(Validation::getError)
                        .map(Error::getError).collect(Collectors.toSet()))));

        return new Result(collect1, !proceed);
    }

}
