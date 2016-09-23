package org.jclouds.packet.features;

import java.util.List;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.packet.domain.Facility;
import org.jclouds.packet.filters.AddXAuthTokenToRequest;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

@Path("/facilities")
@Consumes(MediaType.APPLICATION_JSON)
@RequestFilters(AddXAuthTokenToRequest.class)
public interface FacilityApi {

    /**
     * List all facilities
     */
    @Named("facility:list")
    @GET
    @SelectJson("facilities")
    @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
    List<Facility> list();
}
