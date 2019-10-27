package com.sample.git.parser.api;

import com.sample.git.parser.impl.GitDecoratedClient;
import com.sample.git.parser.impl.GitParserException;
import com.sample.git.parser.impl.models.Commit;
import com.sample.git.parser.impl.models.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/git/parser")
public class GitParserApiController {

	@Autowired
	private GitDecoratedClient client;

	@GetMapping("/commits")
	public List<Commit> getCommits(@RequestParam String url,
	                               @RequestParam(required = false) Integer page,
	                               @RequestParam(required = false) Integer count) throws GitParserException {
		Page pagination = null;
		if(page != null && count != null) pagination = new Page(page, count);
		return client.getCommits(url, pagination);
	}

	@ExceptionHandler(GitParserException.class)
	public ResponseEntity<ErrorResponse> handleCheckedException(GitParserException e) {
		return new ResponseEntity<>(new ErrorResponse(e.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
