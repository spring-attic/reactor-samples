/*
 * Copyright (c) 2011-2013 GoPivotal, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.projectreactor.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Environment;
import reactor.rx.Streams;
import reactor.rx.stream.HotStream;
import rx.Observable;
import rx.Observer;

/**
 * @author Stephane Maldini
 */
public class RxJavaSamples {

	static final Logger      LOG  = LoggerFactory.getLogger(RxJavaSamples.class);
	static final Environment ENV  = new Environment();

	public static void main(String... args) throws Exception {

		final HotStream<Integer> stream = Streams.<Integer>defer(ENV);


		stream
				.map(i -> ":" + i)
				.consume(i -> LOG.info("consumed:" + i));

		Observable<Integer> obs = Observable.from(1, 2, 3, 4, 5);
		obs.subscribe(new Observer<Integer>() {
			@Override
			public void onCompleted() {
				stream.broadcastComplete();
			}

			@Override
			public void onError(Throwable e) {
				stream.broadcastError(e);
			}

			@Override
			public void onNext(Integer arg) {
				stream.broadcastNext(arg);
			}
		});


		ENV.shutdown();
	}

}
