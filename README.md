# kineticDNS
Dynamic DNS client with support to multiple providers and domains.

Supported dynamic DNS providers: NoIP, DynDNS, DuckDNS

To configure the first time on unix systems, make sure you have java installed with version 17 or above. Then, run as sudo with no arguments and follow the setup instructions. 

\# java -jar /path/to/kineticDNS.jar

Configuration files are created in the folder /usr/local/etc/kineticDNS or /etc/kineticDNS, each configuration file will be named after the configured domain name. To configure an additional domain name, run with the option --configure.

\# java -jar /path/to/kineticDNS.jar --configure

Logs are created in the folder /var/log/kineticDNS or /var/lib/kineticDNS. It is recommended to change the permissions of the log folder to avoid needing to run the program as root everytime, maybe change to nobody or other unprivileged user.

The DNS records will only be updated if current IP does not match the last IP logged in ipMonitor log file. To force an update, run with option --force:

$ java -jar /path/to/kineticDNS.jar --force

Note that this will attempt to update every domain name configured.
