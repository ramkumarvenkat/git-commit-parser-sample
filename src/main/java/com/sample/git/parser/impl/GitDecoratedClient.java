package com.sample.git.parser.impl;

import com.sample.git.parser.impl.gitapi.GitApiClient;
import com.sample.git.parser.impl.gitcli.GitCliClient;
import com.sample.git.parser.impl.models.Commit;
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
	public List<Commit> getCommits(String url) throws GitParserException {
		return apiClient.getCommits(url);
	}

	@Recover
	public List<Commit> fallback(GitParserException e, String url) throws GitParserException {
		return cliClient.getCommits(url);
	}
}
