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

import java.util.List;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;

/**
* {"id":"e69c0169-4726-46ea-98f1-939c9e8a3607","slug":"baremetal_0","name":"Type 0","description":"Our Type 0 configuration is a general use \"cloud killer\" server, with a Intel Atom 2.4Ghz processor and 8GB of RAM.","line":"baremetal",
  "specs":{"cpus":[{"count":1,"type":"Intel Atom C2550 @ 2.4Ghz"}],"memory":{"total":"8GB"},"drives":[{"count":1,"size":"80GB","type":"SSD"}],"nics":[{"count":2,"type":"1Gbps"}],
  "features":{"raid":false,"txt":true}},"available_in":[{"href":"/facilities/2b70eb8f-fa18-47c0-aba7-222a842362fd"},{"href":"/facilities/8e6470b3-b75e-47d1-bb93-45b225750975"}],
  "pricing":{"hour":0.05}}
 */
@AutoValue
public abstract class Plan {

    public abstract String id();
    public abstract String slug();
    public abstract String name();
    public abstract String description();
    public abstract String line();
    @Nullable
    public abstract Specs specs();
    public abstract List<Href> availableIn();
    public abstract Pricing pricing();

    @SerializedNames({"id", "slug", "name", "description", "line", "specs", "available_in", "pricing"})
    public static Plan create(final String id, String slug, String name, String description, String line, Specs specs, List<Href> availableIn, Pricing pricing) {
        return new AutoValue_Plan(id, slug, name, description, line,
                specs,
                availableIn == null ? ImmutableList.<Href> of() : ImmutableList.copyOf(availableIn),
                pricing
        );
    }

    Plan() {}
}
