# LSP4Jakarta 
[![License](https://img.shields.io/badge/License-EPL%202.0-red.svg?label=license&logo=eclipse)](https://www.eclipse.org/legal/epl-2.0/)

This project contains: 

- [jakarta.ls](/jakarta.ls) - Language Server for Jakarta EE
- [jakarta-eclipse](/jakarta-eclipse) - Eclipse IDE client that consumes the Language Server for Jakarta EE

## Building the Language Server for Jakarta EE and Eclipse IDE Client

1. Run `./buildAll.sh` to create the `jarkata-ls-1.0-SNAPSHOT-jar-with-dependencies.jar` and move it into the `jakarata-eclipse` folder

2. Install Eclipse Plugin Development Environment (step 1 here: https://medium.com/@ravi_theja/creating-your-first-eclipse-plugin-9b1b5ba33b58)

3. Import `jakarta-eclipse` and `jakarta.ls` in Eclipse

4. Ensure that projects are being built with `JavaSE-1.8` ("Right-click project" --> "Properties" --> "Java Build Path" --> "Libraries")

5. Add the generated `jarkata-ls-1.0-SNAPSHOT-jar-with-dependencies.jar` to the `jakartaee-eclipse` folder

6. Ensure that jar is on the Java Build Path for the `jakarta-eclipse` project

7. Right-click on the project, "Run As" --> "Eclipse Application"

## Contributing

Our [CONTRIBUTING](CONTRIBUTING.md) document contains details for submitting pull requests.

## Feedback

Please report bugs, issues and feature requests by creating a [GitHub issue](https://github.com/MicroShed/jakarta-ls/issues).
