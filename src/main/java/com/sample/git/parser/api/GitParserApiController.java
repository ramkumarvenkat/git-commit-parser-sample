package com.sample.git.parser.api;

import com.sample.git.parser.impl.GitParserException;
import com.sample.git.parser.impl.cli.GitCliClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/git/parser")
public class GitParserApiController {

	@Autowired
	private GitCliClient cliClient;

	@GetMapping("/commits")
	public ResponseEntity getCommits(@RequestParam String url) {
		try {
			return new ResponseEntity<>(cliClient.getCommits(url), HttpStatus.OK);
		} catch (GitParserException e) {
			return new ResponseEntity<>(new ErrorResponse(e.getMessage()), null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
