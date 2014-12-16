/*
 * Copyright (c) 2011-2015 Pivotal Software Inc., Inc. All Rights Reserved.
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
package org.projectreactor.samples

import reactor.core.configuration.DispatcherType
import reactor.groovy.config.GroovyEnvironment

def env = GroovyEnvironment.create {

	environment{
		defaultDispatcher = 'sync'

		dispatcher('sync'){
			type = DispatcherType.SYNCHRONOUS
		}
	}

	reactor('teamReactor'){
		stream{
			map{
				it.replyTo ? it.copy(it.data.toLowerCase()) : it
			}
		}

		stream('Stephane'){
			map{
				it.copy(it.data.toUpperCase())
			}
		}

		on('Jon'){
			reply "I'm good thanks $it"
		}

		on('Stephane'){
			reply "Talk to me again $it and I'm smashing your face"
		}
	}
}

def person = "Chuck Norris"
env['teamReactor'].send('Jon', person ) {
	println it
}

env['teamReactor'].send('Stephane', person ) {
	println it
}

env.environment().shutdown()