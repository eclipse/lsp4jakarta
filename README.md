# Eclipse LSP4Jakarta 
[![License](https://img.shields.io/badge/License-EPL%202.0-red.svg?label=license&logo=eclipse)](https://www.eclipse.org/legal/epl-2.0/)
[![Build](https://github.com/eclipse/lsp4jakarta/workflows/Java%20CI%20-%20LSP4Jakarta/badge.svg)](https://github.com/eclipse/lsp4jakarta/actions)
[![Gitter](https://badges.gitter.im/eclipse/jakartaee-languageserver.svg)](https://gitter.im/eclipse/jakartaee-languageserver?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

The Eclipse LSP4Jakarta (Language Server for Jakarta EE) project provides core language support capabilities for the specifications defined under the Jakarta EE (EE4J) umbrella.

This project contains: 

- [jakarta.ls](/jakarta.ls) - Language Server for Jakarta EE
- [jakarta.jdt](/jakarta.jdt) - Jakarta EE Eclipse JDT LS extension 
- [jakarta.eclipse](/jakarta.eclipse) - Eclipse IDE client (for local testing) that consumes the Language Server for Jakarta EE

<img src="/docs/images/components.png" alt="component diagram" height="80%" width="80%"/>

# Table of Contents
- [Client IDEs](#client-ides)
- [Jakarta EE API Language Features](#jakarta-ee-api-language-features)
   - [Jakarta Bean Validation](#jakarta-bean-validation)
   - [Jakarta Persistence](#jakarta-persistence)
   - [Jakarta RESTful Web Services](#jakarta-restful-web-services)
   - [Jakarta Servlet](#jakarta-servlet)
   - [Jakarta Transactions](#jakarta-transactions)
- [Building](#Building)
- [Contributing](#Contributing)
- [Feedback](#Feedback)
--- 
## Client IDEs

Eclipse LSP4Jakarta is consumed by the following IDEs:
-  Eclipse IDE with [Liberty Tools for Eclipse](https://github.com/OpenLiberty/liberty-tools-eclipse#welcome-to-the-liberty-tools-for-eclipse-project)

## Jakarta EE API Language Features

The following is a list of Eclipse LSP4Jakarta snippets offered for each [Jakarta EE API](https://jakarta.ee/specifications/). For a full list of language features (snippets, diagnostics and quick-fixes) currently offered by Eclipse LSP4Jakarta refer to the checked off items in [issue #16](https://github.com/eclipse/lsp4jakarta/issues/16). Language features will only be delivered for a given Jakarta EE API if that API is found on the project's classpath. Eclipse LSP4Jakarta is currently targeting Jakarta EE 9/9.1 (jakarta.x namespace). 
 
If you would like to see language feature assistance for a Jakarta EE API that is not yet listed here, please open an issue on the [issue tracker](https://github.com/eclipse/lsp4jakarta/issues).

### Jakarta Bean Validation

Eclipse LSP4Jakarta provides the following [Jakarta Bean Validation snippets](./jakarta.ls/src/main/resources/bean-validation.json):

| Snippet prefix | description |
| --------- | ------------ |
| @Email | Email address constraint, which validates a well-formed email address |

### Jakarta Persistence

Eclipse LSP4Jakarta provides the following [Jakarta Persistence snippets](./jakarta.ls/src/main/resources/persistence.json):

| Snippet prefix | description |
| --------- | ------------ |
| persist_context | Entity manager injection and associated persistence context |
| persist_context_extended | Entity manager injection with extended persistence context |
| persist_context_extended_unsync | Entity manager injection with extended, unsynchronized persistence context |
| persist_entity | Generic persistence entity model |

### Jakarta RESTful Web Services

Eclipse LSP4Jakarta provides the following [Jakarta RESTful Web Services snippets](./jakarta.ls/src/main/resources/restfulWs.json):

| Snippet prefix | description |
| --------- | ------------ |
| rest_class | Resource class with GET resource method |
| rest_get | GET resource method |
| rest_post | POST resource method |
| rest_put | PUT resource method |
| rest_delete | DELETE resource method |
| rest_head | HEAD resource method |

### Jakarta Servlet

Eclipse LSP4Jakarta provides the following [Jakarta Servlet snippets](./jakarta.ls/src/main/resources/servlet.json):

| Snippet prefix | description |
| --------- | ------------ |
| servlet_generic | Generic protocol independent servlet |
| servlet_doget | HTTPServlet with GET request |
| servlet_dopost | HTTPServlet with POST request |
| servlet_webfilter | Servlet WebFilter |

### Jakarta Transactions

Eclipse LSP4Jakarta provides the following [Jakarta Transactions snippets](./jakarta.ls/src/main/resources/transactions.json):

| Snippet prefix | description |
| --------- | ------------ |
| tx_user_inject | Initializes a UserTransaction object via injection |
| tx_user_jndi | Initializes a UserTransaction object via JNDI lookup |
| @Transactional | Transactional annotation with rollbackOn and dontRollbackOn |

## Building

Refer to our [building](docs/BUILDING.md) document for information about prerequisites, setting up an Eclipse workspace, and common errors. 

## Contributing

Our [contributing](docs/CONTRIBUTING.md) document contains details for submitting pull requests and contributing new language features.

## Feedback

Please report bugs, issues and feature requests by creating a [GitHub issue](https://github.com/eclipse/lsp4jakarta/issues).
