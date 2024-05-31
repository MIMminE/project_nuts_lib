package nuts.lib.manager.security_manager.strategy;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

public abstract class Csrf {

    static public HttpSecurityStrategy disable = http -> http.csrf(AbstractHttpConfigurer::disable);
}
