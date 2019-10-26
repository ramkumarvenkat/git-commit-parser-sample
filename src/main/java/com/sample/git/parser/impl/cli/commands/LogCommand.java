package com.sample.git.parser.impl.cli.commands;

import com.sample.git.parser.impl.GitParserException;
import com.sample.git.parser.impl.cli.ProcessRunner;
import com.sample.git.parser.impl.models.Commit;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class LogCommand implements IGitCommand<List<Commit>> {

	private final Path directory;
	private final DateFormat dateFormat;
	private final PrettyFormat prettyFormat;
	private final ProcessRunner runner;

	@Override
	public List<Commit> call() throws GitParserException {
		Function<String, Commit> fn = this::parseGitLogToCommit;

		StringBuilder dateFormatBuilder = new StringBuilder();
		if(dateFormat != null) {
			dateFormatBuilder.append("--date=" + dateFormat.getValue());
		}

		StringBuilder prettyFormatBuilder = new StringBuilder();
		if(prettyFormat != null) {
			List<String> formatStrings = new ArrayList<>();
			if(prettyFormat.sha) formatStrings.add("%h");
			if(prettyFormat.authorName) formatStrings.add("%an");
			if(prettyFormat.authorEmail) formatStrings.add("%ae");
			if(prettyFormat.date) formatStrings.add("%ad");
			if(prettyFormat.subject) formatStrings.add("%s");

			prettyFormatBuilder.append("--pretty=format:")
					.append("\"")
					.append(String.join(",", formatStrings))
					.append("\"");
		}

		ProcessRunner.ExecutionResult<Commit> resultsF = runner.run(
				directory,
				fn,
				"git", "log", dateFormatBuilder.toString(), prettyFormatBuilder.toString()
		);

		// Bad code, this is a blocking call
		try {
			resultsF.getErrorF().get().stream().forEach(exception -> {
				String error = String.format("Cannot do git log, error is %s", exception);
				log.error(error);
				throw new RuntimeException(error);
			});
			return resultsF.getOutputF().get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(String.format("RuntimeException in LogCommand when parsing errorstream", e));
		}
	}

	private Commit parseGitLogToCommit(String log) {
		String[] values = log.trim().split(",");
		return new Commit(values[0], values[1], values[2], ZonedDateTime.parse(values[3]).toInstant(), values[4]);
	}

	public enum DateFormat {
		ISO_STRICT("iso-strict"),
		ISO("iso");

		@Getter
		private String value;

		DateFormat(String s) {
			value = s;
		}
	}

	@Data
	@Builder
	public static class PrettyFormat {
		private boolean sha;
		private boolean authorName;
		private boolean authorEmail;
		private boolean date;
		private boolean subject;
	}
}
