<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>deegree-tools-base</artifactId>
  <packaging>jar</packaging>
  <name>deegree-tools-base</name>
  <description>Command line tools for most subsystems (feature, crs, coverage, metadata). Needs splitting</description>

  <properties>
    <deegree.module.status>rework</deegree.module.status>
    <toolboxname>d3toolbox</toolboxname>
  </properties>

  <parent>
    <groupId>de.latlon</groupId>
    <artifactId>deegree-tools</artifactId>
    <version>3.5.0.0</version>
  </parent>

  <dependencies>
    <dependency>
      <groupId>jakarta.xml.bind</groupId>
      <artifactId>jakarta.xml.bind-api</artifactId>
    </dependency>
    <dependency>
      <groupId>com.sun.xml.bind</groupId>
      <artifactId>jaxb-impl</artifactId>
    </dependency>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-core-rendering-2d</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-featurestore-sql</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-mdstore-iso</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-protocol-csw</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>org.apache.logging.log4j</groupId>
      <artifactId>log4j-slf4j-impl</artifactId>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <artifactId>maven-assembly-plugin</artifactId>
        <configuration>
          <descriptors>
            <descriptor>src/assembly/assembly.xml</descriptor>
          </descriptors>
        </configuration>
        <executions>
          <execution>
            <id>make-assembly</id>
            <phase>package</phase>
            <goals>
              <goal>single</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>

</project>
