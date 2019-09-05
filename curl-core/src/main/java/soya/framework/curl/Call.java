package soya.framework.curl;

public interface Call<T> {
    Request getRequest();

    Response<T> execute();

    void commit(Callback<T> callback);

}
