package org.aeron.demo.cluster;

import io.aeron.Aeron;
import lombok.Data;
import org.aeron.demo.config.NodeProperties;
import org.aeron.demo.processor.TradePublisher;
import org.aeron.demo.processor.TradeSubscriber;
import org.agrona.CloseHelper;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.IdleStrategy;
import org.agrona.concurrent.SleepingIdleStrategy;

@Data
public class Node implements Agent, AutoCloseable {

  final TradePublisher publisher;
  final TradeSubscriber subscriber;

  final IdleStrategy idle = new SleepingIdleStrategy(100);

  public Node(Aeron aeron, String name, NodeProperties nodeProperties) {
    this.publisher =
        new TradePublisher(
            aeron,
            name,
            nodeProperties.getChannel(),
            nodeProperties.getStreamID(),
            nodeProperties.getTimeout());

    this.subscriber =
        new TradeSubscriber(aeron, name, nodeProperties.getChannel(), nodeProperties.getStreamID());
  }

  @Override
  public int doWork() throws Exception {
    int work = 0;
    if (!subscriber.getSubscription().isConnected()) {
      idle.idle();
    } else {
      work += publisher.doWork();
      work += subscriber.doWork();
    }

    return work;
  }

  @Override
  public String roleName() {
    return publisher.roleName();
  }

  @Override
  public void close() throws Exception {
    CloseHelper.closeAll();
  }
}
