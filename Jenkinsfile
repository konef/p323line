def send_message(Boolean state, String stage, String desc) {
    if (state) {
        mail bcc: '', body: desc, cc: '', from: '', replyTo: '', subject: "Stage \'${stage}\' succeeded", to: 'aranich@ya.ru'
    }
    else {
        mail bcc: '', body: desc, cc: '', from: '', replyTo: '', subject: "Stage \'${stage}\' failed", to: 'aranich@ya.ru'
    }
}

node("${SLAVE}") {
    stage ('Preparation (Checking out)') {
        def stage = STAGE_NAME
        def state = true
        def desc = "Cloning from github was "
        try {
            git branch: 'aaranski', url: 'https://github.com/MNT-Lab/p323line.git'
            desc += "finished"
        } catch (err) {
            state = false
            desc += "interrupted"
        }
        send_message(state,stage,desc)
    }
    stage ('Building code') {
        def stage = STAGE_NAME
        def state = true
        def desc = "Building of the code was "
        try {
            withMaven(maven: 'mavenLocal') {
                sh "mvn -f ./helloworld-ws/pom.xml package"
            }
            desc += "finished"
        } catch (err) {
            state = false
            desc += "interrupted"
        }
        send_message(state,stage,desc)
    }
    stage ('Testing') {
        def stage = STAGE_NAME
        def state = true
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
        }
        send_message(state,stage,desc)
    }
    stage ('Triggering job and fetching artefact after finishing') {
        def stage = STAGE_NAME
        def state = true
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
        }
        send_message(state,stage,desc)
    }
    stage ('Packaging and Publishing results') {
        def stage = STAGE_NAME
        def state = true
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
        }
        send_message(state,stage,desc)
    }
    stage ('Asking for manual approval') {
        def userInput = true
        def stage = STAGE_NAME
        def state = true
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
        }
        send_message(state,stage,desc)
    }
    stage ('Deployment') {
        def stage = STAGE_NAME
        def state = true
        def desc = "The deployment was "
        try {
            sh '''
            export GROOVY_HOME=/home/student/groovy-2.5.1
            export PATH=$PATH:$GROOVY_HOME/bin
            groovy push_pull.groovy pull
            tar -xzf pipeline*.tar.gz && rm -f pipeline*.tar.gz
            ssh vagrant@tomcat << "EO"
            cd /opt/tomcat/webapps && rm -f helloworld-ws.war.old
            mv helloworld-ws.war helloworld-ws.war.old
            "EO"
            scp helloworld-ws.war vagrant@tomcat:/opt/tomcat/webapps/
            response=$( curl -I http://tomcat:8080/helloworld-ws/ 2>/dev/null | head -n 1 | cut -d$' ' -f2 )
            if [ "$response" == "200" ]; then
            page_value=$( curl http://tomcat:8080/helloworld-ws/ 2>/dev/null | grep "helloworld-ws" )
            fi
            if [ "$response" != "200" ] || [ "$page_value" == "" ]; then
            ssh vagrant@tomcat << "END"
            cd /opt/tomcat/webapps && rm -f helloworld-ws.war
            cp helloworld-ws.war.old helloworld-ws.war
            "END"
            fi
            rm -f helloworld-ws.war'''
            desc += "finished"
        } catch (err) {
            state = false
            desc += "interrupted"
        }
        send_message(state,stage,desc)
    }
}
