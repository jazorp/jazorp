package io.github.jazorp;

import java.util.HashMap;
import java.util.Map;

public class Env {

    private Map<String, String> env;

    private Env(Map<String, String> env) {
        this.env = env;
    }

    public static Env empty() {
        return new Env(new HashMap<>());
    }

    public String get(String key) {
        return env.get(key);
    }

    public static class Builder {

        private Map<String, String> env = new HashMap<>();

        public Builder set(String key, String value) {
            env.put(key, value);
            return this;
        }

        public Env build() {
            return new Env(env);
        }

    }
}
