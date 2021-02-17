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
      Initialization Tool V1.0 (SNAPSHOT)
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
ANSI_BOLD=$(tput bold)
NORMAL=$(tput sgr0)
# Init variables
BANZAI_SH_L="/usr/local/bin/banzai"
JAR_FILE_NAME="$(ls ../target | grep "jar-with-dependencies.jar")"
JAR_LOCATION_SM="/usr/local/bin/${JAR_FILE_NAME}"

function autogenerateproperties() {
  read AUTO_SERVER_PROP"?  Do you want to generate a server configuration property file automatically? [Y/N] "
  if [[ $AUTO_SERVER_PROP == "Y" ]] || [[ $AUTO_SERVER_PROP == "y" ]] || [[ $AUTO_SERVER_PROP == "yes" ]]; then
    cd ..
    cd src
    cd main
    cd resources
    read NAME"? Enter the name for the .properties file (e.g app.properties): "
    if [[ -f "$NAME" ]]; then
       printf "  ${ANSI_RED}${NAME} already exists. ${ANSI_NC}\n"
       exit 1
    fi
    read PORT"? Enter port number for the server to run on (e.g. 8091): "
    read HOST"? Enter host for the server to run on (e.g. localhost or 0.0.0.0): "
    SERVERNAME="Banzai"
    read WEBROOT"? Enter webroot for the server to run on (e.g. www): "

    touch $NAME;
    {
      echo "server.port=$PORT"
      echo "server.host=$HOST"
      echo "server.name=$SERVERNAME"
      echo "server.webroot=$WEBROOT"
    } >> $NAME
    WORKDIR=$(pwd)
    printf "  ${ANSI_WHITE}Properties file created in ${WORKDIR}/{$NAME} \n"
  fi
}

# trap ctrl-c and call ctrl_c()
trap ctrl_c INT
function ctrl_c() {
  echo ""
  printf "  ${ANSI_RED}The installation process has been cancelled. You may need to run the installation utility again to continue.${ANSI_NC}\n"
  echo ""
  exit 1
}

if [ -L "$BANZAI_SH_L" ]; then
  printf "  ${ANSI_RED}Banzai has been already installed!${ANSI_NC}\n"
  printf "  ${ANSI_RED}Please delete $BANZAI_SH_L, $JAR_LOCATION_SM to start again.${ANSI_NC}\n"
  echo ""
  exit 1
fi


read SYMLINK"?  Do you want to create a symbolic link to banzai.sh in ${ANSI_BOLD}/usr/local/bin/banzai?${ANSI_NORMAL} [Y/N] "
if [[ $SYMLINK == "Y" ]] || [[ $SYMLINK == "y" ]] || [[ $SYMLINK == "yes" ]]; then
  printf "  ${ANSI_CYAN}Creating a symbolic link for banzai.sh to ${ANSI_BOLD}/usr/local/bin/banzai${ANSI_NORMAL}${ANSI_NC}\n"
  echo "  Working directory: $(pwd)"
  if [[ -L "$BANZAI_SH_L" ]]; then
    printf "  ${ANSI_RED}Banzai is already symlinked${ANSI_NC}\n"
    exit 1
  fi
  chmod 755 "$(pwd)/banzai.sh"
  ln -s "$(pwd)/banzai.sh" "/usr/local/bin/banzai"
  printf "  ${ANSI_GREEN}Symbolic link created!${ANSI_NC}\n"
  read COPYJAR"?  Do you want to copy the Banzai jar file to ${ANSI_BOLD}/usr/local/bin${ANSI_NORMAL} ? [Y/N] "
  if [[ $COPYJAR == "Y" ]] || [[ $COPYJAR == "y" ]] || [[ $COPYJAR == "yes" ]]; then
    printf "  ${ANSI_PURPLE}Copying the jar file to /usr/local/bin/${ANSI_NC}\n"
    if [[ -f "$JAR_LOCATION_SM" ]]; then
       printf "  ${ANSI_RED}JAR file already exists!${ANSI_NC}\n"
       exit 1
    else
      sudo cp "../target/$JAR_FILE_NAME" "$JAR_LOCATION_SM"
      printf "  ${ANSI_GREEN}Done!${ANSI_NC}\n"
    fi
  fi

fi

echo ""
echo "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
printf "  ${ANSI_CYAN}${ANSI_BOLD}Installation completed.${ANSI_NORMAL}${ANSI_NC}\n"
printf "  ${ANSI_CYAN}You can run Banzai server with banzai [options].${ANSI_NC}\n"
echo "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
echo ""






