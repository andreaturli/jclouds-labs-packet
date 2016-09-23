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
package org.jclouds.packet.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Location;
import org.jclouds.logging.Logger;
import org.jclouds.packet.domain.Device;
import org.jclouds.packet.domain.IpAddress;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;

/**
 * Transforms an {@link Device} to the jclouds portable model.
 */
@Singleton
public class DeviceToNodeMetadata implements Function<Device, NodeMetadata> {

    @Resource
    @Named(ComputeServiceConstants.COMPUTE_LOGGER)
    protected Logger logger = Logger.NULL;

    private final Supplier<Map<String, ? extends Image>> images;
    private final Supplier<Map<String, ? extends Hardware>> hardwares;
    private final Supplier<Set<? extends Location>> locations;
    private final Function<Device.State, NodeMetadata.Status> toPortableStatus;
    private final GroupNamingConvention groupNamingConvention;

    @Inject
    DeviceToNodeMetadata(Supplier<Map<String, ? extends Image>> images,
                         Supplier<Map<String, ? extends Hardware>> hardwares,
                         @Memoized Supplier<Set<? extends Location>> locations,
                         Function<Device.State, NodeMetadata.Status> toPortableStatus,
                         GroupNamingConvention.Factory groupNamingConvention) {
        this.images = checkNotNull(images, "images cannot be null");
        this.hardwares = checkNotNull(hardwares, "hardwares cannot be null");
        this.locations = checkNotNull(locations, "locations cannot be null");
        this.toPortableStatus = checkNotNull(toPortableStatus, "toPortableStatus cannot be null");
        this.groupNamingConvention = checkNotNull(groupNamingConvention, "groupNamingConvention cannot be null")
                .createWithoutPrefix();
    }

    @Override
    public NodeMetadata apply(Device input) {
        NodeMetadataBuilder builder = new NodeMetadataBuilder();
        builder.ids(String.valueOf(input.id()));
        builder.name(input.hostname());
        builder.hostname(input.hostname());
        builder.group(groupNamingConvention.extractGroup(input.hostname()));

//        builder.hardware(getHardware(input.sizeSlug()));
//        builder.location(getLocation(input.region()));
//
//        Optional<? extends Image> image = findImage(input.image(), input.region().slug());
//        if (image.isPresent()) {
//            builder.imageId(image.get().getId());
//            builder.operatingSystem(image.get().getOperatingSystem());
//        } else {
//            logger.info(">> image with id %s for droplet %s was not found. "
//                            + "This might be because the image that was used to create the droplet has a new id.",
//                    input.operatingSystem().slug()), input.id());
//        }

//        builder.backendStatus(input.status().name());
        builder.status(toPortableStatus.apply(input.state()));

        if (!input.ipAddresses().isEmpty()) {
            builder.publicAddresses(FluentIterable
                    .from(input.ipAddresses())
                    .transform(new Function<IpAddress, String>() {
                        @Override
                        public String apply(final IpAddress input) {
                            return input.address();
                        }
                    })
            );
        }

        if (!input.ipAddresses().isEmpty()) {
            builder.privateAddresses(FluentIterable
                    .from(input.ipAddresses())
                    .transform(new Function<IpAddress, String>() {
                        @Override
                        public String apply(final IpAddress input) {
                            return input.address();
                        }
                    })
            );
        }

        return builder.build();
    }
}
