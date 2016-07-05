package gr.cti.android.experimentation;

/*-
 * #%L
 * Smartphone Experimentation Web Service
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2015 - 2016 CTI - Computer Technology Institute and Press "Diophantus"
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.TimeZone;

import static springfox.documentation.builders.PathSelectors.regex;


@SpringBootApplication
@Configuration
@ComponentScan(basePackages = "gr.cti.android.experimentation")
@PropertySource("classpath:application.properties")
@EnableAsync
@EnableSwagger2
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
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        final ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
        for (String name : ctx.getBeanDefinitionNames()) {
            LOGGER.info(name);
        }
    }

    public void run(String... args) throws Exception {

    }


    @Bean
    public EmbeddedServletContainerCustomizer servletContainerCustomizer() {
        return servletContainer -> ((TomcatEmbeddedServletContainerFactory) servletContainer).addConnectorCustomizers(
                (TomcatConnectorCustomizer) connector -> {
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
        );
    }


    @Bean
    public Docket smartphoneApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("smartphone")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/api/v1/.*"))
                .build()
//                .groupName("contact")
//                .apiInfo(apiInfo())
//                .select()
//                .paths(regex("/api/v1/contact/.*"))
//                .build()
//                .groupName("ranking")
//                .apiInfo(apiInfo())
//                .select()
//                .paths(regex("/api/v1/ranking/.*"))
//                .build()
//                .groupName("data")
//                .apiInfo(apiInfo())
//                .select()
//                .paths(regex("/api/v1/data/.*"))
//                .build()
//                .groupName("experiment")
//                .apiInfo(apiInfo())
//                .select()
//                .paths(regex("/api/v1/experiment/.*"))
//                .build()
//                .groupName("plugin")
//                .apiInfo(apiInfo())
//                .select()
//                .paths(regex("/api/v1/plugin/.*"))
//                .build()
                ;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Organicity Smartphone Experimentation API")
                .description("Organicity Smartphone Experimentation API")
                .termsOfServiceUrl("http://www.organicity.eu")
                .contact(new Contact("Organicity Helpdesk", "https://support.zoho.com/portal/organicity/home", "helpdesk@organicity.eu)"))
                .version("1").build();
    }
}