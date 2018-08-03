def send_email(String stage, String desc) {
    mail bcc: '', body: "${desc}", cc: '', from: '', replyTo: '', subject: "Stage ${stage} failed", to: 'mishok26@gmail.com'
}

node ("${SLAVE}") {
    stage ('Preparation (Checking out)') {
        def stagex = STAGE_NAME
        def desc = "git clone was failed"
        try {
            checkout([$class: 'GitSCM', branches: [[name: 'piatliou']], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/p323line.git']]])
        }
        catch (err) {
            send_email(stagex, desc)
            throw err
        }
    }
    stage ('Building code') {
        withMaven(maven: 'mavenLocal') {
            def stagex = STAGE_NAME
            def desc = "building was failed"
            try {
                sh "mvn -f ./helloworld-ws/pom.xml package"
            }
            catch (err) {
                send_email(stagex, desc)
                throw err
            }
        }
    }
    stage ('Testing') {
        def stagex = STAGE_NAME
        def desc = "tests were failed"
        try {
            parallel ('pre-integration-test': {
                withMaven(maven: 'mavenLocal') {
                    sh "mvn -f ./helloworld-ws/pom.xml pre-integration-test"
                }
            },
                    'integration-test': {
                        withMaven(maven: 'mavenLocal') {
                            sh "mvn -f ./helloworld-ws/pom.xml integration-test"
                        }
                    },
                    'post-integration-test': {
                        withMaven(maven: 'mavenLocal') {
                            sh "mvn -f ./helloworld-ws/pom.xml post-integration-test"
                        }
                    }
            )
        }
        catch (err) {
            send_email(stagex,desc)
            throw err
        }
    }
    stage ('Triggering job and fetching artifact after finishing') {
        def stagex = STAGE_NAME
        def desc = "job triggering was failed"
        try {
            build job: 'MNTLAB-mpiatliou-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: 'mpiatliou')], wait: true
            copyArtifacts filter: '*.tar.gz', projectName: 'MNTLAB-mpiatliou-child1-build-job', selector: lastSuccessful()
        }
        catch (err) {
            send_email(stagex,desc)
            throw err
        }
    }
    stage ('Packaging and Publishing results') {
        def stagex = STAGE_NAME
        def desc = "artifact packaging or publishing was failed"
        try {
            sh "tar -xzf mpiatliou_dsl_script.tar.gz"
            sh "tar -czf pipeline-mpiatliou-${BUILD_NUMBER}.tar.gz Jenkinsfile jobs.groovy -C helloworld-ws/target helloworld-ws.war"
            archiveArtifacts artifacts: "pipeline-mpiatliou-${BUILD_NUMBER}.tar.gz", onlyIfSuccessful: true
            sh '''
            export GROOVY_HOME=/home/student/install/groovy-2.5.1
            export PATH=$PATH:$GROOVY_HOME/bin
            groovy ./pipeline_pullsh.groovy push
            '''
        }
        catch (err) {
            send_email(stagex,desc)
            throw err
        }

    }
    stage ('Asking for manual approval') {
        def stagex = STAGE_NAME
        def desc = "artifact deploying was not approved"
        try {
            timeout(time: 60, unit: 'SECONDS') {
                input(id: "Deploy Gate", message: "Do you approve?", ok: 'Approve')
            }
        }
        catch (err) {
            send_email(stagex,desc)
            throw err
        }
    }
    stage ('Deployment') {
        def stagex = STAGE_NAME
        def desc = "tomcat deploying was successful"
        try {
            sh '''
            export GROOVY_HOME=/home/student/install/groovy-2.5.1
            export PATH=$PATH:$GROOVY_HOME/bin
            groovy ./pipeline_pullsh.groovy pull
            tar -xzf artifact.tar.gz && rm -f artifact.tar.gz
            scp -P 2201 helloworld-ws.war vagrant@epbyminw1374:/opt/tomcat/apache-tomcat-8.0.53/webapps/helloworld-ws.war
            rm -f helloworld-ws.war
            '''
        }
        catch (err) {
            send_email(stagex,desc)
            throw err
        }
    }
    currentBuild.result = 'SUCCESS'
    mail bcc: '', body: "the result of ${env.BUILD_URL} is ${currentBuild.result}", cc: '', from: '', replyTo: '', subject: "pipeline status is ${currentBuild.result}", to: 'mishok26@gmail.com'
}
