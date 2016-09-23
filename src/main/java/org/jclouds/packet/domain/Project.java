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

import java.security.PublicKey;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Project {

    public abstract String id();
    public abstract String name();
    @Nullable
    public abstract String fingerprint();
    @Nullable
    public abstract PublicKey publicKey();

    @SerializedNames({ "id", "name", "fingerprint", "public_key" })
    public static Project create(String id, String name, String fingerprint, PublicKey publicKey) {
        return new AutoValue_Project(id, name, fingerprint, publicKey);
    }

    Project() {}
}
