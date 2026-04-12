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

src/main/module-info/
└── module-info.java  # Java Platform Module System descriptor (separate for IDE compatibility)
```

## Module System (JPMS)

This project uses the Java Platform Module System. The `module-info.java` file is located in `src/main/module-info/` (instead of the standard `src/main/java/`) to avoid compatibility issues with Eclipse IDE.

**Key Points:**
- The module descriptor is automatically included during Maven builds via `build-helper-maven-plugin`
- Eclipse users won't encounter module-related compilation errors
- IntelliJ IDEA and other IDEs handle this setup correctly
- The final JAR is a proper modular JAR with `module-info.class`

See `src/main/module-info/README.md` for detailed information about this setup.

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

## IDE Setup

### Eclipse
Eclipse may have issues with `module-info.java`. This project addresses this by keeping the module descriptor in a separate directory (`src/main/module-info/`) that Eclipse doesn't process. You can import the project as a standard Maven project without any special configuration.

### IntelliJ IDEA
IntelliJ IDEA handles Java modules well and should work with this project out of the box:
1. Open the project via `File → Open` and select the `pom.xml`
2. Let IntelliJ import the Maven project
3. The module descriptor will be recognized automatically