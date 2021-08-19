# Eclipse LSP4Jakarta 
[![License](https://img.shields.io/badge/License-EPL%202.0-red.svg?label=license&logo=eclipse)](https://www.eclipse.org/legal/epl-2.0/) [![Build](https://github.com/eclipse/lsp4jakarta/workflows/Java%20CI%20-%20LSP4Jakarta/badge.svg)](https://github.com/eclipse/lsp4jakarta/actions)

The Eclipse LSP4Jakarta (Language Server for Jakarta EE) project provides core language support capabilities for the specifications defined under the Jakarta EE (EE4J) umbrella.

This project contains: 

- [lsp4jakarta](/lsp4jakarta) - Language Server for Jakarta EE
- [jakarta-eclipse](/jakarta-eclipse) - Eclipse JDT LS extension and Eclipse IDE client that consumes the Language Server for Jakarta EE

# Table of Contents  
- [Getting Started](#Getting-Started)  
- [Building](#Building)
    - [Prerequisites](#Prerequisites)
    - [Setup Instructions](#Setup-Instructions)
    - [Common Errors](#Common-Errors)
- [Contributing](#Contributing)
- [Feedback](#Feedback)
---
## Getting Started

Follow the instructions below to install the LSP4Jakarta Eclipse plugin:

1. Build the installable Eclipse client (packaged as a Jar) using our [MANUALBUILDING](docs/MANUALBUILDING.md) instructions. Alternatively, if releases are available you can download the `*.jar` from the [releases page](https://github.com/eclipse/lsp4jakarta/releases). To do so, nagivate to the [releases page](https://github.com/eclipse/lsp4jakarta/releases) and download the `*.jar` file for the Eclipse plugin.  

2. Move the `*.jar` file to the **dropins** folder and start **Eclipse** in `clean` mode, as directed below:
    - **For Mac:** 
        - In **Finder**, navigate to the **Eclipse** application.
        - Right-click on the application and click "Show Package Contents".
        - Navigate through the folders as follows: **Contents > Eclipse**
        - In this directory, update the **eclipse.ini** file by adding `-clean` to the top of this file, save, and exit the file.
        - Next, in the same directory as the  **eclipse.ini** file, click on **dropins** folder.
        - Move the `*.jar` inside the **dropins** folder. 
    - **For Windows:**
        - Locate the directory with the **Eclipse** executable. 
        - In this directory, update the **eclipse.ini** file by adding `-clean` to the top of this file, save, and exit the file.
        - In the same directory, click on **dropins** folder.
        - Move the `*.jar` inside the **dropins** folder. 
    - **For Linux:**
        - In the terminal, use the following command to locate the directory with the **eclipse.ini** file: `ls -l /usr/bin | grep 'eclipse'`
        - In this directory, update the **eclipse.ini** file by adding `-clean` to the top of this file, save, and exit the file.
        - In the same directory, click on **dropins** folder.
        - Move the `*.jar` inside the **dropins** folder. 

3. Restart the **Eclipse** application.

## Building

Refer to our [BUILDING](docs/BUILDING.md) document for information about prerequisites, setting up, and common errors. 

## Contributing

Our [CONTRIBUTING](docs/CONTRIBUTING.md) document contains details for submitting pull requests.

## Feedback

Please report bugs, issues and feature requests by creating a [GitHub issue](https://github.com/eclipse/lsp4jakarta/issues).
