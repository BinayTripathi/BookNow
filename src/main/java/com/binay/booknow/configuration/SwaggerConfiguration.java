package com.binay.booknow.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
       
       public static final Contact DEFAULT_CONTACT = new Contact("Binay",  "www.linkedin.com/in/binay-tripathi-jan86", "binay.mckv@gmail.com");
         public static final ApiInfo DEFAULT_API_INFO = new ApiInfo("Api  Documentation For BookNow Reservation System", "BookNow API Documentation", "1.0",  "urn:tos",
                 DEFAULT_CONTACT, "Apache 2.0",  "http://www.apache.org/licenses/LICENSE-2.0");
         
       static String[] producerConsumer = new String[] {"Application/Json" ,  "Application/XML"};
       //Set<String> DEFAULT_PRODUCER_CONSUMER = new  HashSet<String>(Arrays.asList(producerConsumer));
       
       
       
       @Bean
       public Docket api()
       {
              return new Docket(DocumentationType.SWAGGER_2)
            		  .apiInfo(DEFAULT_API_INFO)
            		  .select()       
                      .apis(RequestHandlerSelectors.basePackage("com.binay.booknow.rest")).build();
       }
}
