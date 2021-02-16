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
config="noconfig"
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
        *) echo "Unknown parameter passed: $1"; exit 1 ;;
    esac
    shift
done
echo "========================= SERVER STARTING ========================="


if [ "$config" = "noconfig" ]; then
  java -jar target/BanzaiServer-1.0-SNAPSHOT-jar-with-dependencies.jar --port $port --host $host --webroot $webroot --name $name --backlog $backlog
else
  cd /Users/egehurturk/Development/BanzaiServer
  java -jar target/BanzaiServer-1.0-SNAPSHOT-jar-with-dependencies.jar --config $config
fi







