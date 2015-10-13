#Kinetic Chassis JSON Interface 
===============================

##Terms Definition
--------------------------
 * JSON: a lightweight data exchange format. http://www.json.org/
 * Ubuntu: a Debian-based Linux operating system.  http://www.ubuntu.com/
 * Kinetic chassis vendor: a company that produces Kinetic compatible chassis.  Such as SuperMicro and Seagate.
 * Executable Program or Program: a set of encoded instructions that caused a computer to perform indicated tasks. https://en.wikipedia.org/wiki/Executable

##Kinetic chassis Vendor Requirements
---------------------------------------------
Each Kinetic chassis vendor is to provide a program to obtain basic content of the vendor specific chassis.
The program uses the vendor specific protocol to discover/query chassis and produces output that conforms to the specified JSON format.

  1. Execution environment.  
  The executable program, command, script must be at least executable under Ubuntu OS environment.  

  ```
  OS: Linux, Ubuntu 14.x and up.
  Language: Linux/Ubuntu supported programming language, command, or script.
  ```

  2. Execution instructions. 
  Each chassis vendor should provide simple instructions to describe how the Program is invoked and the location of the output JSON file.

  3. JSON output format. 
  The output of the execution result format should conform to the JSON format as specified below.  

  Kinetic chassis JSON template:
  https://github.com/Seagate/kinetic-java-tools/blob/master/kinetic-chassis/config/chassis_template.json
  
  The description of each JSON field is included in the schema here:
  https://github.com/Seagate/kinetic-java-tools/blob/master/kinetic-tools/config/hwview-schema.json
