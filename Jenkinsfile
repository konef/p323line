String student = "knovichuk"
String MAVEN = "mavenLocal"
String JDK = "java8"

node() {
        
    try {
        stage('Preparation (Checking out)') {
            checkout([$class: 'GitSCM',
                    branches: [[name: "*/${student}"]],
                    doGenerateSubmoduleConfigurations: false,
                    extensions: [],
                    submoduleCfg: [],
                    userRemoteConfigs:[[url: 'https://github.com/MNT-Lab/p323line']]])                          
            }
        }

    catch (Exception ex){
            println("Checkout failed")
        }

    try {
        stage('Building code'){
            withEnv(["JAVA_HOME=${tool JDK}", "PATH+MAVEN=${tool MAVEN}/bin"]) {
                sh "mvn -f helloworld-ws/pom.xml package"
            }
        }
    }

    catch (Exception ex){
            println("Maven build failed")
        }

}