package hello.world.myjwt;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = "hello.world.myjwt")
@PropertySource("application.properties")
public class AppConfig {

}
