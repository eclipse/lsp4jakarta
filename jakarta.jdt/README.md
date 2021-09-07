## jakarta.jdt
Jakarta EE [Eclipse JDT LS](https://github.com/eclipse/eclipse.jdt.ls) Extension plug-in.
This project is built using Eclipse Tycho (https://www.eclipse.org/tycho/) and requires at least maven 3.0 (http://maven.apache.org/download.html) to be built via CLI. 
Simply run :
```
    mvn install
```
### Components 

- [org.eclipse.lsp4jakarta.jdt.core](./org.eclipse.lsp4jakarta.jdt.core) 
    - Eclipse plug-in
- [org.eclipse.lsp4jakarta.jdt.site](./org.eclipse.lsp4jakarta.jdt.site) 
    - Eclipse update site project
    - Creates a zipped p2 repository in the `/target` directory
- [org.eclipse.lsp4jakarta.jdt.test](./org.eclipse.lsp4jakarta.jdt.test) 
    - Eclipse test plug-in 
    - Contains the bulk of the automated tests

See more information about the Tycho packaging types: https://wiki.eclipse.org/Tycho/Packaging_Types. 