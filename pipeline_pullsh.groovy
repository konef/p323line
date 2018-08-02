@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.7.2')
import groovyx.net.http.RESTClient
import org.apache.http.entity.*
import hudson.model.*

String[] parsing(def workspace) {
    def pom = new XmlSlurper().parse(new File("${workspace}/pom.xml"))
    def gav_list = []
    gav_list.add(pom.groupId)
    gav_list.add(pom.artifactId)
    gav_list.add(pom.version)
    return gav_list
}


void push(String[] gavv) {
    def buildNumber = System.getenv('BUILD_NUMBER')
    def gav = gavv
    def groupId = gav[0].replace('\\.','/')
    def artifactId = gav[1]
    def version = gav[2]
    def restClient = new RESTClient('http://epbyminw1374/nexus/repository/project-releases/')
    restClient.auth.basic 'nexus-service-user', 'nexus'
    restClient.encoder.'application/zip' = this.&encodingZipFile
    restClient.put(
            path: "http://epbyminw1374/nexus/repository/project-releases/${groupId}/${artifactId}/${version}/pipeline-mpiatliou-${buildNumber}.tar.gz",
            body: new File("pipeline-mpiatliou-${buildNumber}.tar.gz"),
            requestContentType: 'application/zip'
    )
}


def encodingZipFile(Object data) throws UnsupportedEncodingException {
    def entity = new FileEntity((File) data, 'application/zip')
    entity.setContentType('application/zip')
    return entity
}

def arguments = new CliBuilder().parse(args).arguments()
def workspace = System.getenv('WORKSPACE')
def gav = parsing(workspace)

if (arguments.size() != 0 && arguments[0] == 'pull') {
    pull(gav)
}
else if (arguments.size() != 0 && arguments[0] == 'push') {
    push(gav)
}
