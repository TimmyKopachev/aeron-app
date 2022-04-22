package org.aeron.demo.config;

import io.aeron.Aeron;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aeron.demo.cluster.Node;
import org.aeron.demo.cluster.NodeOrchestrator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties({AeronNodesProperties.class})
public class AeronTradeConfiguration {

  final AeronNodesProperties aeronNodesProperties;

  @Bean
  NodeOrchestrator orchestrator() {
    var nodes =
        aeronNodesProperties.getNodes().entrySet().stream()
            .map(entry -> new Node(Aeron.connect(), entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    return new NodeOrchestrator(nodes);
  }
}
