package org.trade.demo;

import io.aeron.driver.MediaDriver;
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
    final MediaDriver.Context context = new MediaDriver.Context();

    context.terminationHook(barrier::signal);

    try (MediaDriver mediaDriver = MediaDriver.launch(context)) {
      barrier.await();
    }
  }
}
