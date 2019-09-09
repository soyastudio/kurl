package soya.framework.curl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import soya.framework.curl.support.CurlUtils;

import java.util.*;

public final class CurlTemplate {

    private final ImmutableList<String> fragments;
    private final ImmutableMap<String, String> parameters;

    private CurlTemplate(ImmutableList<String> fragments, ImmutableMap<String, String> parameters) {
        this.fragments = fragments;
        this.parameters = parameters;
    }

    public Curl create(String json) {
        StringBuilder builder = new StringBuilder();
        fragments.forEach(e -> {
            if (e.startsWith("{{")) {
                String expression = parameters.get(e);
                builder.append(CurlUtils.evaluate(expression, json));

            } else {
                builder.append(e);
            }
        });

        return Curl.fromCurl(builder.toString());
    }

    public static CurlTemplateBuilder builder(String template) throws InvalidFormatException {
        return new CurlTemplateBuilder(template);
    }

    public static class CurlTemplateBuilder {
        private final transient String template;

        private String[] fragments;
        private Set<String> parameterNames = new LinkedHashSet<>();

        private transient Map<String, String> parameters = new HashMap<>();

        private CurlTemplateBuilder(String template) throws InvalidFormatException {
            this.template = template;
            if (template == null || template.trim().isEmpty()) {
                throw new IllegalArgumentException("Template can not be null or empty.");
            }

            fragments = CurlUtils.fragments(template, "{");
            for(String token: fragments) {
                if(token.startsWith("{{") && token.endsWith("}}")) {
                    parameterNames.add(token.substring(2, token.length() - 2));
                }
            }
        }

        public Iterator<String> parameterNames() {
            return parameterNames.iterator();
        }

        public CurlTemplateBuilder parameter(String paramName, String paramExp) {
            if(parameterNames.contains(paramName)) {
                parameters.put("{{" + paramName + "}}", paramExp);
            } else {
                throw new IllegalArgumentException("Parameter not defined: '" + paramName + "'.");
            }
            return this;
        }

        public CurlTemplate create() {
            parameterNames.forEach(e -> {
                String key = "{{" + e + "}}";
                if (!parameters.containsKey(key)) {
                    throw new IllegalStateException("Parameter is not set: '" + e + "'.");
                }
            });
            return new CurlTemplate(ImmutableList.copyOf(fragments), ImmutableMap.copyOf(parameters));
        }
    }


}
