package com.arc.arp;

import com.arc.arp.converter.XmlMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IngestClearanceRequestApplication {

  public static void main(String[] args) {
    SpringApplication.run(IngestClearanceRequestApplication.class, args);
  }

  @Bean
  public XmlMessageConverter xmlMessageConverter() {
    return new XmlMessageConverter();
  }
}
