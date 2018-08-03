import java.text.SimpleDateFormat


student = 'ysokal'
archive_name = "pipeline-$student-${BUILD_NUMBER}.tar.gz"
date = new Date()
date_time = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss")

DL = 'yauheni_sokal@epam.com'
stage_name = ""
step_name = ""



def notification(stage_name, step_name, message, to) {
mail bcc: '', body: """------------------
Stage: "$stage_name",
Step: "$step_name",
Date/Time: ${date_time.format(date)},
Pipeline "$JOB_NAME" is $message!
------------------

You can find more information: http://EPBYMINW3088/jenkins/job/$JOB_NAME""", cc: '', from: '', replyTo: '', subject: "Jenkins notification: $JOB_NAME, build #$BUILD_NUMBER - $message", to: "${to}"

}


try {

    node($SLAVE) {
        def groovy = 'groovy4'
        stage('Preparation (Checking out)') {
            stage_name = "Preparation."
            step_name = "Clear WORKSPACE directory."
            deleteDir()

            step_name = "Download from GitHub."
            git branch: "$student", url: 'https://github.com/MNT-Lab/p323line.git'
        }

        stage('Building code') {
            stage_name = "Building code"
            step_name = "Implementation custom index.html."
            def CUR_USER = wrap([$class: 'BuildUser']) {
                return env.BUILD_USER
            }
            sh """cp /opt/jenkins/index.html helloworld-ws/src/main/webapp/
            sed -i "s/BuildNumber/$BUILD_NUMBER/" helloworld-ws/src/main/webapp/index.html
            sed -i "s/BuildTime/${date_time.format(date)}/" helloworld-ws/src/main/webapp/index.html
            sed -i "s/User/${CUR_USER}/" helloworld-ws/src/main/webapp/index.html"""

            step_name = "Building package from Maven."
            withMaven(jdk: 'java8', maven: 'mavenLocal') {
                sh 'mvn -f helloworld-ws/pom.xml package'
            }
        }
        stage('Testing') {
            stage_name = "Testing"
            parallel(
                    'Pre-integration Test': {
                        step_name = "Pre-integration Test"
                        withMaven(jdk: 'java8', maven: 'mavenLocal') {
                            sh 'mvn -f helloworld-ws/pom.xml pre-integration-test'
                        }
                    },
                    'Integration Test': {
                        sleep 3
                        step_name = "Integration Test"
                        withMaven(jdk: 'java8', maven: 'mavenLocal') {
                            sh 'mvn -f helloworld-ws/pom.xml integration-test'
                        }
                    },
                    'Post-integration Test': {
                        sleep 10
                        step_name = "Post-integration Test"
                        withMaven(jdk: 'java8', maven: 'mavenLocal') {
                            sh 'mvn -f helloworld-ws/pom.xml post-integration-test'
                        }
                    }
            )
        }

        stage('Triggering job') {
            stage_name = "Triggering job."
            step_name = "Running MNTLAB-$student-child1-build-job."
            build job: "MNTLAB-$student-child1-build-job", parameters: [string(name: 'BRANCH_NAME', value: "$student")], wait: true

            step_name = "Copy the artifacts to the triggering job."
            copyArtifacts filter: '*_dsl_script.tar.gz', projectName: "MNTLAB-$student-child1-build-job", selector: lastSuccessful()
        }

        stage('Packaging and Publishing') {
            stage_name = "Packaging and Publishing"
            step_name = "Packaging files to the tar-archive."
            sh """tar -xzf *_dsl_script.tar.gz -C helloworld-ws/target/
              tar -czf  $archive_name Jenkinsfile -C helloworld-ws/target/ helloworld-ws.war jobs.groovy"""

            step_name = "Archive the artifact"
            archiveArtifacts "$archive_name"

            step_name = "Push the artifact to Nexus."
            withEnv(["GROOVY_HOME=${tool groovy}"]) {
                sh "$GROOVY_HOME/bin/groovy archive_loader.groovy -n $archive_name -c push -r My-release"
            }
        }

        stage('Asking for manual approval') {
            stage_name = "Asking for manual approval."
            step_name = "Waiting 1 minute."
            timeout(time: 1, unit: 'MINUTES') {
                input 'Do you approve the deployment?'
            }
        }

        stage('Deployment') {
            stage_name = "Deployment."
            step_name = "Publishing through SSH."
            sh """scp -P2201 $archive_name vagrant@EPBYMINW3088:/home/vagrant/Jenkins
            scp -P2201 deploy.sh vagrant@EPBYMINW3088:/home/vagrant/Jenkins
            ssh -P2201 vagrant@EPBYMINW3088 'chmod +x /home/vagrant/Jenkins/deploy.sh'
            ssh -P2201 vagrant@EPBYMINW3088 "bash /home/vagrant/Jenkins/deploy.sh $archive_name" &&
            ssh -P2201 vagrant@EPBYMINW3088 "rm /home/vagrant/Jenkins/$archive_name"
            """

//            sshPublisher(publishers: [sshPublisherDesc(configName: 'Tomcat_8', transfers: [sshTransfer(excludes: '',
//                    execCommand: """chmod +x ~/Jenkins/deploy.sh
//                    ~/Jenkins/deploy.sh $archive_name $BUILD_NUMBER
//                    rm -rf ~/Jenkins/""", execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false,
//                    patternSeparator: '[, ]+', remoteDirectory: 'Jenkins', remoteDirectorySDF: false, removePrefix: '',
//                    sourceFiles: "$archive_name, deploy.sh")], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
        }

        stage('Results') {
            stage_name = "Results"
            step_name = "Publishing results."
            junit '**/target/surefire-reports/TEST-*.xml'
            archive 'target/*.jar'
        }

        stage('Send notification') {
            notification("Deployment", "Application has been deployed on http://EPBYMINW3088/tomcat/helloworld-ws/index.html", "COMPLETED", "$DL")
        }
    }
} catch (Exception e) {
    notification("$stage_name", "$step_name", "FAIL", "$DL")
    currentBuild.result = "FAILURE"
}
