package com.sample.git.parser.impl.gitcli

import com.sample.git.parser.impl.GitParserException
import com.sample.git.parser.impl.models.Commit
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant
import java.util.concurrent.CompletableFuture

class GitCliClientSpec extends Specification {

	def "getCommits works"() {
		given:
			def url = "https://github.com/test/test1.git"

			def runner = Mock(ProcessRunner)
			def client = new GitCliClient(runner)

			def cloneOutputFuture = new CompletableFuture<List<String>>()
			def cloneErrorFuture = new CompletableFuture<List<String>>()
			runner.run(_, _, "git", "clone", _, _) >> new ProcessRunner.ExecutionResult(cloneOutputFuture, cloneErrorFuture)

			def logOutputFuture = new CompletableFuture<List<Commit>>()
			def logErrorFuture = new CompletableFuture<List<String>>()
			runner.run(_, _, "git", "log", _, _) >> new ProcessRunner.ExecutionResult(logOutputFuture, logErrorFuture)

		when:
			cloneOutputFuture.complete(Collections.emptyList())
			cloneErrorFuture.complete(Arrays.asList("Cloning into 'kafka-spark-streaming-druid'", "Unpacking objects: 100% (98/98), done"))

			logOutputFuture.complete(Arrays.asList(new Commit("sha", "name", "name@gmail.com", Instant.now(), "subject")))
			logErrorFuture.complete(Collections.emptyList())

			def results = client.getCommits(url)

		then:
			results.size() == 1
	}

	def "getCommits throws exception when repository doesn't exist"() {
		given:
			def url = "https://github.com/test/test1.git"

			def runner = Mock(ProcessRunner)
			def client = new GitCliClient(runner)

			def cloneOutputFuture = new CompletableFuture<List<String>>()
			def cloneErrorFuture = new CompletableFuture<List<String>>()
			runner.run(_, _, "git", "clone", _, _) >> new ProcessRunner.ExecutionResult(cloneOutputFuture, cloneErrorFuture)

		when:
			cloneOutputFuture.complete(Collections.emptyList())
			cloneErrorFuture.complete(Arrays.asList("Cloning into 'kafka-spark-streaming-druid'", "fatal: Repository not found"))

			client.getCommits(url)

		then:
			thrown(GitParserException)
	}

	def "getCommits throws checked exception when git clone times out"() {
		given:
			def url = "https://github.com/test/test1.git"

			def runner = Mock(ProcessRunner)
			def client = new GitCliClient(runner)

			runner.run(_, _, "git", "clone", _, _) >> {
				throw new GitParserException("error")
			}

		when:
			client.getCommits(url)

		then:
			thrown(GitParserException)
	}

	@Unroll
	def "getCommits throws exception when there is a runtime exception in git clone"() {
		given:
			def url = "https://github.com/test/test1.git"

			def runner = Mock(ProcessRunner)
			def client = new GitCliClient(runner)

			runner.run(_, _, "git", "clone", _, _) >> {
				throw e
			}

		when:
			client.getCommits(url)

		then:
			thrown(RuntimeException)

		where:
			e << [new RuntimeException(), new NullPointerException(), new IllegalArgumentException()]
	}

	def "getCommits throws checked exception when git log times out"() {
		given:
			def url = "https://github.com/test/test1.git"

			def runner = Mock(ProcessRunner)
			def client = new GitCliClient(runner)

			def cloneOutputFuture = new CompletableFuture<List<String>>()
			def cloneErrorFuture = new CompletableFuture<List<String>>()
			runner.run(_, _, "git", "clone", _, _) >> new ProcessRunner.ExecutionResult(cloneOutputFuture, cloneErrorFuture)

			runner.run(_, _, "git", "log", _, _) >> {
				throw new GitParserException("error")
			}

		when:
			cloneOutputFuture.complete(Collections.emptyList())
			cloneErrorFuture.complete(Arrays.asList("Cloning into 'kafka-spark-streaming-druid'", "Unpacking objects: 100% (98/98), done"))

			client.getCommits(url)

		then:
			thrown(GitParserException)
	}

	def "getCommits throws exception when git log returns an error"() {
		given:
			def url = "https://github.com/test/test1.git"

			def runner = Mock(ProcessRunner)
			def client = new GitCliClient(runner)

			def cloneOutputFuture = new CompletableFuture<List<String>>()
			def cloneErrorFuture = new CompletableFuture<List<String>>()
			runner.run(_, _, "git", "clone", _, _) >> new ProcessRunner.ExecutionResult(cloneOutputFuture, cloneErrorFuture)

			def logOutputFuture = new CompletableFuture<List<Commit>>()
			def logErrorFuture = new CompletableFuture<List<String>>()
			runner.run(_, _, "git", "log", _, _) >> new ProcessRunner.ExecutionResult(logOutputFuture, logErrorFuture)

		when:
			cloneOutputFuture.complete(Collections.emptyList())
			cloneErrorFuture.complete(Arrays.asList("Cloning into 'kafka-spark-streaming-druid'", "Unpacking objects: 100% (98/98), done"))

			logOutputFuture.complete(Arrays.asList(new Commit("sha", "name", "name@gmail.com", Instant.now(), "subject")))
			logErrorFuture.complete("Error")

			client.getCommits(url)

		then:
			thrown(RuntimeException)
	}

	def "getCommits throws exception when there is a runtime exception in git log"() {
		given:
			def url = "https://github.com/test/test1.git"

			def runner = Mock(ProcessRunner)
			def client = new GitCliClient(runner)

			def cloneOutputFuture = new CompletableFuture<List<String>>()
			def cloneErrorFuture = new CompletableFuture<List<String>>()
			runner.run(_, _, "git", "clone", _, _) >> new ProcessRunner.ExecutionResult(cloneOutputFuture, cloneErrorFuture)

			def logOutputFuture = new CompletableFuture<List<Commit>>()
			def logErrorFuture = new CompletableFuture<List<String>>()
			runner.run(_, _, "git", "log", _, _) >> new RuntimeException()

		when:
			cloneOutputFuture.complete(Collections.emptyList())
			cloneErrorFuture.complete(Arrays.asList("Cloning into 'kafka-spark-streaming-druid'", "Unpacking objects: 100% (98/98), done"))

			client.getCommits(url)

		then:
			thrown(RuntimeException)
	}
}
