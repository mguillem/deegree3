<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>deegree-core-filterfunctions</artifactId>
  <name>deegree-core-filterfunctions</name>
  <packaging>jar</packaging>
  <description>Common filter functions</description>

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
      <artifactId>deegree-core-base</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>org.locationtech.jts</groupId>
      <artifactId>jts-core</artifactId>
    </dependency>
  </dependencies>

</project>

