package dev.branow.cli;

import java.time.LocalDate;
import java.util.*;

public class ArgsParser {

    public static final String DEFAULT = ".";
    public static final String OPTION = "-";

    public static ArgsParser of(String[] args) {
        var values = new ArrayList<String>();
        var options = new HashMap<String, String>();

        for (var i = 0; i < args.length; i++) {
            if (args[i].startsWith(OPTION)) {
                String flag = args[i];
                String value = "";
                if (i + 1 < args.length && !args[i + 1].startsWith(OPTION)) {
                    value = args[i + 1];
                    i++;
                }
                options.put(flag.substring(1), value);
            } else {
                values.add(args[i]);
            }
        }

        return new ArgsParser(values, options);
    }

    private final List<String> values;
    private final Map<String, String> options;

    private ArgsParser(List<String> values, Map<String, String> options) {
        this.values = values;
        this.options = options;
    }

    public<T> Argument<T> parse(int index, Class<T> clazz) {
        Optional<String> value = index < values.size() ? Optional.of(values.get(index)) : Optional.empty();
        return new Argument<>(value, clazz);
    }

    public<T> Argument<T> parse(String flag, Class<T> clazz) {
        Optional<String> value = options.containsKey(flag) ? Optional.of(options.get(flag)) : Optional.empty();
        return new Argument<>(value, clazz);
    }

    public List<String> getValues() {
        return values;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public record Argument<T>(Optional<String> value, Class<T> type) {

        public T get() {
            var v = value.orElseThrow(() -> new IllegalArgumentException("No value specified"));
            return parseValue(v);
        }

        public T orDefault(T defaultValue) {
            var v = value.orElseThrow(() -> new IllegalArgumentException("No value specified"));
            return v.equals(DEFAULT) ? defaultValue : parseValue(v);
        }

        public T orElse(T defaultValue, T other) {
            return value.isPresent() ? orDefault(defaultValue) : other;
        }

        private T parseValue(String value) {
            if (type == String.class) {
                return type.cast(value);
            } else if (type == Integer.class) {
                return type.cast(Integer.parseInt(value));
            } else if (type == Long.class) {
                return type.cast(Long.parseLong(value));
            } else if (type == LocalDate.class) {
                return type.cast(LocalDate.parse(value));
            }
            throw new IllegalArgumentException("Unsupported type: " + type);
        }

    }

}
