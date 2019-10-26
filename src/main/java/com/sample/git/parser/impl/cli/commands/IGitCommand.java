package com.sample.git.parser.impl.cli.commands;

import com.sample.git.parser.impl.GitParserException;

public interface IGitCommand<V> {
	V call() throws GitParserException;
}
