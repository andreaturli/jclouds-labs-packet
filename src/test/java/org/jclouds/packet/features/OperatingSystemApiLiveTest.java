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
import static org.testng.util.Strings.isNullOrEmpty;

import java.util.concurrent.atomic.AtomicInteger;

import org.jclouds.packet.compute.internal.BasePacketApiLiveTest;
import org.jclouds.packet.domain.OperatingSystem;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

@Test(groups = "live", testName = "OperatingSystemApiLiveTest")
public class OperatingSystemApiLiveTest extends BasePacketApiLiveTest {

   public void testListOperatingSystems() {
      final AtomicInteger found = new AtomicInteger(0);
      assertTrue(Iterables.all(api().list(), new Predicate<OperatingSystem>() {
         @Override
         public boolean apply(OperatingSystem input) {
            found.incrementAndGet();
            return !isNullOrEmpty(input.slug());
         }
      }), "All images must have the 'slug' field populated");
      assertTrue(found.get() > 0, "Expected some images to be returned");
   }
   
   // TODO: Delete live test once the create/transfer operations are implemented
   
   private OperatingSystemApi api() {
      return api.operatingSystemApi();
   }
}
