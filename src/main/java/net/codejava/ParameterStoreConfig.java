package net.codejava;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.MapPropertySource;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ParameterStoreConfig {

    private final ParameterStoreService parameterStoreService;
    private final Environment environment;

    public ParameterStoreConfig(ParameterStoreService parameterStoreService, Environment environment) {
        this.parameterStoreService = parameterStoreService;
        this.environment = environment;
    }

    @PostConstruct
    public void loadParameters() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("COGNITO_CLIENT_ID", parameterStoreService.getParameter("/cloud-guesser-game/config/oauth2/client-id"));
        parameters.put("COGNITO_CLIENT_SECRET", parameterStoreService.getParameter("/cloud-guesser-game/config/oauth2/client-secret"));
        parameters.put("COGNITO_REDIRECT_URI", parameterStoreService.getParameter("/cloud-guesser-game/config/oauth2/redirect-uri"));
        parameters.put("REDIS_HOST", parameterStoreService.getParameter("/cloud-guesser-game/config/redis/host"));
        parameters.put("REDIS_PORT", parameterStoreService.getParameter("/cloud-guesser-game/config/redis/port"));
        
        MutablePropertySources propertySources = ((StandardEnvironment) environment).getPropertySources();
        propertySources.addFirst(new MapPropertySource("awsParameterStore", parameters));
    }
}

