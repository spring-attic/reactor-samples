package org.projectreactor.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Environment;
import reactor.core.composable.Deferred;
import reactor.core.composable.Stream;
import reactor.core.composable.spec.Streams;
import reactor.function.Predicate;
import reactor.function.support.Boundary;

/**
 * @author Jon Brisbin
 */
public class StreamSamples {

	static final Logger      LOG = LoggerFactory.getLogger(StreamSamples.class);
	static final Environment ENV = new Environment();

	public static void main(String... args) throws InterruptedException {

		simpleStream();

		transformValues();

		filterValues();

		ENV.shutdown();
	}

	private static void simpleStream() {
		Boundary b = new Boundary();

		// Deferred is the publisher, Stream the consumer
		Deferred<String, Stream<String>> deferred = Streams.<String>defer()
				.env(ENV)
		                                                   .dispatcher(Environment.RING_BUFFER)
		                                                   .get();
		Stream<String> stream = deferred.compose();

		// Consume values passing through the Stream
		stream.consume(b.<String>bind(s -> LOG.info("Consumed String {}", s)));

		// Publish a value
		deferred.accept("Hello World!");

		b.await();
	}

	private static void transformValues() {
		Boundary b = new Boundary();

		// Deferred is the publisher, Stream the consumer
		Deferred<String, Stream<String>> deferred = Streams.<String>defer()
		                                                   .env(ENV)
		                                                   .dispatcher(Environment.RING_BUFFER)
		                                                   .get();
		Stream<String> stream = deferred.compose();

		// Transform values passing through the Stream
		stream.map(String::toUpperCase).consume(b.bind(s -> LOG.info("UC String {}", s)));

		// Publish a value
		deferred.accept("Hello World!");

		b.await();
	}

	private static void filterValues() {
		Boundary b = new Boundary();

		// Deferred is the publisher, Stream the consumer
		Deferred<String, Stream<String>> deferred = Streams.<String>defer()
		                                                   .env(ENV)
		                                                   .dispatcher(Environment.RING_BUFFER)
		                                                   .get();
		Stream<String> stream = deferred.compose();

		// Filter values passing through the Stream
		stream.filter(new Predicate<String>() {
			@Override
			public boolean test(String s) {
				return s.startsWith("Hello");
			}
		}).consume(b.<String>bind(s -> LOG.info("Filtered String {}", s)));

		// Publish a value
		deferred.accept("Hello World!");
		deferred.accept("Goodbye World!");

		b.await();
	}

}