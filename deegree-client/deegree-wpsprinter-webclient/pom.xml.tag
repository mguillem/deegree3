<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>deegree-wpsprinter-webclient</artifactId>
  <packaging>war</packaging>
  <name>deegree-wpsprinter-webclient</name>
  <description>Web client for accessing a Web Processing Service configured for using the process provider creating PDFs</description>

  <properties>
    <deegree.module.status>check</deegree.module.status>
  </properties>

  <parent>
    <groupId>de.latlon</groupId>
    <artifactId>deegree-client</artifactId>
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
      <artifactId>deegree-jsf-core</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>org.apache.tomcat</groupId>
      <artifactId>el-api</artifactId>
    </dependency>
  </dependencies>
</project>
