package org.aeron.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@Slf4j
@SpringBootApplication
public class AeronTradeProcessorRunner {

  public static void main(String[] args) {
    new SpringApplicationBuilder()
        .sources(AeronTradeProcessorRunner.class)
        .bannerMode(Banner.Mode.OFF)
        .run(args);
  }
}
