<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>deegree-themes-remotewms</artifactId>
  <packaging>jar</packaging>
  <version>3.5.0.0</version>
  <name>deegree-themes-remotewms</name>
  <description>Map layer theme implementation for remote Web Map Services</description>

  <properties>
    <deegree.module.status>check</deegree.module.status>
  </properties>

  <parent>
    <groupId>de.latlon</groupId>
    <artifactId>deegree-themes</artifactId>
    <version>3.5.0.0</version>
  </parent>

  <build>
    <plugins>
      <plugin>
        <groupId>org.jvnet.jaxb2.maven2</groupId>
        <artifactId>maven-jaxb2-plugin</artifactId>
        <dependencies>
          <dependency>
            <groupId>de.latlon</groupId>
            <artifactId>deegree-core-theme</artifactId>
            <version>${project.version}</version>
          </dependency>
          <dependency>
            <groupId>de.latlon</groupId>
            <artifactId>deegree-remoteows-wms</artifactId>
            <version>${project.version}</version>
          </dependency>
        </dependencies>
      </plugin>
    </plugins>
  </build>

  <dependencies>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-core-theme</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-remoteows-wms</artifactId>
      <version>${project.version}</version>
    </dependency>
  </dependencies>

</project>
