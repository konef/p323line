node ("${SLAVE}")  {
    stage ('Preparation (Checking out)') {
        checkout([$class: 'GitSCM', branches: [[name: 'mpiatliou']], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/p323line.git']]])
    }
    stage ('Building code') {
        withMaven(maven: 'mavenLocal') {
            sh "mvn -f ./helloworld-ws/pom.xml package"
        }
    }
    stage ('Testing') {
        parallel ('pre-integration-test': {
            withMaven(maven: 'mavenLocal') {
                sh "mvn -f ./helloworld-ws/pom.xml pre-integration-test; sleep 10"
            }
        },
                'integration-test': {
                    withMaven(maven: 'mavenLocal') {
                        sh "mvn -f ./helloworld-ws/pom.xml integration-test; sleep 10"
                    }
                },
                'post-integration-test': {
                    withMaven(maven: 'mavenLocal') {
                        sh "mvn -f ./helloworld-ws/pom.xml post-integration-test; sleep 10"
                    }
                }
        )
    }
    stage ('Triggering job and fetching artifact after finishing') {
        build job: 'MNTLAB-mpiatliou-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: 'mpiatliou')], wait: true
        copyArtifacts filter: '*.tar.gz', projectName: 'MNTLAB-mpiatliou-child1-build-job', selector: lastSuccessful()
    }
    stage ('Packaging and Publishing results') {
        sh "tar -xzf mpiatliou_dsl_script.tar.gz"
        sh "tar -czf pipeline-mpiatliou-${BUILD_NUMBER}.tar.gz Jenkinsfile jobs.groovy -C helloworld-ws/target helloworld-ws.war"
        archiveArtifacts artifacts: "pipeline-mpiatliou-${BUILD_NUMBER}.tar.gz", onlyIfSuccessful: true
        sh 'export GROOVY_HOME=/home/student/install/groovy-2.5.1; export PATH=$PATH:$GROOVY_HOME/bin; groovy ./pipeline_pullsh.groovy push'
    }
    stage ('Asking for manual approval') {
        try {
            timeout(time: 60, unit: 'SECONDS') {
                input(id: "Deploy Gate", message: "Do you approve?", ok: 'Approve')
            }
        }
        catch (error) {
            throw error
        }
    }
    stage ('Deployment') {
        sh '''
        export GROOVY_HOME=/home/student/install/groovy-2.5.1
        export PATH=$PATH:$GROOVY_HOME/bin
        groovy ./pipeline_pullsh.groovy pull
        tar -xzf artifact.tar.gz && rm -f artifact.tar.gz
        scp helloworld-ws.war vagrant@epbyminw1374/tomcat:/opt/tomcat/apache-tomcat-8.0.53/webapps/helloworld-ws.war
        rm -f helloworld-ws.war
        '''
    }
}

