How to run snmp demo
==============================
1. git clone https://github.com/Seagate/kinetic-java-tools.git
2. cd <kinetic-java-tools-folder>/kinetic-chassis/snmp
3. mvn clean package
4. cd <kinetic-java-tools-folder>/kinetic-chassis/snmp/bin
5. ./startVirtualDrives.sh
6. ./startSnmpAgent.sh
7. ./startSnmpUtil.sh -agentIps 127.0.0.1:2001,127.0.0.1:2002,127.0.0.1:2003,127.0.0.1:2004,127.0.0.1:2005
8. hwview.json will be generated under current work directory by default
9. Copy hwview.json to <kinetic-java-tools-folder>/kinetic-console/config/default/
10. cd <kinetic-java-tools-folder>/kinetic-console/bin
11. ./startKineticConsole.sh
12. Go to browser(recommend Firefox), http://localhost:8080/kinetic/console/index.html
13. Select hwview.json from configuration list to show the detail information of the drives/simulators

More information
===============================
1. "./startSnmpUtil -help" to see more options for snmp util.
2. <kinetic-java-tools-folder>/kinetic-chassis/snmp/conf/mibs/kinetic.mib*.json configures drives ip/port in this chassis.