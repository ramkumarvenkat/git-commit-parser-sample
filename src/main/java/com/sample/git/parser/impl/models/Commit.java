package com.sample.git.parser.impl.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class Commit {

	private String sha;
	private String authorName;
	private String authorEmail;
	private Instant timestamp;
	private String subject;

}
