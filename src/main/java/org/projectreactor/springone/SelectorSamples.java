package org.projectreactor.springone;

import static reactor.event.selector.Selectors.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;
import reactor.event.Event;
import reactor.function.support.Boundary;

/**
 * @author Jon Brisbin
 */
public class SelectorSamples {

	static final Logger      LOG = LoggerFactory.getLogger(SelectorSamples.class);
	static final Environment ENV = new Environment();

	public static void main(String... args) throws InterruptedException {

		Reactor r = Reactors.reactor()
		                    .env(ENV)
		                    .dispatcher(Environment.RING_BUFFER)
		                    .get();
		Boundary b = new Boundary();

		// Simple topic selection
		r.on($("/some/topic"), b.<Event<String>>bind(ev -> LOG.info("Got event '{}'", ev.getData())));

		// Topic selection based on regex
		r.on(R("/some/(.+)"), b.<Event<String>>bind(ev -> {
			// RegexSelector puts capture groups into headers using convention 'group'
			// + [capture group #]
			String topic = ev.getHeaders().get("group1");

			LOG.info("Got event '{}' for {}", ev.getData(), topic);
		}));

		// Topic selection based on URI template
		r.on(U("/some/{topic}"), b.<Event<String>>bind(ev -> {
			// UriTemplateSelector puts path segment matches into headers using the
			// path variable name (like Spring MVC)
			String topic = ev.getHeaders().get("topic");

			LOG.info("Got event '{}' for {}", ev.getData(), topic);
		}));

		// Type selection based on inheritance
		r.on(T(Exception.class), b.<Event<Exception>>bind(ev -> LOG.error(ev.getData().getMessage())));

		// A single publish goes to three Consumers
		r.notify("/some/topic", Event.wrap("Hello World!"));
		// Publish exception using Exception class as the key
		r.notify(IllegalArgumentException.class,
		         Event.wrap(new IllegalArgumentException("That argument was invalid")));

		// Wait for all Consumers to have been called
		b.await();

		ENV.shutdown();
	}

}
