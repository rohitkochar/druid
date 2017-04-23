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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.metamx.emitter.service.ServiceMetricEvent;
import io.druid.emitter.dropwizard.eventconverters.SendAllDropwizardEventConverter;
import io.druid.emitter.dropwizard.eventconverters.WhiteListBasedConverter;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", defaultImpl = SendAllDropwizardEventConverter.class)
@JsonSubTypes(value= {
        @JsonSubTypes.Type(name="all", value = SendAllDropwizardEventConverter.class),
        @JsonSubTypes.Type(name="whiteList", value = WhiteListBasedConverter.class)
})
public interface DruidToDropwizardEventConverter {
    /**
     * This function acts as a filter. It returns <tt>null</tt> if the event is not suppose to be emitted to Dropwizard
     * Also This function will define the conversion between the druid event dimension's values and Dropwizard's metric name
     *
     * @param serviceMetricEvent Druid event ot type {@link com.metamx.emitter.service.ServiceMetricEvent}
     *
     * @return {@link io.druid.emitter.dropwizard.DropwizardEvent} or <tt>null</tt>
     */
    DropwizardEvent druidEventToDropwizard(ServiceMetricEvent serviceMetricEvent);
}