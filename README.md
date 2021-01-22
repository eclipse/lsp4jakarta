# LSP4Jakarta 
[![License](https://img.shields.io/badge/License-EPL%202.0-red.svg?label=license&logo=eclipse)](https://www.eclipse.org/legal/epl-2.0/)

The Eclipse LSP4Jakarta (Language Server for Jakarta EE) project provides core language support capabilities for the specifications defined under the Jakarta EE (EE4J) umbrella.

This project contains: 

- [jakarta.ls](/jakarta.ls) - Language Server for Jakarta EE
- [jakarta-eclipse](/jakarta-eclipse) - Eclipse JDT LS extension and Eclipse IDE client that consumes the Language Server for Jakarta EE

## Getting Started

1. Run the `./buildAll.sh` script to build the `jarkata-ls-1.0-SNAPSHOT-jar-with-dependencies.jar`

2. Install Eclipse Plugin Development Environment (step 1 here: https://medium.com/@ravi_theja/creating-your-first-eclipse-plugin-9b1b5ba33b58)

3. Import `jakarta-eclipse` and `jakarta.ls` in Eclipse

4. Ensure that projects are being built with `JavaSE-1.8` ("Right-click project" --> "Properties" --> "Java Build Path" --> "Libraries")

5. Add the generated `jarkata-ls-1.0-SNAPSHOT-jar-with-dependencies.jar` to the `jakarta-eclipse` folder

6. Ensure that `jarkata-ls-1.0-SNAPSHOT-jar-with-dependencies.jar` jar is on the Java Build Path for the `jakarta-eclipse` project

7. Run the `jakarta-eclipse` as an Eclipse Application to launch a new instance of Eclipse with LSP4Jakarta (Right-click on the `jakarta-eclipse` project, "Run As" --> "Eclipse Application")

Common errors encountered when setting up the Eclipse workspace: 
- *Bundle 'org.apache.commons.lang3' cannot be resolved* in Eclipse worskspace [#46](https://github.com/eclipse/lsp4jakarta/issues/46)

## Contributing

Our [CONTRIBUTING](CONTRIBUTING.md) document contains details for submitting pull requests.

## Feedback

Please report bugs, issues and feature requests by creating a [GitHub issue](https://github.com/MicroShed/jakarta-ls/issues).
