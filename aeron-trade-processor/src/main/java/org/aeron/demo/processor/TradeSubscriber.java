package org.aeron.demo.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aeron.Aeron;
import io.aeron.FragmentAssembler;
import io.aeron.Subscription;
import io.aeron.logbuffer.FragmentHandler;
import io.aeron.logbuffer.Header;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aeron.demo.common.model.Trade;
import org.agrona.CloseHelper;
import org.agrona.DirectBuffer;
import org.agrona.concurrent.Agent;

@Data
@Slf4j
public class TradeSubscriber implements Agent, AutoCloseable {

  private final String name;
  private final Subscription subscription;
  private final ObjectMapper objectMapper;
  private final FragmentHandler tradeHandler = new FragmentAssembler(this::handlerTrade);

  public TradeSubscriber(Aeron aeron, String name, String channel, Integer streamID) {
    this.name = name;
    this.subscription = aeron.addSubscription(channel, streamID);
    this.objectMapper = new ObjectMapper();
  }

  private void handlerTrade(DirectBuffer buffer, int offset, int length, Header header) {
    var trade = Trade.decode(objectMapper, buffer.getStringUtf8(offset), offset, length);
    log.info("Subscription has received trade: {}", trade);
  }

  @Override
  public int doWork() throws Exception {
    return subscription.poll(tradeHandler, 10);
  }

  @Override
  public String roleName() {
    return this.name;
  }

  @Override
  public void close() throws Exception {
    CloseHelper.closeAll();
  }
}
