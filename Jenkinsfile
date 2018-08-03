String STUDENT = "knovichuk"
String MAVEN = "mavenLocal"
String JDK = "java8"

node() {

    try {
        stage('Preparation (Checking out)') {
            git branch: "$STUDENT", url: 'https://github.com/MNT-Lab/p323line.git'
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

    try {
        stage('Triggering job and fetching artefact after finishing'){
            step_name = "Triggering job"
            build job: "MNTLAB-$STUDENT-child1-build-job", parameters: [string(name: 'BRANCH_NAME', value: "$STUDENT")], wait: true

            step_name = "Fetching the artefact"
            copyArtifacts filter: "${STUDENT}_dsl_script.tar.gz", projectName: "MNTLAB-$STUDENT-child1-build-job", selector: lastSuccessful()
        }
    }

    catch (Exception ex) {
        println("Triggering job failed")
    }    
}