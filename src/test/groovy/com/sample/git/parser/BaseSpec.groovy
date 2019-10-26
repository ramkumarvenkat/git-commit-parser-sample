package com.sample.git.parser

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import spock.lang.Specification

class BaseSpec extends Specification {

	protected final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor()

	def setup() {
		executor.initialize()
	}
}
