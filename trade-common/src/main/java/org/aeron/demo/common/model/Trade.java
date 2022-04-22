package org.aeron.demo.common.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trade {

  private BigDecimal price;
  private Date time;

  public static Trade decode(ObjectMapper mapper, String json, int offset, int length) {
    try {
      return mapper.readValue(json.getBytes(StandardCharsets.UTF_8), Trade.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
