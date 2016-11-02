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


import com.google.common.base.Predicate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.TimeZone;

import static com.google.common.base.Predicates.or;
import static org.apache.logging.log4j.LogManager.getLogger;
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
    private static final org.apache.logging.log4j.Logger LOGGER = getLogger(Application.class);

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
    public Docket smartphoneApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("SmartphoneService-API")
                .apiInfo(apiInfo())
                .select()
                .paths(paths())
                .build()
                ;
    }

    private Predicate<String> paths() {
        return or(
                regex("/v1/.*")
        );
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("OrganiCity Smartphone Experimentation API")
                .description("Backend service for the SET tool for the OrganiCity Platform")
                .termsOfServiceUrl("http://organicity.eu/cookies-privacy-policy/")
                .contact(new Contact("OrganiCity MailingList", "https://groups.google.com/forum/#!forum/organicity-experimentation", "organicity-experimentation@googlegroups.com)"))
                .version("1").build();
    }
}