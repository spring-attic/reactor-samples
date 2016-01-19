package org.projectreactor.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.core.publisher.Processors;
import reactor.spring.context.annotation.Consumer;
import reactor.spring.context.annotation.Selector;
import reactor.spring.context.config.EnableReactor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;


/**
 * @author Jon Brisbin
 * @author Stephane Maldini
 */
@EnableAutoConfiguration
public class SpringSamples implements CommandLineRunner {


	@Autowired
	private TestService service;

	private EventBus eventBus;

	@Override public void run(String... args) throws Exception {
		service.test();
		eventBus.getProcessor().onComplete();
	}

	public static void main(String... args) {
		SpringApplication.run(SpringSamples.class, args);
	}

	@Configuration
	@ComponentScan
	@EnableReactor
	public static class ReactorConfiguration {

		@Bean public EventBus eventBus() {
			return EventBus.config()
			               .processor(Processors.topic())
			               .get();
		}

		@Bean public Logger log() {
			return LoggerFactory.getLogger(EventBusSamples.class);
		}

	}

	@Consumer
	public static class AnnotatedHandler {
		@Autowired
		private Logger  log;
		@Autowired
		public  EventBus eventBus;

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
		private EventBus eventBus;

		public void test() {
			log.info("Testing service...");
			eventBus.notify("test.topic", Event.wrap("Hello World!"));
		}
	}

}
