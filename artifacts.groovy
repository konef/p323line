@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7')

import groovyx.net.http.RESTClient
import org.apache.http.entity.FileEntity

hostname="192.168.1.4:8081"
username="jenkins"
password="jenkins"
reponame="mvnrepo"
doJob()

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
