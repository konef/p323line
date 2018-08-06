String STUDENT = "knovichuk"
String MAVEN = "mavenLocal"
String JDK = "java8"



def notification(stage, message) {
    message = """------------------
Stage: ${stage},
Pipeline $JOB_NAME is "${message}"!
------------------
"""
    mail bcc: '', body: message, cc: '', subject: 'Jenkins result', to: 'k.rahidb@gmail.com'
}

node("$SLAVE") {
    def groovy = 'groovy4'
    try {
        stage('Preparation (Checking out)') {
            git branch: "$STUDENT", url: 'https://github.com/MNT-Lab/p323line.git'
        }
    }

    catch (Exception ex) {
        println("Checkout failed")
        notification("Checkout SCM", "failed")
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
        notification("Building code", "failed")
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
        notification("Testing", "failed")
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
        notification("Triggering job", "failed")
    }

    try {
        stage('Packaging and Publishing results'){
            step_name = "Creating archive"
            sh """tar -xf ${STUDENT}_dsl_script.tar.gz jobs.groovy
                  tar -czvf pipeline-${STUDENT}-${env.BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile -C ./helloworld-ws/target/ helloworld-ws.war"""

            step_name = "Creating artefact"
            archiveArtifacts "pipeline-${STUDENT}-${env.BUILD_NUMBER}.tar.gz"

            step_name = "Publishing to Nexus"
            withEnv(["GROOVY_HOME=${tool groovy}"]) {
            sh "$GROOVY_HOME/bin/groovy pushpull.groovy push pipeline-${STUDENT}-${env.BUILD_NUMBER}.tar.gz" 
            }
        }
    }

    catch (Exception ex) {
        println("Publishing artefact failed")
        notification("Packaging and Publishing results", "failed")
    }


    stage('Asking for manual approval') {
        timeout(time: 1) {
            input 'Do you approve the deployment?'
        }
    }

    try {
        stage('Deployment') {
            sh """scp -P 2222 pushpull.groovy vagrant@EPBYMINW2473:/opt/apps/
                  ssh -p2222 vagrant@EPBYMINW2473 '''cd /opt/apps/
                                        groovy pushpull.groovy pull pipeline-${STUDENT}-${env.BUILD_NUMBER}.tar.gz
                                        tar -xf ${STUDENT}-${env.BUILD_NUMBER}.tar.gz helloworld-ws.war
                                        mv /opt/tomcat/webapps/helloworld-ws.war /opt/apps/old/helloworld-ws-old.war
                                        sudo rm -rf /opt/tomcat/webapps/helloworld-ws
                                        cp helloworld-ws.war /opt/tomcat/webapps/helloworld-ws.war
                                        sleep 30
                                        if curl -L http://epbyminw2473/tomcat/helloworld-ws/index.html | grep "helloworld-ws Quickstart"
                                        then 
                                            echo "good"
                                        else
                                            cp /opt/apps/old/helloworld-ws-old.war /opt/tomcat/webapps/helloworld-ws.war
                                            echo "no good"
                                            sudo rm -rf /opt/tomcat/webapps/helloworld-ws
                                        fi
                                        '''

               """
        }
    }

    catch (Exception ex) {
        println("Deploing artefact failed")
    }
    if (currentBuild.result != "FAILURE") {
        notification("Deployment", "successfully done")
    }
}
