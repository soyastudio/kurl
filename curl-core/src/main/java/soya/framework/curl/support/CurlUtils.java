package soya.framework.curl.support;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.yaml.snakeyaml.Yaml;

import java.util.Base64;
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

    public static String base64Encode(String data) {
        return new String(Base64.getEncoder().encode(data.getBytes()));
    }

    public static String base64Decode(String data) {
        return new String(Base64.getDecoder().decode(data.getBytes()));
    }

    public static String yamlToJson(String src) {
        Yaml yaml = new Yaml();
        Object configuration = yaml.load(src);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(configuration);
    }

    public static String jsonPath(String exp, String json) {
        DocumentContext context = JsonPath
                .using(Configuration.builder()
                        .options(Option.SUPPRESS_EXCEPTIONS)
                        .build())
                .parse(json);

        return context.read(exp).toString();
    }

}
