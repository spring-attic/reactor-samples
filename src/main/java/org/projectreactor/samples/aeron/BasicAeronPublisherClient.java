package org.projectreactor.samples.aeron;

import org.reactivestreams.Subscription;
import reactor.aeron.Context;
import reactor.aeron.publisher.AeronPublisher;
import reactor.core.subscriber.BaseSubscriber;
import reactor.io.IO;

/**
 * Sample of AeronPublisher usage on the client side.
 * See {@link BasicAeronSubscriberServer} for the server-side implementation.
 *
 * @author Anatoly Kadyshev
 */
public class BasicAeronPublisherClient {

	public static final String SENDER_HOST = "127.0.0.1";

	private static class AeronClientSubscriber extends BaseSubscriber<String> {

		private Subscription subscription;

		int counter = 0;

		@Override
		public void onSubscribe(Subscription s) {
			super.onSubscribe(s);

			subscription = s;
			subscription.request(1);
		}

		@Override
		public void onNext(String value) {
			super.onNext(value);

			System.out.println("onNext: " + value);

			subscription.request(1);

			if(++counter == 10) {
				subscription.cancel();
			}
		}

		@Override
		public void onError(Throwable t) {
			t.printStackTrace();
		}

		@Override
		public void onComplete() {
		}
	}

	public static void main(String[] args) {
		Context context = new Context()
				.name("publisher")
				.autoCancel(true)
				.senderChannel("udp://" + SENDER_HOST + ":12000");

		AeronPublisher publisher = AeronPublisher.create(context);
		IO.bufferToString(publisher).subscribe(new AeronClientSubscriber());
	}

}
