package soya.framework.curl;

public final class CurlOption {
    private final CurlOptionType optionType;
    private String value;


    public CurlOption(CurlOptionType optionType) {
        this.optionType = optionType;
    }

    public CurlOption(CurlOptionType optionType, String value) {
        this.optionType = optionType;
        this.value = value;
    }

    public CurlOptionType getOptionType() {
        return optionType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (optionType.getSimpleName() != null) {
            builder.append(optionType.getSimpleName());
        } else {
            builder.append(optionType.getName());
        }

        if (value != null) {
            builder.append(" ").append(value);
        }

        return builder.toString();
    }
}
