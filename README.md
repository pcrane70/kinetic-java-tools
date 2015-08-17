# kinetic-java-tools
Kinetic Java Tools are used to help the deployment and management of Kinetic drives

##Required Environments
==================================
  * Latest version of Git for your OS: [http://git-scm.com/downloads](http://git-scm.com/downloads)

  * JDK 1.7 or above: [http://www.oracle.com/technetwork/java/javase/downloads/index.html](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

  * Maven 3.0.3 or later: [http://maven.apache.org/download.cgi](http://maven.apache.org/download.cgi)

##Building kinetic tools
==============================
  1. Clone the code: `git clone https://github.com/Seagate/kinetic-java-tools.git`
  2. cd to "kinetic-java-tools" folder
  3. Run "mvn clean package"
  
##Supported commands and usage examples

  * The kinetic tools command line script is under "kinetic-java-tools/kinetic-tools/bin" folder

###ktool command help and options
Print a list of available commands and usage information.
```
ktool.sh -help

  For instance:
  ./ktool.sh -help

```

###Drive discovery
Discover kinetic drives with heartbeats or within a subset of IP range.

The optional inputs for the script includes the following:
  * out ~ Used to specify path of output file for discovered IP Address list, i.e. drives.txt. If option is not specified, the default file output name will be drives_timestamp in milliseconds. The content of output format is in JSON format. 
  * timeout ~ Used to specify the sampling time to collect the devices in seconds when subnet option is not used. The default timeout is set to 30 seconds if not specified 
  * subnet ~ When the subnet is specified, only devices within the subnet (0-255) are discovered and added to the output file. If not specified, devices with detectable heartbeats will be discovered by default via multicast. 
  * usessl ~ Used only when subnet option is specified. If true, the Kinetic tool uses secure socket connection to discover the devices. By defualt, this option is set to false. If subnet option is not specified, this option will be ignored.
  * clversion ~ If specified, only devices set with the specified cluster version are discovered. For discover command, this option is ignored if subnet option is not specified.
  * identity ~ User identity used to authenticate with the devices.  Each user's priviledges are based on configured Access Control List (ACL) for each device. The default user identity ("1") is provided if not specified.  For discover command, this option is ignored if subnet option is not specified.
  * key ~ The security key of the user.  A default key ("asdfasdf") is used if not specified.  For discover command, this option is ignored if subnet option is not specified.
  * reqtimeout ~ Used to specify the request timeout for the request. Option is only applicable when subnet option is applied. The default request timeout is set to 30 seconds. 

```
ktool.sh -discover [-out <driveListOutputFile>] [-timeout <timeoutInSecond>] [-subnet <subnet>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]

   For instance:
  ./ktool.sh -discover
   or
   ./ktool.sh -discover -out drives.txt -timeout 10
   or
   ./ktool.sh -discover -subnet 10.24.70
```

###Ping drives
Ping the devices specified in the driveListInputFile.  This command is used to ping if the devices specified in the driveListInputFile are healthy and operational.

The required input for the script includes the following:
  * in <driveListInputFile> ~ Used to specify path of the input file that contains the list of IP addresses for the commmand, i.e. drives.txt.

The optional inputs for the script includes the following:
  * out ~ Used to specify name of output file for pinged IP Address list, i.e. pingdrives.txt. If option is not specified, the default file output name will be pingsuccessdrives_timestamp in milliseconds. The format is in JSON format. 
  * usessl ~ If option is set to true, the Kinetic tool uses secure socket connection to communicate with the devices. By defualt, this option is set to false.
  * clversion ~ If specified, only devices matched with the specified cluster version are performed.
  * identity ~ User identity used to authenticate with the device.  Each user's priviledges are based on configured Access Control List (ACL) for each device. The default user identity ("1") is used if not specified.
  * key ~ The security key of the user.  A default key ("asdfasdf") is used if not specified.
  * reqtimeout ~ Used to specify the request timeout for the request. The default request timeout is set to 30 seconds. 


```
ktool.sh -ping <-in <driveListInputFile>> [-out <driveListOutputFile>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>] 

   For instance:
   ./ktool.sh -ping -in drives.txt
```

###Firmware upgrade
Upgrade firmware for devices specified in the driveListInputFile. This command is used to update device firmware releases.

The required inputs for the script includes the following:
  * fmfile ~ Path to Kinetic firmware image file.
  * in ~ Used to specify path of the input file that contains the list of IP addresses for the commmand, i.e. drives.txt.

The optional inputs for the script are the same as ping command specified above.

```
ktool.sh -firmwaredownload <fmFile> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
   
   For instance:
   ./ktool.sh -firmwaredownload ~/kineticd-installer-v2.6.0.slod -in drives.txt
```

###Check versions
Check if the version of each device specified in the driveListInputFile matched the specified version. This command is used to verify if the devices specified in driveListInputFile are equal to the indicated version.

The required inputs for the script includes the following:
  * v ~ Indicated firmware version to be matched with devices in the driveListInputFile
  * in ~ Used to specify path of the input file that contains the list of IP addresses for the commmand, i.e. drives.txt.

The optional inputs for the script are the same as ping command specified above.

```
ktool.sh -checkversion <-v <expectFirmwareVersion>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
   
   For instance:
   ./ktool.sh -checkversion -v 2.6.0 -in drives.txt
```

###Set erase pins
Set the erase pins for the devices specified in the driveListInputFile.  An erase pin is required to perform instant erase or secure erase commands to a drive.

The required inputs for the script includes the following:
  * oldpin ~ Old pin of the devices specified in list of IP addresses. If the devices have not been previously configured, default old pin is "". 
  * newpin ~ New pin to be used to access the devices to perform instant erase or secure erase
  * in ~ Used to specify path of the input file that contains the list of IP addresses for the commmand, i.e. drives.txt.

The optional inputs for the script are the same as ping command specified above.

```
ktool.sh -seterasepin <-oldpin <oldErasePinInString>> <-newpin <newErasePinInString>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]

   For instance:
   ./ktool.sh -seterasepin -oldpin "" -newpin 123 -in drives.txt
```

###Set lock pins
Set the lock pins for the devices specified in the driveListInputFile.  A lock pin is required to lock a device

The required inputs for the script includes the following:
  * oldpin ~ Old lock pin of the devices specified in list of IP addresses. If the devices have not been previously configured, default old lock pin is "". 
  * newpin ~ New lock pin to be used to lock and unlock the devices
  * in ~ Used to specify path of the input file that contains the list of IP addresses for the commmand, i.e. drives.txt.

The optional inputs for the script are the same as ping command specified above.

```
ktool.sh -setlockpin <-oldpin <oldLockPinInString>> <-newpin <newLockPinInString>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]

   For instance:
   ./ktool.sh -setlockpin -oldpin "" -newpin 123 -in drives.txt
```

###Instant erase the drives
Erase the drives specified in the driveListInputFile with the instant erase protocol.  All contents on the drive will be instant erased and the drive is reset to its factory default settings. A matched pin is required in order to perform the instanterase command.

The required inputs for the script includes the following:
  * pin ~ The erase pin used to perform the instant erase command for the devices.
  * in ~ Used to specify path of the input file that contains the list of IP addresses for the commmand, i.e. drives.txt.

The optional inputs for the script are the same as ping command specified above.
```
ktool.sh -instanterase <-pin <erasePinInString>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
  
   For instance:
   ./ktool.sh -instanterase -pin 123 -in drives.txt
```

###Secure erase the drives
Erase the drive specified in the driveListInputFile with the secure erase protocol.  All contents on the drive will be secure erased and the drive is reset to its factory default settings. A matched pin is required in order to perform the secureerase command.

The required inputs for the script includes the following:
  * pin ~ The erase pin used to perform the secure erase command for the devices.
  * in ~ Used to specify path of the input file that contains the list of IP addresses for the commmand, i.e. drives.txt.

The optional inputs for the script are the same as ping command specified above.
```
ktool.sh -secureerase <-pin <erasePinInString>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
  
   For instance:
   ./ktool.sh -secureerase -pin 123 -in drives.txt
```

###Set cluster versions
Set cluster version for the devices specified in the driveListInputFile.

The required inputs for the script includes the following:
  * newclversion ~ Used to specify desired cluster version for the devices.
  * in ~ Used to specify path of the input file that contains the list of IP addresses for the commmand, i.e. drives.txt.

The optional inputs for the script are the same as ping command specified above.
```
ktool.sh -setclusterversion <-newclversion <newClusterVersionInString>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
   
   For instance:
   ./ktool.sh -setclusterversion -newclversion 1 -clversion 0 -in drives.txt
```

###Set security
Set security (ACL) for the devices specified in the driveListInputFile.  The Access Control List (ACL) is specified in the securityFile.  An example ACL template file is located at ./template/security.template.

The required inputs for the script includes the following:
  * setsecurity ~ Used to specify the path of the file that holds the ACL
  * in ~ Used to specify path of the input file that contains the list of IP addresses for the commmand, i.e. drives.txt.

The optional inputs for the script are the same as ping command specified above.
```
ktool.sh -setsecurity <securityFile> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]

   For instance:
   ./ktool.sh -setsecurity ./template/security.template -in drives.txt
```

###Get logs
Get device logs for the devices specified in the driveListInputFile.

The required input for the script includes the following:
  * in ~ Used to specify name of the input file that contains the list of IP addresses for the commmand, i.e. drives.txt.

The optional inputs for the script includes the following:
  * out ~ Used to specify name of output file for device logs IP Address list, i.e. pingdrives.txt. If option is not specified, the default file output name will be getlogs_timestamp in milliseconds. The format is in JSON format.
  * type ~ Used to specify desired type of device logs. If not specified, the default type is all.
   * `utilization` ~ Utilization of the HDA, EN0, EN1, and CPU of the devices.
   * `temperature` ~ Temperature of the devices.
   * `capacity` ~ Capacity usage of the devices in bytes.
   * `configuration` ~ Configuration information of the devices, which includes vendor name, model, serial number, firmware version, compilation date, source hash, protocol version, and protocol compilation date.
   * `statistic` ~ Statitistcs of operations used in device in counts and bytes.
   * `message` ~ Detailed log information of devices' activity.
   * `limits` ~ Maximum limits of the device key size, value size, version size, tag size, connections, oustanding read and write requests, message size, key range count, and identity count.
   * `all` ~ All log types stated above. Do not need to use type option for this selection.
   * The optional inputs (usessl, clversion, identity, key, reqtimeout) for the script are the same as ping command specified above.

```
ktool.sh -getlog <-in <driveListInputFile>> [-out <logOutputFile>] [-type <utilization|temperature|capacity|configuration|message|statistic|limits|all>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
    
    For instance:
    ./ktool.sh -getlog -in drives.txt
    or
    ./ktool.sh -getlog -in drives.txt -type configuration
```

###Get vendor specific logs
Get vendor specific logs from the devices specified in the driveListInputFile. 

The required inputs for the script includes the following:
  * name ~ Used to specify the specific vendor log name. The Seagate drive specific log name is com.Seagate.Kinetic.HDD.Gen1 (as shown in the example).
  * in ~ Used to specify path of the input file that contains the list of IP addresses for the commmand, i.e. drives.txt.

The optional inputs (usessl, clversion, identity, key, reqtimeout) for the script are the same as ping command specified above.
```
ktool.sh -getvendorspecificdevicelog <-name <vendorspecificname>> <-in <driveListInputFile>> [-out <logOutputFile>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
    
    For instance, to get Seagate specific drive logs:
    ./ktool.sh -getvendorspecificdevicelog -name com.Seagate.Kinetic.HDD.Gen1 -in drives.txt
```

###Lock devices
Lock device for the devices specified in the driveListInputFile. The devices are locked and cannot be accessed until they are unlocked (see unlock command). 

The required inputs for the script includes the following:
  * pin ~ Assigned lock pin for the devices in string format.
  * in ~ Used to specify path of the input file that contains the list of IP addresses for the commmand, i.e. drives.txt.

The optional inputs (usessl, clversion, identity, key, reqtimeout) for the script are the same as ping command specified above.

```
ktool.sh -lockdevice <-pin <lockPinInString>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
    
    For instance:
    ./ktool.sh -lockdevice -pin 123 -in drives.txt
```

###Unlock devices
Unlock devices for the those specified in the driveListInputFile.

The required inputs for the script includes the following:
  * pin ~ Assigned lock pin for the devices in string format.
  * in ~ Used to specify path of the input file that contains the list of IP addresses for the commmand, i.e. drives.txt.

The optional inputs (usessl, clversion, identity, key, reqtimeout) for the script are the same as ping command specified above.
```
ktool.sh -unlockdevice <-pin <lockPinInString>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
    
    For instance:
    ./ktool.sh -unlockdevice -pin 123 -in drives.txt
```

###Run smoke tests
Run smoke tests again devices specified in the driveListInputFile. This command is used to perform sanity check on the devices specified in driveListInputFile. 

The required input for the script includes the following:
  * in ~ Used to specify path of the input file that contains the list of IP addresses for the commmand, i.e. drives.txt.

```
ktool.sh -runsmoketest <-in <driveListInputFile>>

   For instance:
   ./ktool.sh -runsmoketest -in drives.txt
```

###Run performance tests
Run performance tests against devices specified in the driveListInputFile. This command is used to run simple performance tests (with YCSB) on the devices specified in driveListInputFile. The output file is named workloadkinetic

The required input for the script includes the following:
  * in ~ Used to specify path of the input file that contains the list of IP addresses for the commmand, i.e. drives.txt.
```
ktool.sh -perf <-in <driveListInputFile>>

   For instance:
   ./ktool.sh -perf -in drives.txt
```

##Kinetic REST API
==============================
Kinetic tools REST API enables users to invoke kinetic tools commands with REST/HTTP protocol.  

The following are steps to start the Kinetic tools REST service.

  1. cd to "kinetic-java-tools" folder
  2. run "mvn clean package"
  3. start REST service with the command below. The default service ports are set to 8080 (HTTP) and 8081 (HTTPS) if not set.
  
```
java -jar ./kinetic-tools/target/kinetic-tools-0.0.1-SNAPSHOT-jar-with-dependencies.jar [http-port https-port]

For example:
java -jar ./kinetic-tools/target/kinetic-tools-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

For each REST request command API, the supported request options are similar to those specified in the kinetic tools CLI above.  The options should be set in the HTTP request message body in JSON format if not specified otherwise in the individual command.

The REST response message is set in the HTTP response message body in JSON format (UTF-8 encoding).

The examples in this section assumes that there is a Kinetic Tools REST service and simulator running on the  localhost.

###Drive discovery with the REST API

```
POST /kinetic/tools/discover

For example:
curl -d '{discoid=mycluster, timeout=10}' "http://localhost:8080/kinetic/tools/discover"

Example response message:
{"discoid":"mycluster","devices":[{"device":{"inet4":["10.24.145.149","127.0.0.1"],"port":8123,"tlsPort":8443,"wwn":"92503018-7360-45df-89ae-7374aa195cc1","model":"Simulator","serialNumber":"S664024157","firmwareVersion":"0.8.0.4-SNAPSHOT"},"status":200}],"overallStatus":200,"messageType":"DISCOVER_REPLY"}
```

###Ping a set of discovered drives with the REST API

```
POST /kinetic/tools/ping

For example:
curl -d '{discoid=mycluster}' "http://localhost:8080/kinetic/tools/ping"

Example response message:
{"devices":[{"deviceId":{"ips":["10.24.145.149","127.0.0.1"],"port":8123,"tlsPort":8443,"wwn":"92503018-7360-45df-89ae-7374aa195cc1"},"status":200}],"overallStatus":200,"messageType":"PING_REPLY"}
```

###getlog from a set of discovered drives with the REST API

The options should be set in the request URL as request parameters.

```
GET /kinetic/tools/getlog

For example:
curl "http://localhost:8080/kinetic/tools/getlog?discoid=mycluster&type=capacity"

Example response:
{"deviceLogs":[{"deviceStatus":{"deviceId":{"ips":["10.24.145.149","127.0.0.1"],"port":8123,"tlsPort":8443,"wwn":"92503018-7360-45df-89ae-7374aa195cc1"},"status":200},"capacity":{"nominalCapacityInBytes":499418034176,"portionFull":0.29467958}}],"overallStatus":200,"messageType":"GETLOG_REPLY"}

```
