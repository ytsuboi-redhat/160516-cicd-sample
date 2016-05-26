package cicd.sandbox.web.ws.rs;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * @author <a href="mailto:ytsuboi@redhat.com">Yosuke TSUBOI</a>
 * @since 2016/04/26
 */
@Path("/sandbox")
public interface SandboxResource {

    @GET
    @Path("/{key}")
    String get(@PathParam("key") String key);

    @POST
    @Path("/{key}/{value}")
    void post(@PathParam("key") String key, @PathParam("value") String value);

    @PUT
    @Path("/{key}/{value}")
    void put(@PathParam("key") String key, @PathParam("value") String value);
    
    @DELETE
    @Path("/{key}")
    void remove(@PathParam("key") String key);
}
