package at.kalauner.dezsys12.server.config;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import javax.inject.Named;
import javax.ws.rs.Path;

/**
 * JerseyConfig
 *
 * @author Paul Kalauner 5BHIT
 * @version 20160212.1
 */
@Named
public class JerseyConfig extends ResourceConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(JerseyConfig.class);

    public JerseyConfig() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Path.class));
        for (BeanDefinition bd : scanner.findCandidateComponents("at.kalauner.dezsys12.server"))
            try {
                this.register(Class.forName(bd.getBeanClassName()));
                LOGGER.info("Discovered " + bd.getBeanClassName());
            } catch (ClassNotFoundException e) {
                LOGGER.error("Error while discovering paths", e);
            }
        this.register(JacksonFeature.class);
    }
}