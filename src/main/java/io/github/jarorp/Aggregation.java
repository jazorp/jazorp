package io.github.jarorp;

import java.util.*;
import java.util.stream.Collectors;

public class Aggregation {

	private Set<ValidationThunk> validations;
	private Map<String, Aggregation> nested = new TreeMap<>();

	private Env env = Env.empty();

	private Aggregation aggregate(ValidationThunk... thunks) {
        // TODO
		Set<ValidationThunk> hashSet = new HashSet<>(Arrays.asList(thunks));
		validations = new TreeSet<>(hashSet);
		return this;
	}

	private Aggregation() {
	}

	public static Aggregation of(ValidationThunk... thunks) {
		Aggregation agg = new Aggregation();
		agg.aggregate(thunks);
		return agg;
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

    public Aggregation withEnv(Env env) {
        this.env = env;
        return this;
    }

	public Result validate() {

		Result result = validateImpl(this);

		if (!result.wasBlocked()) {
			for (Map.Entry<String, Aggregation> entry : nested.entrySet()) {
				String key = entry.getKey();
				Aggregation nestedAggr = entry.getValue();
				Result validate = nestedAggr.validate();
				if (!validate.isValid()) {
					result.put(key, validate);
				}
			}
		}

		return result;
	}

	private Result validateImpl(Aggregation aggregation) {

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
						.map(Error::getError).sorted().collect(Collectors.toList()))));

		return new Result(collect1, !proceed);
	}
}
