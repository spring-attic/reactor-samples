package org.projectreactor.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.Environment;
import reactor.rx.Promise;
import reactor.rx.Promises;

import java.util.concurrent.TimeUnit;

/**
 * @author Jon Brisbin
 * @author Stephane Maldini
 */
public class PromiseSamples {

	static final Logger      LOG = LoggerFactory.getLogger(PromiseSamples.class);
	static final Environment ENV = new Environment();

	public static void main(String... args) throws Exception {
		// Deferred is the publisher, Promise the consumer
		Promise<String> promise = Promises.<String>prepare(ENV);

		promise.onComplete(p -> LOG.info("Promise completed {}", p))
		       .onSuccess(s -> LOG.info("Got value: {}", s))
		       .onError(t -> LOG.error(t.getMessage(), t));

		try {
			promise.onNext("Hello World!");
			//deferred.broadcastNext(new IllegalArgumentException("Hello Shmello! :P"));

			String s = promise.await(1, TimeUnit.SECONDS);
			LOG.info("s={}", s);
		} finally {
			ENV.shutdown();
		}
	}


}
