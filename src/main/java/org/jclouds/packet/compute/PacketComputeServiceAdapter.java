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
package org.jclouds.packet.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.filter;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.location.Provider;
import org.jclouds.logging.Logger;
import org.jclouds.packet.PacketApi;
import org.jclouds.packet.compute.options.PacketTemplateOptions;
import org.jclouds.packet.compute.utils.URIs;
import org.jclouds.packet.domain.ActionType;
import org.jclouds.packet.domain.BillingCycle;
import org.jclouds.packet.domain.Device;
import org.jclouds.packet.domain.Facility;
import org.jclouds.packet.domain.OperatingSystem;
import org.jclouds.packet.domain.Plan;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * defines the connection between the {@link org.jclouds.packet.PacketApi} implementation and
 * the jclouds {@link org.jclouds.compute.ComputeService}
 */
@Singleton
public class PacketComputeServiceAdapter implements ComputeServiceAdapter<Device, Plan, OperatingSystem, Facility> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final PacketApi api;
   private final Predicate<String> nodeRunningPredicate;
   private final String projectId;

   @Inject
   public PacketComputeServiceAdapter(PacketApi api, @Provider final Supplier<Credentials> creds, @Named(TIMEOUT_NODE_RUNNING) Predicate<String> nodeRunningPredicate) {
      this.api = checkNotNull(api, "api");
      this.projectId = creds.get().identity;
      this.nodeRunningPredicate = nodeRunningPredicate;
   }

   @Override
   public NodeAndInitialCredentials<Device> createNodeWithGroupEncodedIntoName(String group, String name, Template template) {

      PacketTemplateOptions templateOptions = template.getOptions().as(PacketTemplateOptions.class);
      checkNotNull(templateOptions.getLoginPrivateKey(), "login privateKey must not be null");
      // TODO make it configurable
      Map<String, String> features = Maps.newHashMap();
      // TODO make it configurable
      BillingCycle billingCycle = BillingCycle.HOURLY;
      // TODO make it configurable
      boolean locked = false;

      String userdata = Optional.fromNullable(templateOptions.getUserData()).or("");
      Set<String> tags = templateOptions.getTags();

      String plan = template.getHardware().getId();
      String facility = template.getLocation().getId();
      String operatingSystem = template.getImage().getId();

      URI deviceUriLocation = api.deviceApi(projectId).create(
              name,
              plan,
              billingCycle.value(),
              facility,
              features,
              operatingSystem,
              locked,
              userdata,
              tags);
      String deviceId = URIs.toId(deviceUriLocation);
      nodeRunningPredicate.apply(deviceId);
      Device device = api.deviceApi(projectId).get(deviceId);


      LoginCredentials defaultCredentials = LoginCredentials.builder()
              .user("root")
              //.password(device.rootPassword())
              .privateKey(templateOptions.getLoginPrivateKey())
              .build();

      return new NodeAndInitialCredentials<Device>(device, device.id(), defaultCredentials);
   }

   @Override
   public Iterable<Plan> listHardwareProfiles() {
      return Iterables.filter(api.planApi().list(), new Predicate<Plan>() {
         @Override
         public boolean apply(Plan input) {
            return input.line().equals("baremetal");
         }
      });
   }

   @Override
   public Iterable<OperatingSystem> listImages() {
      return api.operatingSystemApi().list();
   }

   @Override
   public OperatingSystem getImage(final String id) {

      Optional<OperatingSystem> first = Iterables.tryFind(api.operatingSystemApi().list(), new Predicate<OperatingSystem>() {
         @Override
         public boolean apply(OperatingSystem input) {
            return input.slug().equals(id);
         }
      });

      if (!first.isPresent()) throw new IllegalStateException("Cannot find image with the required slug " + id);
      return first.get();
   }

   @Override
   public Iterable<Facility> listLocations() {
      return api.facilityApi().list();
   }

   @Override
   public Device getNode(String id) {
      return api.deviceApi(projectId).get(id);
   }

   @Override
   public void destroyNode(String id) {
      api.deviceApi(projectId).delete(id);
   }

   @Override
   public void rebootNode(String id) {
      if (api.deviceApi(projectId).get(id).state() != Device.State.ACTIVE) {
         api.deviceApi(projectId).actions(id, ActionType.REBOOT);
      }
   }

   @Override
   public void resumeNode(String id) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void suspendNode(String id) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Iterable<Device> listNodes() {
     return api.deviceApi(projectId).list();
   }

   @Override
   public Iterable<Device> listNodesByIds(final Iterable<String> ids) {
      return filter(listNodes(), new Predicate<Device>() {
         @Override
         public boolean apply(Device device) {
            return contains(ids, String.valueOf(device.id()));
         }
      });
   }

}
