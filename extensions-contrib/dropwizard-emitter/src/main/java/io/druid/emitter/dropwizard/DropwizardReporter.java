/*
 * Licensed to Metamarkets Group Inc. (Metamarkets) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Metamarkets licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.druid.emitter.dropwizard;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.druid.emitter.dropwizard.reporters.DropwizardConsoleReporter;
import io.druid.emitter.dropwizard.reporters.DropwizardJMXReporter;

import java.io.IOException;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value= {
        @JsonSubTypes.Type(name="console", value = DropwizardConsoleReporter.class),
        @JsonSubTypes.Type(name="jmx", value = DropwizardJMXReporter.class),
})
public interface DropwizardReporter {

    public void start(MetricRegistry metricRegistry) throws IOException;
    public void flush() throws IOException;
    public void close() throws IOException;
}