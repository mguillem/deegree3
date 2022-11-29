<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <artifactId>deegree-workspaces</artifactId>
  <packaging>pom</packaging>
  <name>deegree-workspaces</name>
  <description>Example configurations for deegree web services</description>

  <parent>
    <groupId>de.latlon</groupId>
    <artifactId>deegree</artifactId>
    <version>3.5.0.0</version>
  </parent>

  <build>
    <pluginManagement>
      <plugins>
        <plugin>
          <artifactId>maven-assembly-plugin</artifactId>
          <version>3.3.0</version>
          <configuration>
            <descriptors>
              <descriptor>src/assembly/assembly.xml</descriptor>
            </descriptors>
            <appendAssemblyId>false</appendAssemblyId>
          </configuration>
          <executions>
            <execution>
              <id>create-zip</id>
              <phase>package</phase>
              <goals>
                <goal>single</goal>
              </goals>
            </execution>
          </executions>
        </plugin>
      </plugins>
    </pluginManagement>
  </build>

  <modules>
    <module>deegree-workspace-aixm</module>
    <module>deegree-workspace-compliance-tests</module>
    <module>deegree-workspace-csw</module>
    <module>deegree-workspace-csw-memory-tests</module>    
    <module>deegree-workspace-geosciml</module>
    <module>deegree-workspace-utah</module>
    <module>deegree-workspace-wcts</module>
    <module>deegree-workspace-wps</module>
  </modules>

</project>

