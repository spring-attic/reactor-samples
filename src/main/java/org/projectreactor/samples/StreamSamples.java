package org.projectreactor.samples;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.rx.Broadcaster;

/**
 * @author Jon Brisbin
 * @author Stephane Maldini
 */
public class StreamSamples {

	static final Logger LOG = LoggerFactory.getLogger(StreamSamples.class);

	public static void main(String... args) throws InterruptedException {

		simpleStream();

		transformValues();

		filterValues();

	}

	private static void simpleStream() throws InterruptedException {
		// A Stream is a data publisher
		Broadcaster<String> stream = Broadcaster.create();

		// Log values passing through the Stream and capture the first coming signal
		Mono<String> promise = stream.
				                                doOnNext(s -> LOG.info("Consumed String {}", s)).
				                                next();

		// Publish a value
		stream.onNext("Hello World!");

		promise.get();
	}

	private static void transformValues() throws InterruptedException {
		// A Stream is a data publisher
		Broadcaster<String> stream = Broadcaster.create();

		// Transform values passing through the Stream, observe and capture the result once.
		Mono<String> promise = stream.
				                                map(String::toUpperCase).
				                                doOnNext(s -> LOG.info("UC String {}", s)).
				                                next();

		// Publish a value
		stream.onNext("Hello World!");

		promise.get();
	}

	private static void filterValues() throws InterruptedException {
		// A Stream is a data publisher
		Broadcaster<String> stream = Broadcaster.create();

		// Filter values passing through the Stream, observe and capture the result once.
		Mono<List<String>> promise = stream.
				                                filter(s -> s.startsWith("Hello")).
				                                doOnNext(s -> LOG.info("Filtered String {}", s)).
				                                toList();

		// Publish a value
		stream.onNext("Hello World!");
		stream.onNext("Goodbye World!");

		promise.get();
	}

}