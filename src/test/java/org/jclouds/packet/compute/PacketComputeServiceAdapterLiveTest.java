package org.jclouds.packet.compute;/*
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.util.Properties;
import java.util.Random;

import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.packet.PacketApi;
import org.jclouds.packet.compute.internal.BasePacketApiLiveTest;
import org.jclouds.packet.compute.options.PacketTemplateOptions;
import org.jclouds.packet.domain.Device;
import org.jclouds.packet.domain.Facility;
import org.jclouds.packet.domain.OperatingSystem;
import org.jclouds.packet.domain.Plan;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Injector;
import com.google.inject.Module;

@Test(groups = "live", singleThreaded = true, testName = "PacketComputeServiceAdapterLiveTest")
public class PacketComputeServiceAdapterLiveTest extends BasePacketApiLiveTest {

   private PacketComputeServiceAdapter adapter;
   private TemplateBuilder templateBuilder;
   private NodeAndInitialCredentials<Device> guest;

   @AfterClass(alwaysRun = true)
   protected void tearDown() {
      if (guest != null) {
         adapter.destroyNode(guest.getNode().id());
      }
      super.tearDown();
   }

   @Override
   protected PacketApi create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      adapter = injector.getInstance(PacketComputeServiceAdapter.class);
      templateBuilder = injector.getInstance(TemplateBuilder.class);
      return injector.getInstance(PacketApi.class);
   }

   public void testCreateNodeWithGroupEncodedIntoNameThenStoreCredentials() {
      String name = "server" + new Random().nextInt();
      Template template = templateBuilder.imageId("ubuntu_14_04").locationId("ams1").build();
      PacketTemplateOptions options = template.getOptions().as(PacketTemplateOptions.class);
      guest = adapter.createNodeWithGroupEncodedIntoName("test", name, template);
      assertEquals(guest.getNodeId(), guest.getNode().id());
   }

   public void testListHardwareProfiles() {
      Iterable<Plan> plans = adapter.listHardwareProfiles();
      assertFalse(Iterables.isEmpty(plans));

      for (Plan plan : plans) {
         assertNotNull(plan);
      }
   }

   public void testListLocations() {
      Iterable<Facility> facilities = adapter.listLocations();
      assertFalse(Iterables.isEmpty(facilities));

      for (Facility facility : facilities) {
         assertNotNull(facility);
      }
   }

   public void testListImages() {
      Iterable<OperatingSystem> operatingSystems = adapter.listImages();
      assertFalse(Iterables.isEmpty(operatingSystems));

      for (OperatingSystem operatingSystem : operatingSystems) {
         assertNotNull(operatingSystem);
      }
   }

   public void testListNodes() {
      Iterable<Device> devices = adapter.listNodes();
      assertFalse(Iterables.isEmpty(devices));

      for (Device device : devices) {
         assertNotNull(device);
      }
   }

   @Override
   protected Iterable<Module> setupModules() {
      return ImmutableSet.<Module> of(getLoggingModule(), new SshjSshClientModule());
   }
}
