package com.sample.git.parser.impl;

import com.sample.git.parser.impl.gitapi.GitApiClient;
import com.sample.git.parser.impl.gitcli.GitCliClient;
import com.sample.git.parser.impl.models.Commit;
import com.sample.git.parser.impl.models.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GitDecoratedClient implements IRepositoryClient {

	private final GitApiClient apiClient;
	private final GitCliClient cliClient;

	@Override
	public List<Commit> getCommits(String url, Page page) throws GitParserException {
		return apiClient.getCommits(url, page);
	}

	@Recover
	public List<Commit> fallback(GitParserException e, String url, Page page) throws GitParserException {
		return cliClient.getCommits(url, page);
	}
}
