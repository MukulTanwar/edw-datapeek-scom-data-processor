#!/bin/bash - 
#===============================================================================
#
#          FILE: generatePom.sh
# 
#         USAGE: ./generatePom.sh 
# 
#   DESCRIPTION: Expands pom.xml template for all environments for RPM.
# 
#       OPTIONS: ---
#  REQUIREMENTS: ---
#          BUGS: ---
#         NOTES: ---
#        AUTHOR: Ajay Singh, ajsingh@expedia.com
#  ORGANIZATION: Expedia, Inc.
#       CREATED: 07/07/2014 12:27:17 PM IST
#      REVISION:  ---
#===============================================================================

set -o nounset                              # Treat unset variables as an error

for env in local test dev  maui chandler; do
#####################################################################
# Expand pom.xml for this particular environment                    #
#####################################################################
cat env-pom.xml.template | sed "s/\${deploy.env}/${env}/" > ${env}/pom.xml


echo "INFO: ScomDataProcessor configuration for '${env}' completed."
done

