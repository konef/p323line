@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.6')

import groovyx.net.http.*
import java.util.regex.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*


def http = new HTTPBuilder("http://epbyminw1766/nexus/service/siesta/rest/beta/search?repository=project-releases&group=pipeline2&version=1")
http.headers['Authorization'] = "Basic " + "nexus-service-user:nexus-service-user".getBytes('iso-8859-1').encodeBase64()
http.request(Method.GET) {
    response.success = { resp, json ->
        download_url = json.items.assets.downloadUrl*.getAt(0)[0]       
    }
    response.failure = { resp, json ->
        
    }
}
def art_name = System.getenv("art_name")
println(art_name)

download_url="http://epbyminw1766/nexus/repository/project-releases/pipeline2/1/1/${art_name}"
def download = new HTTPBuilder(download_url)
download.headers['Authorization'] = "Basic " + "nexus-service-user:nexus-service-user".getBytes('iso-8859-1').encodeBase64()
download.request(Method.GET, ContentType.BINARY) {
    response.success = { resp, binary ->
        new File("${art_name}") << binary.bytes
    }
    response.failure = { resp, json ->       
    }
}
