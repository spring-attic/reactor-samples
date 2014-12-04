package org.projectreactor.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.event.Event;
import reactor.event.EventBus;
import reactor.event.selector.Selector;
import reactor.function.Consumer;
import reactor.function.support.Boundary;
import reactor.spring.context.config.EnableReactor;
import reactor.tuple.Tuple2;

import static reactor.event.selector.Selectors.$;

/**
 * @author Jon Brisbin
 */
@EnableAutoConfiguration
public class DispatcherSamples implements CommandLineRunner {

	@Autowired
	private Consumer<Event<Reactor>> consumer;
	@Autowired
	private Environment              env;
	@Autowired
	private EventBus                  threadPoolReactor;

	@Override public void run(String... args) throws Exception {
		threadPoolDispatcher();
		multipleRingBufferDispatchers();

		env.shutdown();
	}

	private void threadPoolDispatcher() {
		Boundary b = new Boundary();

		// Bind to a Selector using an anonymous object
		Tuple2<Selector, Object> anon = $();

		threadPoolReactor.on(anon.getT1(), b.bind(consumer, 3));

		threadPoolReactor.notify(anon.getT2(), Event.wrap(threadPoolReactor));
		threadPoolReactor.notify(anon.getT2(), Event.wrap(threadPoolReactor));
		threadPoolReactor.notify(anon.getT2(), Event.wrap(threadPoolReactor));

		b.await();
	}

	private void multipleRingBufferDispatchers() {
		Boundary b = new Boundary();

		Reactor r1 = EventBus.create()
		                     .env(env)
		                     .dispatcher(Environment.RING_BUFFER)
		                     .get();
		Reactor r2 = EventBus.create()
		                     .env(env)
		                     .dispatcher(Environment.RING_BUFFER)
		                     .get();

		// Bind to a Selector using an anonymous object
		Tuple2<Selector, Object> anon = $();

		r1.on(anon.getT1(), b.bind(consumer, 3));
		r2.on(anon.getT1(), b.bind(consumer, 2));

		r1.notify(anon.getT2(), Event.wrap(r1));
		r1.notify(anon.getT2(), Event.wrap(r1));
		r1.notify(anon.getT2(), Event.wrap(r1));

		r2.notify(anon.getT2(), Event.wrap(r2));
		r2.notify(anon.getT2(), Event.wrap(r2));

		b.await();
	}

	public static void main(String... args) {
		SpringApplication.run(DispatcherSamples.class, args);
	}

	@Configuration
	@EnableReactor
	static class ReactorConfiguration {

		@Bean public Logger log() {
			return LoggerFactory.getLogger(DispatcherSamples.class);
		}

		@Bean public Consumer<Event<Reactor>> consumer(Logger log) {
			return ev -> log.info(
					"Triggered by anonymous object in thread {} on {}", Thread.currentThread(), ev.getData()
			);
		}

		@Bean public Reactor threadPoolReactor(Environment env) {
			return EventBus.create()
			               .env(env)
			               .dispatcher(Environment.THREAD_POOL)
			               .get();
		}

	}

}
