import groovyx.net.http.RESTClient
import org.apache.http.entity.FileEntity

hostname="192.168.1.4:8081"
username="jenkins"
password="jenkins"
reponame="mvnrepo"

node("${SLAVE}") {
    stage('Preparation (Checking out)') {
        git branch: 'stsitou', url: 'https://github.com/MNT-Lab/p323line.git'
    }
    stage('Building code') {
        withMaven(maven: 'mavenLocal') {
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
        copyArtifacts filter: "${student}_dsl_script.tar.gz", projectName: "MNTLAB-stsitou-child1-build-job", selector: lastSuccessful()
        echo "Job status: ${childjob.result}"
    }
    stage('Packaging and Publishing results') {
        sh "tar -xzf stsitou_dsl_script.tar.gz "
        sh "tar -czf pipeline-stsitou-${env.BUILD_NUMBER}.tar.gz Jenkinsfile jobs.groovy -C helloworld-ws/target/ helloworld-ws.war"
        archiveArtifacts "pipeline-stsitou-${env.BUILD_NUMBER}.tar.gz"
        push(pipeline-stsitou-${env.BUILD_NUMBER}.tar.gz)
        echo "Artifacts are packaged and published"
    }
}


void pull(artifact) {
    restClient = new RESTClient("http://" + hostname + "/repository/" + reponame + "/")
    restClient.auth.basic(username, password)
    def response = restClient.get(path: "http://" + hostname + "/repository/" + reponame +"/" + artifact,
    )
    new File(artifact) << response.data
}

void push(artifact) {
    restClient = new RESTClient("http://" + hostname + "/repository/"+ reponame +"/")
    restClient.auth.basic 'Artifacts-service-user', 'Artifacts'
    restClient.encoder.'application/zip' = this.&setZipMimeType
    restClient.put(
            path: "http://" + hostname +"/repository/" + mvnrepo + "/" + artifact,
            body: new File(artifact),
            requestContentType: 'application/zip'
    )
}

FileEntity setZipMimeType(Object data) throws UnsupportedEncodingException {
    def entity = new FileEntity((File) data, 'application/zip')
    entity.setContentType('application/zip')
    return entity
}
