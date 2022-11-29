<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>deegree-tilestore-filesystem</artifactId>
  <name>deegree-tilestore-filesystem</name>
  <packaging>jar</packaging>
  <description>Tile store implementation for accessing tile files stored in directory hierarchies in the filesystem</description>

  <properties>
    <deegree.module.status>ok</deegree.module.status>
  </properties>

  <parent>
    <groupId>de.latlon</groupId>
    <artifactId>deegree-tilestores</artifactId>
    <version>3.5.0.0</version>
  </parent>

  <build>
    <plugins>
      <plugin>
        <groupId>org.jvnet.jaxb2.maven2</groupId>
        <artifactId>maven-jaxb2-plugin</artifactId>
        <dependencies>
          <!-- workaround for a maven-jaxb2-plugin issue, project dependencies seem not to be added properly -->
          <dependency>
            <groupId>de.latlon</groupId>
            <artifactId>deegree-tilestore-commons</artifactId>
            <version>${project.version}</version>
          </dependency>
        </dependencies>
      </plugin>
    </plugins>
  </build>

  <dependencies>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-tilestore-commons</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>com.sun.media</groupId>
      <artifactId>jai-codec</artifactId>
    </dependency>
    <dependency>
      <groupId>com.sun.media</groupId>
      <artifactId>jai_imageio</artifactId>
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
