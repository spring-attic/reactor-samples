package org.projectreactor.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.Environment;
import reactor.bus.ringbuffer.RingBatcher;
import reactor.bus.ringbuffer.spec.RingBatcherSpec;
import reactor.io.buffer.Buffer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Jon Brisbin
 */
public class RingBatcherSamples {

	static final Logger      LOG  = LoggerFactory.getLogger(RingBatcherSamples.class);
	static final Environment ENV  = new Environment();
	static final int         runs = 10000000;

	public static void main(String... args) throws Exception {
		CountDownLatch latch = new CountDownLatch(runs);
		AtomicLong sum = new AtomicLong();

		RingBatcher<Buffer> proc = new RingBatcherSpec<Buffer>()
				.singleThreadedProducer()
				.dataBufferSize(1024 * 16)
				.dataSupplier(() -> new Buffer(4, true))
				.consume(buff -> {
					sum.addAndGet(buff.readInt());
					buff.clear();
					latch.countDown();
				})
				.get();
		final AtomicInteger i = new AtomicInteger(0);

		double start = System.currentTimeMillis();
		while(i.get() < runs) {
			//Operation<Buffer> op = proc.prepare();
			//op.get().append(i.getAndIncrement()).flip();
			//op.commit();
			proc.batch(512, buff -> buff.append(i.getAndIncrement()).flip());
		}

		latch.await(5, TimeUnit.SECONDS);

		double end = System.currentTimeMillis();
		LOG.info("throughput: {}/sec", (long)(runs / ((end - start) / 1000)));

		ENV.shutdown();
	}

}
