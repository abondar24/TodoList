package org.abondar.experimental.todolist.service;

import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.rs.security.jose.common.JoseException;
import org.apache.cxf.rs.security.jose.jaxrs.JwtAuthenticationFilter;
import org.apache.cxf.rs.security.jose.jaxrs.JwtCookieAuthenticationFilter;
import org.apache.cxf.rs.security.jose.jwt.JwtToken;
import org.apache.cxf.security.SecurityContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.ContainerRequestContext;
import java.io.IOException;

public class TokenRenewalFilter extends JwtCookieAuthenticationFilter {

    private AuthService authService;
    private static final String DEFAULT_AUTH_SCHEME = "JWT";
    private String expectedAuthScheme = "JWT";


    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String encodedJwtToken;
        try {
           encodedJwtToken = getEncodedJwtToken(requestContext);
        } catch (JoseException ex){
            return;
        }

        JwtToken token = super.getJwtToken(encodedJwtToken);
        SecurityContext securityContext = configureSecurityContext(token);
        if (securityContext != null) {
            JAXRSUtils.getCurrentMessage().put(SecurityContext.class, securityContext);
            try {
                ((HttpServletResponse) JAXRSUtils.getCurrentMessage().get("HTTP.RESPONSE"))
                        .addCookie(new Cookie("X-JWT-AUTH",authService.renewToken(encodedJwtToken)));
            } catch (Exception e) {

            }
        }

    }



    public void setExpectedAuthScheme(String expectedAuthScheme) {
        this.expectedAuthScheme = expectedAuthScheme;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }
}
