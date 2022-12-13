pipeline {
  agent any
  tools {
    jdk 'temurin-jdk17-latest'
  }
  environment {
    MAVEN_HOME = "$WORKSPACE/.m2/"
    MAVEN_USER_HOME = "$MAVEN_HOME"
  }
  parameters {
      string(name: 'VERSION', defaultValue: '', description: 'Version to Release?')
  }
  stages {
    stage("Release LSP4Jakarta Language Server"){
      steps {
        script {
          if (!params.VERSION) {
            error('Not releasing')
          }
        }
        withMaven {
          sh "VERSION=${params.VERSION}"
          sh '''
                cd jakarta.jdt
                ./mvnw -Dtycho.mode=maven org.eclipse.tycho:tycho-versions-plugin:set-version -DnewVersion=$VERSION-SNAPSHOT
                ./mvnw versions:set-scm-tag -DnewTag=$VERSION
                ./mvnw clean deploy -B -Peclipse-sign -Dcbi.jarsigner.skip=false
                cd ..
              '''
        }
      }
    }

    stage('Deploy to downloads.eclipse.org') {
      steps {
        sshagent ( ['projects-storage.eclipse.org-bot-ssh']) {
          sh "VERSION=${params.VERSION}"
          sh '''
            targetDir=/home/data/httpd/download.eclipse.org/lsp4jakarta/releases/$VERSION
            ssh genie.lsp4jakarta@projects-storage.eclipse.org rm -rf $targetDir
            ssh genie.lsp4jakarta@projects-storage.eclipse.org mkdir -p $targetDir
            scp -r jakarta.jdt/org.eclipse.lsp4jakarta.jdt.site/target/*.zip genie.lsp4jakarta@projects-storage.eclipse.org:$targetDir
            ssh genie.lsp4jakarta@projects-storage.eclipse.org unzip $targetDir/*.zip -d $targetDir/repository
            '''
        }
        //Push release to latest
        sshagent ( ['projects-storage.eclipse.org-bot-ssh']) {
          sh "VERSION=latest"
          sh '''
            targetDir=/home/data/httpd/download.eclipse.org/lsp4jakarta/releases/$VERSION
            ssh genie.lsp4jakarta@projects-storage.eclipse.org rm -rf $targetDir
            ssh genie.lsp4jakarta@projects-storage.eclipse.org mkdir -p $targetDir
            scp -r jakarta.jdt/org.eclipse.lsp4jakarta.jdt.site/target/*.zip genie.lsp4jakarta@projects-storage.eclipse.org:$targetDir
            ssh genie.lsp4jakarta@projects-storage.eclipse.org unzip $targetDir/*.zip -d $targetDir/repository
            '''
        }
      }
    }

    stage('Push tag to git') {
      steps {
		sshagent(['git.eclipse.org-bot-ssh']) {
          sh "VERSION=${params.VERSION}"
          sh '''
            git config --global user.email "lsp4jakarta-bot@eclipse.org"
            git config --global user.name "LSP4Jakarta GitHub Bot"
            git add "**/pom.xml" "**/MANIFEST.MF"
            git commit -sm "Release $VERSION"
            git tag $VERSION
            git push origin $VERSION
          '''
        }
      }
    }
  }
}
