pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
            }
        }
    }
}

node(maven){
    stage('Prepare'){
        mvn install:install-file -DgroupId=com.dingtalk.open -DartifactId=taobao-sdk-java-auto -Dversion=1.0 -Dpackaging=jar -Dfile=./taobao-sdk-java-auto_1479188381469-20191227.jar
    }

    stage('Build'){
        buildPlugin()
    }
}