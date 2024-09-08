pipeline {
    agent any
    stages {
        stage('test') {
            steps {
                sh(script: """ whoami;pwd;ls -la """, label: "test step")
            }
        }
    }
}