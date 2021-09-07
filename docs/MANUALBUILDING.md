# Manually building the installable Eclipse client plug-in

TODO: these instructions need to be updated now that the Jakarta EE Eclipse JDT LS Extension has been decoupled from the Jakarta EE Eclipse client plug-in.

1. Ensure that **Eclipse workspace** is setup properly by following the [setup instructions](/docs/BUILDING.md).

2. In the package explorer in **Eclipse**, right-click on the `org.eclipse.lsp4jakarta.core` and then click **Export**.

    <img src="/docs/images/img1.png" alt="Exporting" height="30%" width="30%"/>

3. Then, click **Plug-in Development > Deployable plug-ins and fragments > Next**. 

    <img src="/docs/images/img2.png" alt="Exporting" height="50%" width="50%"/>

4. In the **Available Plug-ins and Fragments** section, make sure only the `org.jakartaee.lsp4e` is selected. 

    <img src="/docs/images/img3.png" alt="Exporting" height="50%" width="50%"/>
    
5. Select the directory of your choice to export the `*.jar` file into. 

6. Click **Finish** and the `*.jar` file will be the directory you specified earlier.


## Installing the LSP4Jakarta Eclipse plug-in

Follow the instructions below to install the LSP4Jakarta Eclipse plug-in:

1. Build the installable Eclipse client (packaged as a Jar) using our [manual building](docs/MANUALBUILDING.md) instructions. Alternatively, if releases are available you can download the `*.jar` from the [releases page](https://github.com/eclipse/lsp4jakarta/releases). To do so, navigate to the [releases page](https://github.com/eclipse/lsp4jakarta/releases) and download the `*.jar` file for the Eclipse plug-in.  

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