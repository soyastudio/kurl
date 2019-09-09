package soya.framework.curl;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Curl {

    private static Map<String, CurlOptionType> optionTypes = new HashMap<>();

    private final String url;
    private final CurlOption[] options;

    private transient Map<CurlOptionType, List<CurlOption>> index = new HashMap<>();

    static {
        for (CurlOptionType ot : CurlOptionType.values()) {
            optionTypes.put(ot.getName(), ot);
            if (ot.getSimpleName() != null) {
                optionTypes.put(ot.getSimpleName(), ot);
            }
        }
    }

    public Curl(String url, CurlOption[] options) {
        this.url = url;
        this.options = options;
        if (options != null) {
            for (CurlOption option : options) {
                if (!index.containsKey(option.getOptionType())) {
                    List<CurlOption> wrapper = new ArrayList<>();
                    index.put(option.getOptionType(), wrapper);
                }

                index.get(option.getOptionType()).add(option);
            }
        }
    }

    public String getUrl() {
        return url;
    }

    public CurlOption[] getOptions() {
        return options;
    }

    public CurlOption[] getOptions(CurlOptionType type) {
        return index.containsKey(type) ? index.get(type).toArray(new CurlOption[index.get(type).size()]) : null;
    }

    public CurlOption getOption(CurlOptionType type) {
        return index.containsKey(type) ? index.get(type).get(0) : null;
    }

    public boolean contains(CurlOptionType type) {
        return index.containsKey(type);
    }

    public Map<String, String> getHeaders() {
        Map<String, String> map = new HashMap<>();
        if (index.containsKey(CurlOptionType.HEADER)) {
            CurlOption[] options = getOptions(CurlOptionType.HEADER);
            for (CurlOption option : options) {
                String header = option.getValue();
                if (header.startsWith("\"") && header.endsWith("\"") || header.startsWith("'") && header.endsWith("'")) {
                    header = header.substring(1, header.length() - 1);
                    int comma = header.indexOf(":");
                    String value = header.substring(comma + 1);
                    header = header.substring(0, comma);
                    map.put(header, value.trim());
                }

            }
        }

        return ImmutableMap.copyOf(map);
    }

    public String getData() {
        if (!index.containsKey(CurlOptionType.DATA)) {
            return null;
        }

        String data = index.get(CurlOptionType.DATA).get(0).getValue();
        if (data.startsWith("\"") && data.endsWith("\"") || data.startsWith("'") && data.endsWith("'")) {
            data = data.substring(1, data.length() - 1);
        }

        return data;

    }

    public String toString() {
        StringBuilder builder = new StringBuilder("curl");
        if (url != null) {
            builder.append(" ").append(CurlOptionType.URL.getName()).append(" \"").append(url).append("\"");
        }
        if (options != null) {
            for (CurlOption opt : options) {
                builder.append(" ").append(opt);
            }
        }
        return builder.toString();
    }

    public static Curl fromCurl(String curl) {
        return new CommandLineVisitor().accept(curl).create();
    }

    public static class CommandLineVisitor {
        private String url;
        private List<CurlOption> options = new ArrayList();

        private transient CurlOption current;

        private CommandLineVisitor() {
        }

        private CommandLineVisitor accept(String command) {
            char[] array = command.toCharArray();
            char prev = ' ';
            char expected = ' ';

            StringBuilder stringBuilder = null;
            for (char c : array) {
                if (prev == ' ') {
                    if (c == ' ') {
                        // do nothing
                    } else {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(c);
                    }

                } else {
                    if (c == ' ') {
                        String token = stringBuilder.toString();
                        visit(token);
                        stringBuilder = null;

                    } else {
                        stringBuilder.append(c);
                    }

                }

                prev = c;
            }

            if (stringBuilder != null) {
                String token = stringBuilder.toString();
                visit(token);
                stringBuilder = null;
            }

            if (current != null) {
                add(current);
            }

            return this;

        }

        private void visit(String token) {
            if ("curl".equalsIgnoreCase(token)) {

            } else if (optionTypes.containsKey(token)) {
                if (current != null) {
                    add(current);
                }

                current = new CurlOption(optionTypes.get(token));

            } else if (current != null && current.getValue() == null) {
                current.setValue(token);

            } else {
                if (current != null) {
                    add(current);
                }

                current = new CurlOption(CurlOptionType.URL);
                current.setValue(token);
            }
        }

        private void add(CurlOption option) {
            if (CurlOptionType.URL.equals(option.getOptionType())) {
                this.url = option.getValue();
                if (url.startsWith("\"") && url.endsWith("\"") || url.startsWith("'") && url.endsWith("'")) {
                    this.url = url.substring(1, url.length() - 1);
                }
            } else {
                this.options.add(option);
            }
        }

        public Curl create() {
            return new Curl(url, options.toArray(new CurlOption[options.size()]));
        }
    }
}
