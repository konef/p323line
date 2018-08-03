/*
import hudson.model.*
import hudson.EnvVars
import groovy.json.JsonSlurperClassic
import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import java.net.URL
@Grapes(@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7'))
import groovyx.net.http.RESTClient
import org.apache.http.entity.*
import hudson.model.*
*/
@Library('lib') _
import java.text.SimpleDateFormat

// Nexus attributes
serv = 'http://EPBYMINW7423/nexus/repository/'
username = 'admin'
password = 'admin123'
repo = 'Artifact-storage'

// Pipeline variables
String student = 'aandryieuski'
String user_mail = 'andrei_andryieuski@epam.com'
String stage_pipe = ''
String step_pipe = ''

def mail_to(String stage, String state, String step, recipient) {
    date = new Date()
    sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
    sdf.format(date)
    if (state == 'SUCCESS') {
        symb = '\u2705'
    } else {
        symb = '\u274E'
    }
    mail subject: "JOB ${env.JOB_NAME} (${env.BUILD_NUMBER}): State ***${state}*** ",
         body: """
***
Date/Time: ${sdf.format(date)},
Stage: "${stage}",
Step: "${step}",
Job "${env.JOB_NAME}" has status: "${state}" ${symb}
***
 
JOB_URL: ${env.JOB_URL}  
""",
        to: recipient,
        replyTo: recipient,
        from: 'noreply@jenkins.io',
        bcc: '',
        cc: ''
}

try {
    node (){
        def mvn_version = 'mavenLocal'
        def java_version = 'java8'
        def groovy_version = 'groovy4'

        properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')), disableConcurrentBuilds()])
        stage('Preparation') {
            stage_pipe = 'Preparation'
            step_pipe = 'Clear workspace'
            deleteDir()
            def USER_J = wrap([$class: 'BuildUser']) {
                return env.BUILD_USER
            }
            step_pipe = 'Code Checkout'
            git branch: 'aandryieuski', poll: false, url: 'https://github.com/MNT-Lab/p323line.git'
            step_pipe = 'Prepare of the status-page.html'
            sh "sed -i 's/##BUILD##/${env.BUILD_NUMBER}/' helloworld-ws/src/main/webapp/status-page.html"
            sh "sed -i 's/##AUTHOR##/${USER_J}/' helloworld-ws/src/main/webapp/status-page.html"
            echo "\u2776: Preparation Stage is done \u2705"
        }
        stage('Building code') {
            stage_pipe = 'Building code'
            step_pipe = 'Build project with mvn'
            withEnv(["PATH+MAVEN=${tool mvn_version}/bin"]) {
                sh 'mvn -f helloworld-ws/pom.xml package'
            }
            echo "\u2777: Building code Stage is done \u2705"
        }
        stage('Testing') {
            stage_pipe = 'Testing'
            withEnv(["JAVA_HOME=${tool java_version}", "PATH+MAVEN=${tool mvn_version}/bin"]) {
                parallel PreIntegrationTest: {
                    try {
                        echo "\u27A1 Build pre-integration-test parallel stage"
                        step_pipe = 'pre-integration-test'
                        sh 'mvn -f helloworld-ws/pom.xml pre-integration-test'

                    }
                    finally {
                        sh 'echo "Finished this stage"'

                    }
                }, IntegrationTest: {
                    try {
                        echo "\u27A1 Build integration-test parallel stage"
                        step_pipe = 'integration-test'
                        sleep 5
                        sh 'mvn -f helloworld-ws/pom.xml integration-test'

                    }
                    finally {
                        sh 'echo "Finished this stage"'

                    }
                }, PostIntegrationTest: {
                    try {
                        echo "\u27A1 Build post-integration-test parallel stage"
                        step_pipe = 'post-integration-test'
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
        stage('Triggering job and fetching artefact after finishing') {
            stage_pipe = 'Triggering job and fetching artefact after finishing'
            step_pipe = 'Call job MNTLAB-{student}-child1-build-job'
            build job: "MNTLAB-${student}-child1-build-job", parameters: [string(name: 'BRANCH_NAME', value: student)]
            step_pipe = 'Fetching of the MNTLAB-{student}-child1-build-job Artifact'
            copyArtifacts filter: "${student}_dsl_script.tar.gz", projectName: "MNTLAB-${student}-child1-build-job", selector: lastSuccessful()
            echo "\u2779: Triggering job and fetching artefact after finishing Stage is done \u2705"
        }
        stage('Packaging and Publishing results') {
            stage_pipe = 'Packaging and Publishing results'
            step_pipe = 'Archieve Artifact for deployment'
            sh "tar -xzf ${student}_dsl_script.tar.gz "
            sh "tar -czf pipeline-${student}-${env.BUILD_NUMBER}.tar.gz Jenkinsfile jobs.groovy -C helloworld-ws/target/ helloworld-ws.war"
            step_pipe = 'Attach this artifact to current job'
            archiveArtifacts "pipeline-${student}-${env.BUILD_NUMBER}.tar.gz"
            step_pipe = 'Pushing of the Artifact'
            nexus(serv, username, password, repo, "pipeline-${student}-${env.BUILD_NUMBER}.tar.gz", "push")
            //withEnv(["GROOVY_HOME=${tool groovy_version}"]) {
            //    sh "$GROOVY_HOME/bin/groovy push-pull.groovy ${serv} ${username} ${password} ${repo} pipeline-${student}-${env.BUILD_NUMBER}.tar.gz push"
            //}
            echo "\u277a: Packaging and Publishing results Stage is done \u2705"
        }
        stage('Asking for manual approval') {
            stage_pipe = 'Asking for manual approval'
            step_pipe = 'User should press the approval button'
            timeout(time: 30, unit: 'SECONDS') {
                input 'Deploy to prod?'
            }
            echo "\u277b: Asking for manual approval Stage is done \u2705"
        }
        stage('Deployment') {
            stage_pipe = 'Deployment'
            step_pipe = 'Clear remote tmp dir'
            sh "rm -rf pipeline-${student}-${env.BUILD_NUMBER}.tar.gz"
            step_pipe = 'Pull the Artifact'
            nexus(serv, username, password, repo, "pipeline-${student}-${env.BUILD_NUMBER}.tar.gz", "pull")
            //withEnv(["GROOVY_HOME=${tool groovy_version}"]) {
            //    sh "$GROOVY_HOME/bin/groovy push-pull.groovy ${serv} ${username} ${password} ${repo} pipeline-${student}-${env.BUILD_NUMBER}.tar.gz pull"
            //}
            step_pipe = 'Remote deployment through ssh'
            sh returnStatus: true, script: 'chmod 600 id_rsa'
            sh returnStatus: true, script: "scp -o StrictHostKeyChecking=no -i id_rsa -P2201 pipeline-${student}-${env.BUILD_NUMBER}.tar.gz jboss@EPBYMINW7423:/tmp/jenkins_tmp"
            sh returnStatus: true, script: "ssh -o StrictHostKeyChecking=no -i id_rsa -p 2201 jboss@EPBYMINW7423 'bash -s' < deploy.sh"
            sh returnStatus: true, script: "ssh -o StrictHostKeyChecking=no -i id_rsa -p 2201 jboss@EPBYMINW7423 'rm -rf /tmp/jenkins_tmp/*.tar.gz'"
            echo "\u277c: Deployment Stage is done \u2705"
        }

    }
} catch (err) {

    currentBuild.result = "FAILURE"
    throw err
}

finally{
    if (currentBuild.result != "FAILURE") {
        mail_to("Deployment", "SUCCESS", "Application has been deployed on the JBOSS Server", user_mail)
    } else
    {
        mail_to(stage_pipe, "FAILURE", step_pipe, user_mail)
    }
}