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
Discover a cluster of drives within a set of IP ranges.
If subnet option is present, only devices within the subnet (0-255) are discovered and added to the output file.
The timeout option is used to specify the sampling time to collect the devices. The default is set to 30 seconds if not set.
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
```
ktool.sh -ping <-in <driveListInputFile>> [-out <driveListOutputFile>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>] 

   For instance:
   ./ktool.sh -ping -in drives.txt
```

###Firmware upgrade
Upgrade firmware for devices specified in the driveListInputFile. This command is used to update device firmware releases.
```
ktool.sh -firmwaredownload <fmFile> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
   
   For instance:
   ./ktool.sh -firmwaredownload ~/kineticd-installer-v2.6.0.slod -in drives.txt
```

###Check versions
Check if the version of each device specified in the driveListInputFile matched the specified version. This command is used to verify if the devices specified in driveListInputFile are equal to the indicated version.
```
ktool.sh -checkversion <-v <expectFirmwareVersion>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
   
   For instance:
   ./ktool.sh -checkversion -v 2.6.0 -in drives.txt
```

###Set erase pins
Set the erase pins for the devices specified in the driveListInputFile.  A rease pin is required to perform instant erase or secure erase commands to a drive.
```
ktool.sh -seterasepin <-oldpin <oldErasePinInString>> <-newpin <newErasePinInString>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]

   For instance:
   ./ktool.sh -seterasepin -oldpin "" -newpin 123 -in drives.txt
```

###Set lock pins
Set the lock pins for the devices specified in the driveListInputFile.  A lock pin is required to lock a drive.
```
ktool.sh -setlockpin <-oldpin <oldLockPinInString>> <-newpin <newLockPinInString>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]

   For instance:
   ./ktool.sh -setlockpin -oldpin "" -newpin 123 -in drives.txt
```

###Instant erase the drives
Erase the drives specified in the driveListInputFile with the instant erase protocol.  All contents on the drive will be instant erased and the drive is reset to its factory default settings. A matched pin is required in order to perform the instanterase command.
```
ktool.sh -instanterase <-pin <erasePinInString>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
  
   For instance:
   ./ktool.sh -instanterase -pin 123 -in drives.txt
```

###Secure erase the drives
Erase the drive specified in the driveListInputFile with the secure erase protocol.  All contents on the drive will be secure erased and the drive is reset to its factory default settings. A matched pin is required in order to perform the secureerase command.
```
ktool.sh -secureerase <-pin <erasePinInString>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
  
   For instance:
   ./ktool.sh -secureerase -pin 123 -in drives.txt
```

###Set cluster versions
Set cluster version for the devices specified in the driveListInputFile.
```
ktool.sh -setclusterversion <-newclversion <newClusterVersionInString>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
   
   For instance:
   ./ktool.sh -setclusterversion -clversion 0 -in drives.txt
```

###Set security
Set security (ACL) for the devices specified in the driveListInputFile.  The ACL is specified in the securityFile.  An example ACL templet file is located at ./template/security.template.
```
ktool.sh -setsecurity <securityFile> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]

   For instance:
   ./ktool.sh -setsecurity ./template/security.template -in drives.txt
```

###Get logs
Get device logs for the devices specified in the driveListInputFile.  The output is written to the specified logOutputFile.  The supported types are:

* `utilization`
* `temperature`
* `capacity`
* `configuration`
* `statistic`
* `message`
* `limits`
* `all`
```
ktool.sh -getlog <-in <driveListInputFile>> [-out <logOutputFile>] [-type <utilization|temperature|capacity|configuration|message|statistic|limits|all>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
    
    For instance:
    ./ktool.sh -getlog -in drives.txt
    or
    ./ktool.sh -getlog -in drives.txt -type configuration
```

###Get vendor specific logs
Get vendor specific logs from the devices specified in the driveListInputFile. The Seagate drive specific log name is com.Seagate.Kinetic.HDD.Gen1 (as shown in the example).
```
ktool.sh -getvendorspecificdevicelog <-name <vendorspecificname>> <-in <driveListInputFile>> [-out <logOutputFile>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
    
    For instance, to get Seagate specific drive logs:
    ./ktool.sh -getvendorspecificdevicelog -name com.Seagate.Kinetic.HDD.Gen1 -in drives.txt
```

###Lock devices
Lock device for the devices specified in the driveListInputFile. The devices are locked and cannot be accessed until they are unlocked (see unlock command). 
```
ktool.sh -lockdevice <-pin <lockPinInString>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
    
    For instance:
    ./ktool.sh -lockdevice -pin 123 -in drives.txt
```

###Unlock devices
Unlock devices for the those specified in the driveListInputFile.
```
ktool.sh -unlockdevice <-pin <lockPinInString>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
    
    For instance:
    ./ktool.sh -unlockdevice -pin 123 -in drives.txt
```

###Run smoke tests
Run smoke tests again devices specified in the driveListInputFile. This command is used to perform sanity check on the devices specified in driveListInputFile.
```
ktool.sh -runsmoketest <-in <driveListInputFile>>

   For instance:
   ./ktool.sh -runsmoketest -in drives.txt
```

###Run performance tests
Run performance tests again devices specified in the driveListInputFile. This command is used to run simple performance tests (with YCSB) on the devices specified in driveListInputFile.
```
ktool.sh -perf <-in <driveListInputFile>>

   For instance:
   ./ktool.sh -perf -in drives.txt
```
