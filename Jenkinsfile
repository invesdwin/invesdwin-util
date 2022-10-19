pipeline {
  agent any
  stages {
    stage('Build and test') {
      steps{
        withMaven {
          sh 'mvn clean deploy -DdeployAtEnd=true -f invesdwin-util-parent/pom.xml -T4'
        }  
      }
    }
  }
}