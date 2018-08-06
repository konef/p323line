node("${SLAVE}") {
    stage('Preparation (Checking out)') {
        git branch: 'stsitou', url: 'https://github.com/MNT-Lab/p323line.git'
    }
    stage('Building code') {
        withMaven(maven: 'mavenLocal') {
            sh "sed -i 's/helloworld-ws Quickstart/helloworld-ws Quickstart build #${env.BUILD_NUMBER}/' ./helloworld-ws/src/main/webapp/index.html"
            sh "mvn -f ./helloworld-ws/pom.xml package"
        }
    }
    stage("Testing")
    withMaven(maven: 'mavenLocal') {
        parallel(
                'pre-integration-test': {
                    sh "mvn -f ./helloworld-ws/pom.xml pre-integration-test"
                },
                'integration-test': {
                    sh "mvn -f ./helloworld-ws/pom.xml integration-test"
                },
                'post-integration-test': {
                    sh "mvn -f ./helloworld-ws/pom.xml post-integration-test"
                }
        )
    }

    stage("Triggering job and fetching artifact after finishing"){
        echo "Starting another job"
        def childjob  = build job: 'EPBYMINW2466/MNTLAB-stsitou-child1-build-job', parameters: [string(name: 'BRANCH_NAME', value: 'stsitou')]
        copyArtifacts filter: "stsitou_dsl_script.tar.gz", projectName: "MNTLAB-stsitou-child1-build-job", selector: lastSuccessful()
        echo "Job status: ${childjob.result}"
    }

    stage('Packaging and Publishing results') {
        sh "tar -xzf stsitou_dsl_script.tar.gz "
        sh "tar -czf pipeline-stsitou-${env.BUILD_NUMBER}.tar.gz Jenkinsfile jobs.groovy helloworld-ws/target/helloworld-ws.war"
        archiveArtifacts "pipeline-stsitou-${env.BUILD_NUMBER}.tar.gz"
        sh "groovy ./artifacts push pipeline-stsitou-${env.BUILD_NUMBER}.tar.gz ${env.BUILD_NUMBER}"
        echo "Artifacts are packaged and published"
    }

    stage ('Asking for manual approval') {
        timeout(time: 120, unit: 'SECONDS') {
            input message: 'Deploy?', ok: 'Yes'
        }
    }

    stage('Deployment') {
        sh "groovy ./artifacts pull pipeline-stsitou-${env.BUILD_NUMBER}.tar.gz ${env.BUILD_NUMBER}"
        sh "ls ${artifact}"
        echo "Artifact is downloaded"
        sh "tar -xf"
        sh "sudo -u vagrant scp ./pom.xml 192.168.1.5:/home/vagrant/tomcat/webapps"
    }

}

