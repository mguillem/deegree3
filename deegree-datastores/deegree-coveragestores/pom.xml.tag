<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>deegree-coveragestores</artifactId>
  <packaging>pom</packaging>
  <name>deegree-coveragestores</name>
  <description>Abstraction layer for coverage persistence and backend implementations</description>

  <parent>
    <groupId>de.latlon</groupId>
    <artifactId>deegree-datastores</artifactId>
    <version>3.5.0.0</version>
  </parent>

  <profiles>
    <profile>
      <id>oracle</id>
      <modules>
        <module>deegree-coveragestore-oracle-georaster</module>
      </modules>
    </profile>
  </profiles>
  
</project>

