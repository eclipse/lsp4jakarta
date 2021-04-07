# jakarta-eclipse extension

### org.eclipse.lsp4jakarta.core

This component contains:
- [Eclipse jdt.ls](https://github.com/eclipse/eclipse.jdt.ls) extension providing Jakarta support.
- Eclipse IDE client that consumes the [Language Server for Jakarta EE](../lsp4jakarta) by loading the `lsp4jakarta-1.0-SNAPSHOT-jar-with-dependencies.jar`.

### org.eclipse.lsp4jakarta.tests

A [Tycho eclipse-test-plugin](https://wiki.eclipse.org/Tycho/Packaging_Types#eclipse-test-plugin) that runs tests for [org.eclipse.lsp4jakarta.core](/org.eclipse.lsp4jakarta.core).

Note: This eclipse-test-plugin does not load the `lsp4jakarta-1.0-SNAPSHOT-jar-with-dependencies.jar` directly, it only loads the `org.jakartaee.lsp4e` eclipse plugin exposed by `org.eclipse.lsp4jakarta.core`. This requires certain `DiagnosticParams` and `CodeActionParams` to be duplicated from lsp4jakarta in org.eclipse.lsp4jakarta.core. See [JakartaDiagnosticsParams](https://github.com/eclipse/lsp4jakarta/blob/master/jakarta-eclipse/org.eclipse.lsp4jakarta.core/src/io/microshed/jakartals/commons/JakartaDiagnosticsParams.java).