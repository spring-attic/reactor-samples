package org.projectreactor.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Environment;
import reactor.rx.Promise;
import reactor.rx.spec.Promises;

import java.util.concurrent.TimeUnit;

/**
 * @author Jon Brisbin
 */
public class PromiseSamples {

	static final Logger      LOG = LoggerFactory.getLogger(PromiseSamples.class);
	static final Environment ENV = new Environment();

	public static void main(String... args) throws Exception {
		// Deferred is the publisher, Promise the consumer
		Promise<String> deferred = Promises.<String>defer()
		                                                     .env(ENV)
		                                                     .dispatcher(Environment.RING_BUFFER)
		                                                     .get();
		Promise<String> promise = deferred;

		promise.onComplete(p -> LOG.info("Promise completed {}", p))
		       .onSuccess(s -> LOG.info("Got value: {}", s))
		       .onError(t -> LOG.error(t.getMessage(), t));

		try {
			deferred.broadcastNext("Hello World!");
			//deferred.broadcastNext(new IllegalArgumentException("Hello Shmello! :P"));

			String s = promise.await(1, TimeUnit.SECONDS);
			LOG.info("s={}", s);
		} finally {
			ENV.shutdown();
		}
	}


}
