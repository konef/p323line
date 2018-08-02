import hudson.model.*
import hudson.EnvVars
import groovy.json.JsonSlurperClassic
import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import java.net.URL

// Nexus attributes
serv = 'http://EPBYMINW7423/nexus/repository/'
username = 'admin'
password = 'admin123'
repo = 'Artifact-storage'

// Pipeline variables
String student = 'aandryieuski'
String step = ''

node{

    def mvn_version = 'mavenLocal'
    def java_version = 'java8'
    def groovy_version = 'groovy4'

    properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')), disableConcurrentBuilds()])
    stage('Preparation') {
        deleteDir()
        def USER_J = wrap([$class: 'BuildUser']) {
            return env.BUILD_USER
        }
        git branch: 'aandryieuski', poll: false, url: 'https://github.com/MNT-Lab/p323line.git'
        sh "sed -i 's/##BUILD##/${env.BUILD_NUMBER}/' helloworld-ws/src/main/webapp/status-page.html"
        sh "sed -i 's/##AUTHOR##/${USER_J}/' helloworld-ws/src/main/webapp/status-page.html"
        echo "\u2776: Preparation Stage is done \u2705"
    }
    stage('Building code'){
        withEnv(["PATH+MAVEN=${tool mvn_version}/bin"]) {
            sh 'mvn -f helloworld-ws/pom.xml package'
        }
        echo "\u2777: Building code Stage is done \u2705"
    }
    stage('Testing'){
        withEnv(["JAVA_HOME=${tool java_version}","PATH+MAVEN=${tool mvn_version}/bin"]) {
            parallel PreIntegrationTest: {
                try {
                    echo "\u27A1 Build pre-integration-test parallel stage"

                        sh 'mvn -f helloworld-ws/pom.xml pre-integration-test'

                }
                finally {
                    sh 'echo "Finished this stage"'

                }
            }, IntegrationTest: {
                try {
                    echo "\u27A1 Build integration-test parallel stage"

                        sleep 5
                        sh 'mvn -f helloworld-ws/pom.xml integration-test'

                }
                finally {
                    sh 'echo "Finished this stage"'

                }
            }, PostIntegrationTest: {
                try {
                    echo "\u27A1 Build post-integration-test parallel stage"

                        sleep 10
                        sh 'mvn -f helloworld-ws/pom.xml post-integration-test'

                }
                finally {
                    sh 'echo "Finished this stage"'

                }
            }, failFast: true
        }
        echo "\u2778: Testing Stage is done \u2705"
    }
    stage('Triggering job and fetching artefact after finishing'){
        build job: "MNTLAB-${student}-child1-build-job", parameters: [string(name: 'BRANCH_NAME', value: student)]
        copyArtifacts filter: "${student}_dsl_script.tar.gz", projectName: "MNTLAB-${student}-child1-build-job", selector: lastSuccessful()
        echo "\u2779: Triggering job and fetching artefact after finishing Stage is done \u2705"
    }
    stage('Packaging and Publishing results'){
        sh "tar -xzf ${student}_dsl_script.tar.gz "
        sh "tar -czf pipeline-${student}-${env.BUILD_NUMBER}.tar.gz Jenkinsfile jobs.groovy -C helloworld-ws/target/ helloworld-ws.war"
        archiveArtifacts "pipeline-${student}-${env.BUILD_NUMBER}.tar.gz"
        withEnv(["GROOVY_HOME=${tool groovy_version}"]) {
            sh "$GROOVY_HOME/bin/groovy push-pull.groovy ${serv} ${username} ${password} ${repo} pipeline-${student}-${env.BUILD_NUMBER}.tar.gz push"
        }
        echo "\u277a: Packaging and Publishing results Stage is done \u2705"
    }
    stage('Asking for manual approval'){
        timeout(time: 30, unit: 'SECONDS') {
            input 'Deploy to prod?'
        }
        echo "\u277b: Asking for manual approval Stage is done \u2705"
    }
    stage('Deployment'){
        sh "rm -rf pipeline-${student}-${env.BUILD_NUMBER}.tar.gz"
        withEnv(["GROOVY_HOME=${tool groovy_version}"]) {
            sh "$GROOVY_HOME/bin/groovy push-pull.groovy ${serv} ${username} ${password} ${repo} pipeline-${student}-${env.BUILD_NUMBER}.tar.gz pull"
        }

        sh returnStatus: true, script: 'chmod 600 id_rsa'
        sh returnStatus: true, script: "scp -o StrictHostKeyChecking=no -i id_rsa -P2201 pipeline-${student}-${env.BUILD_NUMBER}.tar.gz jboss@EPBYMINW7423:/tmp/jenkins_tmp"
        sh returnStatus: true, script:"ssh -o StrictHostKeyChecking=no -i id_rsa -p 2201 jboss@EPBYMINW7423 'bash -s' < deploy.sh"
        sh returnStatus: true, script:"ssh -o StrictHostKeyChecking=no -i id_rsa -p 2201 jboss@EPBYMINW7423 'rm -rf /tmp/jenkins_tmp/*.tar.gz'"
        echo "\u277c: Deployment Stage is done \u2705"
    }

}

/*
 err = caughtError
 currentBuild.result = "FAILURE"
 String recipient = 'infra@lists.jenkins-ci.org'
 mail subject: "${env.JOB_NAME} (${env.BUILD_NUMBER}) failed",
         body: "It appears that ${env.BUILD_URL} is failing, somebody should do something about that",
           to: recipient,
      replyTo: recipient,
 from: 'noreply@ci.jenkins.io'
*/