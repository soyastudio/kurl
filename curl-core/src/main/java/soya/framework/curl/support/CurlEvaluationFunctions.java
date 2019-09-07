package soya.framework.curl.support;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.bazaarvoice.jolt.Transform;
import com.google.gson.*;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import soya.framework.curl.CurlOption;
import soya.framework.curl.CurlOptionType;

import java.util.*;

public class CurlEvaluationFunctions {
    private static Map<String, Transform> joltTransforms = new HashMap<>();

    private CurlEvaluationFunctions() {

    }

    public static String queryString(String param, String exp, String json) {
        StringBuilder builder = new StringBuilder();
        String[] arr = CurlUtils.fromJsonArray(json);
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
        String[] array = CurlUtils.fromJsonArray(json);
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

    public static String jsonPath(String exp, String json) {
        DocumentContext context = JsonPath
                .using(Configuration.builder()
                        .options(Option.SUPPRESS_EXCEPTIONS)
                        .build())
                .parse(json);

        return context.read(exp).toString();
    }

    public static String jolt(String exp, String json) {
        return CurlUtils.jolt(exp, json);
    }

    public static String mustache(String template, String encode, String json) {
        return CurlUtils.mustache(template, json);
    }
}
