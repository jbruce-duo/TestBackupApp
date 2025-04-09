#!/bin/bash -eu

show_help() {
  echo "$0 - run a backup of test app"
  echo " "
  echo "options:"
  echo "-h, --help           show brief help"
  echo "local|cloud          destination for backup, controls which underlying Android transport is used"
}

if [ $# -lt 1 ] || [ "$1" == "-h" ] || [ "$1" == "--help" ]; then
  show_help
  exit 0
fi


DESTINATION=$1

init_local_transport() {
  echo "initializing local transport"
  adb shell bmgr transport com.android.localtransport/.LocalTransport | grep -q "Selected transport" || (echo "Error: error selecting local transport"; exit 1)
  adb shell bmgr init com.android.localtransport/.LocalTransport
  adb shell settings put secure backup_local_transport_parameters 'is_encrypted=true'
}

init_cloud_transport() {
  echo "initializing cloud transport"
  adb shell bmgr transport com.google.android.gms/.backup.BackupTransportService | grep -q "Selected transport" || (echo "Error: error selecting cloud transport"; exit 1)
}

init_transport() {
  if [ "$DESTINATION" == "local" ]; then
    init_local_transport
  elif [ "$DESTINATION" == "cloud" ]; then
    init_cloud_transport
  else
    show_help
    exit 0
  fi
}

run_backup() {
  adb shell bmgr backupnow "com.example.testbackupapp"
}

adb shell bmgr enable true
init_transport
run_backup
adb shell bmgr transport com.google.android.gms/.backup.BackupTransportService
echo "Done"
