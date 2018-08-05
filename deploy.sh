#! /bin/bash

home_dir=/home/vagrant/Jenkins

tar -xvzf $home_dir/$1 -C $home_dir/

if [[ -f $home_dir/helloworld-ws.war_old ]]
   then 
      sudo mv $home_dir/helloworld-ws.war_old $home_dir/helloworld-ws.war_old_v2
fi

if [[ -f /opt/tomcat/webapps/helloworld-ws.war ]] 
   then
      sudo mv /opt/tomcat/webapps/helloworld-ws.war $home_dir/helloworld-ws.war_old
fi

sudo mv $home_dir/helloworld-ws.war /opt/tomcat/webapps/
sleep 5

curl http://EPBYMINW3088/tomcat/helloworld-ws/index.html | grep "Build Number: #$2"
a=$?
curl -I http://EPBYMINW3088/tomcat/helloworld-ws/ | awk '{print $2}' | head -n 1 | grep 200
b=$?

if [ "$a" -eq 0 ] && [ "$b" -eq 0 ] 
then
  [ -f $home_dir/helloworld-ws.war_old_v2 ] && sudo rm -rf $home_dir/helloworld-ws.war_old_v2
  echo "OK. $a,$b"
  exit 0
else
  if [ -f $home_dir/helloworld-ws.war_old ]
  then
      sudo rm -rf /opt/tomcat/webapps/helloworld-ws.war
      sudo mv $home_dir/helloworld-ws.war_old /opt/tomcat/webapps/helloworld-ws.war
  fi
  [ -f $home_dir/helloworld-ws.war_old_v2 ] && sudo mv $home_dir/helloworld-ws.war_old_v2 $home_dir/helloworld-ws.war_old
  echo "FAIL - return to previous version. $a,$b"
  exit 1
fi

rm $home_dir/$1
