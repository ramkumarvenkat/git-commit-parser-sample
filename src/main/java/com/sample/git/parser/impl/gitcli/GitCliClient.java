package com.sample.git.parser.impl.gitcli;

import com.sample.git.parser.impl.GitParserException;
import com.sample.git.parser.impl.IRepositoryClient;
import com.sample.git.parser.impl.gitcli.commands.CloneCommand;
import com.sample.git.parser.impl.gitcli.commands.LogCommand;
import com.sample.git.parser.impl.gitcli.commands.LogCommand.DateFormat;
import com.sample.git.parser.impl.gitcli.commands.LogCommand.Pagination;
import com.sample.git.parser.impl.gitcli.commands.LogCommand.PrettyFormat;
import com.sample.git.parser.impl.models.Commit;
import com.sample.git.parser.impl.models.Page;
import com.sample.git.parser.impl.models.Repository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Component
@Data
public class GitCliClient implements IRepositoryClient {

	private ProcessRunner runner;

	public GitCliClient(ProcessRunner runner) {
		this.runner = runner;
	}

	@Override
	public List<Commit> getCommits(String url, Page page) throws GitParserException {
		try {
			Repository repository = new Repository(url);
			Path directory = Files.createTempDirectory("git-parser-sample");

			CloneCommand cloneCommand = new CloneCommand(directory, repository, runner);
			Path repositoryDirectory = cloneCommand.call();

			PrettyFormat format = PrettyFormat.builder()
					.sha(true)
					.authorEmail(true)
					.authorName(true)
					.date(true)
					.subject(true)
					.build();

			Pagination pagination = null;
			if(page != null) pagination = new Pagination(page.getPageNumber(), page.getCount());

			LogCommand logCommand = new LogCommand(repositoryDirectory, DateFormat.ISO_STRICT, format, pagination, runner);
			return logCommand.call();
		} catch(IOException e) {
			log.error("Could not create tmp directory", e);
			throw new RuntimeException("Could not create tmp directory");
		}
	}
}
