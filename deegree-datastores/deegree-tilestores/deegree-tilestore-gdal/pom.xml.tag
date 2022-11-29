<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>de.latlon</groupId>
  <artifactId>deegree-tilestore-gdal</artifactId>
  <packaging>jar</packaging>
  <description>Tile store implementation that accesses raster files via GDAL (http://www.gdal.org)</description>  

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

  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>de.latlon</groupId>
        <artifactId>deegree-tilestore-commons</artifactId>
        <version>${project.version}</version>
      </dependency>
      <dependency>
        <groupId>de.latlon</groupId>
        <artifactId>deegree-core-gdal</artifactId>
        <version>${project.version}</version>
      </dependency>      
    </dependencies>
  </dependencyManagement>

  <dependencies>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-tilestore-commons</artifactId>
    </dependency>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-core-gdal</artifactId>
    </dependency>
  </dependencies>
</project>
