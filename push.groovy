@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.6')

import groovyx.net.http.*
import java.util.regex.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
def str = System.getenv("art_name")
def str2 = System.getenv("BUILD_NUMBER")

def upload = new HTTPBuilder ("http://epbyminw1766/nexus/repository/project-releases/pipeline/1/1/pipeline-ypapkou-${str2}.tar.gz")
upload.headers[ 'Authorization' ] = "Basic " + "nexus-service-user:nexus-service-user".getBytes('iso-8859-1').encodeBase64()
upload.request(Method.PUT, ContentType.BINARY) {
    body = new File ("pipeline-ypapkou-${str2}.tar.gz").bytes
    response.success = { resp, data ->
    }
    response.failure = { resp, json ->
        println("Unexpected error: ${resp.statusLine.statusCode} : ${resp.statusLine.reasonPhrase}")
    }
}