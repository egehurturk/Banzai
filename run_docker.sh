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
      Docker Build Tool
      ~Ege Hurturk~
"

# ANSI COLORS
ANSI_RED="\033[0;31m"
ANSI_GREEN="\033[0;32m"
ANSI_BLUE="\033[0;34m"
ANSI_PURPLE="\033[0;35m"
ANSI_CYAN="\033[0;36m"
ANSI_WHITE="\033[1;37m"
ANSI_YELLOW="\033[1;33m"
ANSI_NC="\033[0m" # no color
ANSI_BOLD=$(tput bold 2> /dev/null) # bold
NORMAL=$(tput sgr0 2> /dev/null)  # escape bold

# Build up docker and run
# docker exec -ti  wizardly_tesla sh
# docker run -v $(PWD):/banzai -p 9091:8080 -it egeh/banzai:1.0-SNAPSHOT
# docker build -t egeh/banzai:1.0-SNAPSHOT .

# Build the docker image with the tag egeh/banzai:1.0-SNAPSHOT
# Accept the absolute path of directory of the contents to be served (e.g. /Users/po/dev/dproject)
# Accept an environment file (.env) file as argument (e.g. Users/po/dev/dproject/.env)
# docker run -v $absolute_path:$(basename $absolte_path)  --env-file=$env_file -p $port:$port -it egeh/banzai:1.0-SNAPSHOT

trap ctrl_c INT
function ctrl_c() {
  echo ""
  printf "  ${ANSI_RED}The installation process has been cancelled. You may need to run the installation utility again to continue.${ANSI_NC}\n"
  echo ""
  exit 1
}

function normalfunc() {
  chmod 755 ./scripts/parser.sh
  CONFIGFILE="$1/server.properties"
  printf "  ${ANSI_cyan}Parsing $1/server.properties... ${ANSI_NC}\n"
  PORT=$(./scripts/parser.sh "server.port" "$CONFIGFILE" )
  NAME=$(./scripts/parser.sh "server.name" "$CONFIGFILE" )
  HOST=$(./scripts/parser.sh "server.host" "$CONFIGFILE" )
  WEBR=$(./scripts/parser.sh "server.webroot" "$CONFIGFILE")
  DEB=$(./scripts/parser.sh "debug" "$CONFIGFILE" )
  PORT=${PORT:=9090}
  HOST=${HOST:=0.0.0.0}
  DEB=${DEB:=false}
  WEBR=${WEBR:="$1/www"}
  NAME=${NAME:="Banzai"}
  printf "  ${ANSI_CYAN}Creating environment file in $1/.env \n${ANSI_NC}"
  touch $1/.env
  {
    echo "SERVER_NAME=$NAME"
    echo "SERVER_WEBROOT=$WEBR"
    echo "SERVER_PORT=$PORT"
    echo "SERVER_HOST=$HOST"
    echo "SERVER_DEBUG=$DEBUG"
    echo "SERVER_CONFIG_PATH=/$(basename $1)/server.properties"
  } > "$1/.env"
  printf "  ${ANSI_CYAN}Environment file created in $1/.env \n${ANSI_NC}"
}

function autogenerateproperties() {
  read -p "${ANSI_BOLD}Enter port number for the server to run on (e.g. 8091): ${NORMAL}" PORT
  read -p "${ANSI_BOLD}Enter host for the server to run on (e.g. localhost or 0.0.0.0): ${NORMAL}" HOST
  SERVERNAME="Banzai"
  read -p "${ANSI_BOLD}Enter webroot for the server to run on (e.g. www): ${NORMAL}" WEBROOT
  read -p "${ANSI_BOLD}Enter debug mode (true/false): ${NORMAL}" DEBUG
  PORT=${PORT:=9090}
  HOST=${HOST:=0.0.0.0}
  DEBUG=${DEBUG:=false}
  WEBROOT=${WEBROOT:="$1/www"}

  touch $1/server.properties
  {
    echo "server.port=$PORT"
    echo "server.host=$HOST"
    echo "server.name=$SERVERNAME"
    echo "server.webroot=$WEBROOT"
    echo "debug=$DEBUG"
   } > "$1/server.properties"
  printf "  ${ANSI_CYAN}Properties file created in $1/server.properties \n${ANSI_NC}"
  touch $1/.env
  {
    echo "SERVER_NAME=$SERVERNAME"
    echo "SERVER_WEBROOT=$HOST"
    echo "SERVER_PORT=$PORT"
    echo "SERVER_HOST=$WEBROOT"
    echo "SERVER_DEBUG=$DEBUG"
    echo "SERVER_CONFIG_PATH=/$(basename $1)/server.properties"
  } > "$1/.env"
  printf "  ${ANSI_CYAN}Environment file created in $1/.env \n${ANSI_NC}"
}


project_dir=""
while [[ "$#" -gt 0 ]]; do
    case $1 in
        -d|--projdir) project_dir="$2"; shift ;;
        *) echo "Unknown parameter passed: $1" ;;
    esac
    shift
done

env_file="$project_dir/.env"

if [[ -z $project_dir ]]; then
  echo "Project directory should be passed in as an argument:"
  echo "  ./run_docker.sh -d <path>"
  exit 1
fi


read -p "Do you want to autogenerate server configuration file [Y/n]: " AUTOGENERATE
if [[ $AUTOGENERATE == "Y" ]] || [[ $AUTOGENERATE == "y" ]] || [[ $AUTOGENERATE == "yes" ]]; then
  autogenerateproperties $project_dir
else
  normalfunc $project_dir
fi

PRODUCTION=1


if [[ $PRODUCTION -ne 1 ]]; then
  echo "[DEBUG run_docker.sh] Port number: $PORT"
  echo "[DEBUG run_docker.sh] Web root: $WEBROOT"
  echo "[DEBUG run_docker.sh] Environment file: $env_file"
  echo "[DEBUG run_docker.sh] Project directory (local): $project_dir"
fi

printf "  ${ANSI_GREEN}Building Dockerfile with the tag egeh/banzai:1.0-SNAPSHOT...${ANSI_NC}\n\n"
printf "  ${ANSI_GREEN}Building started!${ANSI_NC}\n"
docker build -t egeh/banzai:1.0-SNAPSHOT --build-arg server_port=$PORT .
printf "  ${ANSI_GREEN}Building ended!${ANSI_NC}\n\n"
printf "  ${ANSI_GREEN}Starting up the docker container with the volume to ${project_dir} in local machine [$PORT:$PORT] ${ANSI_NC}\n"
docker run -v $project_dir:"/$(basename $project_dir)" --env-file=$env_file -p $PORT:$PORT -it egeh/banzai:1.0-SNAPSHOT
