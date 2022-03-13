#!/usr/bin/env bash

set -eu -o pipefail

if [ "$EUID" -ne 0 ]
  then echo "Please run as root"
  exit
fi


SEED=$(openssl rand -hex 10)
USER="pamtest"
TOTP_LENGTH="6"
TOTP_WINDOW="30"
PAM_CONFIG="auth sufficient pam_oath.so debug usersfile=/etc/users.oath window=${TOTP_WINDOW}\n"

echo "HOTP/T${TOTP_WINDOW}/${TOTP_LENGTH}	${USER}	-	$SEED" > /etc/users.oath
chmod 600 /etc/users.oath
if [[ $(head -1 /etc/pam.d/su | grep -q auth; echo $?) -eq 1 ]]; then
  sed -i "1s|.*|${PAM_CONFIG}|" /etc/pam.d/su
fi
qrencode -t UTF8 "otpauth://totp/${USER}@$(hostname)?secret=$(printf $SEED | xxd -r -p | base32)"

# auth sufficient pam_oath.so debug usersfile=/etc/users.oath window=20
