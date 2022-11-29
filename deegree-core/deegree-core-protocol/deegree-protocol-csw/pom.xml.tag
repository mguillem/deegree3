<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>deegree-protocol-csw</artifactId>
  <name>deegree-protocol-csw</name>
  <description>Client for accessing remote Catalogue Services for the Web</description>
  <packaging>jar</packaging>

  <properties>
    <deegree.module.status>check</deegree.module.status>
  </properties>

  <parent>
    <groupId>de.latlon</groupId>
    <artifactId>deegree-core-protocol</artifactId>
    <version>3.5.0.0</version>
  </parent>

  <dependencies>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-protocol-commons</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-core-metadata</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
    </dependency>
    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>mockito-core</artifactId>
    </dependency>
  </dependencies>
</project>
