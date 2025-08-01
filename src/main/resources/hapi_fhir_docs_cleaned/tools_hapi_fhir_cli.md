---
source: https://hapifhir.io/hapi-fhir/docs/tools/hapi_fhir_cli.html
crawled: 2025-08-01T14:09:13.787640
---

# Tools Hapi Fhir Cli

#  15.0.1Command Line Tool for HAPI FHIR
[ ](https://hapifhir.io/hapi-fhir/docs/tools/hapi_fhir_cli.html#command-line-tool-for-hapi-fhir)
**hapi-fhir-cli** is the HAPI FHIR Command Line tool. It features a number of HAPI's built-in features as easy to use command line options.
##  15.0.1.1Download and Installation[](https://hapifhir.io/hapi-fhir/docs/tools/hapi_fhir_cli.html#download-and-installation)
You can get the tool by downloading it from our [GitHub Releases](https://github.com/hapifhir/hapi-fhir/releases) page (look for the archive named `hapi-fhir-[version]-cli.tar.bz2` on OSX/Linux or `hapi-fhir-[version]-cli.zip` on Windows).
When you have downloaded the archive (either ZIP or tar.bz2), expand it into a directory where you will keep it, and add this directory to your path.
You can now try the tool out by executing the following command: `hapi-fhir-cli`
This command should show a help screen, as shown in the screenshot below.
![Basic screen shot](https://hapifhir.io/hapi-fhir/docs/images/hapi-fhir-cli.png)
##  15.0.1.2Download and Installation - Mac/OSX[](https://hapifhir.io/hapi-fhir/docs/tools/hapi_fhir_cli.html#download-and-installation-macosx)
hapi-fhir-cli is available as a [Homebrew](https://brew.sh/) package for Mac. It can be installed using the following command:
```
brew install hapi-fhir-cli

```

Copy
##  15.0.1.3Troubleshooting[](https://hapifhir.io/hapi-fhir/docs/tools/hapi_fhir_cli.html#troubleshooting)
The tool should work correctly on any system that has Java 8 (or newer) installed. If it is not working correctly, first try the following command to test if Java is installed:
```
java -version

```

Copy
If this command does not produce output similar to the following, you should install/reinstall Java.
```
java version "1.8.0_60"
Java(TM) SE Runtime Environment (build 1.8.0_60-b27)
Java HotSpot(TM) 64-Bit Server VM (build 25.60-b23, mixed mode)

```

Individual commands can be troubleshooted by adding the `--debug` command line argument.
If this does not help, please post a question on our [Google Group](https://groups.google.com/d/forum/hapi-fhir).
#  15.0.2Server (run-server)
[ ](https://hapifhir.io/hapi-fhir/docs/tools/hapi_fhir_cli.html#server-run-server)
The CLI tool can be used to start a local, fully functional FHIR server which you can use for testing. To start this server, simply issue the command `hapi-fhir-cli run-server` as shown in the example below:
![Run Server](https://hapifhir.io/hapi-fhir/docs/images/hapi-fhir-cli-run-server.png)
Once the server has started, you can access the testing webpage by pointing your browser at <http://localhost:8080/>. The FHIR server base URL will be <http://localhost:8080/baseDstu3/>.
Note that by default this server will not be populated with any resources at all. You can easily populate it with the FHIR example resources by **leaving it running** and opening a second terminal window, then using the `hapi-fhir-cli upload-examples` command (see the section below).
The server uses a local Derby database instance for storage. You may want to execute this command in an empty directory, which you can clear if you want to reset the server.
#  15.0.3Upload Example Resources (upload-examples)
[ ](https://hapifhir.io/hapi-fhir/docs/tools/hapi_fhir_cli.html#upload-example-resources-upload-examples)
The **upload-examples** command downloads the complete set of FHIR example resources from the HL7 website, and uploads them to a server of your choice. This can be useful to populate a server with test data.
To execute this command, uploading test resources to a local CLI server, issue the following: `hapi-fhir-cli upload-examples -v dstu3 -t http://localhost:8080/baseDstu3`
Note that this command may take a surprisingly long time to complete because of the large number of examples.
#  15.0.4Upload Terminology
[ ](https://hapifhir.io/hapi-fhir/docs/tools/hapi_fhir_cli.html#upload-terminology)
The HAPI FHIR JPA server has a terminology server, and has the ability to be populated with "external" code systems. These code systems are systems that contain large numbers of codes, so the codes are not stored directly inside the resource body.
HAPI has methods for uploading several popular code systems into its tables using the distribution files produced by the respective code systems. This is done using the `upload-terminology` command. The following examples show how to do this for several popular code systems.
Note that the path and exact filename of the terminology files will likely need to be adjusted for your local disk structure.
15.0.4.0.1SNOMED CT ```
./hapi-fhir-cli upload-terminology -d Downloads/SnomedCT_InternationalRF2_PRODUCTION_20220131T120000Z.zip -v r4 -t http://localhost:8080/fhir -u http://snomed.info/sct

```
15.0.4.0.2LOINC ```
./hapi-fhir-cli upload-terminology -d Downloads/LOINC_2.54_MULTI-AXIAL_HIERARCHY.zip -d Downloads/LOINC_2.54_Text.zip -v r4 -t http://localhost:8080/fhir -u http://loinc.org

```
15.0.4.0.3ICD-10 (International Version) ```
./hapi-fhir-cli upload-terminology -d Downloads/icdClaML2019ens.zip -v r4 -t http://localhost:8080/fhir -u http://hl7.org/fhir/sid/icd-10

```
15.0.4.0.4ICD-10-CM ```
./hapi-fhir-cli upload-terminology -d Downloads/icd10cm_tabular_2021.xml -v r4 -t http://localhost:8080/fhir -u http://hl7.org/fhir/sid/icd-10-cm

```

#  15.0.5Migrate Database
[ ](https://hapifhir.io/hapi-fhir/docs/tools/hapi_fhir_cli.html#migrate-database)
The `migrate-database` command may be used to Migrate a database schema when upgrading a [HAPI FHIR JPA](https://hapifhir.io/hapi-fhir/docs/server_jpa/introduction.html) project from one version of HAPI FHIR to another version.
See [Upgrading HAPI FHIR JPA](https://hapifhir.io/hapi-fhir/docs/server_jpa/upgrading.html) for information on how to use this command.
#  15.0.6Clear Migration lock
[ ](https://hapifhir.io/hapi-fhir/docs/tools/hapi_fhir_cli.html#clear-migration-lock)
the `clear-migration-lock` command should be used if an upgrade to HAPI-FHIR failed during a migration. The migration system creates a lock row when it begins. If the migration is cancelled before it finishes, the system will be left in an inconsistent state. In order to resume the migration, the lock row must be removed. From your migration logs, you will see a line which looks like the following:
```
Migration Lock Row added. [uuid=05931c87-c2a4-49d6-8d82-d8ce09fdd8ef]

```

Copy
In order to clear this migration lock, you can run:
```
clear-migration-lock --lock-uuid 05931c87-c2a4-49d6-8d82-d8ce09fdd8ef

```

Copy
#  15.0.7Reindex Terminology
[ ](https://hapifhir.io/hapi-fhir/docs/tools/hapi_fhir_cli.html#reindex-terminology)
The `reindex-terminology` command may be used to recreate freetext indexes for terminology resources.
To execute this command to reindex terminology resources to a local CLI server, issue the following:
```
reindex-terminology -v r4 -t "http://localhost:8000"

```

15.0 Command Line Interface (CLI) Tool 
* Tools 
* [ 15.0  Command Line Interface (CLI) Tool ](https://hapifhir.io/hapi-fhir/docs/tools/hapi_fhir_cli.html)
* * *
![raccoon logo without text](https://hapifhir.io/hapi-fhir/images/logos/raccoon-forwards.png)