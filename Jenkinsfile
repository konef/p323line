student = 'ysokal'

node() {
    stage('Preparation (Checking out)') {
        deleteDir()
        git branch: ${student}, url: 'https://github.com/MNT-Lab/p323line.git'
        }

    stage('Building code') {
        def CUR_USER = wrap([$class: 'BuildUser']) {
                    return env.BUILD_USER
                }
        sh """cp /opt/jenkins/index.html $WORKSPACE/helloworld-ws/src/main/webapp/
        sed -i "s/BuildNumber/$BUILD_NUMBER/" $WORKSPACE/helloworld-ws/src/main/webapp/index.html
        sed -i "s/User/$CUR_USER/" $WORKSPACE/helloworld-ws/src/main/webapp/index.html"""

        withMaven(jdk: 'java8', maven: 'mavenLocal') {
        sh 'mvn -f helloworld-ws/pom.xml package'
        }
    }
    stage('Testing') {
        parallel (
            'Pre-integration Test': {
                withMaven(jdk: 'java8', maven: 'mavenLocal') {
                    sh 'mvn -f helloworld-ws/pom.xml pre-integration-test'
                }
            },
            'Integration Test': {
                withMaven(jdk: 'java8', maven: 'mavenLocal') {
                    sh 'mvn -f helloworld-ws/pom.xml integration-test'
                    sleep 30
                }
            },
            'Post-integration Test': {
                withMaven(jdk: 'java8', maven: 'mavenLocal') {
                    sh 'mvn -f helloworld-ws/pom.xml post-integration-test'
                    sleep 60
                }
            }
        )
    }

    stage('Triggering job') {
        build job: "MNTLAB-${student}-child1-build-job", parameters: [string(name: 'BRANCH_NAME', value: ${student})], wait: true
        copyArtifacts filter: '*_dsl_script.tar.gz', projectName: "MNTLAB-${student}-child1-build-job", selector: lastSuccessful()
    }

    stage('Packaging and Publishing') {
        sh """tar -xzf *_dsl_script.tar.gz -C helloworld-ws/target/
              tar -czf  pipeline-${student}-${BUILD_NUMBER}.tag.gz Jenkinsfile -C helloworld-ws/target/ helloworld-ws.war jobs.groovy"""
        archive "pipeline-${student}-${BUILD_NUMBER}.tag.gz"
        sh """export GROOVY_HOME=/home/student/Downloads/soft/groovy-2.5.1
              export PATH=$PAHT:$GROOVY_HOME/bin
              groovy -n pipeline-${student}-${BUILD_NUMBER}.tag.gz -c push -r My-release archive_loader.groovy"""

    }


    stage('Results') {
        junit '**/target/surefire-reports/TEST-*.xml'
        archive 'target/*.jar'
    }
}
