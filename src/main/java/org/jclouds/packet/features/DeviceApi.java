/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.packet.features;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.packet.domain.Device;
import org.jclouds.packet.filters.AddXAuthTokenToRequest;
import org.jclouds.packet.functions.URIParser;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
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
    @Fallback(EmptyListOnNotFoundOr404.class)
    List<Device> list();

    @Named("device:create")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @MapBinder(BindToJsonPayload.class)
    @ResponseParser(URIParser.class)
    URI create(@PayloadParam("hostname") String hostname, @PayloadParam("plan") String plan,
               @PayloadParam("billing_cycle") String billingCycle, @PayloadParam("facility") String facility,
               @PayloadParam("features") Map<String, String> features, @PayloadParam("operating_system") String operatingSystem,
               @PayloadParam("locked") boolean locked, @PayloadParam("userdata") String userdata, @PayloadParam("tags") List<String> tags
                );

    @Named("device:get")
    @GET
    @Path("/{id}")
    @Fallback(NullOnNotFoundOr404.class)
    @Nullable
    Device get(@PathParam("id") String id);

    @Named("device:delete")
    @DELETE
    @Path("/{id}")
    @Fallback(VoidOnNotFoundOr404.class)
    void delete(@PathParam("id") String id);
}
