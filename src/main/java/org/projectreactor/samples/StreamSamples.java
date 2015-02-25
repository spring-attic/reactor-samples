package org.projectreactor.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.Environment;
import reactor.rx.Promise;
import reactor.rx.broadcast.Broadcaster;

import static reactor.Environment.get;

/**
 * @author Jon Brisbin
 * @author Stephane Maldini
 */
public class StreamSamples {

	static final Logger LOG = LoggerFactory.getLogger(StreamSamples.class);

	static {
		Environment.initializeIfEmpty()
		           .assignErrorJournal();
	}

	public static void main(String... args) throws InterruptedException {

		simpleStream();

		transformValues();

		filterValues();

		get().shutdown();
	}

	private static void simpleStream() throws InterruptedException {
		// A Stream is a data publisher
		Broadcaster<String> stream = Broadcaster.create(get());

		// Log values passing through the Stream and capture the first coming signal
		Promise<String> promise = stream.
				                                observe(s -> LOG.info("Consumed String {}", s)).
				                                next();

		// Publish a value
		stream.onNext("Hello World!");

		promise.await();
	}

	private static void transformValues() throws InterruptedException {
		// A Stream is a data publisher
		Broadcaster<String> stream = Broadcaster.create(get());

		// Transform values passing through the Stream, observe and capture the result once.
		Promise<String> promise = stream.
				                                map(String::toUpperCase).
				                                observe(s -> LOG.info("UC String {}", s)).
				                                next();

		// Publish a value
		stream.onNext("Hello World!");

		promise.await();
	}

	private static void filterValues() throws InterruptedException {
		// A Stream is a data publisher
		Broadcaster<String> stream = Broadcaster.create(get());

		// Filter values passing through the Stream, observe and capture the result once.
		Promise<String> promise = stream.
				                                filter(s -> s.startsWith("Hello")).
				                                observe(s -> LOG.info("Filtered String {}", s)).
				                                next();

		// Publish a value
		stream.onNext("Hello World!");
		stream.onNext("Goodbye World!");

		promise.await();
	}

}