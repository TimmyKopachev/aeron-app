package org.aeron.demo.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aeron.Aeron;
import io.aeron.Publication;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Date;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aeron.demo.common.model.Trade;
import org.agrona.CloseHelper;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.UnsafeBuffer;

@Data
@Slf4j
public class TradePublisher implements Agent, AutoCloseable {

  private final String name;
  private final Publication publication;

  private final ObjectMapper objectMapper = new ObjectMapper();

  private final long timeout;

  private long lastOfferResult = 0;
  private long lastOfferTimestamp = 0;

  public TradePublisher(Aeron aeron, String name, String channel, Integer streamID, long timeout) {
    this.name = name;
    this.timeout = timeout;
    this.publication = aeron.addPublication(channel, streamID);
  }

  @Override
  public int doWork() throws Exception {
    int work = 0;

    long now = System.currentTimeMillis();
    if (lastOfferResult < 0 || now - lastOfferTimestamp > timeout) {

      MutableDirectBuffer tradeBuffered = getDummyTradeBuffered();
      lastOfferResult = publication.offer(tradeBuffered, 0, tradeBuffered.capacity());

      lastOfferTimestamp = now;
      work++;
    }
    return work;
  }

  @Override
  public String roleName() {
    return this.name;
  }

  @Override
  public void close() throws Exception {
    CloseHelper.closeAll();
  }

  @SneakyThrows
  private MutableDirectBuffer getDummyTradeBuffered() {
    var trade = new Trade(new BigDecimal(1000), new Date());
    var jsonTrade = objectMapper.writeValueAsString(trade);

    log.info("Publication message: {} ", trade);

    UnsafeBuffer tradeBuffered = new UnsafeBuffer(ByteBuffer.allocate(256));
    tradeBuffered.putStringUtf8(0, jsonTrade);

    return tradeBuffered;
  }
}
