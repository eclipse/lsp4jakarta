# jakarta-eclipse extension

### org.eclipse.lsp4jakarta.core

This component contains:
- [Eclipse jdt.ls](https://github.com/eclipse/eclipse.jdt.ls) extension providing Jakarta support.
- Eclipse IDE client that consumes the [Language Server for Jakarta EE](../jakarta.ls) by loading the `jakarta.ls-1.0-SNAPSHOT-jar-with-dependencies.jar`.

### org.eclipse.lsp4jakarta.tests

A [Tycho eclipse-test-plugin](https://wiki.eclipse.org/Tycho/Packaging_Types#eclipse-test-plugin) that runs tests for [org.eclipse.lsp4jakarta.core](/org.eclipse.lsp4jakarta.core).

Note: This eclipse-test-plugin does not load the `jakarta.ls-1.0-SNAPSHOT-jar-with-dependencies.jar` directly, it only loads the `org.jakartaee.lsp4e` eclipse plugin exposed by `org.eclipse.lsp4jakarta.core`. This requires certain `DiagnosticParams` and `CodeActionParams` to be duplicated from jakarta.ls in org.eclipse.lsp4jakarta.core. See [JakartaDiagnosticsParams](https://github.com/eclipse/lsp4jakarta/blob/b04dbba40e2fe164fbfdc0f5c7645df240473521/jakarta-eclipse/org.eclipse.lsp4jakarta.core/src/io/microshed/jakartals/commons/JakartaDiagnosticsParams.java).