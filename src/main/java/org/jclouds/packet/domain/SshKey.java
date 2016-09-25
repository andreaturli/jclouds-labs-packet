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
package org.jclouds.packet.domain;

import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SshKey {

    @AutoValue
    public abstract static class User {

        public abstract String href();

        @SerializedNames({ "href" })
        public static User create(String href) {
            return new AutoValue_SshKey_User(href);
        }
    }

    public abstract String id();
    public abstract String label();
    public abstract String key();
    public abstract String fingerprint();
    public abstract String createdAt();
    public abstract String updatedAt();
    public abstract User user();
    public abstract String href();

    @SerializedNames({"id", "label", "key", "fingerprint", "created_at", "updated_at", "user", "href"})
    public static SshKey create(String id, String label, String key, String fingerprint, String createdAt, String updatedAt, User user, String href) {
        return new AutoValue_SshKey(id, label, key, fingerprint, createdAt, updatedAt, user, href);
    }

    SshKey() {}
}
