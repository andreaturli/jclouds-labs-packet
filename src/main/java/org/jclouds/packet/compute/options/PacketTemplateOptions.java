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

import java.util.Set;

import org.jclouds.compute.options.TemplateOptions;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * Custom options for the Packet API.
 */
public class PacketTemplateOptions extends TemplateOptions implements Cloneable {

    private Set<String> sshKeyIds = ImmutableSet.of();
    private boolean autoCreateKeyPair = true;

    /**
     * Sets the ssh key ids to be added to the device.
     */
    public PacketTemplateOptions sshKeyIds(Iterable<String> sshKeyIds) {
        this.sshKeyIds = ImmutableSet.copyOf(checkNotNull(sshKeyIds, "sshKeyIds cannot be null"));
        return this;
    }

    /**
     * Sets whether an SSH key pair should be created automatically.
     */
    public PacketTemplateOptions autoCreateKeyPair(boolean autoCreateKeyPair) {
        this.autoCreateKeyPair = autoCreateKeyPair;
        return this;
    }

    public Set<String> getSshKeyIds() {
        return sshKeyIds;
    }

    public boolean getAutoCreateKeyPair() {
        return autoCreateKeyPair;
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
            eTo.autoCreateKeyPair(autoCreateKeyPair);
            eTo.sshKeyIds(sshKeyIds);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), autoCreateKeyPair, sshKeyIds);
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
        return super.equals(other) &&
                equal(this.autoCreateKeyPair, other.autoCreateKeyPair) &&
                equal(this.sshKeyIds, other.sshKeyIds);
    }

    @Override
    public ToStringHelper string() {
        ToStringHelper toString = super.string().omitNullValues();
        if (!sshKeyIds.isEmpty()) {
            toString.add("sshKeyIds", sshKeyIds);
        }
        toString.add("autoCreateKeyPair", autoCreateKeyPair);
        return toString;
    }

    public static class Builder {

        /**
         * @see PacketTemplateOptions#sshKeyIds
         */
        public static PacketTemplateOptions sshKeyIds(Iterable<String> sshKeyIds) {
            PacketTemplateOptions options = new PacketTemplateOptions();
            return options.sshKeyIds(sshKeyIds);
        }

        /**
         * @see PacketTemplateOptions#autoCreateKeyPair
         */
        public static PacketTemplateOptions autoCreateKeyPair(boolean autoCreateKeyPair) {
            PacketTemplateOptions options = new PacketTemplateOptions();
            return options.autoCreateKeyPair(autoCreateKeyPair);
        }
    }
}
