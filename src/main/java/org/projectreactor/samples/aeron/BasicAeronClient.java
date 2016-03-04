package org.projectreactor.samples.aeron;

import org.reactivestreams.Subscription;
import reactor.aeron.Context;
import reactor.aeron.publisher.AeronFlux;
import reactor.core.subscriber.BaseSubscriber;
import reactor.io.buffer.Buffer;

/**
 * Sample of AeronFlux usage on the client side.
 * See {@link BasicAeronServer} for the server-side implementation.
 *
 * @author Anatoly Kadyshev
 */
public class BasicAeronClient {

	/**
	 * Put in here IP of a host on which server is run
	 */
	public static final String SENDER_HOST = "127.0.0.1";

	/**
	 * Put in here IP of the current host network interface to connect to the server
	 */
	private static final String RECEIVER_HOST = "127.0.0.1";

	static final class ClientSubscriber implements BaseSubscriber<String> {

		private Subscription subscription;

		int counter = 0;

		@Override
		public void onSubscribe(Subscription s) {
			BaseSubscriber.super.onSubscribe(s);

			subscription = s;
			subscription.request(1);
		}

		@Override
		public void onNext(String value) {
			BaseSubscriber.super.onNext(value);

			System.out.println("onNext: " + value);

			subscription.request(1);

			if(++counter == 10) {
				subscription.cancel();
			}
		}

		@Override
		public void onError(Throwable t) {
			BaseSubscriber.super.onError(t);
			t.printStackTrace();
		}

		@Override
		public void onComplete() {
		}
	}

	public static void main(String[] args) {
		Context context = Context.create()
				.name("publisher")
				.autoCancel(true)
				.senderChannel("udp://" + SENDER_HOST + ":12000")
				.receiverChannel("udp://" + RECEIVER_HOST + ":12001");

		AeronFlux.listenOn(context).as(Buffer::bufferToString).subscribe(new ClientSubscriber());
	}

}
