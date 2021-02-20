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
ANSI_BOLD=$(tput bold) # bold
NORMAL=$(tput sgr0)  # escape bold

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )" # full working directory (e.g. here -> /Users/egehurturk/Development/BanzaiServer/scripts)
BINARY_PATH="/usr/local/bin" # place to store banazi executable
BANZAI_SH_L="$BINARY_PATH/banzai" # binary banzai path (/usr/local/bin/banzai)
JAR_FILE_NAME="$(ls $(dirname $DIR)/target | grep "jar-with-dependencies.jar")" # jar file name (only the name w/ extension)
JAR_LOCATION_NM="$(dirname $DIR)/target/$JAR_FILE_NAME" # jar file location normal (in the project directory)
JAR_LOCATION_SM="/opt/banzai/${JAR_FILE_NAME}"
JAR_OTHER="/etc/banzai/${JAR_FILE_NAME}"


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
  printf "  ${ANSI_RED}Please delete $BANZAI_SH_L to start again.${ANSI_NC}\n"
  echo ""
  exit 1
elif [ -f "$JAR_LOCATION_SM" ]; then
  printf "  ${ANSI_RED}Banzai has been already installed!${ANSI_NC}\n"
  printf "  ${ANSI_RED}Please delete $JAR_LOCATION_SM to start again.${ANSI_NC}\n"
  echo ""
  exit 1
elif [ -f "/var/log/banzai" ]; then
  printf "  ${ANSI_RED}Banzai has been already installed!${ANSI_NC}\n"
  printf "  ${ANSI_RED}Please delete /var/log/banzai to start again.${ANSI_NC}\n"
  echo ""
  exit 1
fi


read -p "Creating symbolic link to banzai.sh in ${ANSI_BOLD}/usr/local/bin/banzai. Do you wish to continue?${ANSI_NORMAL} [Y/N] " SYMLINK
if [[ $SYMLINK == "Y" ]] || [[ $SYMLINK == "y" ]] || [[ $SYMLINK == "yes" ]]; then
  printf "  ${ANSI_CYAN}Creating a symbolic link for banzai.sh to ${ANSI_BOLD}/usr/local/bin/banzai${ANSI_NORMAL}${ANSI_NC}\n"
  if [[ -L "$BANZAI_SH_L" ]]; then
    printf "  ${ANSI_RED}Banzai is already symlinked${ANSI_NC}\n"
    exit 1
  fi
  chmod 755 "$DIR/banzai.sh"
  ln -s "$DIR/banzai.sh" "$BANZAI_SH_L"
  printf "  ${ANSI_GREEN}Symbolic link created!${ANSI_NC}\n"

  printf "  ${ANSI_PURPLE}Copying the jar file to $JAR_LOCATION_SM${ANSI_NC}\n"
  if [[ -f "$JAR_LOCATION_SM" ]]; then
     printf "  ${ANSI_RED}JAR file already exists!${ANSI_NC}\n"
     exit 1
  else

    if [ ! -d "$(dirname $(dirname $JAR_LOCATION_SM))" ]; then
      printf "  ${ANSI_RED}$(dirname $(dirname $JAR_LOCATION_SM)) does not exists. Using the other default directory $JAR_OTHER ${ANSI_NC}\n"
       echo "Copying requires sudo (enter machine password) "
       sudo mkdir "/etc/banzai"
       sudo cp "$JAR_LOCATION_NM" "$JAR_OTHER"
       printf "  ${ANSI_GREEN}Done!${ANSI_NC}\n"
    else
      echo "Copying requires sudo (enter machine password) "
      sudo mkdir "/opt/banzai"
      sudo cp "$JAR_LOCATION_NM" "$JAR_LOCATION_SM"
      printf "  ${ANSI_GREEN}Done!${ANSI_NC}\n"
    fi
  fi

  printf "  ${ANSI_PURPLE}Creating banzai directory in /var/log/banzai ${ANSI_NC}\n"
  if [[ -d "/var/log/banzai" ]]; then
    printf "  ${ANSI_RED}Banzai is already installed${ANSI_NC}\n"
    exit 1
  fi
  echo "Creating log directory requires sudo (enter machine password) "
  sudo mkdir "/var/log/banzai"
  printf "  ${ANSI_GREEN}Done!${ANSI_NC}\n"
fi

echo -e ""
echo -e "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
echo -e "  ${ANSI_YELLOW}${ANSI_BOLD}Installation completed.${ANSI_NORMAL}${ANSI_NC}"
echo -e "  ${ANSI_CYAN}${ANSI_BOLD}Summary: ${ANSI_NORMAL}${ANSI_NC}"
echo -e "  ${ANSI_CYAN}${ANSI_BOLD}\t\t \u2b95  Moved the banzai executable to /usr/local/bin ${ANSI_NORMAL}${ANSI_NC}"
echo -e "  ${ANSI_CYAN}${ANSI_BOLD}\t\t \u2b95  Copied application JAR file to /opt/banzai/{jar} or /etc/banzai/{jar} depending on your computer ${ANSI_NORMAL}${ANSI_NC}"
echo -e "  ${ANSI_CYAN}${ANSI_BOLD}\t\t    If /opt/ does not exists on your machine, the jar file is copied into /etc/banzai ${ANSI_NORMAL}${ANSI_NC}"
echo -e "  ${ANSI_CYAN}${ANSI_BOLD}\t\t \u2b95  Created a directory under /var/log/banzai for global logs ${ANSI_NORMAL}${ANSI_NC}"
echo -e "  ${ANSI_PURPLE}You can run Banzai server with banzai [options].${ANSI_NC}"
echo -e "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
echo -e ""

# -->> /var/log/banzai: log directory
# -->> /opt/banzai: banzai jar files
# -->> /usr/local/bin/banzai --> banzai executable
