<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>deegree-core-featureinfo</artifactId>
  <name>deegree-core-featureinfo</name>
  <packaging>jar</packaging>
  <description>Featureinfo export utilities (Templating, HTML, format handling)</description>

  <properties> 
    <deegree.module.status>rework</deegree.module.status>
  </properties>

  <parent>
    <groupId>de.latlon</groupId>
    <artifactId>deegree-core</artifactId>
    <version>3.5.0.0</version>
  </parent>

  <build>
    <plugins>
      <plugin>
        <groupId>org.antlr</groupId>
        <artifactId>antlr3-maven-plugin</artifactId>
        <executions>
          <execution>
            <goals>
              <goal>antlr</goal>
            </goals>
            <configuration>
                <libDirectory>target/generated-sources/antlr3/org/deegree/featureinfo/templating/</libDirectory>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>

  <dependencies>
    <dependency>
      <groupId>de.latlon</groupId>
      <artifactId>deegree-core-base</artifactId>
      <version>${project.version}</version>
    </dependency>
    <dependency>
      <groupId>org.antlr</groupId>
      <artifactId>antlr-runtime</artifactId>
    </dependency>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
    </dependency>
  </dependencies>

</project>

