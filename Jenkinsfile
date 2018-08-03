student = "knovichuk"

node() {
        
        try {
            stage('Preparation (Checking out)') {
                checkout([$class: 'GitSCM',
                          branches: [[name: "*/${student}"]],
                          doGenerateSubmoduleConfigurations: false,
                          extensions: [],
                          submoduleCfg: [],
                          userRemoteConfigs:
                                  [[url: 'https://github.com/MNT-Lab/p323line']]])
            }
        }
        catch (Exception ex){
            println("Checkout failed")
        }
}