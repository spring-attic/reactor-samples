package org.projectreactor.samples;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.rx.Promise;


/**
 * @author Jon Brisbin
 * @author Stephane Maldini
 */
public class PromiseSamples {

	static final Logger      LOG = LoggerFactory.getLogger(PromiseSamples.class);


	public static void main(String... args) throws Exception {
		// Deferred is the publisher, Promise the consumer
		Promise<String> promise = Promise.prepare();

		promise.doOnSuccess(p -> LOG.info("Promise completed {}", p))
		       .doOnTerminate((s, e) -> LOG.info("Got value: {}", s))
		       .doOnError(t -> LOG.error(t.getMessage(), t));

		promise.onNext("Hello World!");
		//promise.onError(new IllegalArgumentException("Hello Shmello! :P"));

		String s = promise.await(1, TimeUnit.SECONDS);
		LOG.info("s={}", s);
	}


}
