node() {
    stage ('Preparation (Checking out)') {
        git branch: 'aaranski', url: 'https://github.com/MNT-Lab/p323line.git'
    }
    stage ('Building code') {
        withMaven(maven: 'mavenLocal') {
            sh "mvn -f ./helloworld-ws/pom.xml package"
        }
    }
/*    stage ('Testing') {
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
    }*/
    stage ('Triggering job and fetching artefact after finishing') {
        build job: "MNTLAB-aaranski-child1-build-job", parameters: [
            string(name: 'BRANCH_NAME', value: 'aaranski')
            ], wait: true
        step($class: 'CopyArtifact', projectName: 'MNTLAB-aaranski-child1-build-job', filter: '*.tar.gz')
    }
    stage ('Packaging and Publishing results') {
        sh """
        tar -xzf aaranski*.tar.gz
        tar -czf pipeline-aaranski-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile -C helloworld-ws/target helloworld-ws.war
        """
        archiveArtifacts artifacts: "pipeline-aaranski-${BUILD_NUMBER}.tar.gz"
        sh '''
        export GROOVY_HOME=/opt/jenkins/master/tools/hudson.plugins.groovy.GroovyInstallation/Groovy2.5.1
        export PATH=$PATH:$GROOVY_HOME/bin
        groovy push_pull.groovy push
        '''
    }
    stage ('Asking for manual approva') {
        def userInput = true
        try {
            timeout(time: 60, unit: 'SECONDS') {
                userInput = input(
                id: 'Proceed1', message: 'Approve release?', parameters: [
                [$class: 'BooleanParameterDefinition', defaultValue: true, description: '', name: 'Please confirm you agree with this']
                ])
            }
        } catch(err) {
            def user = err.getCauses()[0].getUser()
            echo "Aborted by: [${user}]"
            currentStage.result = 'ABORTED'
        }
    }
}
