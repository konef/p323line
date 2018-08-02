@Grapes([
        @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7'),
        @GrabConfig(systemClassLoader=true)
])
import groovyx.net.http.RESTClient
import org.apache.http.entity.*
import hudson.model.*


void push() {
    def workspace = System.getenv('WORKSPACE')
    def buildNumber = System.getenv('BUILD_NUMBER')
    def pom = new XmlSlurper().parse("${workspace}"+'/helloworld-ws/pom.xml')
    def repo = "maven-artifacts"
    def restClient = new RESTClient("http://nexus/repository/${repo}/")
    restClient.auth.basic 'nexus-service-user', 'nexus'
    restClient.encoder.'application/zip' = this.&encodingZipFile
    def correct_path = (pom.parent.groupID).replaceAll('\\.','/')
    def artifactId = pom.artifactId
    def version = pom.parent.version
    def launch = restClient.put(
            path: "http://EPBYMINW2472/nexus/repository/${repo}/${correct_path}/${artifactId}/${version}/pipeline-hviniarski-${buildNumber}.tar.gz",
            body: new File("${workspace}/pipeline-hviniarski-${buildNumber}.tar.gz"),
            requestContentType: 'application/zip'
    )
}

def encodingZipFile(Object data) throws UnsupportedEncodingException {
    def entity = new FileEntity((File) data, 'application/zip')
    entity.setContentType('application/zip')
    return entity
}

void pull(String[] pom_return) {
    def buildNumber = System.getenv('BUILD_NUMBER')
    def workspace = System.getenv('WORKSPACE')
    def artifactName = "pipeline-hviniarski-${buildNumber}"
    def repo = "project-releases"
    def pom = new XmlSlurper().parse("${workspace}"+'/helloworld-ws/pom.xml')
    def correct_path = (pom.parent.groupID).replaceAll('\\.','/')
    def artifactId = pom.artifactId
    def version = pom.parent.version
    def restClient = new RESTClient("http://EPBYMINW2472/nexus/repository/${repo}/")
    restClient.auth.basic 'admin', 'admin123'
    def launch = restClient.get(path: "http://EPBYMINW2472/nexus/repository/${repo}/${correct_path}/${artifactId}/${version}/${artifactName}"
    )
    new File("${workspace}/${artifactId}.tar.gz") << launch.data
}

def cli = new CliBuilder()
def options = cli.parse(args)
def arguments = options.arguments()

if (arguments.size() != 0 && arguments[0] == 'pull' ){
    pull()
}
else  if (arguments.size() != 0 && arguments[0] == 'push' ){
    push()
}