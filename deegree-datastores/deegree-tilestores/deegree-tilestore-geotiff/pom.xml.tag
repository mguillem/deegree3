<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>deegree-tilestore-geotiff</artifactId>
  <name>deegree-tilestore-geotiff</name>
  <packaging>jar</packaging>
  <description>Tile store implementation that accesses tiles stored in GeoTIFF/BigTIFF files</description>

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
      <groupId>de.latlon</groupId>
      <artifactId>deegree-core-coverage</artifactId>
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
      <groupId>it.geosolutions.imageio-ext</groupId>
      <artifactId>imageio-ext-tiff</artifactId>
    </dependency>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
    </dependency>
  </dependencies>
</project>

