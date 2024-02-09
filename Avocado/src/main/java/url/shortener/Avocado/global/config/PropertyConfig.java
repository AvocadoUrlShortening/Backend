package url.shortener.Avocado.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
        @PropertySource("classpath:env.properties"),
        @PropertySource("classpath:oauth.properties")
})
public class PropertyConfig {

}
