How to run snmp tool
==============================
1. git clone https://github.com/Seagate/kinetic-java-tools.git
2. cd <kinetic-java-tools-folder>/kinetic-titan-snmp
3. mvn clean package
4. cd <kinetic-java-tools-folder>/kinetic-titan-snmp/bin
5. ./startSnmpUtil.sh -file snmpTool.conf
   hwview_chassis_"chassis.id".json will be generated under current work directory by default.
6. ./generateRackJsonFromChassisJson.sh -files <chassisJsonFileA, chassisJsonFileB, ...> [-rackId <rackId> -rackCoordinate <rackCoordinate> -out <hvOutputFile>]
   hwview_rack_"rackId".json will be generated under current work directory by default.
7. ./generateHwviewFromRackJson.sh -files <rackJsonFileA, rackJsonFileB, ...>
   hwview.json will be generated under current work directory by default.

More information
===============================
1. "./startSnmpUtil.sh -help" to see more options.
2. "./generateRackJsonFromChassisJson.sh -help" to see more options.
3. "./generateHwviewFromRackJson.sh -help" to see more options.
4. <kinetic-java-tools-folder>/kinetic-titan-snmp/conf/agent.config configures snmp agent ip/port and OIDs.

snmpTool.conf content instruction
===============================
    agent.ip=192.168.1.68                             ---Titan chassis IP Address, should be modified.
    agent.port=161                                    ---Titan chassis SNMP port, no need to modify.
    user=snmp                                         ---Titan chassis SNMP default username, no need to modify if keep the default one.
    password=password                                 ---Titan chassis SNMP default password, no need to modify if keep the default one.
    drive.ipa.oid=1.3.6.1.4.1.3581.12.7.2.1.1.10      ---Titan chassis plane A ip address OID prefix for drive, no need to modify.
    drive.ipb.oid=1.3.6.1.4.1.3581.12.7.2.1.1.11      ---Titan chassis plane B ip address OID prefix for drive, no need to modify.
    oid.start.index=12                                ---IP address OID start index, no need to modify.
    drive.count=84                                    ---Titan chassis capacity for drives, no need to modify unless you'd like to get less drives info.
    chassis.id=1                                      ---chassis id, modify it when against different chassis.
    out.file=./hwview_chassis                         ---output file path, modify it if you'd like to change.
