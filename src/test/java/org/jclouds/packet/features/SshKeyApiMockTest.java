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
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Map;

import org.jclouds.packet.compute.internal.BasePacketApiMockTest;
import org.jclouds.packet.domain.SshKey;
import org.testng.annotations.Test;

import com.google.common.reflect.TypeToken;

@Test(groups = "unit", testName = "SshKeyApiMockTest", singleThreaded = true)
public class SshKeyApiMockTest extends BasePacketApiMockTest {

   public void testListKeys() throws InterruptedException {
      server.enqueue(jsonResponse("/keys-first.json"));
      server.enqueue(jsonResponse("/keys-last.json"));

      Iterable<SshKey> keys = api.sshKeyApi().list();

      assertEquals(size(keys), 7); // Force the PagedIterable to advance
      assertEquals(server.getRequestCount(), 2);

      assertSent(server, "GET", "/account/keys");
      assertSent(server, "GET", "/account/keys?page=2&per_page=5");
   }

   public void testListSshKeysReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<SshKey> keys = api.sshKeyApi().list();

      assertTrue(isEmpty(keys));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/account/keys");
   }
   
   public void testCreateSshKey() throws InterruptedException {
      server.enqueue(jsonResponse("/key.json").setStatus("HTTP/1.1 201 Created"));
      
      String dsa = stringFromResource("/ssh-dsa.pub");
      
      URI key = api.sshKeyApi().create("foo", dsa);
      
      assertEquals(key, keyFromResource("/key.json"));
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "POST", "/account/keys", String.format("{\"name\":\"foo\", \"public_key\":\"%s\"}", dsa));
   }
   
   public void testGetSshKey() throws InterruptedException {
      server.enqueue(jsonResponse("/key.json"));

      SshKey key = api.sshKeyApi().get("1");

      assertEquals(key, keyFromResource("/key.json"));
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/account/keys/1");
   }

   public void testGetSshKeyReturns404() throws InterruptedException {
      server.enqueue(response404());

      SshKey key = api.sshKeyApi().get("1");

      assertNull(key);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/account/keys/1");
   }
   
   public void testGetSshKeyUsingFingerprint() throws InterruptedException {
      server.enqueue(jsonResponse("/key.json"));

      SshKey key = api.sshKeyApi().get("1a:cc:9b:88:c8:4f:b8:77:96:15:d2:0c:95:86:ff:90");

      assertEquals(key, keyFromResource("/key.json"));
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/account/keys/1a:cc:9b:88:c8:4f:b8:77:96:15:d2:0c:95:86:ff:90");
   }

   public void testGetSshKeyUsingFingerprintReturns404() throws InterruptedException {
      server.enqueue(response404());

      SshKey key = api.sshKeyApi().get("1a:cc:9b:88:c8:4f:b8:77:96:15:d2:0c:95:86:ff:90");

      assertNull(key);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/account/keys/1a:cc:9b:88:c8:4f:b8:77:96:15:d2:0c:95:86:ff:90");
   }
   
   public void testDeleteSshKey() throws InterruptedException {
      server.enqueue(response204());

      api.sshKeyApi().delete("1");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/account/keys/1");
   }

   public void testDeleteSshKeyReturns404() throws InterruptedException {
      server.enqueue(response404());

      api.sshKeyApi().delete("1");

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/account/keys/1");
   }
   
   public void testDeleteSshKeyUsingFingerprint() throws InterruptedException {
      server.enqueue(response204());

      api.sshKeyApi().delete("1a:cc:9b:88:c8:4f:b8:77:96:15:d2:0c:95:86:ff:90");
      
      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/account/keys/1a:cc:9b:88:c8:4f:b8:77:96:15:d2:0c:95:86:ff:90");
   }

   public void testDeleteSshKeyUsingfingerprintReturns404() throws InterruptedException {
      server.enqueue(response404());

      api.sshKeyApi().delete("1a:cc:9b:88:c8:4f:b8:77:96:15:d2:0c:95:86:ff:90");

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/account/keys/1a:cc:9b:88:c8:4f:b8:77:96:15:d2:0c:95:86:ff:90");
   }
   
   private SshKey keyFromResource(String resource) {
      return onlyObjectFromResource(resource, new TypeToken<Map<String, SshKey>>() {
         private static final long serialVersionUID = 1L;
      }); 
   }
}
