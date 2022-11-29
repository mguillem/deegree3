<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>deegree-featurestores</artifactId>
  <packaging>pom</packaging>
  <name>deegree-featurestores</name>
  <description>Abstraction layer for feature ("geo object") persistence and backend implementations</description>

  <parent>
    <groupId>de.latlon</groupId>
    <artifactId>deegree-datastores</artifactId>
    <version>3.5.0.0</version>
  </parent>

  <modules>
    <module>deegree-featurestore-commons</module>
    <module>deegree-featurestore-memory</module>
    <module>deegree-featurestore-remotewfs</module>
    <module>deegree-featurestore-shape</module>
    <module>deegree-featurestore-simplesql</module>
    <module>deegree-featurestore-sql</module>
  </modules>

</project>

