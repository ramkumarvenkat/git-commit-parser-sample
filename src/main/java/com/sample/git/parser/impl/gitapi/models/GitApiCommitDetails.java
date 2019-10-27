package com.sample.git.parser.impl.gitapi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GitApiCommitDetails {

	private GitApiCommitAuthor author;
	private String message;
}
