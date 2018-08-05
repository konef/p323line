def email(String stage, String desc) {
    mail bcc: '', body: "${desc}", cc: '', from: '', replyTo: '', subject: "Stage ${stage} failed", to: 'danzisakau@gmail.com'
}

node("${SLAVE}") {
    stage('Preparating (Checking out)'){
        try {
            git branch: 'disakau',
                    url: 'https://github.com/MNT-Lab/p323line'
        }
        catch (err) {
            email(stagex, desc)
            throw err
        }
    }
    stage("Testing"){
        try {
            withMaven(maven: 'mavenLocal'){
                parallel (
                        "pre-Integration test": {
                            sh "mvn -f ./helloworld-ws/pom.xml pre-integration-test"},
                        "Integration test": {
                            sh "mvn -f ./helloworld-ws/pom.xml integration-test"},
                        "post-Integration test": {
                            sh "mvn -f ./helloworld-ws/pom.xml post-integration-test"})
            }
        }
        catch (err) {
            email(stagex, desc)
            throw err
        }
    }

    stage("Triggering job and fetching artefact after finishing"){
        try {
            build job: 'MNTLAB-disakau-child-1-build-job',
                    parameters: [string(name: 'BRANCH_NAME', value: 'disakau')]
            copyArtifacts filter: 'disakau_dsl_script.tar.gz',
                    fingerprintArtifacts: true,
                    projectName: 'MNTLAB-disakau-child-1-build-job',
                    selector: lastSuccessful()
        }
        catch (err) {
            email(stagex, desc)
            throw err
        }
    }

    stage ('Packaging and Publishing results') {
        try {
            sh "tar -xvf disakau_dsl_script.tar.gz"
            sh 'cp -f helloworld-ws/target/helloworld-ws.war .'
            sh "tar -czf pipeline-disakau-${BUILD_NUMBER}.tar.gz jobs.groovy Jenkinsfile helloworld-ws.war"
            archiveArtifacts "pipeline-disakau-${BUILD_NUMBER}.tar.gz"
            sh "groovy pull_push.groovy -p push -a pipeline-disakau-${BUILD_NUMBER}.tar.gz"
        }
        catch (err) {
            email(stagex, desc)
            throw err
        }
    }

    stage ('Asking for manual approval') {
        try {
            timeout(time: 120, unit: 'SECONDS') {
                input message:'Do you approve that deployment?', ok: 'Yes'
            }
        }
        catch (err) {
            email(stagex, desc)
            throw err
        }
    }

    stage("Deployment") {
        try {
            sh "groovy pull_push.groovy -p pull -a pipeline-disakau-${BUILD_NUMBER}.tar.gz"
            sh "scp pipeline-disakau-${BUILD_NUMBER}.tar.gz vagrant@192.168.100.20:/vagrant/apache-tomcat-8.5.32/webapps/"
            sh "ssh vagrant@192.168.100.20 'cd /vagrant/apache-tomcat-8.5.32/webapps/ && tar xzf pipeline-disakau-${BUILD_NUMBER}.tar.gz && rm -rf pipeline-disakau-${BUILD_NUMBER}.tar.gz Jenkinsfile jobs.groovy'"
        }
        catch (err) {
            email(stagex, desc)
            throw err
        }
    }

    stage('Sending status') {
        try{
            println "COMPLETED"
        }
        finally {
            email(stagex, desc)
        }
    }
}