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

    public static Curl fromCurl(String curl) throws InvalidFormatException {
        String url = null;
        List<CurlOption> options = new ArrayList();

        String cmd = curl.trim();
        if (cmd.startsWith("curl ")) {
            cmd = cmd.substring("curl ".length());
        }
        cmd = cmd + " ";

        char[] arr = cmd.toCharArray();
        char ch1 = ' ';
        char ch2 = ' ';
        String pn = null;

        StringBuilder builder = new StringBuilder();
        for (char c : arr) {
            if (c == ' ') {
                ch1 = ' ';
                ch2 = ' ';

                String token = builder.toString();
                if (token.startsWith("-")) {
                    if (pn != null) {
                        options.add(new CurlOption(optionTypes.get(pn)));
                    }
                    pn = token;
                } else if (pn != null && optionTypes.containsKey(pn)) {
                    CurlOptionType type = optionTypes.get(pn);
                    if (type.equals(CurlOptionType.URL)) {
                        url = token;

                    } else {
                        options.add(new CurlOption(type, token));
                    }

                } else {
                    url = token;
                }

                builder = new StringBuilder();

            } else if (c == '-') {
                ch1 = ch2;
                ch2 = c;

                builder.append(c);

            } else {
                builder.append(c);
            }

        }

        return new Curl(url, options.toArray(new CurlOption[options.size()]));
    }

}
