String student = "knovichuk"
String MAVEN = "mavenLocal"
String JDK = "java8"

node() {

    try {
        stage('Preparation (Checking out)') {
            git branch: "$student", url: 'https://github.com/MNT-Lab/p323line.git'
        }
    }

    catch (Exception ex) {
        println("Checkout failed")
    }

    try {
        stage('Building code') {
            withEnv(["JAVA_HOME=${tool JDK}", "PATH+MAVEN=${tool MAVEN}/bin"]) {
                sh "mvn -f helloworld-ws/pom.xml package"
            }
        }
    }

    catch (Exception ex) {
        println("Maven build failed")
    }

    try {
        stage('Testing') {
            withEnv(["JAVA_HOME=${tool JDK}", "PATH+MAVEN=${tool MAVEN}/bin"]) {
                parallel(
                        'pre-integration test': {
                            sh 'mvn -f helloworld-ws/pom.xml pre-integration-test'
                        },
                        'integration test': {
                            sleep 15
                            sh 'mvn -f helloworld-ws/pom.xml integration-test'
                        },
                        'post-integration test': {
                            sleep 30
                            sh 'mvn -f helloworld-ws/pom.xml post-integration-test'
                        }
                )
            }
        }
    }

    catch (Exception ex) {
        println("Tests failed")
    }
}