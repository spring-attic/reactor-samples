package org.projectreactor.samples;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Mono;

/**
 * @author Stephane Maldini
 */
public class FluxSamples {

	static final Logger LOG = LoggerFactory.getLogger(FluxSamples.class);

	public static void main(String... args) throws InterruptedException {

		simpleFlux();

		transformValues();

		filterValues();

	}

	private static void simpleFlux() throws InterruptedException {
		// A Flux is a data publisher
		EmitterProcessor<String> stream = EmitterProcessor.<String>create().connect();

		// Log values passing through the Flux and capture the first coming signal
		Mono<String> promise = stream.doOnNext(s -> LOG.info("Consumed String {}", s))
		                             .next()
		                             .subscribe();

		// Publish a value
		stream.onNext("Hello World!");

		promise.get();
	}

	private static void transformValues() throws InterruptedException {
		// A Flux is a data publisher
		EmitterProcessor<String> stream = EmitterProcessor.<String>create().connect();

		// Transform values passing through the Flux, observe and capture the result once.
		Mono<String> promise = stream.map(String::toUpperCase)
		                             .doOnNext(s -> LOG.info("UC String {}", s))
		                             .next()
		                             .subscribe();

		// Publish a value
		stream.onNext("Hello World!");

		promise.get();
	}

	private static void filterValues() throws InterruptedException {
		// A Flux is a data publisher
		EmitterProcessor<String> stream = EmitterProcessor.<String>create().connect();

		// Filter values passing through the Flux, observe and capture the result once.
		Mono<List<String>> promise = stream.filter(s -> s.startsWith("Hello"))
		                                   .doOnNext(s -> LOG.info("Filtered String {}", s))
		                                   .collectList()
		                                   .subscribe();

		// Publish a value
		stream.onNext("Hello World!");
		stream.onNext("Goodbye World!");
		stream.onComplete();

		promise.get();
	}

}