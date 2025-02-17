package uk.ac.rhul.cs2810.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import uk.ac.rhul.cs2810.model.Customer;
import uk.ac.rhul.cs2810.model.Menu;
import uk.ac.rhul.cs2810.model.MenuItem;
import uk.ac.rhul.cs2810.model.Waiter;

/**
 * By default, Spring does not reveal the identifier of each object over REST. The below
 * configuration class is added to include the id of objects.
 */
@Configuration
public class RestConfiguration implements RepositoryRestConfigurer {

  @Override
  public void configureRepositoryRestConfiguration(
      RepositoryRestConfiguration config, CorsRegistry cors) {
    config.exposeIdsFor(Customer.class);
    config.exposeIdsFor(Menu.class);
    config.exposeIdsFor(MenuItem.class);
    config.exposeIdsFor(Waiter.class);
    
    cors.addMapping("/api/**")
        .allowedOrigins("http://localhost:5173")
        .allowedMethods("GET", "POST", "PUT", "DELETE");
  }
}
