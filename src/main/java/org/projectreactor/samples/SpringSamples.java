package org.projectreactor.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;
import reactor.event.Event;
import reactor.spring.annotation.Selector;
import reactor.spring.context.config.EnableReactor;

/**
 * @author Jon Brisbin
 */
@EnableAutoConfiguration
public class SpringSamples implements CommandLineRunner {

	@Autowired
	private Environment env;
	@Autowired
	private TestService service;

	@Override public void run(String... args) throws Exception {
		service.test();

		env.shutdown();
	}

	public static void main(String... args) {
		SpringApplication.run(SpringSamples.class, args);
	}

	@Configuration
	@EnableReactor
	@ComponentScan
	public static class ReactorConfiguration {

		@Bean public Reactor reactor(Environment env) {
			return Reactors.reactor().env(env).dispatcher(Environment.RING_BUFFER).get();
		}

		@Bean public Logger log() {
			return LoggerFactory.getLogger(ReactorSamples.class);
		}

	}

	@Component
	public static class AnnotatedHandler {
		@Autowired
		private Logger  log;
		@Autowired
		public  Reactor reactor;

		@Selector("test.topic")
		public void onTestTopic(String s) {
			log.info("onTestTopic: {}", s);
		}
	}

	@Service
	public static class TestService {
		@Autowired
		private Logger  log;
		@Autowired
		private Reactor reactor;

		public void test() {
			log.info("Testing service...");
			reactor.notify("test.topic", Event.wrap("Hello World!"));
		}
	}

}
