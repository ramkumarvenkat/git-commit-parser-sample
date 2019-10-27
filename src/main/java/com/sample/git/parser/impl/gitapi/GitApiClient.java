package com.sample.git.parser.impl.gitapi;

import com.sample.git.parser.impl.GitParserException;
import com.sample.git.parser.impl.IRepositoryClient;
import com.sample.git.parser.impl.gitapi.models.GitApiCommit;
import com.sample.git.parser.impl.gitapi.models.GitApiCommitAuthor;
import com.sample.git.parser.impl.gitapi.models.GitApiCommitDetails;
import com.sample.git.parser.impl.models.Commit;
import com.sample.git.parser.impl.models.Repository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Slf4j
@Component
public class GitApiClient implements IRepositoryClient {

	private RestTemplate restTemplate;

	public GitApiClient(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	public List<Commit> getCommits(String url) throws GitParserException {
		Repository repository = new Repository(url);
		String owner = repository.getOwnerName().orElseThrow(() -> new IllegalArgumentException("Repository url is wrong, cannot extract the owner name"));
		String name = repository.getName().orElseThrow(() -> new IllegalArgumentException("Repository url is wrong, cannot extract the name"));

		try {
			ResponseEntity<List<GitApiCommit>> response = restTemplate.exchange(
					"/repos/{owner}/{repo}/commits",
					HttpMethod.GET,
					null,
					new ParameterizedTypeReference<>() {},
					owner, name);
			return response.getBody().stream().map(apiCommit -> {
				GitApiCommitDetails c = apiCommit.getCommit();
				GitApiCommitAuthor author = c.getAuthor();
				return new Commit(apiCommit.getSha(), author.getName(), author.getEmail(), Instant.parse(author.getDate()), c.getMessage());
			}).collect(Collectors.toList());
		} catch(RestClientException e) {
			String error = "Exception in fetching commits from github api " + e.getMessage();
			log.error(error, e);
			throw new GitParserException(error, e);
		}
	}
}
