<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>deegree-tilestores</artifactId>
  <packaging>pom</packaging>
  <name>deegree-tilestores</name>
  <description>Abstraction layer for map tile persistence and backend implementations</description>

  <parent>
    <groupId>de.latlon</groupId>
    <artifactId>deegree-datastores</artifactId>
    <version>3.5.0.0</version>
  </parent>

  <modules>
    <module>deegree-tilestore-cache</module>
    <module>deegree-tilestore-commons</module>
    <module>deegree-tilestore-filesystem</module>
    <module>deegree-tilestore-gdal</module>
    <module>deegree-tilestore-geotiff</module>
    <module>deegree-tilestore-merge</module>
    <module>deegree-tilestore-remotewms</module>
    <module>deegree-tilestore-remotewmts</module>
  </modules>

</project>

