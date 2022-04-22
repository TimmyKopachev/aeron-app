package org.aeron.demo.cluster;

import java.nio.ByteBuffer;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.agrona.ErrorHandler;
import org.agrona.concurrent.AgentRunner;
import org.agrona.concurrent.SigInt;
import org.agrona.concurrent.SleepingMillisIdleStrategy;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.concurrent.status.AtomicCounter;
import org.springframework.beans.factory.InitializingBean;

@Slf4j
@AllArgsConstructor
public class NodeOrchestrator implements InitializingBean {

  final List<Node> nodes;

  @Override
  public void afterPropertiesSet() throws Exception {
    nodes.forEach(
        node -> {
          log.info("Running the node: [{}]", node.roleName());
          AgentRunner runner =
              new AgentRunner(
                  new SleepingMillisIdleStrategy(100),
                  new CustomErrorHandler(),
                  new AtomicCounter(new UnsafeBuffer(ByteBuffer.allocate(1300)), 10),
                  node);
          SigInt.register(runner::close);
          runner.run();
        });
  }

  private static class CustomErrorHandler implements ErrorHandler {

    @Override
    public void onError(Throwable throwable) {
      log.error(throwable.getLocalizedMessage());
    }
  }
}
