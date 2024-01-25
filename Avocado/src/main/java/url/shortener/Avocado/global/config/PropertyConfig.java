package url.shortener.Avocado.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
        @PropertySource("classpath:application-env.properties"),
        @PropertySource("classpath:application-oauth.properties")
})
public class PropertyConfig {

}
