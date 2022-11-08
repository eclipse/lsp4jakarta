# Contributing to Eclipse LSP4Jakarta

We welcome contributions, and request you follow these guidelines.

 - [Raising issues](#raising-issues)
 - [Legal](#legal)
 - [Coding Standards](#coding-standards)
 - [Contributing Snippets](#contributing-snippets)


## Raising issues

Please raise any bug reports on the [issue tracker](https://github.com/eclipse/lsp4jakarta/issues). Be sure to search the list to see if your issue has already been raised.

A good bug report is one that make it easy for us to understand what you were trying to do and what went wrong. Provide as much context as possible so we can try to recreate the issue.

### Legal

In order to make contribution as easy as possible, we follow the same approach as the [Developer's Certificate of Origin 1.1 (DCO)](https://developercertificate.org/) - that the Linux® Kernel [community](https://elinux.org/Developer_Certificate_Of_Origin) uses to manage code contributions.

We simply ask that when submitting a pull request for review, the developer
must include a sign-off statement in the commit message.

Here is an example Signed-off-by line, which indicates that the
submitter accepts the DCO:

```text
Signed-off-by: John Doe <john.doe@example.com>
```

You can include this automatically when you commit a change to your
local git repository using the following command:

```bash
git commit -s
```

### Coding Standards

This project follows Eclipse standard Java language [coding conventions](https://wiki.eclipse.org/Coding_Conventions).

Please note:
 - all PRs must pass Java CheckStyle checks
 - all PRs must have passing builds


### Contributing Snippets

Snippets are completion items that contain a block of helpful code for users. Snippets in Eclipse LSP4Jakarta are contributed through [JSON files](../jakarta.ls/src/main/resources/). After adding snippets update the [Jakarta EE API language features](../README.md#jakarta-ee-api-language-features) documentation.

Snippets follow the format:

```yaml
"<Jakarta EE API Name> - <brief description>": {
      "prefix": "<Jakarta EE API shortened name>_<noun describing the snippet>",
      "body": [
        "<body of snippet, this is the code that will be injected on selection>"
      ],
      "description": "<brief description of snippet, should not be longer than 1 sentence>",
      "context": {
        // snippets will only appear for users if the package specified here is found on the project's classpath
        "type": "<package required for snippet (ie. jakarta.servlet.http.HttpServlet)>"
      }
    }
```