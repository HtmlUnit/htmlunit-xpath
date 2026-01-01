# htmlunit-xpath

This repository contains the XPath support library used by HtmlUnit. It's a Java library that provides XPath functionality for querying and navigating XML/HTML documents.
The project was forked from Apache Xalan, which discontinued development in 2019. Since HtmlUnit only requires XPath functionality, this fork removes unnecessary components, updates the code for modern Java (JDK 8+), and adds comprehensive unit tests with a Maven-based build system.

This project is licensed under Apache License 2.0. Always include the appropriate Apache 2.0 license header in all new files.


## Tech Stack

Language: Java 17+ (version 5.x will require JDK 17)
Build Tool: Maven
Testing: JUnit
Code Quality: Checkstyle, PMD, SpotBugs
CI/CD: Jenkins

## Useful commands

1. Build: `./mvn package`
2. Run tests: `./mvn test`
4. Checks (tests, formatting): `./mvn check`

## Rules and code style

- Follow the Checkstyle rules defined in checkstyle.xml
- Use 4 spaces for indentation (no tabs)
- Maximum line length: check checkstyle.xml for current limit
- Follow Java naming conventions strictly
- All public methods and classes must have Javadoc comments
- Be restrained in regard to writing code comments.
- Always add unit tests for any new feature or bug fixes. They should go either in `rhino` or `tests`; search for
  existing tests to make a decision on a case-by-case basis.
- New test classes should be written using JUnit 5.
- Code style is enforced via checkstyle. After every change, make sure there are no checkstyle validations.