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

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.jclouds.packet.compute.internal.BasePacketApiMockTest;
import org.jclouds.packet.domain.Facility;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "FacilityApiMockTest", singleThreaded = true)
public class FacilityApiMockTest extends BasePacketApiMockTest {

   public void testListFacilities() throws InterruptedException {
      server.enqueue(jsonResponse("/facilities.json"));

      Iterable<Facility> facilities = api.facilityApi().list();

      assertEquals(size(facilities), 3); 
      assertEquals(server.getRequestCount(), 1);

      assertSent(server, "GET", "/facilities");
   }

   public void testListFacilitiesReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<Facility> facilities = api.facilityApi().list();

      assertTrue(isEmpty(facilities));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/facilities");
   }

}
