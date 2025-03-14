package com.arc.arp.function;

import com.arc.arp.IATAAcknowledgement;
import com.arc.arp.IATAPaymentClearanceRQ;
import java.util.function.Function;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@Configuration
public class IngestClearanceRequestFunction {

  @Bean
  public Function<Message<IATAPaymentClearanceRQ>, Message<IATAAcknowledgement>>
      ingestClearanceRequest() {
    return request -> {

      // IATAPaymentClearanceRQ request = request.getPayload();
      System.out.println("request " + request);
      System.out.println("clearanceCount: " + request.getPayload().getCleanranceCount());
      IATAAcknowledgement iataAcknowledgement = new IATAAcknowledgement();
      iataAcknowledgement.setStatus(Boolean.TRUE);
      // return iataAcknowledgement;
      return MessageBuilder.withPayload(iataAcknowledgement).build();
    };
  }
}
