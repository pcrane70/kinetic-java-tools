# kinetic-java-tools
Kinetic Java Tools are used to help the deployment and management of Kinetic drives

##Suggested Development Environments
==================================
  * Latest version of Git for your OS: [http://git-scm.com/downloads](http://git-scm.com/downloads)

  * JDK 1.7 or above: [http://www.oracle.com/technetwork/java/javase/downloads/index.html](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

  * Maven 3.0.3 or later: [http://maven.apache.org/download.cgi](http://maven.apache.org/download.cgi)

##Building kinetic tools
==============================
  1. Clone the code: `git clone https://github.com/Seagate/kinetic-java-tools.git`
  2. cd to "Kinetic-java-tools/kinetic-tools" folder
  3. Run "mvn clean package"
  
##Supported commands and usage examples

  * The kinetic tools command line script is under "Kinetic-java-tools/kinetic-tools/bin" folder

###ktool command help and options
```
sh ktool.sh -help
```

###Drive discovery
Discover a cluster of drives within a set of IP ranges.

```
ktool -discover [-out <driveListOutputFile>] [-timeout <timeoutInSecond>] [-subnet <subnet>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]

   For instance:
   
   sh ktool.sh -discover
   or
   sh ktool.sh -discover -out drives.txt -timeout 10
   or
   sh ktool.sh -discover -subnet 10.24.70
```

###Ping drives

```
ktool -ping <-in <driveListInputFile>> [-out <driveListOutputFile>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>] 

   For instance:
   
   sh ktool.sh -ping -in drives.txt
```

###Firmware upgrade
Upgrade firmware for a cluster of drives.

```
ktool -firmwaredownload <fmFile> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
   
   For instance:
   
   sh ktool.sh -firmwaredownload ~/kineticd-installer-v2.6.0.slod -in drives.txt
```

###Check versions

```
ktool -checkversion <-v <expectFirmwareVersion>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
   
   For instance:
   
   sh ktool.sh -checkversion -v 2.6.0 -in drives.txt
```

###Set erase pins

```
ktool -seterasepin <-oldpin <oldErasePinInString>> <-newpin <newErasePinInString>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]

   For instance:
   
   sh ktool.sh -seterasepin -oldpin "" -newpin 123 -in drives.txt
```

###Set lock pins

```
ktool -setlockpin <-oldpin <oldLockPinInString>> <-newpin <newLockPinInString>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]

   For instance:
   
   sh ktool.sh -setlockpin -oldpin "" -newpin 123 -in drives.txt
```

###Set instance erase pins

```
ktool -instanterase <-pin <erasePinInString>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
  
   For instance:
   
   sh ktool.sh -instanterase -pin 123 -in drives.txt
```

###Set secure erase pins

```
ktool -secureerase <-pin <erasePinInString>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
  
   For instance:
   
   sh ktool.sh -secureerase -pin 123 -in drives.txt
```

###Set cluster versions

```
ktool -setclusterversion <-newclversion <newClusterVersionInString>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
   
   For instance:
   
   sh ktool.sh -setclusterversion -clversion 0 -in drives.txt
```

###Set security

```
ktool -setsecurity <securityFile> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]

   For instance:
   
   sh ktool.sh -setsecurity ./template/security.template -in drives.txt
```

###Get logs

```
ktool -getlog <-in <driveListInputFile>> [-out <logOutputFile>] [-type <utilization|temperature|capacity|configuration|message|statistic|limits|all>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
    
    For instance:
    
    sh ktool.sh -getlog -in drives.txt
    or
    sh ktool.sh -getlog -in drives.txt -type configuration
```

###Get vendor specifci logs

```
ktool -getvendorspecificdevicelog <-name <vendorspecificname>> <-in <driveListInputFile>> [-out <logOutputFile>] [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
    
    For instance, to get Seagate drive logs:
   
    sh ktool.sh -getvendorspecificdevicelog -name com.Seagate.Kinetic.HDD.Gen1 -in drives.txt
```

###Lock devices

```
ktool -lockdevice <-pin <lockPinInString>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
    
    For instance:
   
    sh ktool.sh -lockdevice -pin 123 -in drives.txt
```

###Unlock devices

```
ktool -unlockdevice <-pin <lockPinInString>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInSecond>]
    
    For instance:
   
    sh ktool.sh -unlockdevice -pin 123 -in drives.txt
```

###Run smoke tests

```
ktool -runsmoketest <-in <driveListInputFile>>

   For instance:
   
   sh ktool.sh -runsmoketest -in drives.txt
```
