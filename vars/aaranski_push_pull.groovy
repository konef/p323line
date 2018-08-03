@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7')
import groovyx.net.http.RESTClient
import org.apache.http.entity.*
import hudson.model.*

String[] parsePom(def workspace) {
    def pom = new XmlSlurper().parse(new File("${workspace}/pom.xml"))
    def result_list = []
    result_list.add(pom.groupId)
    result_list.add(pom.artifactId)
    result_list.add(pom.version)
    return result_list
}

void push(String[] pom_gav) {
    def buildNumber = System.getenv('BUILD_NUMBER')
    def repo = 'project-releases' //System.getenv('REPO_NAME')
    def workspace = System.getenv('WORKSPACE')
    def gav = pom_gav
    def restClient = new RESTClient("http://EPBYMINW2695/nexus/repository/${repo}/")
	restClient.auth.basic 'nexus-service-user', 'nexus'
	restClient.encoder.'application/zip' = this.&encodeZipFile
    def correct_path = gav[0].replaceAll('\\.','/')
    def artifactId = gav[1]
    def version = gav[2]
    def response = restClient.put(
    path: "http://EPBYMINW2695/nexus/repository/${repo}/${correct_path}/${artifactId}/${version}/pipeline-aaranski-${buildNumber}.tar.gz",
    body: new File("pipeline-aaranski-${buildNumber}.tar.gz"),
    requestContentType: 'application/zip'
  )
}

def encodeZipFile(Object data) throws UnsupportedEncodingException {
    def entity = new FileEntity((File) data, 'application/zip')
    entity.setContentType('application/zip')
    return entity
}

void pull(String[] pom_gav) {
    def buildNumber = System.getenv('BUILD_NUMBER')
    def artifactName = "pipeline-aaranski-${buildNumber}.tar.gz"
    def repo = 'project-releases' //System.getenv('REPO_NAME')
    def workspace = System.getenv('WORKSPACE')
    def gav = pom_gav
    def correct_path = gav[0].replaceAll('\\.','/')
    def artifactId = gav[1]
    def version = gav[2]
    def restClient = new RESTClient("http://EPBYMINW2695/nexus/repository/${repo}/")
    restClient.auth.basic 'nexus-service-user', 'nexus'
    def response = restClient.get(path: "http://EPBYMINW2695/nexus/repository/${repo}/${correct_path}/${artifactId}/${version}/${artifactName}"
    )
    new File("./${artifactId}.tar.gz") << response.data
}

def cli = new CliBuilder()
def options = cli.parse(args)
def arguments = options.arguments()
def workspace = System.getenv('WORKSPACE')
def gav = parsePom(workspace)

if (arguments.size() != 0 && arguments[0] == 'pull' ){
pull(gav)
}
else if (arguments.size() != 0 && arguments[0] == 'push' ){
push(gav)
}
