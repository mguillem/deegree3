package org.deegree.console.security;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtils {

    public static Authentication authenticate( UserDetailsService service, String userName ) {

        UserDetails userDetails = service.loadUserByUsername( userName );

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken( userDetails,
                                                                                             userDetails.getPassword() );
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
                                                                                              userDetails,
                                                                                              token.getCredentials(),
                                                                                              userDetails.getAuthorities() );
        result.setDetails( token.getDetails() );
        Authentication auth = result;
        getContext().setAuthentication( auth );
        auth = getContext().getAuthentication();
        return auth;
    }

}