package com.sample.git.parser.impl.gitcli;

import com.sample.git.parser.impl.GitParserException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Slf4j
@Data
@RequiredArgsConstructor
@Component
@Configuration
public class ProcessRunner {

	@Value("${git.cli.timeout.seconds:30}")
	private String PROCESS_TIMEOUT_SECONDS;

	private final ThreadPoolTaskExecutor executor;

	public <V> ExecutionResult<V> run(Path directory, Function<String, V> outputStreamMapper, String... command) throws GitParserException {

		Objects.requireNonNull(directory, "Directory must not be null");

		if (!Files.exists(directory)) {
			throw new IllegalArgumentException(String.format("Directory %s not present when running the command %s", directory, command));
		}

		try {
			ProcessBuilder pb = new ProcessBuilder().command(command).directory(directory.toFile());
			Process p = pb.start();

			GobblerThread<V> outputGobbler = new GobblerThread(p.getInputStream(), outputStreamMapper);
			GobblerThread<String> errorGobbler = new GobblerThread(p.getErrorStream(), Function.identity());

			Future<List<V>> outputsF = executor.submit(outputGobbler);
			Future<List<String>> errorsF = executor.submit(errorGobbler);

			/*
			Bad programming below, it will block the main thread
			 */
			if (!p.waitFor(Long.valueOf(PROCESS_TIMEOUT_SECONDS), TimeUnit.SECONDS)) {
				String error = String.format("Timeout running the command %s in directory %s", command, directory);
				log.error(error);
				throw new GitParserException(error);
			}

			return new ExecutionResult<>(outputsF, errorsF);
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(String.format("RuntimeException when running the command %s on directory %s", command, directory));
		}
	}

	@Data
	@AllArgsConstructor
	public static class ExecutionResult<V> {
		private final Future<List<V>> outputF;
		private final Future<List<String>> errorF;
	}

	@RequiredArgsConstructor
	private static class GobblerThread<V> implements Callable<List<V>> {

		private final InputStream is;
		private final Function<String, V> parsingFunction;

		@Override
		public List<V> call() throws IOException {
			List<V> parsedValues = new ArrayList<>();

			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = br.readLine()) != null) {
				line = line.replaceAll("^\"|\"$", ""); //Remove leading and trailing quotes
				parsedValues.add(parsingFunction.apply(line));
			}

			return parsedValues;
		}
	}
}
