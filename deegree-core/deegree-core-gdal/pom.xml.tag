<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>deegree-core-gdal</artifactId>
  <name>deegree-core-gdal</name>
  <packaging>jar</packaging>
  <description>Provides access to GDAL (via JNI)</description>

  <properties>
    <deegree.module.status>ok</deegree.module.status>
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
      <artifactId>deegree-core-geometry</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>org.gdal</groupId>
      <artifactId>gdal</artifactId>
    </dependency>
	<dependency>
      <groupId>commons-collections</groupId>
      <artifactId>commons-collections</artifactId>
	</dependency>    
  </dependencies>
</project>
