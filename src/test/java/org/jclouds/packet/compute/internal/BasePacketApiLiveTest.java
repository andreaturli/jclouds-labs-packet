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
package org.jclouds.packet.compute.internal;

import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.testng.Assert.assertTrue;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.compute.config.ComputeServiceProperties;
import org.jclouds.packet.PacketApi;

import com.google.common.base.Predicate;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

public class BasePacketApiLiveTest extends BaseApiLiveTest<PacketApi> {

   private Predicate<String> deviceRunning;

   public BasePacketApiLiveTest() {
      provider = "packet";
   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.put(ComputeServiceProperties.POLL_INITIAL_PERIOD, 1000);
      props.put(ComputeServiceProperties.POLL_MAX_PERIOD, 10000);
      props.put(ComputeServiceProperties.TIMEOUT_IMAGE_AVAILABLE, TimeUnit.MINUTES.toMillis(45));
      return props;
   }

   @Override
   protected PacketApi create(Properties props, Iterable<Module> modules) {
      Injector injector = newBuilder().modules(modules).overrides(props).buildInjector();
      deviceRunning = injector.getInstance(Key.get(new TypeLiteral<Predicate<String>>(){},
            Names.named(TIMEOUT_NODE_RUNNING)));
      return injector.getInstance(PacketApi.class);
   }

   protected void assertNodeRunning(String deviceId) {
      assertTrue(deviceRunning.apply(deviceId), String.format("Droplet %s did not start in the configured timeout", deviceId));
   }

//   protected Facility firstAvailableRegion() {
//      return api.facilityApi().list().concat().firstMatch(new Predicate<Facility>() {
//         @Override
//         public boolean apply(Facility input) {
//            return input.available();
//         }
//      }).get();
//   }
//
//   protected Size cheapestSizeInRegion(final Region region) {
//      return sizesByPrice().min(api.sizeApi().list().concat().filter(new Predicate<Size>() {
//         @Override
//         public boolean apply(Size input) {
//            return input.available() && input.regions().contains(region.slug());
//         }
//      }));
//   }
//
//   protected Image ubuntuImageInRegion(final Region region) {
//      return api.imageApi().list().concat().firstMatch(new Predicate<Image>() {
//         @Override
//         public boolean apply(Image input) {
//            return "Ubuntu".equalsIgnoreCase(input.distribution()) && !isNullOrEmpty(input.slug())
//                  && input.regions().contains(region.slug());
//         }
//      }).get();
//   }
//
//   protected static Ordering<Size> sizesByPrice() {
//      return new Ordering<Size>() {
//         @Override
//         public int compare(Size left, Size right) {
//            return ComparisonChain.start()
//                  .compare(left.priceHourly(), right.priceHourly())
//                  .compare(left.priceMonthly(), right.priceMonthly())
//                  .result();
//         }
//      };
//   }
}
