package soya.framework.curl.application.api;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Component;
import soya.framework.curl.support.CurlUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/curl")
@Api(value = "Curl Service")
public class CurlResource {

    @POST
    @Path("/base64/encode")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response base64Encoder(String src) {
        return Response.status(200).entity(CurlUtils.base64Encode(src)).build();
    }

    @POST
    @Path("/base64/decode")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response base64Decoder(String src) {
        return Response.status(200).entity(CurlUtils.base64Decode(src)).build();
    }

    @POST
    @Path("/yaml/json")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response yamlToJson(String yaml) {
        return Response.status(200).entity(CurlUtils.yamlToJson(yaml)).build();
    }

    @POST
    @Path("/evaluate/jsonpath")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response jsonPath(@HeaderParam("exp") String exp, String json) {
        return Response.status(200).entity(CurlUtils.jsonPath(exp, json)).build();
    }

    @POST
    @Path("/evaluate/jolt")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response jolt(@HeaderParam("exp") String exp, String json) {
        String jsonpath = CurlUtils.base64Decode(exp);
        return Response.status(200).entity(CurlUtils.jsonPath(jsonpath, json)).build();
    }

    @POST
    @Path("/evaluate/mustache")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response mustache(@HeaderParam("exp") String exp, String json) {
        String jsonpath = CurlUtils.base64Decode(exp);
        return Response.status(200).entity(CurlUtils.jsonPath(jsonpath, json)).build();
    }
}
