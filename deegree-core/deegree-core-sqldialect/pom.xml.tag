<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>deegree-core-sqldialect</artifactId>
  <name>deegree-core-sqldialect</name>
  <packaging>pom</packaging>
  <description>Common interfaces for supporting SQL dialects and specific dialect implementations</description>

  <parent>
    <groupId>de.latlon</groupId>
    <artifactId>deegree-core</artifactId>
    <version>3.5.0.0</version>
  </parent>

  <profiles>
    <profile>
      <id>mssql</id>
      <modules>
        <module>deegree-sqldialect-mssql</module>
      </modules>
    </profile>
  </profiles>

  <modules>
    <module>deegree-sqldialect-commons</module>
    <module>deegree-sqldialect-postgis</module>
    <module>deegree-sqldialect-oracle</module>
  </modules>

</project>

