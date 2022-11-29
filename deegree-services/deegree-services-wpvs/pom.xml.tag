<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>deegree-services-wpvs</artifactId>
  <packaging>jar</packaging>
  <name>deegree-services-wpvs</name>
  <description>Web Perspective View Service (WPVS) implementation - Rendering of 3D terrains and objects</description>

  <properties>
    <deegree.module.status>unmaintained</deegree.module.status>
  </properties>

  <parent>
    <groupId>de.latlon</groupId>
    <artifactId>deegree-services</artifactId>
    <version>3.5.0.0</version>
  </parent>

  <build>
    <plugins>
      <plugin>
        <groupId>org.jvnet.jaxb2.maven2</groupId>
        <artifactId>maven-jaxb2-plugin</artifactId>
      </plugin>
    </plugins>
  </build>

  <dependencies>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-services-commons</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-core-3d</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>javax.servlet</groupId>
      <artifactId>servlet-api</artifactId>
    </dependency>
    <dependency>
      <groupId>commons-fileupload</groupId>
      <artifactId>commons-fileupload</artifactId>
    </dependency>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
    </dependency>
  </dependencies>

</project>
