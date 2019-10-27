package com.sample.git.parser.impl.gitapi


import com.sample.git.parser.impl.GitParserException
import com.sample.git.parser.impl.models.Page
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import spock.lang.Ignore
import spock.lang.Specification

@SpringBootTest
@ActiveProfiles(["test"])
@ContextConfiguration(loader = SpringBootContextLoader)
@Ignore //Executes against actual git apis, should not run as part of the build process
class GitApiClientE2ESpec extends Specification {

	@Autowired
	private GitApiClient client

	def "getCommits works"() {
		given:
			def url = "https://github.com/ramkumarvenkat/kafka-spark-streaming-druid.git"

		when:
			def results = client.getCommits(url, new Page(2, 1))

		then:
			println(results)
			results.size() == 1
	}

	def "getCommits retries when there is an exception in calling git api"() {
		when:
			def url = "https://github.com/test/test1.git"
			client.getCommits(url, new Page(2, 1))

		then:
			thrown(GitParserException)
	}
}
