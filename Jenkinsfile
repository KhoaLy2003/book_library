pipeline {
    agent any
    tools {
        maven 'maven-3.9.4'
        nodejs 'node-22.8.0' 
    }
    stages {
        stage('Info') {
            steps {
                sh(script: """ whoami;pwd;ls -la """, label: "Info step")
            }
        }
        stage('Build Backend') {
            steps {
                dir('book_library_be') {
                    sh(script: """ mvn clean install -DskipTests=true """, label: "Build backend project")
                }
            }
        }
        stage('Build Frontend') {
            steps {
                dir('book_library_fe') {
                    sh(script: """ npm install """, label: "Install Dependencies")
                    sh(script: """ npm run build """, label: "Build front project")
                }
            }
        }
    }
}