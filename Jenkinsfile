def send_message(String stage, String desc) {
        mail bcc: '', body: "${env.BUILD_URL} has resulted in ${currentBuild.result} \n${desc} ", cc: '', from: '', replyTo: '', subject: "Stage ${stage} failed", to: 'glebko123@gmail.com'
    }


node("${SLAVE}") {
        stage('Clone repository'){
            def stage = STAGE_NAME
            def desc = "Cloning from github was failed"
            try {
                git branch: 'hviniarski', url: 'https://github.com/MNT-Lab/p323line'
            } catch (err) {
                currentBuild.result = "FAILED"
                //send_message(stage,desc)
            }
        }

        stage('Build') {
            withMaven(maven: 'mavenLocal',) {
                def stage = STAGE_NAME
                def desc = "Build was failed"
                try {
                    sh "mvn -f ./helloworld-ws/pom.xml package"
                } catch (err){
                  //  send_message(stage, desc)
                }
            }
        }

        stage("Testing") {
            def stage = STAGE_NAME
            def desc = "Tests were failed"
            try {
                withMaven(maven: 'mavenLocal') {


                    parallel(
                            'pre-integration-test': {
                                sh "mvn -f helloworld-ws/pom.xml pre-integration-test"
                            },
                            'integration-test': {
                                sleep 15
                                sh "mvn -f helloworld-ws/pom.xml integration-test"
                            },
                            'post-integration-test': {
                                sleep 30
                                sh "mvn -f helloworld-ws/pom.xml post-integration-test"
                            }
                    )
                }
            } catch (err) {
                currentBuild.result = "FAILED"
                //send_message(stage,desc)
            }
        }

        stage('Triggering job') {
            def stage = STAGE_NAME
            def desc = "Triggering child job failed"
            try {
                build job: 'MNTLAB-hviniarski-child1-build-job', parameters: [[$class: 'GitParameterValue', name: 'BRANCH_NAME', value: 'hviniarski']]
                copyArtifacts filter: 'hviniarski_dsl_script.tar.gz', projectName: 'MNTLAB-hviniarski-child1-build-job', selector: lastSuccessful()
            } catch (err) {
                currentBuild.result = "FAILED"
               // send_message(stage, desc)
            }
        }
        stage("Packaging and Publishing artifact"){
            def stage = STAGE_NAME
            def desc = "Packaging or publishing of artifact was failed"
            try {
                sh "tar -xvf hviniarski_dsl_script.tar.gz"
                sh "tar -czf pipeline-hviniarski-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile -C helloworld-ws/target/ helloworld-ws.war"
                sh '''
                    export GROOVY_HOME=/home/student/groovy-2.5.1
                    export PATH=$PATH:$GROOVY_HOME/bin
                    groovy push_pull.groovy push
                    '''
            }catch (err) {
                currentBuild.result = "FAILED"
             //   send_message(stage,desc)
            }
        }

        stage("Asking for manual approval") {
            def stage = STAGE_NAME
            def desc = "Package wasnt approved"
            try {
                timeout(time: 60, unit: 'SECONDS') {
                input ('Approve?')}
            } catch (err) {
                currentBuild.result = "FAILED"
           //     send_message(stage,desc)
            }
        }
        stage("Deployment"){
            def stage = STAGE_NAME
            def desc = "Tomcat deployment succeeded"
            try{
                sh '''
                    export GROOVY_HOME=/home/student/groovy-2.5.1
                    export PATH=$PATH:$GROOVY_HOME/bin
                    rm -f pipeline*.tar.gz
                    groovy push_pull.groovy pull
                    tar -xvf helloworld-ws.tar.gz
                    scp helloworld-ws.war tomcat@tomcat:/opt/tomcat/webapps/
                  '''
            } catch (err) {
                currentBuild.result = "FAILED"
         //       send_message(stage,desc)
            }
        }
        archiveArtifacts '*.tar.gz'
//        currentBuild.result = 'SUCCESS'
        cleanWs()
       // mail bcc: '', body: "${env.BUILD_URL} has resulted in ${currentBuild.result}", cc: '', from: '', replyTo: '', subject: "Status of pipeline: ${currentBuild.fullDisplayName}", to: 'glebko123@gmail.com'
    }
