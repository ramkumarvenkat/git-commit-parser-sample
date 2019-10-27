package com.sample.git.parser.impl.gitapi.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GitApiCommitAuthor {

	private String name;
	private String email;
	private String date;
}
