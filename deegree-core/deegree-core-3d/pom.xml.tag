<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>deegree-core-3d</artifactId>
  <name>deegree-core-3d</name>
  <packaging>jar</packaging>
  <description>Data model and rendering for terrain data and buildings</description>

  <properties>
    <deegree.module.status>unmaintained</deegree.module.status>
  </properties>

  <parent>
    <groupId>de.latlon</groupId>
    <artifactId>deegree-core</artifactId>
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
      <artifactId>deegree-featurestore-commons</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-core-rendering-2d</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-protocol-wms</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-protocol-wfs</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>jogl</groupId>
      <artifactId>jogl</artifactId>
    </dependency>
    <dependency>
      <groupId>jogl</groupId>
      <artifactId>gluegen-rt</artifactId>
    </dependency>
  </dependencies>
</project>

