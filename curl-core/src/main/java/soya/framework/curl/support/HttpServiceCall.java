package soya.framework.curl.support;

import soya.framework.curl.Call;
import soya.framework.curl.Callback;
import soya.framework.curl.Request;
import soya.framework.curl.Response;

public final class HttpServiceCall implements Call<String> {
    private final Request request;

    public HttpServiceCall(Request request) {
        this.request = request;
    }

    @Override
    public Request getRequest() {
        return request;
    }

    @Override
    public Response<String> execute() {
        return null;
    }

    @Override
    public void commit(Callback<String> callback) {

    }
}
