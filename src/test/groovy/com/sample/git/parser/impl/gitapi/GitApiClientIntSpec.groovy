package com.sample.git.parser.impl.gitapi

import com.sample.git.parser.BaseIntSpec
import com.sample.git.parser.impl.GitParserException
import com.sample.git.parser.impl.gitapi.models.GitApiCommit
import com.sample.git.parser.impl.gitapi.models.GitApiCommitAuthor
import com.sample.git.parser.impl.gitapi.models.GitApiCommitDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

class GitApiClientIntSpec extends BaseIntSpec {

	def restTemplate = Mock(RestTemplate)
	def actualRestTemplate

	@Autowired
	private GitApiClient client

	def setup() {
		actualRestTemplate = client.getRestTemplate()
		client.setRestTemplate(restTemplate)
	}

	def cleanup() {
		client.setRestTemplate(actualRestTemplate)
	}

	def "getCommits works"() {
		given:
			def url = "https://github.com/test/test1.git"
			restTemplate.exchange(_, _, _, _, _, _) >> ResponseEntity.ok(
					Arrays.asList(
							new GitApiCommit("sha", new GitApiCommitDetails(new GitApiCommitAuthor("name", "email@gmail.com", "2016-03-07T07:00:53Z"), "message"))
					)
			)

		when:
			def results = client.getCommits(url)

		then:
			results.size() == 1
	}

	def "getCommits retries when there is an exception in calling git api"() {
		when:
			def url = "https://github.com/test/test1.git"
			client.getCommits(url)

		then:
			thrown(GitParserException)

		and:
			3 * restTemplate.exchange(_, _, _, _, _, _) >> {
				throw new RestClientException("Error")
			}
	}
}
