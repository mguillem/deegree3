<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>deegree-core-base</artifactId>
  <name>deegree-core-base</name>
  <packaging>jar</packaging>
  <description>Data model for feature data, data model and evaluation for filter expressions, GML readers and writers (needs splitting)</description>

  <properties>
    <deegree.module.status>rework</deegree.module.status>
  </properties>

  <parent>
    <groupId>de.latlon</groupId>
    <artifactId>deegree-core</artifactId>
    <version>3.5.0.0</version>
  </parent>

  <dependencies>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-core-coverage</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>commons-codec</groupId>
      <artifactId>commons-codec</artifactId>
    </dependency>
    <dependency>
      <groupId>org.apache.commons</groupId>
      <artifactId>commons-collections4</artifactId>
    </dependency>
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
    <dependency>
      <groupId>com.jayway.jsonpath</groupId>
      <artifactId>json-path-assert</artifactId>
    </dependency>
  </dependencies>
</project>

