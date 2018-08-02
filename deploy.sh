#! /bin/bash

FILE=$(ls -la /tmp/jenkins_tmp/*.tar.gz | awk '{print $9}' | awk -F'/' '{print $NF}' )

BUILD_NUMBER=$(echo $FILE | awk -F'-' '{print $NF}' | awk -F'.tar.gz' '{print $1}')
echo "Build Number ==> $BUILD_NUMBER"

[[ -d /tmp/jenkins_tmp/war ]] || mkdir /tmp/jenkins_tmp/war

cd /tmp/jenkins_tmp/war
tar xfz /tmp/jenkins_tmp/$FILE helloworld-ws.war 

[[ -f /tmp/jenkins_tmp/war/helloworld.war_old ]] &&  mv /tmp/jenkins_tmp/war/helloworld.war_old /tmp/jenkins_tmp/war/helloworld.war_old_old
[[ -f /opt/cd-proc/jboss/server/default/deploy/helloworld.war  ]] && mv /opt/cd-proc/jboss/server/default/deploy/helloworld.war /tmp/jenkins_tmp/war/helloworld.war_old
cp /tmp/jenkins_tmp/war/helloworld-ws.war /opt/cd-proc/jboss/server/default/deploy/helloworld.war

echo "Making of the backup and deploy new build version"

sleep 15

curl http://epbyminw7423/helloworld/status-page.html | grep "Build-Number:$BUILD_NUMBER"
a=$?
curl -I http://epbyminw7423/helloworld/ | awk '{print $2}' | head -n 1 | grep 200
b=$?

sleep 2

if [ "$a" -eq 0 ] && [ "$b" -eq 0 ];then
    rm -rf /tmp/jenkins_tmp/war/helloworld.war_old_old
    echo "OK. $a,$b"
    exit 0
else
    rm -rf /opt/cd-proc/jboss/server/default/deploy/helloworld.war
    sleep 2
    mv /tmp/jenkins_tmp/war/helloworld.war_old /opt/cd-proc/jboss/server/default/deploy/helloworld.war
    sleep 2
    mv /tmp/jenkins_tmp/war/helloworld.war_old_old /tmp/jenkins_tmp/war/helloworld.war_old
    echo "FAIL - return to previous version. $a,$b"
    exit 1
fi

