package com.arc.arp.converter;

import com.arc.arp.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.util.MimeType;

/**
 * XmlMessageConverter class is used as user defined message converter to support content type as
 * application/xml If XmlMessageConverter failed to convert then it will pass to next message
 * converter registered.
 */
@Configuration
public class XmlMessageConverter extends AbstractMessageConverter {
  private final XmlMapper xmlMapper = new XmlMapper();
  private final ObjectMapper jsonMapper = new ObjectMapper();

  /** Constructor of XmlMessageConverter */
  public XmlMessageConverter() {
    super(new MimeType("application", "xml"));
  }

  @Override
  protected boolean supports(Class<?> clazz) {
    return true; // Customize this to support specific classes if needed
  }

  @Override
  protected boolean canConvertFrom(Message<?> message, Class<?> targetClass) {
    return targetClass.getPackage() != null
        && targetClass.getPackage().getName().startsWith("com.arc.arp");
  }

  @Override
  protected boolean canConvertTo(Object payload, @Nullable MessageHeaders headers) {
    return !supportsMimeType(headers);
  }

  /**
   * Convert the message payload from serialized form to an Object. If failed to convert message to
   * an object then next registered message converter will take care of message conversion. To give
   * the charge for next available converter we returning null.
   *
   * @param message the input message
   * @param targetClass the target class for the conversion
   * @param conversionHint an extra object passed to the {@link MessageConverter}, for example, the
   *     associated {@code MethodParameter} (may be {@code null}}
   * @return the result of the conversion, or {@code null} if the converter cannot perform the
   *     conversion
   */
  @Override
  protected Object convertFromInternal(
      Message<?> message, Class<?> targetClass, @Nullable Object conversionHint) {
    if (message.getPayload().getClass().isAssignableFrom(targetClass)) {
      return message.getPayload();
    }
    try {
      Object payload = message.getPayload();
      if (logger.isDebugEnabled()) {
        logger.debug("message payload type : " + message.getPayload().getClass());
        logger.debug("payload class : " + payload.getClass());
      }

      // While testing from local means other than AWS API gateway endpoint then
      // custom-content-type with value application/xml is required to pass as header
      // along with payload.
      // if request received via API gateway then payload will be mapped with body tag
      // in JSON formatted API gateway request.
      Map<String, Object> mssageHeader = message.getHeaders();
      String customHeader = (String) mssageHeader.get(Constants.CUSTOM_CONTENT_TYPE_HEADER);
      if (MediaType.APPLICATION_XML_VALUE.equals(customHeader)) {
        payload = ((String) payload).getBytes(StandardCharsets.UTF_8);
      } else {
        if (payload instanceof byte[]) {
          payload = new String((byte[]) payload, StandardCharsets.UTF_8);
        }
        Map<String, String> structMessage = jsonMapper.readValue((String) payload, Map.class);
        if (structMessage.containsKey("body")) {
          String body = structMessage.get("body");
          payload = ((String) body).getBytes(StandardCharsets.UTF_8);
        } else {
          payload = ((String) payload).getBytes(StandardCharsets.UTF_8);
        }
      }
      return xmlMapper.readValue((byte[]) payload, targetClass);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      if (logger.isDebugEnabled()) {
        Object payload = message.getPayload();
        if (payload instanceof byte[]) {
          payload = new String((byte[]) payload, StandardCharsets.UTF_8);
        }
        logger.debug("Failed to convert value: " + payload + " to: " + targetClass, e);
      }
    }
    return null;
  }

  @Override
  protected Object convertToInternal(
      Object payload, @Nullable MessageHeaders headers, @Nullable Object conversionHint) {
    try {
      return xmlMapper.writeValueAsBytes(payload);
    } catch (Exception e) {
      throw new RuntimeException("Failed to convert to XML message", e);
    }
  }
}
