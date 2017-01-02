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

import static org.testng.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.packet.compute.internal.BasePacketApiMockTest;
import org.jclouds.packet.domain.Facility;
import org.testng.annotations.Test;
import org.testng.util.Strings;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

@Test(groups = "live", testName = "FacilityApiLiveTest")
public class FacilityApiLiveTest extends BasePacketApiMockTest {
   
   public void testListFacilities() {
      final AtomicInteger found = new AtomicInteger(0);
      assertTrue(FluentIterable.from(api().list()).filter(new Predicate<Facility>() {
         @Override
         public boolean apply(Facility input) {
            found.incrementAndGet();
            return !Strings.isNullOrEmpty(input.name());
         }
      }).first().isPresent(), "All facilities must have the 'name' field populated");
      assertTrue(found.get() > 0, "Expected some facilities to be returned");
   }
   
   private FacilityApi api() {
      return api.facilityApi();
   }
}
