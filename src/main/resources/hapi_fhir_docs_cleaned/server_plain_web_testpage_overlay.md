---
source: https://hapifhir.io/hapi-fhir/docs/server_plain/web_testpage_overlay.html
crawled: 2025-08-01T14:06:55.136407
---

# Server Plain Web Testpage Overlay

#  4.8.1Web Testpage Overlay
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/web_testpage_overlay.html#web-testpage-overlay)
HAPI FHIR includes a web UI that can be used to test your server implementation. This UI is the same UI used on the <http://hapi.fhir.org> public HAPI FHIR test server.
The Web Testpage Overlay is a [Maven WAR Overlay](http://maven.apache.org/plugins/maven-war-plugin/overlays.html) project, meaning that you create your own WAR project (which you would likely be doing anyway to create your server) and the overlay drops a number of files into your project. You can then selectively customize the system by replacing the built-in overlay files.
#  4.8.2Adding the Overlay
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/web_testpage_overlay.html#adding-the-overlay)
These instructions assume that you have an existing web project which uses Maven to build. The POM.xml should have a "packaging" type of "war".
Adding the overlay to your project is relatively simple. First, add the "hapi-fhir-testpage-overlay" dependency to the dependencies section of your POM.xml file.
```
<dependencies>
    <!-- ... other dependencies ... -->
    <dependency>
        <groupId>ca.uhn.hapi.fhir</groupId>
        <artifactId>hapi-fhir-testpage-overlay</artifactId>
        <version>${project.version}</version>
        <type>war</type>
        <scope>provided</scope>		
    </dependency>
    <dependency>
        <groupId>ca.uhn.hapi.fhir</groupId>
        <artifactId>hapi-fhir-testpage-overlay</artifactId>
        <version>${project.version}</version>
        <classifier>classes</classifier>
        <scope>provided</scope>		
    </dependency>
</dependencies>

```

Copy
Then, add the following WAR plugin to the plugins section of your POM.xml
```
<build>
    <plugins>
        <!-- ... other plugins ... -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-war-plugin</artifactId>
            <configuration>
                <overlays>
                    <overlay>
                        <groupId>ca.uhn.hapi.fhir</groupId>
                        <artifactId>hapi-fhir-testpage-overlay</artifactId>
                    </overlay>
                </overlays>
            </configuration>
        </plugin>
    </plugins>
</build>

```

Copy
Then, create a Java source file called `FhirTesterConfig.java` and copy the following contents:
```
/**
 * This spring config file configures the web testing module. It serves two
 * purposes:
 * 1. It imports FhirTesterMvcConfig, which is the spring config for the
 *    tester itself
 * 2. It tells the tester which server(s) to talk to, via the testerConfig()
 *    method below
 */
@Configuration
@Import(FhirTesterMvcConfig.class)
public class FhirTesterConfig {

   /**
    * This bean tells the testing webpage which servers it should configure itself
    * to communicate with. In this example we configure it to talk to the local
    * server, as well as one public server. If you are creating a project to
    * deploy somewhere else, you might choose to only put your own server's
    * address here.
    *
    * Note the use of the ${serverBase} variable below. This will be replaced with
    * the base URL as reported by the server itself. Often for a simple Tomcat
    * (or other container) installation, this will end up being something
    * like "http://localhost:8080/hapi-fhir-jpaserver-example". If you are
    * deploying your server to a place with a fully qualified domain name,
    * you might want to use that instead of using the variable.
    */
   @Bean
   public TesterConfig testerConfig() {
      TesterConfig retVal = new TesterConfig();
      retVal.addServer()
            .withId("home")
            .withFhirVersion(FhirVersionEnum.R4)
            .withBaseUrl("${serverBase}/fhir")
            .withName("Local Tester")
            // Add a $diff button on search result rows where version > 1
            .withSearchResultRowOperation(
                  "$diff", id -> id.isVersionIdPartValidLong() && id.getVersionIdPartAsLong() > 1)
            .addServer()
            .withId("hapi")
            .withFhirVersion(FhirVersionEnum.R4)
            .withBaseUrl("http://hapi.fhir.org/baseR4")
            .withName("Public HAPI Test Server")
            // Disable the read and update buttons on search result rows for this server
            .withSearchResultRowInteraction(RestOperationTypeEnum.READ, id -> false)
            .withSearchResultRowInteraction(RestOperationTypeEnum.UPDATE, id -> false);

      /*
       * Use the method below to supply a client "factory" which can be used
       * if your server requires authentication
       */
      // retVal.setClientFactory(clientFactory);

      return retVal;
   }
}

```

Copy
Note that the URL in the file above must be customized to point to the FHIR endpoint your server will be deployed to. For example, if you are naming your project "myfhir-1.0.war" and your endpoint in the WAR is deployed to "/fhirbase/*" then you should put a URL similar to `http://localhost:8080/myfhir-1.0/fhirbase`
Next, create the following directory in your project if it doesn't already exist:
`src/main/webapp/WEB-INF</code>`
In this directory you should open your `web.xml` file, or create it if it doesn't exist. This file is required in order to deploy to a servlet container and you should create it if it does not already exist. Place the following contents in that file, adjusting the package on the `FhirTesterConfig` to match the actual package in which you placed this file.
```
<servlet>
    <servlet-name>spring</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
        <param-name>contextClass</param-name>
        <param-value>org.springframework.web.context.support.AnnotationConfigWebApplicationContext</param-value>
    </init-param>
    <init-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>ca.uhn.example.config.FhirTesterConfig</param-value>
    </init-param>
    <load-on-startup>2</load-on-startup>
</servlet>
<servlet-mapping>
    <servlet-name>spring</servlet-name>
    <url-pattern>/tester/*</url-pattern>
</servlet-mapping>

```

Copy
#  4.8.3Customizing the Overlay
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/web_testpage_overlay.html#customizing-the-overlay)
The most important customization required is to set the FHIR server base URL in the Java configuration file described above.
Beyond this, the entire tester application is built from a number of [Thymeleaf](http://thymeleaf.org) template files, any of which can be replaced to create your own look and feel. All of the templates can be found in your built war (after running the Maven build), or in the target directory's staging area, in `WEB-INF/templates`. By placing a file with the same path/name in your `src/main/webapp/WEB-INF/templates` directory you can replace the built in template with your own file.
#  4.8.4A Complete Example
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/web_testpage_overlay.html#a-complete-example)
The [hapi-fhir-jpaserver-starter](https://github.com/hapifhir/hapi-fhir-jpaserver-starter) project contains a complete working example of the FHIR Tester as a part of its configuration. You may wish to browse its source to see how this works. The example in that project includes an overlay file that overrides some of the basic branding in the base project as well.
#  4.8.5Authentication
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/web_testpage_overlay.html#authentication)
The testing UI uses its own client to talk to your FHIR server. In other words, there are no special "hooks" which the tested uses to retrieve data from your server, it acts as an HTTP client just like any other client.
This does mean that if your server has any authorization requirements, you will need to configure the tester UI to meet those requirements. For example, if your server has been configured to require a HTTP Basic Auth header (e.g. `Authorization: Basic VVNFUjpQQVNT`) you need to configure the tester UI to send those credentials across when it is acting as a FHIR client.
This is done by providing your own implementation of the `ITestingUiClientFactory` interface. This interface takes in some details about the incoming request and produces a client.
[ ](https://hapifhir.io/hapi-fhir/docs/server_plain/paging.html)
4.8 Web Testpage Overlay 
* Plain Server 
* [ 4.0  REST Server Types ](https://hapifhir.io/hapi-fhir/docs/server_plain/server_types.html)
* [ 4.1  Plain Server Introduction ](https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html)
* [ 4.2  Get Started ⚡ ](https://hapifhir.io/hapi-fhir/docs/server_plain/get_started.html)
* [ 4.3  Resource Providers and Plain Providers ](https://hapifhir.io/hapi-fhir/docs/server_plain/resource_providers.html)
* [ 4.4  REST Operations: Overview ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations.html)
* [ 4.5  REST Operations: Search ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_search.html)
* [ 4.6  REST Operations: Extended Operations ](https://hapifhir.io/hapi-fhir/docs/server_plain/rest_operations_operations.html)
* [ 4.7  Paging Search Results ](https://hapifhir.io/hapi-fhir/docs/server_plain/paging.html)
* [ 4.8  Web Testpage Overlay ](https://hapifhir.io/hapi-fhir/docs/server_plain/web_testpage_overlay.html)
* [ 4.9  Multitenancy ](https://hapifhir.io/hapi-fhir/docs/server_plain/multitenancy.html)
* [ 4.10  JAX-RS Support ](https://hapifhir.io/hapi-fhir/docs/server_plain/jax_rs.html)
* [ 4.11  Customizing the CapabilityStatement ](https://hapifhir.io/hapi-fhir/docs/server_plain/customizing_the_capabilitystatement.html)
* [ 4.12  OpenAPI / Swagger ](https://hapifhir.io/hapi-fhir/docs/server_plain/openapi.html)
[ 4.9 Multitenancy ](https://hapifhir.io/hapi-fhir/docs/server_plain/multitenancy.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)