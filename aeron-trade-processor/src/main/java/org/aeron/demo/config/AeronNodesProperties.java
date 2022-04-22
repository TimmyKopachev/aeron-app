package org.aeron.demo.config;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("aeron.connection")
public class AeronNodesProperties {

  private Map<String, NodeProperties> nodes;
}
