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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.util.Strings.isNullOrEmpty;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.packet.compute.internal.BasePacketApiLiveTest;
import org.jclouds.packet.compute.utils.URIs;
import org.jclouds.packet.domain.BillingCycle;
import org.jclouds.packet.domain.Device;
import org.jclouds.ssh.SshKeys;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

@Test(groups = "live", testName = "DeviceApiLiveTest")
public class DeviceApiLiveTest extends BasePacketApiLiveTest {

   private URI keyUri;
   private String deviceId;

   @BeforeClass
   public void setupDevice() {
      Map<String, String> keyPair = SshKeys.generate();
      keyUri = api.sshKeyApi().create(prefix + "-device-livetest", keyPair.get("public"));
   }

   @AfterClass(alwaysRun = true)
   public void tearDown() {
      if (keyUri != null) {
         api.sshKeyApi().delete(URIs.toId(keyUri));
      }
   }

   public void testCreate() {
      URI deviceCreated = api().create(
              prefix + "-device-livetest", // name
              "baremetal_0", // plan.slug()
              BillingCycle.HOURLY.value(),
              "ewr1", // facility.code()
              ImmutableMap.<String, String>of(), // features,
              "ubuntu_16_04", //operatingSystem.slug(),
              false, // locked
              "", // userdata
              ImmutableSet.<String>of() // tags
      );
      
      deviceId = URIs.toId(deviceCreated);
      assertNodeRunning(deviceId);
      Device device = api().get(deviceId);
      assertNotNull(device, "Device must not be null");
   }

   @Test(groups = "live", dependsOnMethods = "testCreate")
   public void testList() {
      final AtomicInteger found = new AtomicInteger(0);
      assertTrue(Iterables.all(api().list(), new Predicate<Device>() {
         @Override
         public boolean apply(Device input) {
            found.incrementAndGet();
            return !isNullOrEmpty(input.id());
         }
      }), "All devices must have the 'id' field populated");
      assertTrue(found.get() > 0, "Expected some devices to be returned");
   }

   @Test(groups = "live", dependsOnMethods = "testList", alwaysRun = true)
   public void testDelete() throws InterruptedException {
      if (deviceId != null) {
         api().delete(deviceId);
         assertNodeTerminated(deviceId);
         assertNull(api().get(deviceId));
      }
   }

   private DeviceApi api() {
      return api.deviceApi(identity);
   }
}
