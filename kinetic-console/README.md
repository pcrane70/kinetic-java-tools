Kinetic Console
================================
Kinetic Console is a GUI monitoring tool for kinetic chassis and drives. 
The features include the following:

1. Kinetic hardware overview: Drive's ip, WWN, Chassis location, etc.
2. Chassis monitoring: Kinetic K/V operation statistics at Chassis level.
3. Drive monitoring: Kinetic K/V operation statistics for each drive.
4. Kinetic K/V logs: Capacity, Temperature, Utilization, etc.


How to run kinetic-console demo with 72 simulators:
==================================
1. mvn clean package

2. cd kinetic-console/bin

3. ./startMultiSimulators.sh

4. ./startKineticConsole.sh

5. open the URL "http://localhost:8080/kinetic/console" with a browser
