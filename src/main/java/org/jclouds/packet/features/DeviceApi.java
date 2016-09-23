package org.jclouds.packet.features;

import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.packet.domain.Device;
import org.jclouds.packet.domain.Href;
import org.jclouds.packet.filters.AddXAuthTokenToRequest;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.binders.BindToJsonPayload;

@Path("/projects/{projectId}/devices")
@Consumes(MediaType.APPLICATION_JSON)
@RequestFilters(AddXAuthTokenToRequest.class)
public interface DeviceApi {

    /**
     * List all devices
     */
    @Named("device:list")
    @GET
    @SelectJson("devices")
    @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
    List<Device> list();

    @Named("device:create")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @MapBinder(BindToJsonPayload.class)
    Href create(@PayloadParam("hostname") String hostname, @PayloadParam("plan") String plan,
                @PayloadParam("billing_cycle") String billingCycle, @PayloadParam("facility") String facility,
                @PayloadParam("features") Map<String, String> features, @PayloadParam("operating_system") String operatingSystem,
                @PayloadParam("locked") boolean locked, @PayloadParam("userdata") String userdata, @PayloadParam("tags") List<String> tags
                );

    @Named("device:get")
    @GET
    @Path("/{id}")
    @Fallback(Fallbacks.NullOnNotFoundOr404.class)
    @Nullable
    Device get(@PathParam("id") String id);
}
