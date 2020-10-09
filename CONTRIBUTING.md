# Contributing to Jakarta EE Language Server

We welcome contributions, and request you follow these guidelines.

 - [Raising issues](#raising-issues)
 - [Legal](#legal)
 - [Coding Standards](#coding-standards)


## Raising issues

Please raise any bug reports on the [issue tracker](https://github.com/MicroShed/jakarta-ls/issues). Be sure to search the list to see if your issue has already been raised.

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