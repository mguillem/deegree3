<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>deegree-mdstores</artifactId>
  <packaging>pom</packaging>
  <name>deegree-mdstores</name>
  <description>Abstraction layer for metadata record persistence and backend implementations</description>

  <parent>
    <groupId>de.latlon</groupId>
    <artifactId>deegree-datastores</artifactId>
    <version>3.5.0.0</version>
  </parent>

  <modules>
    <module>deegree-mdstore-commons</module>
    <module>deegree-mdstore-ebrim-eo</module>
    <module>deegree-mdstore-iso</module>
    <module>deegree-mdstore-iso-memory</module>
  </modules>

</project>

