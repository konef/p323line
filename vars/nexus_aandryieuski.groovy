@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7')
import groovyx.net.http.RESTClient
import org.apache.http.entity.FileEntity
import hudson.model.*

def nexus(String server_url, String user, String passw, String repo, String f_name, String command) {
    
    version = f_name.split('.tar.gz')[0].split('-')[-1]
    groupid = artifactid = f_name.split("-${version}.tar.gz")[0]
    rest = new RESTClient(server_url)
    rest.auth.basic "${user}", "${passw}"

    if (command == 'push') {
        rest.encoder.'application/x-gzip' = this.&encodeZipFile
        resp = rest.put(
                path: "${repo}/${groupid}/${artifactid}/${version}/${f_name}",
                body: new File(f_name),
                requestContentType: 'application/x-gzip'
        )
        assert resp.status == 201
    } else if(command == 'pull'){
        resp = rest.get(
                path: "${server_url}${repo}/${groupid}/${artifactid}/${version}/${f_name}"

        )
        assert resp.status == 200
        new File("./${f_name}") << resp.data
    }

    
}

def encodeZipFile( Object data ) throws UnsupportedEncodingException {
    	def entity = new FileEntity( (File) data, "application/x-gzip" );
    	entity.setContentType( "application/x-gzip" );
    	return entity
}
