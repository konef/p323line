node {
    stage ('Preparation (Checking out)') {
        checkout([$class: 'GitSCM', branches: [[name: 'mpiatliou']], userRemoteConfigs: [[url: 'https://github.com/MNT-Lab/p323line.git']]])
    }
    stage ('Building code') {
        withMaven(maven: 'mavenLocal') {
            sh "mvn -f ./helloworld-ws/pom.xml package"
        }
    }
    /*stage ('Testing') {
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
    }*/
    stage ('Triggering job and fetching artifact after finishing') {
        build job: 'MNTLAB-mpiatliou-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: 'mpiatliou')], wait: true
        copyArtifacts filter: '*.tar.gz', projectName: 'MNTLAB-mpiatliou-child1-build-job', selector: lastSuccessful()
    }
    stage ('Packaging and Publishing results') {
        sh "tar -xzf mpiatliou_dsl_script.tar.gz"
        sh "tar -czf pipeline-mpiatliou-${BUILD_NUMBER}.tar.gz Jenkinsfile jobs.groovy -C helloworld-ws/target helloworld-ws.war"
        archiveArtifacts artifacts: "pipeline-mpiatliou-${BUILD_NUMBER}.tar.gz", onlyIfSuccessful: true
        sh 'export GROOVY_HOME=/home/vagrant/.jenkins/tools/hudson.plugins.groovy.GroovyInstallation/groovy; export PATH=$PATH:$GROOVY_HOME/bin; groovy ./pipeline_pullsh.groovy push'
    }
}

