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
package org.jclouds.packet.compute.options;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.compute.options.TemplateOptions;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Custom options for the Packet API.
 */
public class PacketTemplateOptions extends TemplateOptions implements Cloneable {

   private String userData;

   /**
    * Enables a private network interface if the region supports private networking.
    */
   public PacketTemplateOptions userData(String userData) {
      this.userData = checkNotNull(userData, "userdata");
      return this;
   }

   public String getUserData() {
      return userData;
   }

   @Override
   public PacketTemplateOptions clone() {
      PacketTemplateOptions options = new PacketTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof PacketTemplateOptions) {
         PacketTemplateOptions eTo = PacketTemplateOptions.class.cast(to);
         eTo.userData(userData);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), userData);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!super.equals(obj)) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      PacketTemplateOptions other = (PacketTemplateOptions) obj;
      return super.equals(other) && equal(this.userData, other.userData);
   }

   @Override
   public ToStringHelper string() {
      ToStringHelper toString = super.string().omitNullValues();
      toString.add("userData", userData);
      return toString;
   }

   public static class Builder {

      /**
       * @see PacketTemplateOptions#userData
       */
      public static PacketTemplateOptions userData(String userData) {
         PacketTemplateOptions options = new PacketTemplateOptions();
         return options.userData(userData);
      }

   }
}
