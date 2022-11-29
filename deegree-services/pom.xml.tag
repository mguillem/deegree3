<?xml version="1.0"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>deegree-services</artifactId>
  <packaging>pom</packaging>
  <name>deegree-services</name>
  <description>OGC Web Service implementations</description>

  <parent>
    <groupId>de.latlon</groupId>
    <artifactId>deegree</artifactId>
    <version>3.5.0.0</version>
  </parent>

  <build>
    <resources>
      <resource>
        <directory>src/main/resources</directory>
        <filtering>false</filtering>
      </resource>
      <resource>
        <directory>target/generated-resources</directory>
        <filtering>false</filtering>
      </resource>
    </resources>
  </build>

  <profiles>
    <profile>
      <id>handbook</id>
      <modules>
        <module>deegree-webservices-handbook</module>
      </modules>
    </profile>
  </profiles>

  <modules>
    <module>deegree-services-commons</module>
    <module>deegree-services-config</module>
    <module>deegree-services-csw</module>
    <module>deegree-services-wcs</module>
    <module>deegree-services-wfs</module>
    <module>deegree-services-wms</module>
    <module>deegree-services-wmts</module>
    <module>deegree-services-wps</module>
    <module>deegree-services-wpvs</module>
    <module>deegree-webservices</module>
  </modules>

</project>

