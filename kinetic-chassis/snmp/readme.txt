How to run snmp demo
==============================
1. git clone https://github.com/Seagate/kinetic-java-tools.git
2. cd <kinetic-java-tools-folder>/kinetic-chassis/snmp
3. mvn clean package
4. cd <kinetic-java-tools-folder>/kinetic-chassis/snmp/bin
5. ./startVirtualDrives.sh
6. ./startSnmpAgent.sh
7. ./startSnmpUtil.sh
8. hwview.json will be generated under current work directory by default

More information
===============================
1. "./startSnmpUtil -help" to see more options for snmp util.
2. <kinetic-java-tools-folder>/kinetic-chassis/snmp/conf/mibs/kinetic.mib.json configures drives ip/port in this chassis.
3. <kinetic-java-tools-folder>/kinetic-chassis/snmp/conf/agent.config configures snmp agent ip/port and OIDs.