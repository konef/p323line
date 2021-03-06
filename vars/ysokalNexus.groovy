@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.2' )
import groovyx.net.http.RESTClient
import org.apache.http.entity.*
import hudson.model.*

    
@NonCPS
def call(name, cmd, repo) {
    server = "http://EPBYMINW3088/nexus/repository/"
    groupId = "pipeline"
    username = 'Jenkins'
    password = 'Yauheni1601'

    f_name = name.split('.tar')[0]
    artifactId = f_name.split('-')[1]
    version = f_name.split('-')[-1]

    request = new RESTClient(server)
    request.auth.basic("${username}", "${password}")
    println("name - $name, cmd - $cmd, repo - $repo")
    if (cmd == "push") {
        println("Call upload")
        request.encoder.'application/zip' = this.&encodeZipFile
        respons_up = request.put(
                uri: "${server}${repo}/${groupId}/${artifactId}/${version}/${artifactId}-${version}.tar.gz",
                body: new File(name),
                requestContentType: 'application/zip')
        assert respons_up.status == 201
    }
    else if (cmd == "pull") {
        println("Call download")
        respons_down = request.get(
                uri: "${server}${repo}/${groupId}/${artifactId}/${version}/${artifactId}-${version}.tar.gz")
        def folder = new File( './downloads' )
       if ( !folder.exists()){
            folder.mkdir()
        }
        new File( folder, "${name}") << respons_down.data
        assert respons_down.status == 200
    }
    else {
        println("This script is supported only 'pull' or 'push' commands!")
    }
}

def encodeZipFile(Object data) throws UnsupportedEncodingException {
    def entity = new FileEntity((File) data, "application/zip")
    entity.setContentType("application/zip")
    return entity
}
