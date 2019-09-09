package soya.framework.curl.support;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.google.common.io.CharStreams;
import com.google.gson.*;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.yaml.snakeyaml.Yaml;
import soya.framework.curl.Curl;
import soya.framework.curl.CurlOptionType;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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

    public static String toJson(Object obj) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(obj);
    }

    public static String[] placeHolders(String text, String startToken, boolean includeDelim) {

        boolean startWith = text.startsWith(startToken + "{");
        String prefix;
        String suffix;

        if ("$".equals(startToken)) {
            prefix = "\\$\\{";
            suffix = "}";

        } else if ("#".equals(startToken)) {
            prefix = "\\#\\{";
            suffix = "}";

        } else if ("@".equals(startToken)) {
            prefix = "\\@\\{";
            suffix = "}";

        } else if ("{".equals(startToken)) {
            prefix = "\\{\\{";
            suffix = "}}";

        } else {
            throw new IllegalArgumentException("Start token '" + startToken + "' is not supported.");
        }

        Set<String> set = new HashSet<>();
        String[] array = text.split(prefix);
        for (int i = 0; i < array.length; i++) {
            if(i == 0 && startWith || i > 0) {
                String token = array[i];
                int index = token.indexOf(suffix);
                if (index < 0) {
                    throw new IllegalArgumentException("Token starts with '" + prefix
                            + "' but does not end with '" + suffix + "'");
                }

                if (includeDelim) {
                    token = startToken + "{" + token.substring(0, index + suffix.length());
                } else {
                    token = token.substring(0, index);
                }
                set.add(token);
            }
        }

        List<String> list = new ArrayList<>(set);
        Collections.sort(list);

        return list.toArray(new String[list.size()]);
    }

    public static String[] fragments(String text, String startToken) {
        List<String> list = new ArrayList<>();
        boolean startWith = text.startsWith(startToken + "{");
        String prefix;
        String suffix;

        if ("$".equals(startToken)) {
            prefix = "\\$\\{";
            suffix = "}";

        } else if ("#".equals(startToken)) {
            prefix = "\\#\\{";
            suffix = "}";

        } else if ("@".equals(startToken)) {
            prefix = "\\@\\{";
            suffix = "}";

        } else if ("{".equals(startToken)) {
            prefix = "\\{\\{";
            suffix = "}}";

        } else {
            throw new IllegalArgumentException("Start token '" + startToken + "' is not supported.");
        }

        String[] array = text.split(prefix);
        for (int i = 0; i < array.length; i++) {
            if(i == 0 && startWith || i > 0) {
                String token = array[i];
                int index = token.indexOf(suffix);
                if (index < 0) {
                    throw new IllegalArgumentException("Token starts with '" + prefix
                            + "' but does not end with '" + suffix + "'");
                }

                String first = startToken + "{" + token.substring(0, index + suffix.length());
                String second = token.substring(index + suffix.length());

                list.add(first);
                list.add(second);
            } else {
                list.add(array[i]);
            }
        }

        return list.toArray(new String[list.size()]);
    }

    public static String base64Encode(String data) {
        return new String(Base64.getEncoder().encode(data.getBytes()));
    }

    public static String base64Decode(String data) {
        return new String(Base64.getDecoder().decode(data.getBytes()));
    }

    public static String base64Zip(String src) throws IOException {
        if (src == null || src.length() == 0) {
            return src;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream zos = new GZIPOutputStream(out);
        zos.write(src.getBytes());
        zos.close();

        byte[] bytes = out.toByteArray();
        return new String(Base64.getEncoder().encode(bytes));
    }

    public static String base64Unzip(String data) throws IOException {
        if (data == null || data.length() == 0) {
            return data;
        }

        byte[] bytes = Base64.getDecoder().decode(data.getBytes());
        GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(bytes));
        String result = CharStreams.toString(new InputStreamReader(in));
        in.close();
        return result;
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

    public static String jolt(String exp, String json) {
        Object specs = JsonUtils.jsonToObject(exp);
        Chainr chainr = Chainr.fromSpec(specs);

        Object inputJSON = JsonUtils.jsonToObject(json);
        Object transformedOutput = chainr.transform(inputJSON);
        return JsonUtils.toPrettyJsonString(transformedOutput);
    }

    public static String mustache(String exp, String json) {
        Template tmp = Mustache.compiler().compile(exp);
        return tmp.execute(toStruct(json));
    }

    public static String[] fromJsonArray(String jsonArray) {
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

    public static Object toStruct(String json) {
        return toStruct(new JsonParser().parse(json));
    }

    public static Object toStruct(JsonElement jsonElement) {
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

    public static Object toPrimitive(JsonPrimitive primitive) {
        if (primitive.isBoolean()) {
            return primitive.getAsBoolean();

        } else if (primitive.isNumber()) {
            return primitive.getAsNumber();

        } else {
            return primitive.getAsString();

        }
    }

    public static List<?> toList(JsonArray array) {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JsonElement element = array.get(i);
            list.add(toStruct(element));
        }

        return list;
    }

    public static Map<String, Object> toMap(JsonObject obj) {
        Map<String, Object> map = new LinkedHashMap<>();
        Set<Map.Entry<String, JsonElement>> set = obj.entrySet();
        set.forEach(ent -> {
            JsonElement jsonElement = ent.getValue();
            Object value = toStruct(jsonElement);

            map.put(ent.getKey(), value);
        });

        return map;
    }

    public static String decode(String st, String encode) {
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

    public static String http(Curl curl) throws IOException {
        URL url = new URL(curl.getUrl());

        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod(curl.getOption(CurlOptionType.REQUEST) == null ? "GET" : curl.getOption(CurlOptionType.REQUEST).getValue());
        con.setDoOutput(true);

        // Header:
        if (curl.contains(CurlOptionType.HEADER)) {
            curl.getHeaders().entrySet().forEach(h -> {
                con.setRequestProperty(h.getKey(), h.getValue());
            });
        }

        // Data:
        if (curl.contains(CurlOptionType.DATA) && curl.getOption(CurlOptionType.DATA).getValue() != null) {
            String data = curl.getData();
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(data);
            writer.flush();
            writer.close();
        }

        // Read response:
        int status = con.getResponseCode();
        Reader streamReader = null;
        if (status > 299) {
            streamReader = new InputStreamReader(con.getErrorStream());
        } else {
            streamReader = new InputStreamReader(con.getInputStream());
        }

        String out = CurlUtils.toString(streamReader);
        streamReader.close();

        return out;
    }

    public static String toString(Reader reader) throws IOException {
        return CharStreams.toString(reader);
    }

    public static ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = CurlEvaluationFunctions.class.getClassLoader();
        }

        return classLoader;
    }

}
