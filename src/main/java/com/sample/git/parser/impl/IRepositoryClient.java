package com.sample.git.parser.impl;

import com.sample.git.parser.impl.models.Commit;
import com.sample.git.parser.impl.models.Page;
import org.springframework.retry.annotation.Retryable;

import java.util.List;

public interface IRepositoryClient {
	@Retryable(value = GitParserException.class)
	List<Commit> getCommits(String url, Page page) throws GitParserException;
}
