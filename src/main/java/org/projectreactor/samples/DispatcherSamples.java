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
import reactor.event.Event;
import reactor.event.EventBus;
import reactor.event.selector.Selector;
import reactor.function.Consumer;
import reactor.rx.Promise;
import reactor.rx.Streams;
import reactor.rx.stream.Broadcaster;
import reactor.spring.context.config.EnableReactor;

import java.util.List;

import static reactor.event.selector.Selectors.$;

/**
 * @author Jon Brisbin
 * @author Stephane Maldini
 */
@EnableAutoConfiguration
public class DispatcherSamples implements CommandLineRunner {

	@Autowired
	private Consumer<Event<EventBus>> consumer;
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
		// Bind to a Selector using an anonymous object
		Selector anon = $();

		Broadcaster<Object> broadcaster = Streams.broadcast();
		Promise<List<Object>> promise = broadcaster.toList(3);

		threadPoolReactor.on(anon, consumer);

		threadPoolReactor.notify(anon.getObject(), Event.wrap(threadPoolReactor));
		threadPoolReactor.notify(anon.getObject(), Event.wrap(threadPoolReactor));
		threadPoolReactor.notify(anon.getObject(), Event.wrap(threadPoolReactor));

		promise.poll();
	}

	private void multipleRingBufferDispatchers() {
		Broadcaster<Object> broadcaster = Streams.broadcast();
		Promise<List<Object>> promise = broadcaster.toList(5);

		EventBus r1 = EventBus.config()
		                     .env(env)
		                     .dispatcher(Environment.RING_BUFFER)
		                     .get();
		EventBus r2 = EventBus.config()
		                     .env(env)
		                     .dispatcher(Environment.RING_BUFFER)
		                     .get();

		// Bind to a Selector using an anonymous object
		Selector anon = $();

		r1.on(anon, consumer);
		r2.on(anon, consumer);

		r1.notify(anon.getObject(), Event.wrap(r1));
		r1.notify(anon.getObject(), Event.wrap(r1));
		r1.notify(anon.getObject(), Event.wrap(r1));

		r2.notify(anon.getObject(), Event.wrap(r2));
		r2.notify(anon.getObject(), Event.wrap(r2));

		promise.poll();
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

		@Bean public Consumer<Event<EventBus>> consumer(Logger log) {
			return ev -> log.info(
					"Triggered by anonymous object in thread {} on {}", Thread.currentThread(), ev.getData()
			);
		}

		@Bean public EventBus threadPoolReactor(Environment env) {
			return EventBus.config()
			               .env(env)
			               .dispatcher(Environment.THREAD_POOL)
			               .get();
		}

	}

}
