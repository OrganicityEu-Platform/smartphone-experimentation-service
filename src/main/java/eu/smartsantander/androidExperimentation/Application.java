package eu.smartsantander.androidExperimentation;


import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@Configuration
@ComponentScan(basePackages = "eu.smartsantander.androidExperimentation")
@PropertySource("classpath:application.properties")
@EnableAsync
@EnableScheduling
@EnableJpaRepositories
@EnableAutoConfiguration
@EnableAspectJAutoProxy
public class Application implements CommandLineRunner {


    /**
     * a log4j logger to print messages.
     */
    private static final Logger LOGGER = Logger.getLogger(Application.class);

    public static void main(String[] args)
            throws Exception {
        final ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
        for (String name : ctx.getBeanDefinitionNames()) {
            LOGGER.info(name);
        }
    }

    public void run(String... args) throws Exception {

    }


    @Bean
    public EmbeddedServletContainerCustomizer servletContainerCustomizer() {
        return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer servletContainer) {
                ((TomcatEmbeddedServletContainerFactory) servletContainer).addConnectorCustomizers(
                        new TomcatConnectorCustomizer() {
                            @Override
                            public void customize(Connector connector) {
                                AbstractHttp11Protocol httpProtocol = (AbstractHttp11Protocol) connector.getProtocolHandler();
                                httpProtocol.setCompression("on");
                                httpProtocol.setCompressionMinSize(256);
                                String mimeTypes = httpProtocol.getCompressableMimeTypes();
                                String mimeTypesWithJson = mimeTypes
                                        + "," + MediaType.APPLICATION_JSON_VALUE
                                        + "," + MediaType.IMAGE_PNG + "," + MediaType.IMAGE_GIF + "," + MediaType.IMAGE_JPEG
                                        + "," + "application/javascript" + "," + "text/css";
                                httpProtocol.setCompressableMimeTypes(mimeTypesWithJson);
                            }
                        }
                );
            }
        };
    }
}