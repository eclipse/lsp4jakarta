# Manually building the installable Eclipse client plugin

1. Ensure that **Eclipse workspace** is setup properly by following the [setup instructions](docs/BUILDING.md).

2. In the package explorer in **Eclipse**, right-click on the `org.eclipse.lsp4jakarta.core` and then click **Export**.

    <img src="/images/pics/img1.png" alt="Exporting" style="height: 50%; width:50%;"/>

3. Then, click **Plug-in Development > Deployable plug-ins and fragments > Next**. 

    <img src="/images/pics/img2.png" alt="Exporting" style="height: 50%; width:50%;"/>

4. In the **Available Plug-ins and Fragments** section, make sure only the `org.jakartaee.lsp4e` is selected. 

    <img src="/images/pics/img3.png" alt="Exporting" style="height: 50%; width:50%;"/>

5. Select the directory of your choice to export the `*.jar` file into. 

6. Click **Finish** and the `*.jar` file will be the directory you specified earlier.
