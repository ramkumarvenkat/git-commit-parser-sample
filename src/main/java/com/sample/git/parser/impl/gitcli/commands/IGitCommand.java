package com.sample.git.parser.impl.gitcli.commands;

import com.sample.git.parser.impl.GitParserException;

public interface IGitCommand<V> {
	V call() throws GitParserException;
}
