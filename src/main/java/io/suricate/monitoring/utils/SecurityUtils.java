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

package io.suricate.monitoring.utils;

import io.suricate.monitoring.model.enums.UserRoleEnum;
import io.suricate.monitoring.security.LocalUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public final class SecurityUtils {
    /**
     * Method used to isValid if the connected user is admin
     *
     * @return true if the connected user is admin, false otherwise
     */
    public static boolean isAdmin(final LocalUser connectedUser) {
        return hasRole(connectedUser, UserRoleEnum.ROLE_ADMIN.name());
    }

    /**
     * Method used to isValid if the connected user as all role in list
     *
     * @param roles list of roles
     * @return true if the connected user have all roles, false otherwise
     */
    public static boolean hasRole(LocalUser connectedUser, String... roles) {
        boolean ret = false;

        if (connectedUser != null) {
            List<GrantedAuthority> list = new ArrayList<>();
            for (String role : roles) {
                list.add(new SimpleGrantedAuthority(role));
            }
            ret = connectedUser.getAuthorities().containsAll(list);
        }

        return ret;
    }

    /**
     * Private Constructor
     */
    private SecurityUtils() {
    }
}
