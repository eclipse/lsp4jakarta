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
- [Diagnostics on Java Files](#diagnostics-on-java-files)
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

### Diagnostics on Java Files

Diagnostics highlight potential problems in your Java file, Eclipse LSP4Jakarta provides validation for following annotations:

#### Filter Annotations:

| Annotation | description |
| --------- | ------------ |
| @WebFilter | A WebFilter is used to declare a servlet filter |

#### JSON Annotations:

| Annotation | description |
| --------- | ------------ |
| @JsonbAnnotation | Marks any relevant JSON Binding annotations |
| @JsonbCreator | The JsonbCreator annotation identifies the custom constructor or factory method to use when creating an instance of the associated class |
| @JsonbDateFormat | The JsonbDateFormat annotation provides way how to set custom date format to field or JavaBean property |
| @JsonbNillable | Specifies how fields having null values are serialized into JSON |
| @JsonbNumberFormat | The JsonbNumberFormat annotation provides way how to set custom number format to field or JavaBean property |
| @JsonbProperty | Allows customization of field (or JavaBean property) name.This name is used either in serialization or in deserialization |
| @JsonbPropertyOrder | Specifies order in which properties are serialized |
| @JsonbTransient | Prevents mapping of a Java Bean property, field or type to JSON representation |
| @JsonbTypeAdapter | The JsonbTypeAdapter annotation provides way how to set custom JsonbAdapter to field or JavaBean property |
| @JsonbTypeDeserializer | The annotation provides way how to set custom JsonbDeserializer to field or JavaBean property |
| @JsonbTypeSerializer | The annotation provides way how to set custom JsonbSerializer to field or JavaBean property |
| @JsonbVisibility | The JsonbVisibility annotation provides way how to customize visibility strategy of the JSON Binding |

#### Listener Annotations:

| Annotation | description |
| --------- | ------------ |
| @WebListener | The WebListener annotation is used to declare a WebListener |

#### Managed Bean Annotations:

| Annotation | description |
| --------- | ------------ |
| @ApplicationScoped | Specifies that a bean is application scoped |
| @ConversationScoped | Specifies that a bean is conversation scoped |
| @Decorator | Specifies that a class is a decorator |
| @Dependent | Specifies that a bean belongs to the dependent pseudo-scope |
| @Disposes | Identifies the disposed parameter of a disposer method |
| @Inject | Identifies injectable constructors, methods, and fields. May apply to static as well as instance members |
| @Interceptor | Represents an enabled interceptor |
| @NormalScope | Specifies that an annotation type is a normal scope type |
| @Observes | Identifies the event parameter of an observer method |
| @ObservesAsync | Identifies the event parameter of an asynchronous observer method |
| @Produces | Identifies a producer method or field. May be applied to a method or field of a bean class |
| @RequestScoped | Specifies that a bean is request scoped |
| @SessionScoped | Specifies that a bean is session scoped |
| @Stereotype | Specifies that an annotation type is a stereotype |

#### Persistence Annotations:

| Annotation | description |
| --------- | ------------ |
| @Entity | Specifies that the class is an entity |
| @MapKey | Specifies the map key for associations of type java.util.Map when the map key is itself the primary key or a persistent field or property of the entity that is the value of the map |
| @MapKeyClass | Specifies the type of the map key for associations of type java.util.Map |
| @MapKeyJoinColumn | Specifies a mapping to an entity that is a map key |

#### RESTful Web Services Annotations:

| Annotation | description |
| --------- | ------------ |
| @Context | The Context annotation is used to inject information into a class field, bean property or method parameter |
| @CookieParam | Binds the value of a HTTP cookie to a resource method parameter, resource class field, or resource class bean property |
| @DELETE | Indicates that the annotated method responds to HTTP DELETE requests |
| @FormParam | Binds the value(s) of a form parameter contained within a request entity body to a resource method parameter |
| @GET | Indicates that the annotated method responds to HTTP GET requests |
| @HEAD | Indicates that the annotated method responds to HTTP HEAD requests |
| @HeaderParam | Binds the value(s) of a HTTP header to a resource method parameter, resource class field, or resource class bean property |
| @MatrixParam | Binds the value(s) of a URI matrix parameter to a resource method parameter, resource class field, or resource class bean property |
| @OPTIONS | Indicates that the annotated method responds to HTTP OPTIONS requests |
| @Path | Identifies the URI path that a resource class or class method will serve requests for |
| @PathParam | Binds the value of a URI template parameter or a path segment containing the template parameter to a resource method parameter, resource class field, or resource class bean property |
| @PATCH | Indicates that the annotated method responds to HTTP PATCH requests |
| @POST | Indicates that the annotated method responds to HTTP POST requests |
| @Provider | Marks an implementation of an extension interface that should be discoverable by the runtime during a provider scanning phase |
| @PUT | Indicates that the annotated method responds to HTTP PUT requests |
| @QueryParam | Binds the value(s) of a HTTP query parameter to a resource method parameter, resource class field, or resource class bean property |

#### Servlet Annotations:

| Annotation | description |
| --------- | ------------ |
| @WebServlet | A WebServlet is used to declare a servlet |

#### Standard Annotations:

| Annotation | description |
| --------- | ------------ |
| @Generated | The Generated annotation is used to mark source code that has been generated |
| @PostConstruct | The PostConstruct annotation is used on a method that needs to be executed after dependency injection is done to perform any initialization |
| @PreDestroy | The PreDestroy annotation is used on a method as a callback notification to signal that the instance is in the process of being removed by the container |
| @Resource | The Resource annotation is used to declare a reference to a resource |

#### Validation Annotations:

| Annotation | description |
| --------- | ------------ |
| @AssertFalse | The annotated element must be false |
| @AssertTrue | The annotated element must be true |
| @DecimalMax | The annotated element must be a number whose value must be lower or equal to the specified maximum |
| @DecimalMin | The annotated element must be a number whose value must be higher or equal to the specified minimum |
| @Digits | The annotated element must be a number within accepted range |
| @Email | The string has to be a well-formed email address |
| @Future | The annotated element must be an instant, date or time in the future |
| @FutureOrPresent | The annotated element must be an instant, date or time in the present or in the future |
| @Max | The annotated element must be a number whose value must be lower or equal to the specified maximum |
| @Min | The annotated element must be a number whose value must be higher or equal to the specified minimum |
| @Negative | The annotated element must be a strictly negative number |
| @NegativeOrZero | The annotated element must be a negative number or 0 |
| @NotBlank | The annotated element must not be null and must contain at least one non-whitespace character |
| @NotEmpty | The annotated element must not be null nor empty |
| @Past | The annotated element must be an instant, date or time in the past |
| @PastOrPresent | The annotated element must be an instant, date or time in the past or in the present |
| @Pattern | The annotated CharSequence must match the specified regular expression |
| @Positive | The annotated element must be a strictly positive number |
| @PostiveOrZero | The annotated element must be a positive number or 0 |
| @Size | The annotated element size must be between the specified boundaries (included) |

#### Web Socket Annotations:

| Annotation | description |
| --------- | ------------ |
| @ClientEndpoint | The ClientEndpoint annotation is used to denote that a POJO is a web socket client and can be deployed as such |
| @CloseReason | A class encapsulating the reason why a web socket has been closed, or why it is being asked to close |
| @EndpointConfig | The endpoint configuration contains all the information needed during the handshake process for this end point |
| @OnClose | This method level annotation can be used to decorate a Java method that wishes to be called when a web socket session is closing |
| @OnMessage | This method level annotation can be used to make a Java method receive incoming web socket messages |
| @OnOpen | This method level annotation can be used to decorate a Java method that wishes to be called when a new web socket session is open |
| @PathParam | This annotation may be used to annotate method parameters on server endpoints where a URI-template has been used in the path-mapping of the ServerEndpoint annotation |
| @Session | A Web Socket session represents a conversation between two web socket endpoints |
| @ServerEndpoint | The ServerEndpoint annotation declares that the class it decorates is a web socket endpoint that will be deployed and made available in the URI-space of a web socket server |

## Building

Refer to our [building](docs/BUILDING.md) document for information about prerequisites, setting up an Eclipse workspace, and common errors. 

## Contributing

Our [contributing](docs/CONTRIBUTING.md) document contains details for submitting pull requests and contributing new language features.

## Feedback

Please report bugs, issues and feature requests by creating a [GitHub issue](https://github.com/eclipse/lsp4jakarta/issues).
