package org.projectreactor.springone;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Environment;
import reactor.core.composable.Deferred;
import reactor.core.composable.Promise;
import reactor.core.composable.spec.Promises;

/**
 * @author Jon Brisbin
 */
public class PromiseSamples {

	static final Logger      LOG = LoggerFactory.getLogger(PromiseSamples.class);
	static final Environment ENV = new Environment();

	public static void main(String... args) throws Exception {
		// Deferred is the publisher, Promise the consumer
		Deferred<String, Promise<String>> deferred = Promises.<String>defer()
		                                                     .env(ENV)
		                                                     .dispatcher(Environment.RING_BUFFER)
		                                                     .get();
		Promise<String> promise = deferred.compose();

		promise.onComplete(p -> LOG.info("Promise completed {}", p))
		       .onSuccess(s -> LOG.info("Got value: {}", s))
		       .onError(t -> LOG.error(t.getMessage(), t));

		try {
			deferred.accept("Hello World!");
			//deferred.accept(new IllegalArgumentException("Hello Shmello! :P"));

			String s = promise.await(1, TimeUnit.SECONDS);
			LOG.info("s={}", s);
		} finally {
			ENV.shutdown();
		}
	}


}
