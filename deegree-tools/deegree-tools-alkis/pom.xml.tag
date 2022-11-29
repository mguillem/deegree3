<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>deegree-tools-alkis</artifactId>
  <packaging>jar</packaging>
  <name>deegree-tools-alkis</name>
  <description>Command line tools for ALKIS-related tasks</description>

  <properties>
    <deegree.module.status>check</deegree.module.status>
    <toolboxname>d3alkis</toolboxname>
  </properties>

  <parent>
    <groupId>de.latlon</groupId>
    <artifactId>deegree-tools</artifactId>
    <version>3.5.0.0</version>
  </parent>

  <dependencies>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-core-base</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-services-wfs</artifactId>
      <version>${project.version}</version>
    </dependency>
  </dependencies>

</project>
