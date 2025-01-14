/*
 *
 *  * Copyright 2012-2018 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.suricate.monitoring.model.dto.api.projectgrid;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ProjectGridRequestDto", description = "Create or update a project grid")
public class ProjectGridRequestDto extends AbstractDto {
    /**
     * In case of rotations, should the progress bar be displayed for the project
     */
    @ApiModelProperty(value = "In case of rotations, should the progress bar be displayed for the project")
    private boolean displayProgressBar;

    /**
     * The list of grids
     */
    @ApiModelProperty(value = "The list of grids")
    List<GridRequestDto> grids;

    @Data
    public static class GridRequestDto {
        /**
         * The project grid id
         */
        @ApiModelProperty(value = "The project grid id", example = "1")
        private Long id;

        /**
         * The time
         */
        @ApiModelProperty(value = "The time", example = "30")
        private Integer time;
    }
}
