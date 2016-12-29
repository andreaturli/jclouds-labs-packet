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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.jclouds.packet.compute.internal.BasePacketApiLiveTest;
import org.jclouds.packet.domain.SshKey;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;

@Test(groups = "live", testName = "SshSshKeyApiLiveTest")
public class SshKeyApiLiveTest extends BasePacketApiLiveTest {

   private SshKey dsa;

   public void testCreateSshKey() {
      URI sshUri = api().create("jclouds-test-dsa", loadSshKey("/ssh-dsa.pub"));
      assertNotNull(sshUri);
   }
   
   @Test(dependsOnMethods = "testCreateSshKey")
   public void testListSshKeys() {
      List<SshKey> keys = api().list();
      assertTrue(keys.size() >= 2, "At least the two created keys must exist");
   }
   
   @Test(dependsOnMethods = "testCreateSshKey")
   public void testGetSshKey() {
      assertEquals(api().get(dsa.id()).fingerprint(), dsa.fingerprint());
   }
   

   @AfterClass(alwaysRun = true)
   public void testDeleteSshKey() {
      if (dsa != null) {
         api().delete(dsa.id());
         List<SshKey> keys = api().list();
         assertFalse(keys.contains(dsa), "dsa key must not be present in list");
      }
   }
   
   private String loadSshKey(String resourceName) {
      try {
         return Resources.toString(getClass().getResource(resourceName), Charsets.UTF_8);
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   private SshKeyApi api() {
      return api.sshKeyApi();
   }
}
