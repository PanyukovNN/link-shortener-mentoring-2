package ru.panyukovnn.linkshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import ru.panyukovnn.linkshortener.property.LinkShortenerProperty;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackageClasses = LinkShortenerProperty.class)
public class LinkShortenerApp {

    public static void main(String[] args) {
        SpringApplication.run(LinkShortenerApp.class, args);
    }
}
