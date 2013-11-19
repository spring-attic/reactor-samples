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
import reactor.core.composable.Deferred;
import reactor.core.composable.Stream;
import reactor.core.composable.spec.Streams;
import rx.Observable;

import java.util.Arrays;

/**
 * @author Jon Brisbin
 */
public class RxJavaSamples {

	static final Logger      LOG  = LoggerFactory.getLogger(RxJavaSamples.class);
	static final Environment ENV  = new Environment();
	static final int         runs = 10000000;

	public static void main(String... args) throws Exception {

		//Observable<Integer> obs = Observable.from(1,2,3,4,5);
		//obs.subscribe(i -> System.out.println("a:" + i));

		Deferred<Integer, Stream<Integer>> stream = Streams.<Integer>defer().env(ENV).get();

		stream.compose()
				.consume(i -> System.out.println("0:" + i))
				.map(i -> ":"+i)
				.consume(i -> System.out.println("a:" + i))
				.consume(i -> System.out.println("b:" + i));

		stream.accept(1);
		stream.accept(2);
		stream.accept(3);
		stream.accept(4);



		ENV.shutdown();
	}

}
