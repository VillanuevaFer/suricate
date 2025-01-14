/*
 * Copyright 2012-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.suricate.monitoring.model.dto.nashorn;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.suricate.monitoring.model.enums.NashornErrorTypeEnum;
import io.suricate.monitoring.utils.JsonUtils;
import lombok.*;

import java.util.Date;

/**
 * Response after Nashorn execution
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class NashornResponse extends AbstractDto {

    /**
     * The new calculated data
     */
    private String data;

    /**
     * the log data
     */
    private String log;

    /**
     * The project ID
     */
    private Long projectId;

    /**
     * The projectWidget ID
     */
    private Long projectWidgetId;

    /**
     * Launch date
     */
    private Date launchDate;

    /**
     * Error
     */
    private NashornErrorTypeEnum error;

    /**
     * Check if the Nashorn response is valid
     *
     * @return true if the Nashorn response is valid, false otherwise
     */
    public boolean isValid() {
        return JsonUtils.isValid(data)
                && projectId != null
                && projectWidgetId != null
                && error == null;
    }

    /**
     * Check if the Nashorn error response type is fatal
     *
     * @return true if the Nashorn error response is fatal, false otherwise
     */
    public boolean isFatal() {
        return NashornErrorTypeEnum.FATAL == error;
    }
}
