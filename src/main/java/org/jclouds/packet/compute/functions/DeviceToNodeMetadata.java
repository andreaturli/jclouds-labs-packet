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

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.packet.domain.Device;
import org.jclouds.packet.domain.IpAddress;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * Transforms an {@link Device} to the jclouds portable model.
 */
@Singleton
public class DeviceToNodeMetadata implements Function<Device, NodeMetadata> {

    @Resource
    @Named(ComputeServiceConstants.COMPUTE_LOGGER)
    protected Logger logger = Logger.NULL;

    private final PlanToHardware planToHardware;
    private final OperatingSystemToImage operatingSystemToImage;
    private final FacilityToLocation facilityToLocation;
    private final Function<Device.State, NodeMetadata.Status> toPortableStatus;
    private final GroupNamingConvention groupNamingConvention;

    @Inject
    DeviceToNodeMetadata(PlanToHardware planToHardware, OperatingSystemToImage operatingSystemToImage, FacilityToLocation facilityToLocation,
                         Function<Device.State, NodeMetadata.Status> toPortableStatus,
                         GroupNamingConvention.Factory groupNamingConvention) {
        this.planToHardware = checkNotNull(planToHardware, "planToHardware cannot be null");
        this.operatingSystemToImage = checkNotNull(operatingSystemToImage, "operatingSystemToImage cannot be null");
        this.facilityToLocation = checkNotNull(facilityToLocation, "facilityToLocation cannot be null");
        this.toPortableStatus = checkNotNull(toPortableStatus, "toPortableStatus cannot be null");
        this.groupNamingConvention = checkNotNull(groupNamingConvention, "groupNamingConvention cannot be null")
                .createWithoutPrefix();
    }

    @Override
    public NodeMetadata apply(Device input) {
        NodeMetadataBuilder builder = new NodeMetadataBuilder();
        builder.ids(input.id());
        builder.name(input.hostname());
        builder.hostname(input.hostname());
        builder.group(groupNamingConvention.extractGroup(input.hostname()));
        builder.location(facilityToLocation.apply(input.facility()));
        builder.hardware(planToHardware.apply(input.plan()));
        builder.imageId(input.operatingSystem().slug());
        builder.operatingSystem(operatingSystemToImage.apply(input.operatingSystem()).getOperatingSystem());
        builder.status(toPortableStatus.apply(input.state()));

        if (!input.ipAddresses().isEmpty()) {
            builder.publicAddresses(FluentIterable
                    .from(input.ipAddresses())
                    .filter(new Predicate<IpAddress>() {
                        @Override
                        public boolean apply(IpAddress input) {
                            return input.publicAddress();
                        }
                    })
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
                    .filter(new Predicate<IpAddress>() {
                        @Override
                        public boolean apply(IpAddress input) {
                            return !input.publicAddress();
                        }
                    })
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
