package soya.framework.curl.support;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurlUtils {

    private CurlUtils() {
    }

    public static String compile(String template, Map<String, String> params, List<String> options, String data) {
        StringBuilder builder = new StringBuilder();
        String curl = template;
        if (data != null) {
            JsonParser parser = new JsonParser();
            JsonElement json = parser.parse(data);

            Map<String, String> values = new HashMap<>();
            if (params != null) {
                params.entrySet().forEach(e -> {
                    values.put(e.getKey(), evaluate(e.getValue(), data));
                });
            }

            curl = StrSubstitutor.replace(curl, values, "{{", "}}");
            builder.append(curl);

            if (options != null && !options.isEmpty()) {
                options.forEach(e -> {
                    String opt = CurlEvaluationFunctions.option(e, data);
                    builder.append(" ").append(opt);
                });
            }

        } else {
            builder.append(curl);
        }

        return builder.toString();
    }

    public static String evaluate(String exp, String data) {
        return CurlCompositeFunctionalEvaluator.getInstance().evaluate(exp, data);
    }

}
