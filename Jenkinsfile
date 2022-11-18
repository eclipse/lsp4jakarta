pipeline {
  agent any
  tools {
    jdk 'temurin-jdk17-latest'
  }
  environment {
    MAVEN_HOME = "$WORKSPACE/.m2/"
    MAVEN_USER_HOME = "$MAVEN_HOME"
  }
  stages {
    stage("Build LSP4Jakarta JDT extension"){
      steps {
        withMaven {
          sh 'cd jakarta.jdt && ./mvnw clean verify -B -Peclipse-sign -X && cd ..'
        }
      }
    }
    // stage('Deploy LSP4Jakarta JDT extension to downloads.eclipse.org') {
    //   when {
    //     branch 'master'
    //   }
    //   steps {
    //     sshagent ( ['projects-storage.eclipse.org-bot-ssh']) {
    //       sh '''
    //         VERSION=`grep -o '[0-9].*[0-9]' jakarta.jdt/org.eclipse.lsp4jakarta.jdt.core/target/maven-archiver/pom.properties`
    //         targetDir=/home/data/httpd/download.eclipse.org/lsp4jakarta/snapshots/$VERSION
    //         ssh genie.lsp4jakarta@projects-storage.eclipse.org rm -rf $targetDir
    //         ssh genie.lsp4jakarta@projects-storage.eclipse.org mkdir -p $targetDir
    //         scp -r jakarta.jdt/org.eclipse.lsp4jakarta.jdt.site/target/*.zip genie.lsp4jakarta@projects-storage.eclipse.org:$targetDir
    //         ssh genie.lsp4jakarta@projects-storage.eclipse.org unzip $targetDir/*.zip -d $targetDir/repository
    //         '''
    //     }
    //   }
    // }
    stage("Build LSP4Jakarta Language Server") {
      steps {
        withMaven {
          sh 'cd jakarta.ls && ./mvnw clean verify -B -Dcbi.jarsigner.skip=false -X && cd ..'
        }
      }
    }
    // stage ('Deploy LSP4Jakarta Language Server artifacts to Maven repository') {
    //   when {
    //       branch 'master'
    //   }
    //   steps {
    //     withMaven {
    //       sh 'cd jakarta.ls && ./mvnw deploy -B -DskipTests'
    //     }
    //   }
    // }
  }
  post {
    always {
      junit '**/target/surefire-reports/*.xml'
    }
  }
}