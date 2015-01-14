#!/bin/bash - 
#===============================================================================
#
#          FILE: clean.sh
# 
#         USAGE: ./clean.sh 
# 
#   DESCRIPTION: 
# 
#       OPTIONS: ---
#  REQUIREMENTS: ---
#          BUGS: ---
#         NOTES: ---
#        AUTHOR: Ajay Singh, ajsingh@expedia.com
#  ORGANIZATION: Expedia, Inc.
#       CREATED: 07/07/2014 06:01:17 PM IST
#      REVISION:  ---
#===============================================================================

set -o nounset                              # Treat unset variables as an error

for module in chandler maui dev local test; do
    cd ${module};
    rm -Rf pom.xml target
    cd ..
done
