#!/bin/bash


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

printf "\n"
printf "  ${ANSI_GREEN}Clean building Banzai...${ANSI_NC}\n"

mvn clean package

printf "  ${ANSI_GREEN}Clean build ended!${ANSI_NC}\n"

printf "  ${ANSI_YELLOW}Starting up the server!${ANSI_NC}"

java -jar "/banzai/target/BanzaiServer-1.0-SNAPSHOT-jar-with-dependencies.jar" --config "$SERVER_CONFIG_PATH"

