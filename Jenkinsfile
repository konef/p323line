@Grapes(
@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7')
)
import groovyx.net.http.RESTClient
import org.apache.http.entity.*
import hudson.model.*
def encodeTarFile( Object data ) throws UnsupportedEncodingException {
   def entity = new FileEntity( (File) data, "application/tar.gz" );
   entity.setContentType( "application/tar.gz" );
   return entity
   }

def upload() {
   def repo = "maven-helloworld"
   def gav = parse_gav()
   def file = new File("pipeline-dzhukova-${BUILD_NUMBER}.tar.gz")
   def rest = new RESTClient( "http://192.168.17.4/nexus/repository/${repo}")
        rest.auth.basic 'admin', 'admin'
        rest.encoder.'application/tar.gz' = this.&encodeTarFile
        println "http://192.168.17.4/nexus/repository/${repo}/${gav[1]}/${BUILD_NUMBER}/${gav[1]}-${BUILD_NUMBER}.tar.gz"
        rest.put(path: "http://192.168.17.4/nexus/repository/${repo}/${gav[1]}/${BUILD_NUMBER}/${gav[1]}-${BUILD_NUMBER}.tar.gz", body: file, requestContentType: 'application/tar.gz')

}
String[] parse_gav() {
        def pom = new XmlSlurper().parse(new File("${WORKSPACE}/helloworld-ws/pom.xml"))
        def gavs = []
        gavs.add(pom.parent.groupId)
        gavs.add(pom.artifactId)
        gavs.add(pom.parent.version)
        return gavs
}


node {
       stage('Clone sources') {
        git url: 'https://github.com/MNT-Lab/p323line.git', branch: 'dzhukova';}
stage('Build') {
           withMaven(maven: 'mavenLocal')
        {
           sh "mvn -f ./helloworld-ws/pom.xml clean install"
        }
}
/*parallel('pre-integration-test': {
stage('pre-integration-test') {
           withMaven(maven: 'mavenLocal')
        {
           sh "mvn -f ./helloworld-ws/pom.xml pre-integration-test"
        }
}},
'integration-test': { 
    stage('integration-test') {
           withMaven(maven: 'mavenLocal')
        {
           sh "mvn -f ./helloworld-ws/pom.xml integration-test"
        }
}},
'post-integration-test': {
    stage('post-integration-test') {
           withMaven(maven: 'mavenLocal')
        {
           sh "mvn -f ./helloworld-ws/pom.xml post-integration-test"
        }
}})
*/
stage('build child job') {
    build job: 'dzhukova/MNTLAB-dzhukova-child1-build-job', parameters: [
        string(name: 'BRANCH_NAME', value: 'dzhukova')
    ]
}
stage('Copy Artifact') {
    copyArtifacts filter: 'dzhukova_dsl_script.tar.gz', projectName: 'dzhukova/MNTLAB-dzhukova-child1-build-job', selector: lastSuccessful()
    sh 'tar -xvf dzhukova_dsl_script.tar.gz';
    sh "tar -czvf pipeline-dzhukova-${BUILD_NUMBER}.tar.gz Jenkinsfile jobs.groovy -C helloworld-ws/target/ helloworld-ws.war "
    def repo = "maven-helloworld"
   def gav = parse_gav()
   def file = new File("pipeline-dzhukova-${BUILD_NUMBER}.tar.gz")
   def rest = new RESTClient( "http://192.168.17.4/nexus/repository/${repo}")
        rest.auth.basic 'admin', 'admin'
        rest.encoder.'application/tar.gz' = this.&encodeTarFile
        def response = restClient.put(path: 'http://epbyminw7425/nexus/repository/${repo}/111111/1.tar.gz',
body: new File("pipeline-dzhukova-${BUILD_NUMBER}.tar.gz"),
requestContentType: 'application/tar.gz')
}
       /* rest.put(path: "http://192.168.17.4:8081/nexus/repository/${repo}/123/pipeline-dzhukova-${BUILD_NUMBER}.tar.gz", body: file, requestContentType: 'application/tar.gz')*/
    
        
        
}
    


    


