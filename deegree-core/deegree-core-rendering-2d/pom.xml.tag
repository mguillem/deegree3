<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>deegree-core-rendering-2d</artifactId>
  <name>deegree-core-rendering-2d</name>
  <packaging>jar</packaging>
  <description>Rendering of maps from geospatial data</description>

  <properties>
    <deegree.module.status>check</deegree.module.status>
  </properties>

  <parent>
    <groupId>de.latlon</groupId>
    <artifactId>deegree-core</artifactId>
    <version>3.5.0.0</version>
  </parent>

  <dependencies>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-core-geometry</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-core-tile</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-core-style</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>javax.media</groupId>
      <artifactId>jai-core</artifactId>
    </dependency>
    <dependency>
      <groupId>com.sun.media</groupId>
      <artifactId>jai-codec</artifactId>
    </dependency>
    <dependency>
      <groupId>org.locationtech.jts</groupId>
      <artifactId>jts-core</artifactId>
    </dependency>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
    </dependency>
  </dependencies>

</project>

