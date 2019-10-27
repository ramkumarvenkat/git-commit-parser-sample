package com.sample.git.parser.models

import com.sample.git.parser.impl.models.Repository
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class RepositorySpec extends Specification {

	def "getName works"() {
		given:
			def repository = new Repository(url)

		expect:
			repository.name.get() == name

		where:
			url                                     | name
			"https://github.com/test/test1.git"     | "test1"
			"git@github.com:test/test2.git"         | "test2"
	}

	def "getName returns empty for wrong git urls"() {
		given:
			def repository = new Repository(url)

		expect:
			repository.name.isEmpty()

		where:
			url << ["git@github.com/test2.git", "https://github.com/test1.git"]
	}

	def "getOwnerName works"() {
		given:
			def repository = new Repository(url)

		expect:
			repository.ownerName.get() == owner

		where:
			url                                     | owner
			"https://github.com/test/test1.git"     | "test"
			"git@github.com:test/test2.git"         | "test"
	}

	def "getOwnerName returns empty for wrong git urls"() {
		given:
			def repository = new Repository(url)

		expect:
			repository.ownerName.isEmpty()

		where:
			url << ["git@github.com/test2.git", "https://github.com/test1.git"]
	}
}
