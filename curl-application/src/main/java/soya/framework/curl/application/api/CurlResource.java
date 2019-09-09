package soya.framework.curl.application.api;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Component;
import soya.framework.curl.Curl;
import soya.framework.curl.CurlTemplate;
import soya.framework.curl.ServiceInvocation;
import soya.framework.curl.support.CurlUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

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
    @Path("/base64/zip")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response base64Zip(String src) {
        try {
            return Response.status(200).entity(CurlUtils.base64Zip(src)).build();
        } catch (IOException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/base64/unzip")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response base64Unzip(String src) {
        try {
            return Response.status(200).entity(CurlUtils.base64Unzip(src)).build();
        } catch (IOException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/text/placeholders")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response placeholders(@HeaderParam("token") String token, String text) {
        return Response.status(200).entity(CurlUtils.placeHolders(text, token, true)).build();
    }

    @POST
    @Path("/text/fragments")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response fragments(@HeaderParam("token") String token, String text) {
        return Response.status(200).entity(CurlUtils.fragments(text, token)).build();
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
        String jolt = CurlUtils.base64Decode(exp);
        return Response.status(200).entity(CurlUtils.jolt(jolt, json)).build();
    }

    @POST
    @Path("/evaluate/mustache")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response mustache(@HeaderParam("exp") String exp, String json) {
        String mustache = CurlUtils.base64Decode(exp);
        return Response.status(200).entity(CurlUtils.mustache(mustache, json)).build();
    }

    @POST
    @Path("/evaluate/curl")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response curl(String command) {
        return Response.status(200).entity(CurlUtils.toJson(Curl.fromCurl(command))).build();
    }

    @POST
    @Path("/template/builder")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response templateBuilder(String template) {
        CurlTemplate.CurlTemplateBuilder builder = CurlTemplate.builder(template);
        return Response.status(200).entity(CurlUtils.toJson(builder)).build();
    }

    @POST
    @Path("/template/invocations")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response invocation(String yaml) {
        return Response.status(200).entity(CurlUtils.toJson(ServiceInvocation.fromYaml(yaml))).build();
    }


    @POST
    @Path("/invoke/http")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_HTML})
    public Response http(String curl) {
        try {
            return Response.status(200).entity(CurlUtils.http(Curl.fromCurl(curl))).build();

        } catch (IOException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
}
