<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>deegree-services-wfs</artifactId>
  <packaging>jar</packaging>
  <name>deegree-services-wfs</name>
  <description>OGC Web Feature Service (WFS) implementation - Querying and modifying of geospatial data objects</description>

  <properties>
    <deegree.module.status>ok</deegree.module.status>
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
      <groupId>javax.servlet</groupId>
      <artifactId>servlet-api</artifactId>
    </dependency>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-core-filterfunctions</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-services-commons</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-featurestore-commons</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>commons-lang</groupId>
      <artifactId>commons-lang</artifactId>
    </dependency>
    <dependency>
      <groupId>org.apache.commons</groupId>
      <artifactId>commons-csv</artifactId>
    </dependency>
    <!-- test dependencies -->
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
    </dependency>
    <dependency>
      <groupId>org.mockito</groupId>
      <artifactId>mockito-core</artifactId>
    </dependency>
    <dependency>
      <groupId>org.xmlunit</groupId>
      <artifactId>xmlunit-core</artifactId>
    </dependency>
    <dependency>
      <groupId>org.xmlunit</groupId>
      <artifactId>xmlunit-matchers</artifactId>
    </dependency>
  </dependencies>

</project>

