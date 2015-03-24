How to use Kinetic tools for management:
==============================
1. cd <path>/kinetic-tools

2. mvn clean package

3. cd bin

4. sh ktool.sh -help

Kinetic tools for management example:
==============================
1. ktool -h|-help

   For instance:
   sh ktool.sh -help
   
2. ktool -discover [-out <driveListOutputFile>] [-timeout <timeoutInSecond>]

   For instance:
   sh ktool.sh -discover
   or
   sh ktool.sh -discover -out drives.txt -timeout 10

3. ktool -firmwaredownload <fmFile> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInMilliSecond>]
   
   For instance:
   sh ktool.sh -firmwaredownload ~/kineticd-installer-v2.6.0.slod -in drives.txt
   
4. ktool -checkversion <-v <expectFirmwareVersion>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInMilliSecond>]
   
   For instance:
   sh ktool.sh -checkversion -v 2.6.0 -in drives.txt

5. ktool -seterasepin <-oldpin <oldErasePinInString>> <-newpin <newErasePinInString>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInMilliSecond>]

   For instance:
   sh ktool.sh -seterasepin -oldpin "" -newpin 123 -in drives.txt

6. ktool -instanterase <-pin <erasePinInString>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInMilliSecond>]
  
   For instance:
   sh ktool.sh -instanterase -pin 123 -in drives.txt
   
7. ktool -setclusterversion <-newclversion <newClusterVersionInString>> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInMilliSecond>]
   
   For instance:
   sh ktool.sh -setclusterversion -clversion 0 -in drives.txt
   
8. ktool -setsecurity <securityFile> <-in <driveListInputFile>> [-usessl <true|false>] [-clversion <clusterVersion>] [-identity <identity>] [-key <key>] [-reqtimeout <requestTimeoutInMilliSecond>]

   For instance:
   sh ktool.sh -setsecurity ./template/security.template -in drives.txt
   
9. ktool -runsmoketest <-in <driveListInputFile>>

   For instance:
   sh ktool.sh -runsmoketest -in drives.txt