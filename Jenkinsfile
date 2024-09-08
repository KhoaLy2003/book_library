pipeline {
    agent any
    tools {
        maven 'maven-3.9.4' 
    }
    stages {
        stage('Info') {
            steps {
                sh(script: """ whoami;pwd;ls -la """, label: "Info step")
            }
        }
        stage('Build') {
            steps {
                dir('book_library_be') {
                    sh(script: """ mvn clean install -DskipTests=true """, label: "Build project")
                }
            }
        }
    }
}
