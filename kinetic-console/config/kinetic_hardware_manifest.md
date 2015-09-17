Kinetic Hardware Manifest
=========================
The Kinetic hardware manifest defines the installed Kinetic storage hardware. This manifest fully and completely defines the set of racks, chassis, and chassis drives. The hardware manifest is defined in JSON format, and the JSON schema can be found at:

https://github.com/Seagate/kinetic-java-tools/blob/master/kinetic-tools/config/hwview-schema.json

Creation of Hardware Manifest
-----------------------------
There are 2 ways of creating the hardware manifest file:
1. Manually (using text editor)
2. Chassis discovery utility (chassis-vendor specific)

Manual Creation of Hardware Manifest
------------------------------------
For a very tiny cluster, it may be easiest to create the manifest file using a text editor.

```simulator_manifest.json``` is an example for use with 4 instances of Kinetic simulator (each on different port).

SuperMicro Chassis Discovery Utility
------------------------------------
Please use ```ipmi_rack_to_manifest.py``` (modified as necessary for your environment) for discovery of storage chassis within a rack. This utility will output Kinetic hardware manifest to standard output in JSON format.

Kinetic Console
---------------
Copy the Kinetic hardware manifest (JSON file) to ```kinetic-java-tools/kinetic-console/config/default/hwview_default.json```
