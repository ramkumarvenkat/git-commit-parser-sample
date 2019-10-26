package com.sample.git.parser.impl;

import com.sample.git.parser.impl.models.Commit;
import org.springframework.retry.annotation.Retryable;

import java.util.List;

public interface IRepositoryClient {
	@Retryable(value = GitParserException.class)
	List<Commit> getCommits(String url) throws GitParserException;
}
