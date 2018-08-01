@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7')
import groovyx.net.http.*
import org.apache.http.entity.*

	
void push() {
  	def pom = new XmlSlurper().parse(System.getenv("WORKSPACE") +'/helloworld-ws/pom.xml')
	println pom
	println pom.groupId
	println pom.artefactId
	println pom.version
	def gr = pom.groupId
	def ar = pom.artefactId
	def ver = pom.version
  	def restClient =  new RESTClient('http://epbyminw7425/nexus/repository/maven-archive/')
	def workspace = System.getenv("WORKSPACE")
  	def build = System.getenv("BUILD_NUMBER")
	restClient.auth.basic 'jenkins', 'jenkins'
	restClient.encoder.'application/zip' = this.&encodeZipFile
  
        def response = restClient.put(path: 'http://epbyminw7425/nexus/repository/maven-archive/test_group_id/' + 
                                ar + '/' + 
                                ver + '.' + build +'/' + 
                                ar + '-'+ ver + '.' + build + '.tar.gz', 
                                body: new File(workspace + '/helloworld-ws/target/' + 
                                               ar + '-' + 
                                               ver + '.' + build + '.war'),
                                requestContentType: 'application/zip')
}


def encodeZipFile(Object data) throws UnsupportedEncodingException {
	def entity = new FileEntity((File) data, 'application/zip')
	entity.setContentType('application/zip')
	return entity
}


void pull() {  
  	def pom = new XmlSlurper().parse(System.getenv("WORKSPACE") +'/helloworld-ws/pom.xml')
	def gr = pom.groupId
  	def restClient =  new RESTClient('http://epbyminw7425/nexus/repository/maven-archive/')
	def workspace = System.getenv("WORKSPACE")
  
        def remoteUrl = 'http://epbyminw7425/nexus/repository/maven-archive/' + gr +
        '/'+ System.getenv("ar") +
        '/'+ System.getenv("ver") + '.' + System.getenv("INPUT_BUILD") + 
        '/' + System.getenv("ar") + '-'+ System.getenv("ver") + '.' + System.getenv("INPUT_BUILD") + '.war'
        def url = new URL(remoteUrl)
        def authString = "jenkins:jenkins".getBytes().encodeBase64().toString()
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      
        conn.setRequestProperty( "Authorization", "Basic ${authString}" )
        new File(workspace + '/project.war').withOutputStream { out ->
            out << conn.inputStream
        }
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
