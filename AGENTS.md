# htmlunit-xpath

This repository contains the XPath support library used by HtmlUnit. It's a Java library that provides XPath functionality for querying and navigating XML/HTML documents.
The project was forked from Apache Xalan, which discontinued development in 2019. Since HtmlUnit only requires XPath functionality, this fork removes unnecessary components, updates the code for modern Java, and maintains only XPath support.

This project is licensed under Apache License 2.0. Always include the appropriate Apache 2.0 license header in all new files.

## Tech Stack

- Language: Java 17+ (version 5.x requires JDK 17)
- Build Tool: Maven
- Testing: JUnit Jupiter 6.x
- Code Quality: Checkstyle, PMD, SpotBugs
- CI/CD: Jenkins

## Project Structure

```
src/main/java/org/htmlunit/xpath/
├── axes/        # XPath axis iterators (child, descendant, attribute, etc.)
├── compiler/    # XPath expression compiler, parser, lexer, and op codes
├── functions/   # XPath function implementations (string, boolean, number, node-set)
├── objects/     # XPath result type objects (XBoolean, XNumber, XString, XNodeSet, XObject)
├── operations/  # XPath operators (arithmetic, comparison, logical)
├── patterns/    # XPath pattern matching (step patterns, union patterns)
├── res/         # Error messages and resources
├── xml/         # XML/DTM utilities
└── *.java       # Core classes: XPath, XPathAPI, XPathContext, Expression, etc.
```

## Useful commands

1. Build: `mvn package`
2. Run tests: `mvn test`
3. Run all checks (tests, formatting, static analysis): `mvn verify`
4. Checkstyle only: `mvn checkstyle:check`

## Rules and code style

- Follow the Checkstyle rules defined in `checkstyle.xml`.
- Use 4 spaces for indentation (no tabs).
- Maximum line length: check `checkstyle.xml` for current limit.
- Follow Java naming conventions strictly.
- All public methods and classes must have Javadoc comments.
- Be restrained in regard to writing code comments.
- Always add unit tests for any new feature or bug fixes.
  - Tests go in `src/test/java/org/htmlunit/xpath/` mirroring the main source structure.
  - Function tests go in the `functions/` subdirectory.
  - Operation tests go in the `operations/` subdirectory.
  - New test classes should extend `AbstractXPathTest` for helper methods.
- New test classes should be written using JUnit Jupiter (JUnit 5+).
- Code style is enforced via Checkstyle. After every change, make sure there are no checkstyle violations.