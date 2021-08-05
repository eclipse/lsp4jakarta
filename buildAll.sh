cd lsp4jakarta && mvn clean install
mv target/lsp4jakarta-1.0-SNAPSHOT-jar-with-dependencies.jar ../jakarta-eclipse/org.eclipse.lsp4jakarta.core
cd ../jakarta.jdt/org.eclipse.lsp4jakarta.jdt.core && mvn clean install
cd ../../jakarta-eclipse && mvn clean install
