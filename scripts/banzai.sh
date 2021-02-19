#!/bin/bash
echo "
       ▄▄▄▄    ▄▄▄       ███▄    █ ▒███████▒ ▄▄▄       ██▓
      ▓█████▄ ▒████▄     ██ ▀█   █ ▒ ▒ ▒ ▄▀░▒████▄    ▓██▒
      ▒██▒ ▄██▒██  ▀█▄  ▓██  ▀█ ██▒░ ▒ ▄▀▒░ ▒██  ▀█▄  ▒██▒
      ▒██░█▀  ░██▄▄▄▄██ ▓██▒  ▐▌██▒  ▄▀▒   ░░██▄▄▄▄██ ░██░
      ░▓█  ▀█▓ ▓█   ▓██▒▒██░   ▓██░▒███████▒ ▓█   ▓██▒░██░
      ░▒▓███▀▒ ▒▒   ▓▒█░░ ▒░   ▒ ▒ ░▒▒ ▓░▒░▒ ▒▒   ▓▒█░░▓
      ▒░▒   ░   ▒   ▒▒ ░░ ░░   ░ ▒░░░▒ ▒ ░ ▒  ▒   ▒▒ ░ ▒ ░
       ░    ░   ░   ▒      ░   ░ ░ ░ ░ ░ ░ ░  ░   ▒    ▒ ░
       ░            ░  ░         ░   ░ ░          ░  ░ ░
            ░                      ░

      Banzai Server, V1.0 (SNAPSHOT) -
      ~Ege Hurturk~
"

ANSI_RED="\033[0;31m"
ANSI_CYAN="\033[0;36m"
ANSI_NC="\033[0m" # no color
config=""
np=""
proejctname=""
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )" # full working directory
currentDir=`which banzai`
symbolicDir="/usr/local/bin/banzai"

function autogenerateproperties() {
  if [[ $1 == "Y" ]] || [[ $1 == "y" ]] || [[ $1 == "yes" ]]; then
    mkdir "$DIR/$2/src/main/resources"
    read NAME"? Enter the name for the .properties file (e.g app.properties): "
    if [[ -f "$NAME" ]]; then
       printf "  ${ANSI_RED}${NAME} already exists. ${ANSI_NC}\n"
       exit 1
    fi
    read PORT"? Enter port number for the server to run on (e.g. 8091): "
    read HOST"? Enter host for the server to run on (e.g. localhost or 0.0.0.0): "
    SERVERNAME="Banzai"
    read WEBROOT"? Enter webroot for the server to run on (e.g. www): "
    touch "$DIR/$2/src/main/resources/$NAME";
    {
      echo "server.port=$PORT"
      echo "server.host=$HOST"
      echo "server.name=$SERVERNAME"
      echo "server.webroot=$WEBROOT"
     } >> "$DIR/$2/src/main/resources/$NAME"
    WORKDIR=$(pwd)
    printf "  ${ANSI_WHITE}Properties file created in ${WORKDIR}/{$NAME} \n"
  else
    printf "  ${ANSI_WHITE}You need to create a configuration file under resources directory in src/main/ ${WORKDIR}/{$NAME} \n"
  fi
}

while [[ "$#" -gt 0 ]]; do
    case $1 in
        -p|--port) port="$2"; shift ;;
        -h|--host) host="$2"; shift ;;
        -w|--webroot) webroot="$2"; shift ;;
        -b|--backlog) backlog="$2"; shift ;;
        -n|--name) name="$2"; shift ;;
        -c|--config) config="$2"; shift ;;
        newproject) np="n";;;
        -t|--pname) projectname="$2"; shift ;;
        *) echo "Unknown parameter passed: $1" ;;
    esac
    shift
done
if [[ "$np" != "" && "$projectname" != "" ]]; then
  printf "  ${ANSI_CYAN}Creating new maven quickstart project: ${projectname}... in ${DIR} ${ANSI_NC}\n"
  read SUBDOM"?  Enter subdomain (e.g. org) "
  read COMNAME"?  Enter domain name (e.g. example) "
  printf "  ${ANSI_CYAN}Building maven... ${ANSI_NC}\n"
  mvn archetype:generate -DgroupId="$SUBDOM.$COMNAME" -DartifactId="$projectname" -DarchetypeArtifactId=maven-archetype-quickstart -DarchetypeVersion=1.4 -DinteractiveMode=false
  pwd
  read AUTO_SERVER_PROP"?  Do you want to generate a server configuration property file automatically (default file)? [Y/N] "
  autogenerateproperties $AUTO_SERVER_PROP $projectname
  printf "  ${ANSI_GREEN}Done!${ANSI_NC}\n"


else
  if [[ "$config" == "" ]]; then # & not port == "" ... (continue this)
    if [[  $(ls | grep "target") == "target" ]]; then # check if pwd is equal to project_root/scripts
      java -jar "target/BanzaiServer-1.0-SNAPSHOT-jar-with-dependencies.jar" --port $port --host $host --webroot $webroot --name $name --backlog $backlog
    else
      printf "  ${ANSI_RED}You need to be in the same directory with the project${ANSI_NC}\n"
    fi

  else
     if [[  $(ls | grep "target") == "target" ]]; then # check if pwd is equal to project_root/scripts
       java -jar "target/BanzaiServer-1.0-SNAPSHOT-jar-with-dependencies.jar" --config $config
    else
      printf "  ${ANSI_RED}You need to be in the same directory with the project${ANSI_NC}\n"
    fi
  fi
fi;







# TODO: load local banzai jar file from /opt/banzai to newly created project

