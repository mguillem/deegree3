<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>deegree-layers</artifactId>
  <packaging>pom</packaging>
  <name>deegree-layers</name>
  <description>Map layer implementations</description>

  <parent>
    <groupId>de.latlon</groupId>
    <artifactId>deegree</artifactId>
    <version>3.5.0.0</version>
  </parent>

  <modules>
    <module>deegree-layers-coverage</module>  
    <module>deegree-layers-feature</module>
    <module>deegree-layers-gdal</module> 
    <module>deegree-layers-remotewms</module>
    <module>deegree-layers-tile</module>
  </modules>

</project>

