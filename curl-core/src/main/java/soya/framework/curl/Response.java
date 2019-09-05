package soya.framework.curl;

public interface Response<T> {
    String getName();

    String getRequestName();

    T getBody();
}
