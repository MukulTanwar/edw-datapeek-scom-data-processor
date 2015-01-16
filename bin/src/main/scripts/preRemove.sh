#!/bin/bash

running=$(/sbin/initctl status scomdataprocessor | grep start/running )
if [ -n "${running}" ];then
  /sbin/initctl stop scomdataprocessor 
fi

