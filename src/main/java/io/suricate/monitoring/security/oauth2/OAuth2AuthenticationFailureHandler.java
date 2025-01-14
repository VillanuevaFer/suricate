package io.suricate.monitoring.security.oauth2;

import io.suricate.monitoring.properties.ApplicationProperties;
import io.suricate.monitoring.utils.web.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static io.suricate.monitoring.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    /**
     * The logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2AuthenticationFailureHandler.class);

    /**
     * The authentication request repository
     * Store the authentication request in an HTTP cookie on the IDP response
     */
    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    /**
     * The application properties
     */
    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * Trigger after OAuth2 authentication has failed
     * @param request The request which is the response of the IDP
     * @param response The response to send to the host that authenticated successfully
     * @param exception The authentication exception
     * @throws IOException Any IO Exception
     * @throws ServletException Any servlet exception
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME).map(Cookie::getValue);

        if (!redirectUri.isPresent() && applicationProperties.getAuthentication().getOauth2().isUseReferer()) {
            redirectUri = Optional.ofNullable(request.getHeader(HttpHeaders.REFERER));
            redirectUri.ifPresent(redirect -> LOGGER.debug("Using url {} from Referer header", request.getHeader(HttpHeaders.REFERER)));
        }

        String targetUrl = redirectUri.orElse(applicationProperties.getAuthentication().getOauth2().getDefaultTargetUrl());
        if (StringUtils.isBlank(targetUrl)) {
            targetUrl = "/";
        }

        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", exception.getLocalizedMessage())
                .build().toUriString();

        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
