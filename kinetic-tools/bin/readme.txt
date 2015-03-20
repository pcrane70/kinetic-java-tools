How to use Kinetic tools for management:
==============================
1. cd <path>/kinetic-tools

2. mvn clean package

3. cd bin

4. sh kineticMgmt.sh -help

Kinetic tools for management example:
==============================
1. kineticMgmt -h|-help

   For instance:
   sh kineticMgmt.sh -help
   
2. kineticMgmt -discover [-out <driveListOutputFile>] [-timeout <timeoutInSecond>]

   For instance:
   sh kineticMgmt.sh -discover
   or
   sh kineticMgmt.sh -discover -out drives.txt -timeout 10

3. kineticMgmt -firmwaredownload <fmFile> <-in <driveListInputFile>>
   
   For instance:
   sh kineticMgmt.sh -firmwaredownload ~/kineticd-installer-v2.6.0.slod -in drives.txt
   
4. kineticMgmt -checkversion <-v <expectFirmwareVersion>> <-in <driveListInputFile>>
   
   For instance:
   sh kineticMgmt.sh -checkversion -v 2.6.0 -in drives.txt

5. kineticMgmt -seterasepin <-oldpin <oldErasePinInString>> <-newpin <newErasePinInString>> <-in <driveListInputFile>>

   For instance:
   sh kineticMgmt.sh -seterasepin -oldpin "" -newpin 123 -in drives.txt

6. kineticMgmt -instanterase -pin <erasePinInString>> <-in <driveListInputFile>>
  
   For instance:
   sh kineticMgmt.sh -instanterase -pin 123 -in drives.txt
   
7. kineticMgmt -setclusterversion <-clversion <newClusterVersionInString>> <-in <driveListInputFile>>
   
   For instance:
   sh kineticMgmt.sh -setclusterversion -clversion 0 -in drives.txt
   
8. kineticMgmt -setsecurity <securityFile> <-in <driveListInputFile>>

   For instance:
   sh kineticMgmt.sh -setsecurity ./template/security.template -in drives.txt
   
9. kineticMgmt -runsmoketest <-in <driveListInputFile>>

   For instance:
   sh kineticMgmt.sh -runsmoketest -in drives.txt