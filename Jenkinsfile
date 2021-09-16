pipeline {
  agent any
  stages {
    stage('Build') {
      parallel {
        stage('Build') {
          steps {
            echo 'Building'
            sh '''/var/jenkins_home/tools/hudson.tasks.Maven_MavenInstallation/JenkinsMaven/bin/mvn -B -DskipTests clean package'''
          }
        }

        stage('Test') {
          steps {
            echo 'Testing'
          }
        }

      }
    }

    stage('Deploy') {
      parallel {
        stage('Deploy') {
          steps {
            echo 'Deploying'
          }
        }

        stage('Log') {
          steps {
            writeFile(file: 'Testlog.txt', text: 'This is a test log')
          }
        }

        stage('Artifact') {
          steps {
            archiveArtifacts 'Testlog.txt'
          }
        }

      }
    }

  }
}
