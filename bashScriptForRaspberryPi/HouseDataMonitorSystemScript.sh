#!/bin/bash
# Author: Lukasz Bol
# Version: 1.1

# This script sleeps for 30 seconds before starting the Device Program
# The time allows the system to establish Internet connection, after loading the OS
sleep 30
echo "Starting the House Data Monitor System..."
sudo java -jar /home/pi/NetBeansProjects/HouseDataMonitor-DataIO/dist/HouseDataMonitor-DataIO.jar