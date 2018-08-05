@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.2' )
import groovyx.net.http.RESTClient
import org.apache.http.entity.*
import hudson.model.*


CliBuilder cli = new CliBuilder(usage: 'groovy archive_loader.groovy [-h] -n {name_of_artifact} -c {command} -r {repo}')
cli.h(args: 1, argName: 'help', 'Show usage information',required: false)
cli.n(args: 1, argName: 'name_of_artifact', 'Artifact\'s name what created after building', required: true)
cli.c(args: 1, argName: 'command', 'push/pull a file', required: true)
cli.r(args: 1, argName: 'repo', 'repository where artifacts is storied', required: true)

argument = cli.parse(args)
if (!argument) or (argument.h) {
    cli.usage()
    return
}

server = "http://EPBYMINW3088/nexus/repository/"
groupId = "pipeline"
username = 'Jenkins'
password = 'Yauheni1601'

name = argument.n.split('.tar')[0]
artifactId = name.split('-')[1]
version = name.split('-')[-1]
command = [push: {->upload()}, pull: {->download()}]

request = new RESTClient(server)
request.auth.basic("${username}", "${password}")


def encodeZipFile( Object data ) throws UnsupportedEncodingException {
    def entity = new FileEntity( (File) data, "application/zip" )
    entity.setContentType( "application/zip" )
    return entity
}

def upload() {
        request.encoder.'application/zip' = this.&encodeZipFile
        respons_up = request.put(
                uri: "${server}${argument.r}/${groupId}/${artifactId}/${version}/${artifactId}-${version}.tar.gz",
                body: new File(argument.n),
                requestContentType: 'application/zip')
        assert respons_up.status == 201
}

def download() {
        respons_down = request.get(
                uri: "${server}${argument.r}/${groupId}/${artifactId}/${version}/${artifactId}-${version}.tar.gz")
        new File("./downloads/${argument.n}") << respons_down.data
        assert respons_down.status == 200
}

if (argument.c) {
    command[argument.c]()
}