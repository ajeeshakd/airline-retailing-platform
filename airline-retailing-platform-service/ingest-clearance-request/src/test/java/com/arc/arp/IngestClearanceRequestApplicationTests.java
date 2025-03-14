package com.arc.arp;

import com.arc.arp.utils.Constants;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

@SpringBootTest(
    classes = IngestClearanceRequestApplication.class,
    webEnvironment = WebEnvironment.RANDOM_PORT)
class IngestClearanceRequestApplicationTests {

  @Autowired private TestRestTemplate rest;

  @Test
  public void test() throws Exception {
    String payload =
        "<IATAPaymentClearanceRQ>\r\n"
            + "    <cleanranceCount>3</cleanranceCount>\r\n"
            + "</IATAPaymentClearanceRQ>";

    ResponseEntity<IATAAcknowledgement> result =
        this.rest.exchange(
            RequestEntity.post(new URI("/ingestClearanceRequest"))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                .header(Constants.CUSTOM_CONTENT_TYPE_HEADER, MediaType.APPLICATION_XML_VALUE)
                .body(payload),
            IATAAcknowledgement.class);
    System.out.println(result.getBody());
  }

  @Test
  public void test_jsonPayload() throws Exception {
    String payload = "{\r\n" + " \"cleanranceCount\": 3\r\n" + "}";

    ResponseEntity<IATAAcknowledgement> result =
        this.rest.exchange(
            RequestEntity.post(new URI("/ingestClearanceRequest"))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(payload),
            IATAAcknowledgement.class);
    System.out.println(result.getBody());
  }
}
