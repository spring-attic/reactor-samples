package org.projectreactor.samples.aeron;

import reactor.aeron.Context;
import reactor.aeron.subscriber.AeronSubscriber;
import reactor.io.buffer.Buffer;
import reactor.rx.Stream;

/**
 * Sample of AeronSubscriber usage on the server-side.
 * See {@link BasicAeronClient} for the client implementation.
 *
 * @author Anatoly Kadyshev
 */
public class BasicAeronServer {

	/**
	 * Put in here IP of the current host network interface to be used to communicate with clients
	 */
	public static final String SENDER_HOST = "127.0.0.1";

	public static void main(String[] args) {
		AeronSubscriber subscriber = AeronSubscriber.create(
				Context.create()
						.name("server")
						.senderChannel("udp://" + SENDER_HOST + ":12000"));

		Stream.range(1, 1000).map(i -> Buffer.wrap("" + i)).subscribe(subscriber);
	}

}
