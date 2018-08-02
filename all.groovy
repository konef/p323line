
@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7')
import groovyx.net.http.RESTClient
import org.apache.http.entity.*
import hudson.model.*
def encodeTarFile( Object data ) throws UnsupportedEncodingException {
   def entity = new FileEntity( (File) data, "application/tar.gz" );
   entity.setContentType( "application/tar.gz" );
   return entity
   }


def upload() {
   def WORKSPACE = System.getenv('WORKSPACE');
   def BUILD_NUMBER = System.getenv('BUILD_NUMBER')
   def repo = "maven-helloworld"
   def gav = parse_gav()
   def file = new File("pipeline-dzhukova-${BUILD_NUMBER}.tar.gz")
   def rest = new RESTClient( "http://EPBYMINW1969/nexus/repository/${repo}/")

node
        rest.auth.basic 'admin', 'admin'
        rest.encoder.'application/tar.gz' = this.&encodeTarFile
        rest.put(path: "${gav[1]}/${BUILD_NUMBER}/${gav[1]}-${BUILD_NUMBER}.tar.gz", body: file, requestContentType: 'application/tar.gz')

}
def parse_gav() {
def WORKSPACE = System.getenv('WORKSPACE');
        def pom = new XmlSlurper().parse(new File("${WORKSPACE}/helloworld-ws/pom.xml"))
        def gavs = []
        gavs.add(pom.parent.groupId)
        gavs.add(pom.artifactId)
        gavs.add(pom.parent.version)
        return gavs
}
def download() {
   def gav = parse_gav()
   def BUILD_NUMBER = System.getenv('BUILD_NUMBER')
   def repo = "maven-helloworld"
   def rest = new RESTClient( "http://EPBYMINW1969/nexus/repository/${repo}")
        rest.auth.basic 'admin', 'admin'
        def resp = rest.get( path:  "${gav[1]}/${BUILD_NUMBER}/${gav[1]}-${BUILD_NUMBER}.tar.gz")
   new File("./app.tar.gz") << resp.data
}

def cli = new CliBuilder(usage:'ls')
import org.apache.commons.cli.Option
cli.with
{
   h(longOpt: 'help', 'Usage Information', required: false)
   c(longOpt: 'option', 'push/pull', args: 1, required: true)
}

def options = cli.parse(args)
println options.c
if (options.c=='pull') {   
        download()
}
if (options.c=='push') {
        upload()
}

