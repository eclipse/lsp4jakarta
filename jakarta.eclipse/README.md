## jakarta.eclipse
Eclipse client plug-in that consumes the Jakarta EE Language Server.
This project is built using Eclipse Tycho (https://www.eclipse.org/tycho/) and requires at least maven 3.0 (http://maven.apache.org/download.html) to be built via CLI. 
Simply run :
```
    mvn install
```
### Components 

- [org.eclipse.lsp4jakarta.lsp4e.core](./org.eclipse.lsp4jakarta.lsp4e.core) 
    - Eclipse plug-in
- [org.eclipse.lsp4jakarta.lsp4e.feature](./org.eclipse.lsp4jakarta.lsp4e.feature)
    - Eclipse feature
    - Packages the `org.eclipse.lsp4jakarta.lsp4e.core` plug-in and defines a dependency on `org.eclipse.lsp4jakarta.jdt.core` (JDT extension). See the [feature.xml](./org.eclipse.lsp4jakarta.lsp4e.feature/feature.xml)
- [org.eclipse.lsp4jakarta.lsp4e.site](./org.eclipse.lsp4jakarta.lsp4e.site) 
    - Eclipse update site project
    - Creates a zipped p2 repository in the `/target` directory
- [org.eclipse.lsp4jakarta.lsp4e.test](./org.eclipse.lsp4jakarta.lsp4e.test) 
    - Eclipse test plug-in 

See more information about the Tycho packaging types: https://wiki.eclipse.org/Tycho/Packaging_Types. 

This project was initially generated using the [tycho-eclipse-plug-in-archetype](https://github.com/open-archetypes/tycho-eclipse-plug-in-archetype).