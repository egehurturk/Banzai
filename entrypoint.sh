#!/bin/bash


# ANSI COLORS
ANSI_RED="\033[0;31m"
ANSI_GREEN="\033[0;32m"
ANSI_BLUE="\033[0;34m"
ANSI_PURPLE="\033[0;35m"
ANSI_CYAN="\033[0;36m"
ANSI_WHITE="\033[1;37m"
ANSI_YELLOW="\033[1;33m"
BRIGHT_RED="\033[31;1m"
ANSI_NC="\033[0m" # no color
ANSI_BOLD=$(tput bold 2> /dev/null) # bold
NORMAL=$(tput sgr0 2< /dev/null)  # escape bold

printf "\n"


printf "  ${ANSI_YELLOW}Starting up the server!${ANSI_NC}\n"

sleep 2 2> /dev/null
clear

java -jar "/banzai/target/BanzaiServer-1.0-SNAPSHOT-jar-with-dependencies.jar" --config "$SERVER_CONFIG_PATH"

