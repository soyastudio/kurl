package soya.framework.curl;

public interface Callback<T> {
    void onSuccess(Call<T> call, Response<T> response);

    void onFailure(Call<T> call, Throwable t);
}
