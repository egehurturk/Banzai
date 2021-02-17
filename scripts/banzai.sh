#!/bin/zsh
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
config=""
currentDir=`which banzai`
symbolicDir="/usr/local/bin/banzai"

while [[ "$#" -gt 0 ]]; do
    case $1 in
        -p|--port) port="$2"; shift ;;
        -h|--host) host="$2"; shift ;;
        -w|--webroot) webroot="$2"; shift ;;
        -b|--backlog) backlog="$2"; shift ;;
        -n|--name) name="$2"; shift ;;
        -c|--config) config="$2"; shift ;;
        *) echo "Unknown parameter passed: $1" ;;
    esac
    shift
done
if [[ "$config" == "" ]]; then
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







