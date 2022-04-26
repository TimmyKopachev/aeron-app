package org.trade.demo;

import io.aeron.driver.MediaDriver;
import io.aeron.driver.ThreadingMode;
import org.agrona.concurrent.BusySpinIdleStrategy;
import org.agrona.concurrent.NoOpIdleStrategy;
import org.agrona.concurrent.ShutdownSignalBarrier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class AeronMediaDriverRunner implements ApplicationRunner {

  public static void main(String[] args) {
    new SpringApplicationBuilder()
        .sources(AeronMediaDriverRunner.class)
        .bannerMode(Banner.Mode.OFF)
        .run(args);
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    final ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
    final MediaDriver.Context context =
        new MediaDriver.Context()
            .dirDeleteOnStart(true)
            .threadingMode(ThreadingMode.DEDICATED)
            .conductorIdleStrategy(new BusySpinIdleStrategy())
            .senderIdleStrategy(new NoOpIdleStrategy())
            .receiverIdleStrategy(new NoOpIdleStrategy())
            .dirDeleteOnShutdown(true);

    context.terminationHook(barrier::signal);

    try (MediaDriver mediaDriver = MediaDriver.launchEmbedded(context)) {
      barrier.await();
    }
  }
}
