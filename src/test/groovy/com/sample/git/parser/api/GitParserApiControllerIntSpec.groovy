package com.sample.git.parser.api

import com.sample.git.parser.BaseIntSpec
import com.sample.git.parser.impl.GitParserException
import com.sample.git.parser.impl.cli.GitCliClient
import com.sample.git.parser.impl.cli.ProcessRunner
import com.sample.git.parser.impl.models.Commit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc

import java.time.Instant
import java.util.concurrent.CompletableFuture

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class GitParserApiControllerIntSpec extends BaseIntSpec {

	@Autowired
	private MockMvc mockMvc

	@Autowired
	private GitCliClient client

	private ProcessRunner runner

	def setup() {
		runner = Mock(ProcessRunner)
		client.setRunner(runner)
	}

	def "getCommits works"() {
		given:
			def url = "https://github.com/test/test1.git"
			def cloneOutputFuture = new CompletableFuture<List<String>>()
			def cloneErrorFuture = new CompletableFuture<List<String>>()

			def logOutputFuture = new CompletableFuture<List<Commit>>()
			def logErrorFuture = new CompletableFuture<List<String>>()

		when:
			cloneOutputFuture.complete(Collections.emptyList())
			cloneErrorFuture.complete(Arrays.asList("Cloning into 'kafka-spark-streaming-druid'", "Unpacking objects: 100% (98/98), done"))

			logOutputFuture.complete(Arrays.asList(new Commit("sha", "name", "name@gmail.com", Instant.ofEpochMilli(1000), "subject")))
			logErrorFuture.complete(Collections.emptyList())

			def results = mockMvc.perform(get("/api/v1/git/parser/commits?url=" + url)
					.contentType(MediaType.APPLICATION_JSON_VALUE))

		then:
			results.andExpect(status().isOk())

		and:
			1 * runner.run(_, _, "git", "clone", _, _) >> new ProcessRunner.ExecutionResult(cloneOutputFuture, cloneErrorFuture)
			1 * runner.run(_, _, "git", "log", _, _) >> new ProcessRunner.ExecutionResult(logOutputFuture, logErrorFuture)
	}

	def "getCommits retries happen when git clone times out"() {
		when:
			def url = "https://github.com/test/test1.git"
			def results = mockMvc.perform(get("/api/v1/git/parser/commits?url=" + url)
					.contentType(MediaType.APPLICATION_JSON_VALUE))

		then:
			results.andExpect(status().isInternalServerError())

		and:
			3 * runner.run(_, _, "git", "clone", _, _) >> {
				throw new GitParserException("error")
			}
			0 * runner.run(_, _, "git", "log", _, _)
	}
}
