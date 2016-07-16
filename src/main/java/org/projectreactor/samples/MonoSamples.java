package org.projectreactor.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;

/**
 * @author Stephane Maldini
 */
public class MonoSamples {

	static final Logger LOG = LoggerFactory.getLogger(MonoSamples.class);


	public static void main(String... args) throws Exception {
		// Deferred is the publisher, Promise the consumer
		MonoProcessor<String> promise = MonoProcessor.create();

		Mono<String> result = promise.doOnSuccess(p -> LOG.info("Promise completed {}", p))
		                             .doOnTerminate((s, e) -> LOG.info("Got value: {}", s))
		                             .doOnError(t -> LOG.error(t.getMessage(), t))
		                             .subscribe();

		promise.onNext("Hello World!");
		//promise.onError(new IllegalArgumentException("Hello Shmello! :P"));

		String s = result.blockMillis(1_000);
		LOG.info("s={}", s);
	}


}
