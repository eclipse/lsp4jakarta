# Building 

## Prerequisites

[JavaSE-11](https://www.oracle.com/ca-en/java/technologies/javase-jdk11-downloads.html), [Maven](https://maven.apache.org/download.cgi), and [Eclipse](https://www.eclipse.org/downloads/) (Eclipse IDE for Enterprise Java and Web Developers is recommended) are required to build the lsp4jakarta project.

Ensure the [Eclipse Plug-in Development Environment (PDE)](https://marketplace.eclipse.org/content/eclipse-pde-plug-development-environment) is installed in your Eclipse workspace.

## Setup instructions
The following are instructions to set up your Eclipse workspace

1. Clone this repository onto your local machine

    `git clone https://github.com/eclipse/lsp4jakarta.git`

2. Build the project with Maven by running the `./buildAll.sh` script from the `lsp4jakarta` directory with Maven and [Tycho](https://github.com/eclipse/tycho). This script runs `mvn clean install` on the components in the following order:

    1. Builds Jakarta EE JDT LS Extension and runs automated tests

    2. Builds Jakarta EE Language Server, creating the `org.eclipse.lsp4jakarta.ls-x.x.x-SNAPSHOT-jar-with-dependencies.jar`

    3. Builds Eclipse client plug-in that consumes the Jakarta EE Language Server and runs automated tests, copies the `org.eclipse.lsp4jakarta.ls-x.x.x-SNAPSHOT-jar-with-dependencies.jar` to the `./jakarta.eclipse/org.eclipse.lsp4jakarta.lsp4jakarta.lsp4e.core/server/` directory

3. Import `jakarta.ls`, `org.eclipse.lsp4jakarta.core`, `org.eclipse.lsp4jakarta.tests` and `lsp4jakarta` projects in Eclipse (File --> Open projects from file system --> Select your LSP4Jakarta clone)

    <img src="/docs/images/building_project_explorer.png" alt="Eclipse project explorer" height="30%" width="30%"/>

    You may also need to install Tycho Project Configurations to resolve Maven plug-in execution errors.

    <img src="/docs/images/building_tycho_configurator_1.png" alt="Tycho configurator errors" height="60%" width="60%"/>
    
    <img src="/docs/images/building_tycho_configurator_2.png" alt="Discover m2e connectors" height="40%" width="40%"/> <img src="/docs/images/building_tycho_configurator_3.png" alt="Installing Tycho Project Configurators" height="50%" width="50%"/>

4. Ensure that the Java projects are being built with `JavaSE-11` (Right-click project --> "Properties" --> "Java Build Path" --> "Libraries")

5. Configure the Java build path for the `org.eclipse.lsp4jakarta.lsp4e.core` project:

    1. Right-click "org.eclipse.lsp4jakarta.lsp4e.core project" --> "Properties" --> "Java Build Path" --> "Libraries"

        <img src="/docs/images/building_lsp4e_1.png" alt="lsp4e project build path" height="60%" width="60%"/>

    2. Select "Add External JARs..." and point to the jar located at `./jakarta.eclipse/org.eclipse.lsp4jakarta.lsp4jakarta.lsp4e.core/server/org.eclipse.lsp4jakarta.ls-x.x.x-SNAPSHOT-jar-with-dependencies.jar`

        <img src="/docs/images/building_lsp4e_2.png" alt="lsp4e project build path add external jar" height="60%" width="60%"/>

        <img src="/docs/images/building_lsp4e_3.png" alt="lsp4e project build path selecting external jar" height="60%" width="60%"/>

        <img src="/docs/images/building_lsp4e_4.png" alt="lsp4e project build path confirming jar" height="60%" width="60%"/>

## Common Errors 

1. When setting up the Eclipse workspace:  
&nbsp;- *Bundle 'org.apache.commons.lang3' cannot be resolved* in Eclipse workspace  
&nbsp;Solution: [#46](https://github.com/eclipse/lsp4jakarta/issues/46)

2. If during initial setup `mvn verify` returns errors or compilation failures, verify that you are using [JavaSE-11](https://www.oracle.com/ca-en/java/technologies/javase-jdk11-downloads.html). You may have to configure `$JAVA_HOME` variable and make sure it is pointing to the correct location.

## Running as an Eclipse application

Run `org.eclipse.lsp4jakarta.lsp4e.core` as an Eclipse Application to launch a new instance of Eclipse with LSP4Jakarta (Right-click on the `org.eclipse.lsp4jakarta.lsp4e.core` project, "Run As" --> "Eclipse Application"). A new Eclipse application will launch with the LSP4Jakarta Eclipse client plug-in installed.

<img src="/docs/images/building_run_lsp4e.png" alt="Run lsp4e Eclipse plug-in" height="60%" width="60%"/>

**To Debug**:

Debug `org.eclipse.lsp4jakarta.lsp4e.core` as an Eclipse Application to launch a new instance of Eclipse with LSP4Jakarta (Right-click on the `org.eclipse.lsp4jakarta.lsp4e.core` project, "Run As" --> "Eclipse Application"). A new Eclipse application will launch with the LSP4Jakarta Eclipse client plug-in installed.

<img src="/docs/images/building_debug_lsp4e.png" alt="Debug lsp4e Eclipse plug-in" height="60%" width="60%"/>
