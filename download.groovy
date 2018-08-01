@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7')
import groovyx.net.http.*
import org.apache.http.entity.*

	
void push() {
  	def pom = new XmlSlurper().parse(System.getenv("WORKSPACE") +'/pom.xml')
  	def restClient =  new RESTClient('http://192.168.1.102:8081/repository/maven-archive/')
	def workspace = System.getenv("WORKSPACE")
  	def build = System.getenv("BUILD_NUMBER")
	restClient.auth.basic 'jenkins', 'jenkins'
	restClient.encoder.'application/zip' = this.&encodeZipFile
  
        def response = restClient.put(path: 'http://192.168.1.102:8081/repository/maven-archive/test_group_id/' + 
                                System.getenv("ar") + '/' + 
                                System.getenv("ver") + '.' + build +'/' + 
                                System.getenv("ar") + '-'+ System.getenv("ver") + '.' + build + '.tar.gz', 
                                body: new File(workspace + '/target/' + 
                                               System.getenv("ar") + '-' + 
                                               System.getenv("ver") + '.' + build + '.tar.gz'),
                                requestContentType: 'application/zip')
}


def encodeZipFile(Object data) throws UnsupportedEncodingException {
	def entity = new FileEntity((File) data, 'application/zip')
	entity.setContentType('application/zip')
	return entity
}


void pull() {
  
  	def pom = new XmlSlurper().parse(System.getenv("WORKSPACE") +'/pom.xml')
	def gr = pom.groupId
  	println gr
	//println pom.artifactId
	//println pom.version
	//ver = ver.take(4)
   	// num = System.getenv("BUILD_NUMBER")
  	def restClient =  new RESTClient('http://192.168.1.102:8081/repository/maven-archive/')
	def workspace = System.getenv("WORKSPACE")
  
        def remoteUrl = 'http://192.168.1.102:8081/repository/maven-archive/' + gr +
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

if (arguments.size() != 0 && System.getenv("ACTION") == 'pull' ){
    pull()
}
else  if (arguments.size() != 0 && System.getenv("ACTION") == 'push' ){
    push()
}
