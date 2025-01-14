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

package io.suricate.monitoring.security.ldap;

import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.properties.ApplicationProperties;
import io.suricate.monitoring.security.LocalUser;
import io.suricate.monitoring.services.api.UserService;
import io.suricate.monitoring.utils.exceptions.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.AuthenticationSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Optional;

/**
 * The LDAP authentication
 */
@Configuration
@ConditionalOnProperty(name = "application.authentication.provider", havingValue = "ldap")
public class LdapAuthentication {
    /**
     * The user service
     */
    @Autowired
    private UserService userService;

    /**
     * The LDAP properties
     */
    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * The LDAP authorities populator
     */
    @Autowired
    private UserDetailsServiceLdapAuthoritiesPopulator userDetailsServiceLdapAuthoritiesPopulator;

    /**
     * Check the ldap configuration before launching the Application
     */
    @PostConstruct
    private void checkLdapConfiguration() {
        if (StringUtils.isBlank(applicationProperties.getAuthentication().getLdap().url)) {
            throw new ConfigurationException("The Ldap url is mandatory when the provider is ldap", "application.authentication.ldap.url");
        }
    }

    /**
     * Configure the ldap
     * @param auth the authentication manager
     * @throws Exception Any triggered exception during the configuration
     */
    @Autowired
    public void configureLdap(AuthenticationManagerBuilder auth) throws Exception {
        auth.ldapAuthentication()
            .userDetailsContextMapper(userDetailsContextMapper())
            .ldapAuthoritiesPopulator(userDetailsServiceLdapAuthoritiesPopulator)
            .userSearchFilter(applicationProperties.getAuthentication().getLdap().userSearchFilter)
            .groupRoleAttribute(applicationProperties.getAuthentication().getLdap().groupRoleAttribute)
            .groupSearchBase(applicationProperties.getAuthentication().getLdap().groupSearchBase)
            .groupSearchFilter(applicationProperties.getAuthentication().getLdap().groupSearchFilter)
            .rolePrefix(applicationProperties.getAuthentication().getLdap().rolePrefix)
            .userSearchBase(applicationProperties.getAuthentication().getLdap().userSearchBase)
            .contextSource(contextSource());
    }

    /**
     * Method used to store all user Ldap attribute inside the Security context holder
     * @return the userDetails context
     */
    public UserDetailsContextMapper userDetailsContextMapper() {
        return new LdapUserDetailsMapper() {
            @Override
            public UserDetails mapUserFromContext(DirContextOperations ctx, String username, java.util.Collection<? extends GrantedAuthority> authorities) {
                String email = ctx.getStringAttribute(applicationProperties.getAuthentication().getLdap().mailAttributName);
                Optional<User> currentUser = userService.getOneByEmail(email);

                if (!currentUser.isPresent()) {
                    throw new UsernameNotFoundException("The specified user has not been found");
                }

                return new LocalUser(currentUser.get(), Collections.emptyMap());
            }
        };
    }

    /**
     * Initialize LDAP context
     * @return The default context source configured
     */
    private DefaultSpringSecurityContextSource contextSource() {
        DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(applicationProperties.getAuthentication().getLdap().url);

        if (!StringUtils.isEmpty(applicationProperties.getAuthentication().getLdap().referral)) {
            contextSource.setReferral(applicationProperties.getAuthentication().getLdap().referral);
        }

        if (!StringUtils.isEmpty(applicationProperties.getAuthentication().getLdap().username)
                && !StringUtils.isEmpty(applicationProperties.getAuthentication().getLdap().password) ) {
            contextSource.setAuthenticationSource(new AuthenticationSource() {
                @Override
                public String getPrincipal() {
                    return applicationProperties.getAuthentication().getLdap().username;
                }

                @Override
                public String getCredentials() {
                    return applicationProperties.getAuthentication().getLdap().password;
                }
            });
        }

        contextSource.setCacheEnvironmentProperties(false);
        contextSource.afterPropertiesSet();
        return contextSource;
    }

}
