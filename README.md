# git-commit-parser-sample
Sample Java Spring Boot project to demonstrate how to parse git commits both using the CLI and APIs

## Overview
The project does the following:
*  Fetch and parse git commits through the CLI
*  Fetch and parse git commits through the API
*  Defaults to the API and falls back to the CLI on failure
*  Auto-retry support for both the API and the CLI calls, when there is a timeout or an exception in the response
*  Pagination for both the API and the CLI
*  GET API exposed on top of the functionality above

## Running
`./gradlew bootRun` to run the app and `curl 'http://localhost:8080/api/v1/git/parser/commits?url=https://github.com/ramkumarvenkat/kafka-spark-streaming-druid.git&page=2&count=3'` to test against the API
