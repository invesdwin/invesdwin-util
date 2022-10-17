pipeline {
  agent any
  stages {
    stage('Build and test') {
      steps{
        withMaven {
          sh 'mvn clean install -f invesdwin-util-parent/pom.xml -T4'
        }  
      }
    }
  }
}