# Jakarta EE LS 

CANOSP Fall 2020 Project

- [jakartalsp](/jakarta.ls) - Jakarta EE LS
- [jakarta-eclipse](/jakarta-eclipse) - Eclipse Client that consumes the Jakarta EE LS

# Getting Started

1. Build the Jakarta EE LS: `mvn clean install` from within the `jarkarta.ls` folder to create the `jarkata-ls-1.0-SNAPSHOT-jar-with-dependencies.jar` in the target directory

2. Install Eclipse Plugin Development Environment (step 1 here: https://medium.com/@ravi_theja/creating-your-first-eclipse-plugin-9b1b5ba33b58)

3. Import `jakarta-eclipse` and `jakarta.ls` in Eclipse

4. Ensure that projects are being built with `JavaSE-1.8` ("Right-click project" --> "Properties" --> "Java Build Path" --> "Libraries")

5. Add the generated `jarkata-ls-1.0-SNAPSHOT-jar-with-dependencies.jar` to the `jakartaee-eclipse` folder

6. Ensure that jar is on the Java Build Path for the `jakarta-eclipse` project

7. Right-click on the project, "Run As" --> "Eclipse Application"

8. When you open up a Java file you should see the Jakarta EE server initializing and a sample diagnostic
