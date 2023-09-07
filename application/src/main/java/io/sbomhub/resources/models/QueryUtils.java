package io.sbomhub.resources.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryUtils {

    public static <O> List<O> extractQuery(String value, Function<String, O> mapper) {
        Matcher m = Pattern.compile("\\((.*?)\\)").matcher(value);
        if (m.matches()) {
            return Arrays.stream(m.group(1).split("\\|"))
                    .map(mapper)
                    .toList();
        } else {
            return Collections.emptyList();
        }
    }
}
