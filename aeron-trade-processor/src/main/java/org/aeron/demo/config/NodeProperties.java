package org.aeron.demo.config;

import lombok.Data;

@Data
public class NodeProperties {

  private Integer timeout;

  private String channel;
  private Integer streamID;

}
