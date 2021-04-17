#!/bin/bash

ANSI_RED="\033[0;31m"
ANSI_GREEN="\033[0;32m"
ANSI_BLUE="\033[0;34m"
ANSI_PURPLE="\033[0;35m"
ANSI_CYAN="\033[0;36m"
ANSI_WHITE="\033[1;37m"
BRIGHT_RED="\033[31;1m"
BRIGHT_CYAN="\033[36;1m"
BRIGHT_YELLOW="\033[33;1m"
BRIGHT_GREEN="\033[32;1m"
BRIGHT_WHITE="\033[37;1m"
ANSI_YELLOW="\033[1;33m"
ANSI_NC="\033[0m" # no color
ANSI_BOLD=$(tput bold 2> /dev/null) # bold
NORMAL=$(tput sgr0 2> /dev/null)  # escape bold

get_latest_release() {
  curl --silent "https://api.github.com/repos/$1/releases/latest" | # Get latest release from GitHub api
    grep '"tag_name":' |                                            # Get tag line
    sed -E 's/.*"([^"]+)".*/\1/'                                    # Pluck JSON value
}

read_var() {
    VAR=$(grep $1 $2 | xargs)
    IFS="=" read -ra VAR <<< "$VAR"
    echo ${VAR[1]}
}


VERSIONREMOTE=$(get_latest_release "egehurturk/banzai")

FILEDIR="$HOME/.banzai/"
FILE="$HOME/.banzai/banzai.env"
VAR="RELEASE_VERSION"

VERSIONLOCAL_INSTALLED=$(read_var $VAR $1)

if [[ ! -d $FILEDIR ]]; then
    mkdir $FILEDIR
fi

if [[ ! -f $FILE ]]; then
    touch $FILE
    chmod 666 $FILE
    echo "RELEASE_VERSION=$VERSIONLOCAL_INSTALLED" > $FILE
fi

VERSIONLOCAL=$(read_var $VAR $FILE)
 
if [[ $VERSIONREMOTE ==  $VERSIONLOCAL ]] || [[ $VERSIONREMOTE == "" ]]; then
    printf "  ${BRIGHT_GREEN} Everything is up to date! \n${ANSI_NC}"
    exit
else
    printf "  {Banzai $VERSIONLOCAL is installed on your machine; however, a newer version of Banzai ($VERSIONREMOTE) exists.}  \n\n\n"
    exit
fi


