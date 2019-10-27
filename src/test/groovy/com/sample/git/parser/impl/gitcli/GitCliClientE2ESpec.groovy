package com.sample.git.parser.impl.gitcli

import com.sample.git.parser.BaseSpec
import com.sample.git.parser.impl.GitParserException

class GitCliClientE2ESpec extends BaseSpec {

	def runner = new ProcessRunner(executor)
	def client = new GitCliClient(runner)

	def setup() {
		runner.setPROCESS_TIMEOUT_SECONDS("30")
	}

	def "getCommits works on valid git url"() {
		when:
			def result = client.getCommits("https://github.com/ramkumarvenkat/kafka-spark-streaming-druid.git");

		then:
			result.size() > 0
	}

	def "getCommits throws exception if git repo doesn't exist"() {
		when:
			client.getCommits("https://github.com/ramkumarvenkat/randomrepo.git");

		then:
			thrown(GitParserException)
	}
}
