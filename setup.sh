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
USER_PASS="500000"

# Create user
echo "Create user $USER..."
useradd -m -s /bin/bash -p $(openssl passwd -1 $USER_PASS) pamtest || true

# Configure PAM
echo "Configure PAM..."
echo "HOTP/T${TOTP_WINDOW}/${TOTP_LENGTH}	${USER}	-	$SEED" > /etc/users.oath
chmod 600 /etc/users.oath
if [[ $(head -1 /etc/pam.d/su | grep -q auth; echo $?) -eq 1 ]]; then
  sed -i "1s|.*|${PAM_CONFIG}|" /etc/pam.d/su
fi
echo "Generating a QR code..."
qrencode -t UTF8 "otpauth://totp/${USER}@$(hostname)?secret=$(printf $SEED | xxd -r -p | base32)"
echo "Now scan the QR code with your OTP app"

# auth sufficient pam_oath.so debug usersfile=/etc/users.oath window=20
