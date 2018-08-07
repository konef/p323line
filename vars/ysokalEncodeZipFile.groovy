import org.apache.http.entity.*

@NonCPS
def call(Object data) throws UnsupportedEncodingException {
    def entity = new FileEntity((File) data, "application/zip")
    entity.setContentType("application/zip")
    return entity
}
