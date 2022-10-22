# HtmlUnit - XPath

This is the code repository of the XPath support used by HtmlUnit.

For a long time HtmlUnit uses Apache Xalan (https://xalan.apache.org/). But the development of Xalan
has stopped in 2019. Because HtmlUnit only uses the XPath functionality we decide to make a fork
and remove all no longer needed stuff. Additionally we did some code updated for JDK 8 and we add
unit test and the maven based build.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.sourceforge.htmlunit/htmlunit-xpath/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.sourceforge.htmlunit/htmlunit-xpath)

:heart: [Sponsor](https://github.com/sponsors/rbri)

### Project News
[HtmlUnit@Twitter][3]

### Latest release Version 2.65.0 / October 03, 2022

For maven, you would add:

    <dependency>
        <groupId>net.sourceforge.htmlunit</groupId>
        <artifactId>htmlunit-cssparser</artifactId>
        <version>2.65.0</version>
    </dependency>

### Last CI build
The latest builds are available from our
[Jenkins CI build server][2]

[![Build Status](https://jenkins.wetator.org/buildStatus/icon?job=HtmlUnit+-+CSS+Parser)](https://jenkins.wetator.org/job/HtmlUnit%20-%20CSS%20Parser/)

If you use maven please add:

    <dependency>
        <groupId>net.sourceforge.htmlunit</groupId>
        <artifactId>htmlunit-xpath</artifactId>
        <version>2.66.0-SNAPSHOT</version>
    </dependency>

You have to add the sonatype snapshot repository to your pom distributionManagement section also:

    <repository>
        <id>OSS Sonatype snapshots</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
        <releases>
            <enabled>false</enabled>
        </releases>
    </repository>


## Start HtmlUnit - XPath Development

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.
See deployment for notes on how to deploy the project on a live system.

### Prerequisites

You simply only need a local maven installation.


### Building

Create a local clone of the repository and you are ready to start.

Open a command line window from the root folder of the project and call

```
mvn compile
```

### Running the tests

```
mvn test
```

## Contributing

Pull Requests and and all other Community Contributions are essential for open source software.
Every contribution - from bug reports to feature requests, typos to full new features - are greatly appreciated.

## Deployment and Versioning

This part is intended for committer who are packaging a release.

* Check all your files are checked in
* Execute "mvn clean test" to be sure all tests are passing
* Update the version number in pom.xml and README.md
* Execute "mvn clean test" to be sure all tests are passing
* Commit the changes


* Build and deploy the artifacts 

```
   mvn -up clean deploy
```

* Go to [Sonatype staging repositories](https://oss.sonatype.org/index.html#stagingRepositories) and process the deploy
  - select the repository and close it - wait until the close is processed
  - release the package and wait until it is processed

* Create the version on Github
    * login to Github and open project https://github.com/HtmlUnit/htmlunit-xpath
    * click Releases > Draft new release
    * fill the tag and title field with the release number (e.g. 1.1.0)
    * append 
        * htmlunit-xpath-2.x.x.jar
        * htmlunit-xpath-2.x.x.jar.asc 
        * htmlunit-xpath-2.x.x-javadoc.jar
        * htmlunit-xpath-2.x.x-javadoc.jar.asc
    * and publish the release 

* Update the version number in pom.xml to start next snapshot development
* Update the htmlunit pom to use the new release

## Authors

* **RBRi**
* all the contributors to Xalan

## License

This project is licensed under the Apache 2.0 License

## Acknowledgments

Many thanks to all of you contributing to HtmlUnit/CSSParser/Rhino in the past.


[2]: https://jenkins.wetator.org/job/HtmlUnit%20-%20CSS%20Parser/ "HtmlUnit - XPath CI"
[3]: https://twitter.com/HtmlUnit "https://twitter.com/HtmlUnit"
