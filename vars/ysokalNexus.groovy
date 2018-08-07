@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.2' )
import groovyx.net.http.RESTClient
import org.apache.http.entity.*
import hudson.model.*@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.2' )
import groovyx.net.http.RESTClient
import org.apache.http.entity.*
import hudson.model.*


server = "http://EPBYMINW3088/nexus/repository/"
groupId = "pipeline"
username = 'Jenkins'
password = 'Yauheni1601'


@NonCPS
def call(name, cmd, repo) {

    f_name = name.split('.tar')[0]
    artifact = f_name.split('-')[1]
    ver = f_name.split('-')[-1]

    request = new RESTClient(server)
    request.auth.basic("${username}", "${password}")
    println("name - $name, cmd - $cmd, repo - $repo")
    command = [push: { -> upload(repo, artifact, ver, name) }, 
               pull: { -> download(repo, artifact, ver, name) }]
    command[cmd]
}

def upload(rep, artifactId, version, file_name) {
    println("Call upload")
    request.encoder.'application/zip' = this.&encodeZipFile
    respons_up = request.put(
            uri: "${server}${rep}/${groupId}/${artifactId}/${version}/${artifactId}-${version}.tar.gz",
            body: new File(file_name),
            requestContentType: 'application/zip')
    assert respons_up.status == 201
}

def download(rep, artifactId, version, file_name) {
    println("Call download")
    def folder = new File( './downloads' )
    if ( !folder.exists()){
        folder.mkdir()
    }
    respons_down = request.get(
            uri: "${server}${rep}/${groupId}/${artifactId}/${version}/${artifactId}-${version}.tar.gz")
                new File( folder, "${file_name}") << respons_down.data
                assert respons_down.status == 200
}

def encodeZipFile(Object data) throws UnsupportedEncodingException {
    def entity = new FileEntity((File) data, "application/zip")
    entity.setContentType("application/zip")
    return entity
}



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
    command = [push: {->upload()}, pull: {->download()}]
    
    def upload() {
        println("Call upload!")
        request.encoder.'application/zip' = this.&encodeZipFile
        respons_up = request.put(
                uri: "${server}${repo}/${groupId}/${artifactId}/${version}/${artifactId}-${version}.tar.gz",
                body: new File(name),
                requestContentType: 'application/zip')
        assert respons_up.status == 201
    }

    def download() {
        println("Call download!")
        def folder = new File( './downloads' )
        if ( !folder.exists()){
            folder.mkdir()
        }
        respons_down = request.get(
                uri: "${server}${repo}/${groupId}/${artifactId}/${version}/${artifactId}-${version}.tar.gz")
                    new File( folder, "${name}") << respons_down.data
                    assert respons_down.status == 200
    }

    def encodeZipFile(Object data) throws UnsupportedEncodingException {
        def entity = new FileEntity((File) data, "application/zip")
        entity.setContentType("application/zip")
        return entity
    }
    command[cmd]
}
