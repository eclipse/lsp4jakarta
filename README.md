# Jakarta EE Language Server
[![License](https://img.shields.io/badge/License-EPL%202.0-red.svg?label=license&logo=eclipse)](https://www.eclipse.org/legal/epl-2.0/)


CANOSP Fall 2020 Project

- [jakarta.ls](/jakarta.ls) - Jakarta EE Language Server
- [jakarta-eclipse](/jakarta-eclipse) - Eclipse Client that consumes the Jakarta EE LS

# Building the Jakarta LS and Eclipse Client

1. Run `./buildAll.sh` to create the `jarkata-ls-1.0-SNAPSHOT-jar-with-dependencies.jar` and move it into the `jakarata-elcipse` folder

2. Install Eclipse Plugin Development Environment (step 1 here: https://medium.com/@ravi_theja/creating-your-first-eclipse-plugin-9b1b5ba33b58)

3. Import `jakarta-eclipse` and `jakarta.ls` in Eclipse

4. Ensure that projects are being built with `JavaSE-1.8` ("Right-click project" --> "Properties" --> "Java Build Path" --> "Libraries")

5. Add the generated `jarkata-ls-1.0-SNAPSHOT-jar-with-dependencies.jar` to the `jakartaee-eclipse` folder

6. Ensure that jar is on the Java Build Path for the `jakarta-eclipse` project

7. Right-click on the project, "Run As" --> "Eclipse Application"

# Contributing

Our [CONTRIBUTING](CONTRIBUTING.md) document contains details for submitting pull requests.