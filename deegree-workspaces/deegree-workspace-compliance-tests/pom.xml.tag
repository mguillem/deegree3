<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>deegree-workspace-compliance-tests</artifactId>
  <packaging>pom</packaging>
  <name>deegree-workspace-compliance-tests</name>
  <description>Configuration for the OGC CITE test suites</description>

  <properties>
    <deegree.module.status>check</deegree.module.status>
  </properties>

  <parent>
    <groupId>de.latlon</groupId>
    <artifactId>deegree-workspaces</artifactId>
    <version>3.5.0.0</version>
  </parent>

  <build>
    <plugins>
      <plugin>
        <artifactId>maven-assembly-plugin</artifactId>
      </plugin>
    </plugins>
  </build>

</project>

