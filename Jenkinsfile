def send_message(Boolean state, String stage, String desc) {
    if (state) {
        mail bcc: '', body: desc, cc: '', from: '', replyTo: '', subject: "Stage \'${stage}\' succeeded", to: 'aranich@ya.ru'
    }
    else {
        mail bcc: '', body: desc, cc: '', from: '', replyTo: '', subject: "Stage \'${stage}\' failed", to: 'aranich@ya.ru'
    }
}

node("${SLAVE}") {
    def state = true
    def stage_name = ""
    stage ('Preparation (Checking out)') {
        stage_name = STAGE_NAME
        def desc = "Cloning from github was "
        try {
            git branch: 'aaranski', url: 'https://github.com/MNT-Lab/p323line.git'
            desc += "finished"
        } catch (err) {
            state = false
            desc += "interrupted"
            send_message(state,stage_name,desc)
            throw err
        }
    }
    stage ('Building code') {
        stage_name = STAGE_NAME
        def desc = "Building of the code was "
        try {
            withMaven(maven: 'mavenLocal') {
                sh "mvn -f ./helloworld-ws/pom.xml package"
            }
            desc += "finished"
        } catch (err) {
            state = false
            desc += "interrupted"
            send_message(state,stage_name,desc)
            throw err
        }
    }
    stage ('Testing') {
        stage_name = STAGE_NAME
        def desc = "Testing of the code was "
        try {
            withMaven(maven: 'mavenLocal') {
                parallel (
                        "pre-integration-test" : {
                            sh "mvn -f ./helloworld-ws/pom.xml pre-integration-test"
                        },
                        "integration-test" : {
                            sh "mvn -f ./helloworld-ws/pom.xml integration-test"
                        },
                        "post-integration-test" : {
                            sh "mvn -f ./helloworld-ws/pom.xml post-integration-test"
                        }
                )
            }
            desc += "finished"
        } catch (err) {
            state = false
            desc += "interrupted"
            send_message(state,stage_name,desc)
            throw err
        }
    }
    stage ('Triggering job and fetching artefact after finishing') {
        stage_name = STAGE_NAME
        def desc = "Triggering the child job and copying an artifact were "
        try {
            build job: "MNTLAB-aaranski-child1-build-job", parameters: [
                    string(name: 'BRANCH_NAME', value: 'aaranski')
            ], wait: true
            step($class: 'CopyArtifact', projectName: 'MNTLAB-aaranski-child1-build-job', filter: '*.tar.gz')
            desc += "finished"
        } catch (err) {
            state = false
            desc += "interrupted"
            send_message(state,stage_name,desc)
            throw err
        }
    }
    stage ('Packaging and Publishing results') {
        stage_name = STAGE_NAME
        def desc = "Packaging and publishing results were "
        try {
            sh """
            tar -xzf aaranski*.tar.gz
            tar -czf pipeline-aaranski-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile -C helloworld-ws/target helloworld-ws.war
            """
            archiveArtifacts artifacts: "pipeline-aaranski-${BUILD_NUMBER}.tar.gz"
            sh '''
            export GROOVY_HOME=/home/student/groovy-2.5.1
            export PATH=$PATH:$GROOVY_HOME/bin
            groovy push_pull.groovy push
            '''
            desc += "finished"
        } catch (err) {
            state = false
            desc += "interrupted"
            send_message(state,stage_name,desc)
            throw err
        }
    }
    stage ('Asking for manual approval') {
        def userInput = true
        stage_name = STAGE_NAME
        def desc = "Manual approval was "
        try {
            timeout(time: 60, unit: 'SECONDS') {
                userInput = input(
                        id: 'Proceed1', message: 'Approve release?', parameters: [
                        [$class: 'BooleanParameterDefinition', defaultValue: true, description: '', name: 'Please confirm you agree with this']
                ])
            }
            desc += "succeeded"
        } catch(err) {
            def user = err.getCauses()[0].getUser()
            echo "Aborted by: [${user}]"
            currentStage.result = 'ABORTED'
            state = false
            desc += "aborted"
            send_message(state,stage_name,desc)
        }
    }
    stage ('Deployment') {
        stage_name = STAGE_NAME
        def desc = "The deployment was "
        try {
            sh '''
            export GROOVY_HOME=/home/student/groovy-2.5.1
            export PATH=$PATH:$GROOVY_HOME/bin
            groovy push_pull.groovy pull
            tar -xzf pipeline*.tar.gz && rm -f pipeline*.tar.gz
            scp helloworld-ws.war vagrant@tomcat:/opt/tomcat/webapps/helloworld-ws.war
            rm -f helloworld-ws.war'''
            desc += "finished"
        } catch (err) {
            state = false
            desc += "interrupted"
            send_message(state,stage_name,desc)
            throw err
        }
    }
    stage_name = "Continuous deployment"
    def desc = "The process of deployment to the production "
    if (state) {
        send_message(state,stage_name,desc)
    }
}