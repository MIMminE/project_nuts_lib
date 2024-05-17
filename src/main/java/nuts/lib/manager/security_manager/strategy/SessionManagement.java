package nuts.lib.manager.security_manager.strategy;


import nuts.lib.manager.security_manager.HttpSecurityStrategy;
import org.springframework.security.config.http.SessionCreationPolicy;

public abstract class SessionManagement {

    static public HttpSecurityStrategy sessionPolicyStateless = http -> http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
}