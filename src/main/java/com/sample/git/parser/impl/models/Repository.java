package com.sample.git.parser.impl.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@RequiredArgsConstructor
public class Repository {

	private static final Pattern p = Pattern.compile("^(https|git)(:\\/\\/|@)([^\\/:]+)[\\/:]([^\\/:]+)\\/(.+).git$");

	private final String url;

	public Optional<String> getName() {
		Matcher m = p.matcher(url);
		if(m.matches()) return Optional.of(m.group(5));
		else return Optional.empty();
	}

	public Optional<String> getOwnerName() {
		Matcher m = p.matcher(url);
		if(m.matches()) return Optional.of(m.group(4));
		else return Optional.empty();
	}
}
