package com.sample.git.parser.impl.gitcli.commands;

import com.sample.git.parser.impl.GitParserException;
import com.sample.git.parser.impl.gitcli.ProcessRunner;
import com.sample.git.parser.impl.models.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class CloneCommand implements IGitCommand<Path> {

	private final Path directory;
	private final Repository repository;
	private final ProcessRunner runner;

	@Override
	public Path call() throws GitParserException {
		String name = repository.getName().orElseThrow(() -> new IllegalArgumentException("Repository url is wrong, cannot extract the name"));

		ProcessRunner.ExecutionResult<String> resultsF = runner.run(
				directory,
				Function.identity(),
				"git", "clone", repository.getUrl(), name
		);

		// Bad code, this is a blocking call
		try {
			String error = resultsF.getErrorF().get().stream().filter(x -> x.startsWith("fatal:")).collect(Collectors.joining(","));
			if(!StringUtils.isEmpty(error)) {
				String message = String.format("Cannot do git clone, error is %s", error);
				log.error(message);
				throw new GitParserException(message);
			}
			return Paths.get(directory.toString(), name);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(String.format("RuntimeException in CloneCommand when parsing errorstream", e));
		}
	}
}
