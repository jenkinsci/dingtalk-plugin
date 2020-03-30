def mvnCmd = "mvn install:install-file -DgroupId=com.dingtalk.open -DartifactId=taobao-sdk-java-auto -Dversion=1.0 -Dpackaging=jar -Dfile=./maven/taobao-sdk-java-auto/taobao-sdk-java-auto_1479188381469-20191227.jar"

node('maven'){
    disableConcurrentBuilds()
    checkout scm

    stage('Install'){
        sh mvnCmd
    }

    stage('Build'){
        buildPlugin()
    }
}