# Reactor Samples

> :warning: **Note**: This project is using modules that are being phased out
in Reactor 3.x, most notably reactor-bus. If you have questions about Reactor 3, join the
community (see the [reactor-core README](https://github.com/reactor/reactor/blob/master/README.md#community--support)).

This project contains some simple examples of using various components in Reactor. Most of the classes have a main method you can simply run in your IDE of choice. If they don't have that, then they're probably a Spring Boot application.

### Building

This is a Gradle-based project. Building is pretty simple:

		git clone https://github.com/reactor/reactor-samples.git
		cd reactor-samples
		./gradlew compileJava

### Running

There isn't a master main method to run all the tests. You have to run them individually.
