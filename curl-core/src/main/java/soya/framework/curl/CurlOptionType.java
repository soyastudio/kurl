package soya.framework.curl;

public enum CurlOptionType {
    APPEND("-a/--append"),
    USER_AGENT("-A/--user-agent"),
    ANYAUTH("--anyauth"),
    COOKIE("-b/--cookie"),
    USE_ASCII("-B/--use-ascii"),
    BASIC("--basic"),
    CIPHERS("--ciphers"),
    COMPRESSED("--compressed"),
    CONNECT_TIMEOUT("--connect-timeout"),
    COOKIE_JAR("-c/--cookie-jar"),
    CONTINUE_AT("-C/--continue-at"),
    CREATE_DIRS("--create-dirs"),
    CRLF("--crlf"),
    CRLFILE("--crlfile"),
    DATA("-d/--data"),
    DATA_BINARY("--data-binary"),
    DATA_URLENCODE("--data-urlencode"),
    DELEGATION("--delegation"),
    DIGEST("--digest"),
    DISABLE_EPRT("--disable-eprt"),
    DISABLE_EPSV("--disable-epsv"),
    DUMP_HEADER("-D/--dump-header"),
    REFERER("-e/--referer"),
    ENGINE("--engine"),
    ENVIRONMENT("--environment"),
    EDGE_FILE("--egd-file"),
    CERT("-E/--cert"),
    CERT_TYPE("--cert-type"),
    CACERT("--cacert"),
    CAPATH("--capath"),
    FAIL("-f/--fail"),
    FTP_ACCOUNT("--ftp-account"),
    FTP_CREATE_DIRS("--ftp-create-dirs"),
    FTP_METHOD("--ftp-method"),
    FTP_PASV("--ftp-pasv"),
    FTP_ALTERNATIVE_TO_USER("--ftp-alternative-to-user"),
    FTP_SKIP_PASV_IP("--ftp-skip-pasv-ip"),
    FTP_SSL("--ftp-ssl"),
    FTP_SSL_CONTROL("--ftp-ssl-control"),
    FTP_SSL_REQD("--ftp-ssl-reqd"),
    FTP_SSL_CCC("--ftp-ssl-ccc"),
    FTP_SSL_CCC_MODE("--ftp-ssl-ccc-mode"),
    FORM("-F/--form"),
    FORM_STRING("--form-string"),
    GLOBOFF("-g/--globoff"),
    GET("-G/--get"),
    HELP("-h/--help"),
    HEADER("-H/--header"),
    HOSTPUBMD5("--hostpubmd5"),
    IGNORE_CONTENT_LENGTH("--ignore-content-length"),
    INCLUDE("-i/--include"),
    INTERFACE("--interface"),
    HEAD("-I/--head"),
    JUNK_SESSION_COOKIES("-j/--junk-session-cookies"),
    INSECURE("-k/--insecure"),
    KEEPALIVE_TIME("--keepalive-time"),
    KEY("--key"),
    KEY_TYPE("--key-type"),
    KRB("--krb"),
    CONFIG("-K/--config"),
    LIBCURL("--libcurl"),
    LIMIT_RATE("--limit-rate"),
    LIST_ONLY("-l/--list-only"),
    LOCAL_PORT("--local-port"),
    LOCATION("-L/--location"),
    LOCATION_TRUSTED("--location-trusted"),
    MAX_FILESIZE("--max-filesize"),
    MAX_TIME("-m/--max-time"),
    MANUAL("-M/--manual"),
    NETRC("-n/--netrc"),
    NETRC_OPTIONAL("--netrc-optional"),
    NEGOTIATE("--negotiate"),
    NO_BUFFER("-N/--no-buffer"),
    NO_KEEPALIVE("--no-keepalive"),
    NO_SESSIONID("--no-sessionid"),
    NOPROXY("--noproxy"),
    NTLM("--ntlm"),
    OUTPUT("-o/--output"),
    REMOTE_NAME("-O/--remote-name"),
    REMOTE_NAME_ALL("--remote-name-all"),
    PASS("--pass"),
    POST301("--post301"),
    POST302("--post302"),
    PROXY_ANYAUTH("--proxy-anyauth"),
    PROXY_BASIC("--proxy-basic"),
    PROXY_DIGEST("--proxy-digiest"),
    PROXY_NEGOTIATE("--proxy-negotiate"),
    PROXY_NTLM("--proxy-ntlm"),
    PROXY_1_0("--proxy1.0"),
    PROXYTUNNEL("-p/--proxytunnel"),
    PUBKEY("--pubkey"),
    FTP_PORT("-P/--ftp-port"),
    QUOTE("-Q/--quote"),
    RANDOM_FILE("--random-file"),
    RANGE("-r/--range"),
    RAW("--raw"),
    REMOTE("-R/--remote-time"),
    RETRY("--retry"),
    RETRY_DELAY("--retry-delay"),
    RETRY_MAX_TIME("--retry-max-time"),
    SILENT("-s/--silent"),
    SHOW_ERROR("-S/--show-error"),
    SOCKS4("--socks4"),
    SOCKS4A("--sockets4a"),
    SOCKS5_HOSTNAME("--socks5-hostname"),
    SOCKS5("--socks5"),
    SOCKS5_GSSAPI_SERVICE("--socks5-gssapi-service"),
    SOCKS5_GSSAPI_NEC("--socks5-gssapi-nec"),
    STDERR("--stderr"),
    TCP_NODELAY("--tcp-nodelay"),
    TELNET_OPTION("-t/--telnet-option"),
    UPLOAD_FILE("-T/--upload-file "),
    TRACE("--trace"),
    TRACE_ASCII("--trace-ascii"),
    TRACE_TIME("--trace-time"),
    USER("-u/--user"),
    PROXY_USER("-U/--proxy-user"),
    URL("--url"),
    VERBOSE("-v/--verbose"),
    VERSION("-V/--version"),
    WRITE_OUT("-w/--write-out"),
    PROXY("-x/--proxy"),
    REQUEST("-X/--request"),
    SPEED_TIME("-y/--speed-time"),
    SPEED_LIMIT("-Y/--speed-limit"),
    TIME_COND("-z/--time-cond"),
    MAX_REDIRS("--max-redirs"),
    HTTP_1_0("-0/--http1.0"),
    TLSV1("-1/--tlsv1"),
    SSLV2("-2/--sslv2"),
    SSLV3("-3/--sslv3"),
    IPV4("-4/--ipv4"),
    IPV6("-6/--ipv6"),
    PROGRESS_BAR("-#/--progress-bar");


    private final String name;
    private final String simpleName;
    private final Class<?> valueType;

    CurlOptionType(String value) {
        int index = value.indexOf('/');
        if(index > 0) {
            this.name = value.substring(index+ 1);
            this.simpleName = value.substring(0, index);
        } else {
            this.name = value;
            this.simpleName = null;

        }
        this.valueType = String.class;
    }

    CurlOptionType(String value, Class<?> valueType) {
        int index = value.indexOf('/');
        if(index > 0) {
            this.name = value.substring(index+ 1);
            this.simpleName = value.substring(0, index);
        } else {
            this.name = value;
            this.simpleName = null;

        }
        this.valueType = valueType;
    }

    public String getName() {
        return name;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public Class<?> getValueType() {
        return valueType;
    }
}
