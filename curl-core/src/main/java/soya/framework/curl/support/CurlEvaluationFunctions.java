package soya.framework.curl.support;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.bazaarvoice.jolt.Transform;
import com.google.common.io.CharStreams;
import com.google.gson.*;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import soya.framework.curl.CurlOption;
import soya.framework.curl.CurlOptionType;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class CurlEvaluationFunctions {
    private static Map<String, Transform> joltTransforms = new HashMap<>();

    private CurlEvaluationFunctions() {

    }

    private static String[] fromJsonArray(String jsonArray) {
        if (jsonArray == null || !jsonArray.startsWith("[") || !jsonArray.endsWith("]")) {
            throw new IllegalArgumentException("Not a json array");
        }

        String token = jsonArray.substring(1, jsonArray.length() - 1);
        String[] arr = token.split(",");
        for (int i = 0; i < arr.length; i++) {
            String e = arr[i].trim();
            if (e.startsWith("\"") && e.endsWith("\"") || e.startsWith("'") && e.endsWith("'")) {
                e = e.substring(1, e.length() - 1);
            }
            arr[i] = e;
        }

        return arr;
    }

    public static String queryString(String param, String exp, String json) {
        StringBuilder builder = new StringBuilder();
        String[] arr = fromJsonArray(json);
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                builder.append("&");
            }
            builder.append(param).append("=").append(arr[i]);
        }

        return builder.toString();
    }

    public static String concatString(String exp, String json) {
        StringBuilder builder = new StringBuilder();
        String[] array = fromJsonArray(json);
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                builder.append(",");
            }
            builder.append(array[i]);
        }
        return builder.toString();
    }

    public static String option(String exp, String json) {
        int index = exp.indexOf('(');
        String type = exp.substring(0, index);
        String value = exp.substring(index + 1, exp.length() - 1);

        CurlOption curlOption = new CurlOption(CurlOptionType.valueOf(type));
        curlOption.setValue(value);

        return curlOption.toString();

    }

    public static String base64(String data) {
        byte[] bin = Base64.getDecoder().decode(data.getBytes());
        return new String(bin);
    }

    public static String jsonPath(String exp, String json) {
        DocumentContext context = JsonPath
                .using(Configuration.builder()
                        .options(Option.SUPPRESS_EXCEPTIONS)
                        .build())
                .parse(json);

        return context.read(exp).toString();
    }


    public static String mustache(String template, String encode, String json) {
        String s = decode(template, encode);
        Template tmp = Mustache.compiler().compile(s);
        return tmp.execute(toStruct(json));
    }

    public static String jolt(String path, String json) {

        Transform transform = joltTransforms.get(path);
        if (!joltTransforms.containsKey(path)) {
            Object spec = JsonUtils.jsonToObject(getClassLoader().getResourceAsStream(path));
            transform = Chainr.fromSpec(spec);
            joltTransforms.put(path, transform);
        }

        Object inputJSON = JsonUtils.jsonToObject(json);
        Object transformedOutput = transform.transform(inputJSON);
        return JsonUtils.toPrettyJsonString(transformedOutput);
    }

    public static Object toStruct(String json) {
        return toStruct(new JsonParser().parse(json));
    }

    private static Object toStruct(JsonElement jsonElement) {
        if (jsonElement == null) {
            return null;
        }

        if (jsonElement.isJsonPrimitive()) {
            return toPrimitive(jsonElement.getAsJsonPrimitive());

        } else if (jsonElement.isJsonObject()) {
            return toMap(jsonElement.getAsJsonObject());

        } else if (jsonElement.isJsonArray()) {
            return toList(jsonElement.getAsJsonArray());
        }

        return null;
    }

    private static Object toPrimitive(JsonPrimitive primitive) {
        if (primitive.isBoolean()) {
            return primitive.getAsBoolean();

        } else if (primitive.isNumber()) {
            return primitive.getAsNumber();

        } else {
            return primitive.getAsString();

        }
    }

    private static List<?> toList(JsonArray array) {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JsonElement element = array.get(i);
            list.add(toStruct(element));
        }

        return list;
    }

    private static Map<String, Object> toMap(JsonObject obj) {
        Map<String, Object> map = new LinkedHashMap<>();
        Set<Map.Entry<String, JsonElement>> set = obj.entrySet();
        set.forEach(ent -> {
            JsonElement jsonElement = ent.getValue();
            Object value = toStruct(jsonElement);

            map.put(ent.getKey(), value);
        });

        return map;
    }

    private static String decode(String st, String encode) {
        if ("base64".equalsIgnoreCase(encode)) {
            byte[] bin = Base64.getDecoder().decode(st.getBytes());
            return new String(bin);

        } else if ("classpath".equalsIgnoreCase(encode)) {
            InputStream is = getClassLoader().getResourceAsStream(st);
            try {
                return CharStreams.toString(new InputStreamReader(is));
            } catch (IOException e) {
                return null;
            }

        } else {
            return st;

        }
    }

    private static ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = CurlEvaluationFunctions.class.getClassLoader();
        }

        return classLoader;
    }
}
