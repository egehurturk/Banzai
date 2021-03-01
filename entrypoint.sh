#!/bin/bash

# Build banzai with mvn clean package
# Copy jar to /opt/banzai/$jar
# Run /opt/banzai/$jar --config

# ANSI COLORS
ANSI_RED="\033[0;31m"
ANSI_GREEN="\033[0;32m"
ANSI_BLUE="\033[0;34m"
ANSI_PURPLE="\033[0;35m"
ANSI_CYAN="\033[0;36m"
ANSI_WHITE="\033[1;37m"
ANSI_YELLOW="\033[1;33m"
ANSI_NC="\033[0m" # no color
ANSI_BOLD=$(tput bold) # bold
NORMAL=$(tput sgr0)  # escape bold


BANZAI_LOCATION="/banzai" # Docker container banzai directory
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )" # full working directory in container ("/")
BINARY_PATH="/usr/local/bin" # place to store banzai executable
BANZAI_SYMLINK="$BINARY_PATH/banzai" # binary banzai path (/usr/local/bin/banzai)
JAR_FILE_NAME="$(ls "$BANZAI_LOCATION/target" | grep "jar-with-dependencies.jar")" # jar file name (only the name w/ extension)
JAR_LOCATION_NM="$BANZAI_LOCATION/target/$JAR_FILE_NAME" # jar file location normal (in the project directory)
JAR_LOCATION_COPY="/opt/banzai/${JAR_FILE_NAME}" # copy path Jar file
LOG_DIRECTORY="/var/log/banzai" # Logs directory for jar file
CONFIGURATION_DIRECTORY="/etc/banzai/config" # configuration directory

read -p "Heyyo: " Heyyo
exit 1

# trap ctrl-c and call ctrl_c()
trap ctrl_c INT
function ctrl_c() {
  echo ""
  printf "  ${ANSI_RED}The installation process has been cancelled. You may need to run the installation utility again to continue.${ANSI_NC}\n"
  echo ""
  exit 1
}

if [[ -L "$BANZAI_SYMLINK" || -f "$JAR_LOCATION_COPY" || -f "$LOG_DIRECTORY" ]]; then
  printf "  ${ANSI_RED}Banzai has been already installed!${ANSI_NC}\n"
  printf "  ${ANSI_RED}Please delete $BANZAI_SH_L, $JAR_LOCATION_COPY, $LOG_DIRECTORY to start again.${ANSI_NC}\n"
  echo ""
  exit 1
fi

# BANZAI CONFIGURATION FILE
printf "  ${ANSI_YELLOW}Creating and copying server configuration file to $CONFIGURATION_DIRECTORY${ANSI_NC}\n"
mkdir "/etc/banzai"
mkdir "$CONFIGURATION_DIRECTORY"

{
  echo "# Config your server from here"
  echo "server.port = $SERVER_PORT"
  echo "server.host = $SERVER_HOST"
  echo "server.name = $SERVER_NAME"
  echo "server.webroot = $SERVER_WEBROOT"
  echo "debug=$DEBUG"
} > "$CONFIGURATION_DIRECTORY/server.properties"

read -p "Creating symbolic link to banzai.sh in ${ANSI_BOLD}/usr/local/bin/banzai. Do you wish to continue?${ANSI_NORMAL} [Y/N] " SYMLINK
if [[ $SYMLINK == "Y" ]] || [[ $SYMLINK == "y" ]] || [[ $SYMLINK == "yes" ]]; then
  printf "  ${ANSI_CYAN}Creating a symbolic link for banzai.sh to ${ANSI_BOLD}/usr/local/bin/banzai${ANSI_NORMAL}${ANSI_NC}\n"
  if [[ -L "$BANZAI_SH_L" ]]; then
    printf "  ${ANSI_RED}Banzai is already symlinked${ANSI_NC}\n"
    exit 1
  fi
  chmod 755 "$BANZAI_LOCATION/scripts/banzai.sh"
  ln -s "$BANZAI_LOCATION/scripts/banzai.sh" "$BANZAI_SYMLINK"
  printf "  ${ANSI_GREEN}Symbolic link created!${ANSI_NC}\n"

  printf "  ${ANSI_PURPLE}Copying the jar file to $JAR_LOCATION_COPY${ANSI_NC}\n"
  if [[ -f "$JAR_LOCATION_COPY" ]]; then
     printf "  ${ANSI_RED}JAR file already exists!${ANSI_NC}\n"
     exit 1
  else
    mkdir "/opt/banzai"
    cp "$JAR_LOCATION_NM" "$JAR_LOCATION_COPY"
    printf "  ${ANSI_GREEN}Done!${ANSI_NC}\n"
  fi

  printf "  ${ANSI_PURPLE}Creating banzai directory in /var/log/banzai ${ANSI_NC}\n"
  if [[ -d "/var/log/banzai" ]]; then
    printf "  ${ANSI_RED}Banzai is already installed${ANSI_NC}\n"
    exit 1
  fi
  mkdir "/var/log/banzai"
  printf "  ${ANSI_GREEN}Done!${ANSI_NC}\n"
fi

echo -e ""
echo -e "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
echo -e "  ${ANSI_YELLOW}${ANSI_BOLD}Installation completed.${ANSI_NORMAL}${ANSI_NC}"
echo -e "  ${ANSI_CYAN}${ANSI_BOLD}Summary: ${ANSI_NORMAL}${ANSI_NC}"
echo -e "  ${ANSI_CYAN}${ANSI_BOLD}\t\t \u2b95  Moved the banzai executable to /usr/local/bin ${ANSI_NORMAL}${ANSI_NC}"
echo -e "  ${ANSI_CYAN}${ANSI_BOLD}\t\t \u2b95  Copied application JAR file to /opt/banzai/{jar} ${ANSI_NORMAL}${ANSI_NC}"
echo -e "  ${ANSI_CYAN}${ANSI_BOLD}\t\t \u2b95  Created a directory under /var/log/banzai for logs ${ANSI_NORMAL}${ANSI_NC}"
echo -e "  ${ANSI_PURPLE}You can run Banzai server with banzai [options].${ANSI_NC}"
echo -e "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
echo -e ""


read -p "JAJAJJAJ" j



# TODO: create a proper install.sh for docker
# TODO: Docker expose port should be the port configured on server.properties
# jar -> opt/banzai/$jar
# banzai -> /usr/local/bin/banzai
# html files -> /var/www/
# logs -> /var/log/banzai/
# FIXME: does docker cleans up the container everytime I tell it to run?