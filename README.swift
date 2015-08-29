Environment Variables:
SWIFT_DIR: This variable change the default Swift Dir from /etc/swift to user
defined values.
Example:
 export SWIFT_DIR =/mydir

Alternatively, the following K/V in Json format can be used to change the directory for each REST call
"dir":"<dir name>"

Example:
curl -d '{"resource":"proxy", "dir":"/home/my-swift-dir"}' http://localhost:8080/external?class=Config
------------------------------------------------
The default ring files are as follows:
For Objects: object.builder
For Accounts: account.builder
For Containers: container.builder
The default can be overriden by the following K/V
"file":"<file name>"
Example:
 curl -d '{"resource":"object", "file":"object1.builder"}' http://localhost:8080/external?class=Ring
-----------------------------------------------------
Access Control:
If swift cluster is started as root make sure to start the REST server with root access.
Ring Structure 

Let's consider a Swift cluster with 2 storage nodes, with the following IPs 
addresses: 192.168.0.10 and 192.168.0.11. Each storage node has 
two devices: sdb1 and sdc1.

An example of table with 3 replicas, 8 partitions and 4 devices would be:

r
e  |   +-----------------+
p  | 0 | 0 1 2 3 0 1 2 3 |
l  | 1 | 1 2 3 0 1 2 3 0 |
i  | 2 | 2 3 0 1 2 3 0 1 |
c  v   +-----------------+
a        0 1 2 3 4 5 6 7
       ------------------>
          partition

The table has 3 lines, one for each replica, and 8 columns, one for each partition.
 To find the device storing the replica number 1 of partition number 2, we select 
 the line of index 1 and column of index 2. This lead us to the device ID 3.

To get the Object Ring:
curl -d '{"resource":"object"}' http://localhost:8080/external?class=Partitions
---------------------------------------------------------------------------------
Detailed Informtaion about specific Object partition can be found by invoking Nodes REST
API:
Example:
 curl -d '{"partition":"1", "file":"object.ring.gz"}' http://localhost:8080/external?class=Nodes

A Sample output is shown later in this file
--------------------------------------------------------------------------------------------

For extracting configurations use Config class. 
Examples:
curl -d '{"resource":"proxy"}' http://localhost:8080/external?class=Config
curl -d '{"resource":"account"}' http://localhost:8080/external?class=Config
curl -d '{"resource":"object"}' http://localhost:8080/external?class=Config
curl -d '{"resource":"container"}' http://localhost:8080/external?class=Config

For extracting populating dispersion  use Dispersion class. 
Examples:
curl -d '{"resource":"populate"}' http://localhost:8080/external?class=Dispersion
curl -d '{"resource":"report"}' http://localhost:8080/external?class=Dispersion

To check the status of the each server use the Init class
Examples:
curl -d '{"resource":"container"}' http://localhost:8080/external?class=Init
curl -d '{"resource":"proxy"}' http://localhost:8080/external?class=Init
curl -d '{"resource":"account"}' http://localhost:8080/external?class=Init
curl -d '{"resource":"object"}' http://localhost:8080/external?class=Init

To get the Recon status of each server use the Recon class
Examples:
curl -d '{"resource":"account"}' http://localhost:8080/external?class=Recon
curl -d '{"resource":"container"}' http://localhost:8080/external?class=Recon
curl -d '{"resource":"object"}' http://localhost:8080/external?class=Recon
curl -d '{"resource":"proxy"}' http://localhost:8080/external?class=Recon

To find the status of each ring use the Ring class
Examples:
curl -d '{"resource":"account"}' http://localhost:8080/external?class=Ring
curl -d '{"resource":"container"}' http://localhost:8080/external?class=Ring
curl -d '{"resource":"object"}' http://localhost:8080/external?class=Ring
-----------------------------------------------------------------
Extracting information about objects, accounts, and containers: 
(1) Use the following commands to list the DB files about objects,
accounts, and containers.
curl -d '{"resource":"object"}' http://localhost:8080/external?class=Info
curl -d '{"resource":"account"}' http://localhost:8080/external?class=Info
curl -d '{"resource":"container"}' http://localhost:8080/external?class=Info
Once db files are extartced use the following REST to extract information
about objects, accounts and containers.

REST Example for extracting account info 
 curl -d '{"resource":"account", "file":"/swift/sdv/accounts/802/178/c8bcccab3ddbfdc34b08e9223f4f5178/c8bcccab3ddbfdc34b08e9223f4f5178.db"}' http://localhost:8080/external?class=Info

REST Example for extracting container info 
 curl -d '{"resource":"container", "file":"/swift/sdv/containers/603/9f7/96e90f348f8d45a7288eaeed2473c9f7/96e90f348f8d45a7288eaeed2473c9f7.db"}' http://localhost:8080/external?class=Info
REST Example for extracting object info 
 curl -d '{"resource":"object", "file":"/swift/sdv/object/603/9f7/676786786876786876abcdef45466666666664.db"}' http://localhost:8080/external?class=Info
 --------------------------------------
List of objects in a container, and containers in an account can be extracted using REST calls

Example:
cur -d '{"command":"list", "resource":"dispersion_0", "url":"http://127.0.0.1:8080/auth/v1.0", "user":"test:tester", "key":"testing"}' http://localhost:9090/external?class=Swift

Statistics of containers objects and accounts can be extratced by invoking the REST call.
Example:
url -d '{"command":"stat", "resource":"dispersion_0", "url":"http://127.0.0.1:8080/auth/v1.0", "user":"test:tester", "key":"testing"}' http://localhost:9090/external?class=Swift


================================== Hardware View =============================
Hardware view such as chassis, and drives in the chassis can be found using SuperStore sub-url. This facility is 
limited to Super Micro chassis, only.
The host should be the IPMI interface IP Address or Name.
Example:
curl -d '{"host":"SampleChassis"}' http://localhost:9090/external?class=SuperStore
{"chassis":[{"devices":[{"deviceId":{"ips":["172.16.17.70","172.17.2.143"],"port":8123,"tlsPort":8443,"wwn":"5000c5007987e798"}},{"deviceId":{"ips":["172.16.17.70","172.17.2.143"],"port":8123,"tlsPort":8443,"wwn":"5000c5007987e798"}},{"deviceId":{"ips":["172.16.17.70","172.17.2.143"],"port":8123,"tlsPort":8443,"wwn":"5000c5007987e798"}},{"deviceId":{"ips":["172.16.17.70","172.17.2.143"],"port":8123,"tlsPort":8443,"wwn":"5000c5007987e798"}},{"deviceId":{"ips":["172.16.17.70","172.17.2.143"],"port":8123,"tlsPort":8443,"wwn":"5000c5007987e798"}},{"deviceId":{"ips":["172.16.17.70","172.17.2.143"],"port":8123,"tlsPort":8443,"wwn":"5000c5007987e798"}},{"deviceId":{"ips":["172.16.17.70","172.17.2.143"],"port":8123,"tlsPort":8443,"wwn":"5000c5007987e798"}},{"deviceId":{"ips":["172.16.17.70","172.17.2.143"],"port":8123,"tlsPort":8443,"wwn":"5000c5007987e798"}},{"deviceId":{"ips":["172.16.17.70","172.17.2.143"],"port":8123,"tlsPort":8443,"wwn":"5000c5007987e798"}},{"deviceId":{"ips":["172.16.17.70","172.17.2.143"],"port":8123,"tlsPort":8443,"wwn":"5000c5007987e798"}},{"deviceId":{"ips":["172.16.17.70","172.17.2.143"],"port":8123,"tlsPort":8443,"wwn":"5000c5007987e798"}},{"deviceId":{"ips":["172.16.17.70","172.17.2.143"],"port":8123,"tlsPort":8443,"wwn":"5000c5007987e798"}}]}]}
------------------------------------------------
Auto Discovery of SuperMicro Chassis
SuperMicro chassis list can be extracted by using Discovery sub-url.
Example:
 
curl -d  http://localhost:9090/external/swift?class=Discovery

Optional parameters such as user, password, port, directory can be provided with the command


============================  SAMPLE OUTPUT========================

curl -d '{"resource":"populate"}' http://localhost:8080/external?class=Dispersion
Created 10 containers for dispersion reporting, 0s, 0 retries
Created 10 objects for dispersion reporting, 0s, 0 retries
----------------------------------------------------------------------------
curl -d '{"resource":"report"}' http://localhost:8080/external?class=Dispersion
Created 10 containers for dispersion reporting, 0s, 0 retries
Created 10 objects for dispersion reporting, 0s, 0 retries
Queried 11 containers for dispersion reporting, 0s, 0 retries
100.00% of container copies found (11 of 11)
Sample represents 1.07% of the container partition space
Queried 10 objects for dispersion reporting, 0s, 0 retries
There were 10 partitions missing 0 copy.
100.00% of object copies found (30 of 30)
Sample represents 0.98% of the object partition space
-----------------------------------------------------------

curl -d '{"resource":"container"}' http://localhost:8080/external?class=Init
container-server running (3647 - /etc/swift/container-server.conf)
-----------------------------------------------------------------------
curl -d '{"resource":"proxy"}' http://localhost:8080/external?class=Init
proxy-server running (3646 - /etc/swift/proxy-server.conf)

------------------------------------------------------------------------
curl -d '{"resource":"account"}' http://localhost:8080/external?class=Init
account-server running (3648 - /etc/swift/account-server.conf)

-------------------------------------------------------------------------
curl -d '{"resource":"object"}' http://localhost:8080/external?class=Init

object-server running (3649 - /etc/swift/object-server.conf)
----------------------------------------------------------------------------
curl -d '{"resource":"proxy"}' http://localhost:8080/external?class=Config
# /etc/swift/proxy-server.conf
[filter:cname_lookup]
bind_port = 8080
use = egg:swift#cname_lookup
user = mshafiq

[filter:tempauth]
use = egg:swift#tempauth
user_test2_tester2 = testing2 .admin
bind_port = 8080
user_test_tester = testing .admin
user_test_tester3 = testing3
user_admin_admin = admin .admin .reseller_admin
user_test5_tester5 = testing5 service
user = mshafiq

[filter:proxy-logging]
bind_port = 8080
use = egg:swift#proxy_logging
user = mshafiq

[filter:bulk]
bind_port = 8080
use = egg:swift#bulk
user = mshafiq

[filter:healthcheck]
bind_port = 8080
use = egg:swift#healthcheck
user = mshafiq

[filter:cache]
bind_port = 8080
use = egg:swift#memcache
user = mshafiq

[filter:xprofile]
bind_port = 8080
use = egg:swift#xprofile
user = mshafiq
object_single_process = object-server.conf

[filter:staticweb]
bind_port = 8080
use = egg:swift#staticweb
user = mshafiq

[filter:tempurl]
bind_port = 8080
use = egg:swift#tempurl
user = mshafiq

[filter:list-endpoints]
bind_port = 8080
use = egg:swift#list_endpoints
user = mshafiq

[filter:dlo]
bind_port = 8080
use = egg:swift#dlo
user = mshafiq

[filter:account-quotas]
bind_port = 8080
use = egg:swift#account_quotas
user = mshafiq

[filter:gatekeeper]
bind_port = 8080
use = egg:swift#gatekeeper
user = mshafiq

[filter:container_sync]
bind_port = 8080
use = egg:swift#container_sync
user = mshafiq

[pipeline:main]
bind_port = 8080
pipeline = catch_errors gatekeeper healthcheck proxy-logging cache container_sync bulk tempurl ratelimit tempauth container-quotas account-quotas slo dlo proxy-logging proxy-server
user = mshafiq

[filter:name_check]
bind_port = 8080
use = egg:swift#name_check
user = mshafiq

[filter:domain_remap]
bind_port = 8080
use = egg:swift#domain_remap
user = mshafiq

[filter:slo]
bind_port = 8080
use = egg:swift#slo
user = mshafiq

[filter:ratelimit]
bind_port = 8080
use = egg:swift#ratelimit
user = mshafiq

[filter:catch_errors]
bind_port = 8080
use = egg:swift#catch_errors
user = mshafiq

[app:proxy-server]
bind_port = 8080
use = egg:swift#proxy
user = mshafiq
account_autocreate = true

[filter:container-quotas]
bind_port = 8080
use = egg:swift#container_quotas
user = mshafiq

[filter:formpost]
bind_port = 8080
use = egg:swift#formpost
user = mshafiq

# __file__ = /etc/swift/proxy-server.conf
# log_name = None
-------------------------------------------------------------

curl -d '{"resource":"account"}' http://localhost:8080/external?class=Config

# /etc/swift/account-server.conf
[app:account-server]
bind_port = 6002
mount_check = false
use = egg:swift#account
user = mshafiq
devices = /swift

[account-reaper]
bind_port = 6002
mount_check = false
user = mshafiq
devices = /swift

[account-replicator]
bind_port = 6002
mount_check = false
user = mshafiq
devices = /swift

[filter:xprofile]
bind_port = 6002
mount_check = false
use = egg:swift#xprofile
user = mshafiq
devices = /swift

[account-auditor]
bind_port = 6002
mount_check = false
user = mshafiq
devices = /swift

[filter:healthcheck]
bind_port = 6002
mount_check = false
use = egg:swift#healthcheck
user = mshafiq
devices = /swift

[filter:recon]
bind_port = 6002
mount_check = false
use = egg:swift#recon
user = mshafiq
devices = /swift

[pipeline:main]
bind_port = 6002
mount_check = false
pipeline = healthcheck account-server
user = mshafiq
devices = /swift

# __file__ = /etc/swift/account-server.conf
# log_name = None

---------------------------------------------------------------

curl -d '{"resource":"container"}' http://localhost:8080/external?class=Config

# /etc/swift/container-server.conf
[filter:healthcheck]
bind_port = 6001
mount_check = false
use = egg:swift#healthcheck
user = mshafiq
devices = /swift

[container-auditor]
bind_port = 6001
mount_check = false
user = mshafiq
devices = /swift

[app:container-server]
bind_port = 6001
mount_check = false
use = egg:swift#container
user = mshafiq
devices = /swift

[filter:xprofile]
bind_port = 6001
mount_check = false
use = egg:swift#xprofile
user = mshafiq
devices = /swift

[filter:recon]
bind_port = 6001
mount_check = false
use = egg:swift#recon
user = mshafiq
devices = /swift

[container-updater]
bind_port = 6001
mount_check = false
user = mshafiq
devices = /swift

[container-replicator]
bind_port = 6001
mount_check = false
user = mshafiq
devices = /swift

[pipeline:main]
bind_port = 6001
mount_check = false
pipeline = healthcheck container-server
user = mshafiq
devices = /swift

[container-sync]
bind_port = 6001
mount_check = false
user = mshafiq
devices = /swift

# __file__ = /etc/swift/container-server.conf
# log_name = None
-----------------------------------------------------------------
curl -d '{"resource":"object"}' http://localhost:8080/external?class=Config
# /etc/swift/object-server.conf
[object-replicator]
bind_port = 6000
mount_check = false
disk_chunk_size = 1048576
user = mshafiq
devices = /swift

[app:object-server]
disk_chunk_size = 1048576
use = egg:swift#object
bind_port = 6000
mount_check = false
devices = /swift
user = mshafiq

[pipeline:main]
disk_chunk_size = 1048576
pipeline = healthcheck object-server
bind_port = 6000
mount_check = false
devices = /swift
user = mshafiq

[filter:xprofile]
disk_chunk_size = 1048576
use = egg:swift#xprofile
bind_port = 6000
mount_check = false
devices = /swift
user = mshafiq

[filter:recon]
disk_chunk_size = 1048576
use = egg:swift#recon
bind_port = 6000
mount_check = false
devices = /swift
user = mshafiq

[object-updater]
bind_port = 6000
mount_check = false
disk_chunk_size = 1048576
user = mshafiq
devices = /swift

[object-auditor]
bind_port = 6000
mount_check = false
disk_chunk_size = 1048576
user = mshafiq
devices = /swift

[filter:healthcheck]
disk_chunk_size = 1048576
use = egg:swift#healthcheck
bind_port = 6000
mount_check = false
devices = /swift
user = mshafiq

[object-reconstructor]
bind_port = 6000
mount_check = false
disk_chunk_size = 1048576
user = mshafiq
devices = /swift

# __file__ = /etc/swift/object-server.conf
# log_name = None


curl -d '{"resource":"container"}' http://localhost:9090/external/swift?class=Ring
[[{"id":"0"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6001"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6001"},{"name":"sdv"},{"weight":"1.00"},{"partitions":"3072"},{"balance":"0.00"}]]

------------------------------------------------------------
curl -d '{"resource":"account"}' http://localhost:9090/external/swift?class=Ring
[[{"id":"0"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6002"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6002"},{"name":"sdv"},{"weight":"1.00"},{"partitions":"3072"},{"balance":"0.00"}]]


 ----------------------------------------
  curl -d '{"resource":"object"}' http://localhost:9090/external/swift?class=Ring
  [[{"id":"0"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8100"},{"weight":"1.00"},{"partitions":"62"},{"balance":"0.91"}],[{"id":"1"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8101"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"2"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8102"},{"weight":"1.00"},{"partitions":"62"},{"balance":"0.91"}],[{"id":"3"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8103"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"4"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8104"},{"weight":"1.00"},{"partitions":"62"},{"balance":"0.91"}],[{"id":"5"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8105"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"6"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8106"},{"weight":"1.00"},{"partitions":"62"},{"balance":"0.91"}],[{"id":"7"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8107"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"8"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8108"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"9"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8109"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"10"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8110"},{"weight":"1.00"},{"partitions":"62"},{"balance":"0.91"}],[{"id":"11"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8111"},{"weight":"1.00"},{"partitions":"62"},{"balance":"0.91"}],[{"id":"12"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8112"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"13"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8113"},{"weight":"1.00"},{"partitions":"62"},{"balance":"0.91"}],[{"id":"14"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8114"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"15"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8115"},{"weight":"1.00"},{"partitions":"62"},{"balance":"0.91"}],[{"id":"16"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8116"},{"weight":"1.00"},{"partitions":"62"},{"balance":"0.91"}],[{"id":"17"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8117"},{"weight":"1.00"},{"partitions":"62"},{"balance":"0.91"}],[{"id":"18"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8118"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"19"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8119"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"20"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8120"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"21"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8121"},{"weight":"1.00"},{"partitions":"62"},{"balance":"0.91"}],[{"id":"22"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8122"},{"weight":"1.00"},{"partitions":"62"},{"balance":"0.91"}],[{"id":"23"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8123"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"24"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8124"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"25"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8125"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"26"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8126"},{"weight":"1.00"},{"partitions":"62"},{"balance":"0.91"}],[{"id":"27"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8127"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"28"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8128"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"29"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8129"},{"weight":"1.00"},{"partitions":"62"},{"balance":"0.91"}],[{"id":"30"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8130"},{"weight":"1.00"},{"partitions":"62"},{"balance":"0.91"}],[{"id":"31"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8131"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"32"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8132"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"33"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8133"},{"weight":"1.00"},{"partitions":"62"},{"balance":"0.91"}],[{"id":"34"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8134"},{"weight":"1.00"},{"partitions":"62"},{"balance":"0.91"}],[{"id":"35"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8135"},{"weight":"1.00"},{"partitions":"62"},{"balance":"0.91"}],[{"id":"36"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8136"},{"weight":"1.00"},{"partitions":"62"},{"balance":"0.91"}],[{"id":"37"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8137"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"38"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8138"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"39"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8139"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"40"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8140"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"41"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8141"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"42"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8142"},{"weight":"1.00"},{"partitions":"62"},{"balance":"0.91"}],[{"id":"43"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8143"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"44"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8144"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"45"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8145"},{"weight":"1.00"},{"partitions":"62"},{"balance":"0.91"}],[{"id":"46"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8146"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"47"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8147"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"48"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8148"},{"weight":"1.00"},{"partitions":"61"},{"balance":"-0.72"}],[{"id":"49"},{"region":"1"},{"zone":"1"},{"ipaddress":"127.0.0.1"},{"port":"6000"},{"repliactionip":"127.0.0.1"},{"repliactionport":"6000"},{"name":"127.0.0.1:8149"},{"weight":"1.00"},{"partitions":"62"},{"balance":"0.91"}]]

To get the Object Partitions:
curl -d '{"resource":"object"}' http://localhost:8080/external?class=Partitions
curl -d '{"resource":"object"}' http://localhost:9090/external/swift?class=Partitions
{"0":[2,5,16],"1":[1,3,45],"2":[18,44,46],"3":[10,20,27],"4":[21,23,31],"5":[12,14,41],"6":[19,38,49],"7":[15,39,48],"8":[24,25,32],"9":[9,26,30],"10":[13,35,36],"11":[0,6,8],"12":[11,28,47],"13":[7,40,42],"14":[29,33,34],"15":[4,17,22],"16":[1,37,43],"17":[25,44,47],"18":[4,17,19],"19":[33,37,39],"20":[14,27,43],"21":[7,36,48],"22":[18,30,41],"23":[9,28,42],"24":[5,38,49],"25":[8,20,35],"26":[21,22,34],"27":[6,11,29],"28":[12,13,46],"29":[3,16,40],"30":[2,26,45],"31":[15,24,31],"32":[0,10,23],"33":[11,32,34],"34":[5,24,40],"35":[6,8,27],"36":[29,37,42],"37":[12,13,19],"38":[1,20,47],"39":[2,15,18],"40":[17,21,38],"41":[9,10,48],"42":[7,22,25],"43":[32,35,43],"44":[3,23,26],"45":[28,31,39],"46":[0,41,45],"47":[4,16,46],"48":[14,33,49],"49":[30,36,44],"50":[6,29,42],"51":[18,31,49],"52":[1,5,46],"53":[14,38,47],"54":[13,21,36],"55":[3,16,45],"56":[11,19,26],"57":[17,33,44],"58":[10,35,40],"59":[12,24,39],"60":[20,25,32],"61":[7,27,37],"62":[0,4,34],"63":[8,23,43],"64":[2,22,41],"65":[15,28,30],"66":[3,9,48],"67":[19,22,32],"68":[0,9,47],"69":[28,37,40],"70":[7,27,33],"71":[8,15,17],"72":[12,36,39],"73":[31,35,38],"74":[2,18,46],"75":[25,26,45],"76":[29,41,48],"77":[16,20,30],"78":[4,23,49],"79":[11,24,44],"80":[5,6,10],"81":[14,42,43],"82":[1,13,34],"83":[21,23,43],"84":[2,26,41],"85":[21,24,31],"86":[18,32,44],"87":[22,25,30],"88":[27,40,46],"89":[10,34,38],"90":[0,13,17],"91":[6,15,36],"92":[42,45,49],"93":[20,37,47],"94":[4,5,39],"95":[3,29,35],"96":[9,11,12],"97":[7,19,48],"98":[1,14,28],"99":[8,16,33],"100":[13,17,30],"101":[7,11,19],"102":[5,23,49],"103":[2,29,36],"104":[15,25,41],"105":[9,35,42],"106":[24,40,43],"107":[32,46,47],"108":[1,3,45],"109":[27,28,39],"110":[26,33,48],"111":[8,18,21],"112":[20,22,37],"113":[16,31,44],"114":[0,10,34],"115":[6,12,14],"116":[4,22,38],"117":[11,21,42],"118":[8,44,45],"119":[7,15,26],"120":[30,38,43],"121":[5,33,41],"122":[0,20,37],"123":[16,34,36],"124":[2,13,35],"125":[3,14,25],"126":[19,31,49],"127":[23,40,48],"128":[17,32,47],"129":[1,9,27],"130":[6,12,29],"131":[4,28,39],"132":[10,24,46],"133":[0,18,49],"134":[2,3,44],"135":[9,38,40],"136":[12,17,18],"137":[8,34,39],"138":[6,30,47],"139":[13,15,37],"140":[4,14,28],"141":[22,41,43],"142":[16,19,24],"143":[21,25,35],"144":[10,11,48],"145":[1,23,45],"146":[5,42,46],"147":[7,20,32],"148":[27,31,33],"149":[26,29,36],"150":[15,28,32],"151":[1,3,27],"152":[30,33,45],"153":[13,39,41],"154":[9,40,46],"155":[2,26,43],"156":[17,34,44],"157":[24,35,36],"158":[7,10,21],"159":[12,18,42],"160":[8,19,22],"161":[14,23,48],"162":[6,20,31],"163":[5,25,29],"164":[11,16,47],"165":[4,38,49],"166":[0,11,37],"167":[3,39,43],"168":[29,30,45],"169":[2,22,32],"170":[20,31,46],"171":[14,19,33],"172":[5,7,42],"173":[13,34,35],"174":[15,16,37],"175":[10,18,24],"176":[8,28,48],"177":[21,47,49],"178":[6,25,27],"179":[0,9,12],"180":[4,23,26],"181":[17,38,41],"182":[1,36,44],"183":[11,24,40],"184":[37,39,40],"185":[2,8,29],"186":[12,14,43],"187":[10,23,34],"188":[28,33,36],"189":[31,41,49],"190":[1,47,48],"191":[5,7,22],"192":[4,13,27],"193":[9,38,46],"194":[18,19,30],"195":[0,17,45],"196":[3,15,21],"197":[6,16,26],"198":[32,35,44],"199":[20,25,42],"200":[3,24,46],"201":[2,22,35],"202":[19,38,45],"203":[14,18,21],"204":[28,33,37],"205":[9,31,39],"206":[32,40,44],"207":[13,16,43],"208":[4,6,42],"209":[10,15,27],"210":[7,20,49],"211":[12,23,48],"212":[11,17,26],"213":[8,29,30],"214":[0,25,34],"215":[1,36,47],"216":[5,41,42],"217":[9,12,38],"218":[31,33,40],"219":[2,5,44],"220":[0,17,30],"221":[1,7,23],"222":[32,39,43],"223":[14,35,37],"224":[4,16,49],"225":[3,25,28],"226":[10,18,21],"227":[22,24,41],"228":[8,26,29],"229":[13,36,48],"230":[15,20,47],"231":[19,45,46],"232":[11,27,34],"233":[6,12,22],"234":[17,35,42],"235":[6,10,46],"236":[1,13,33],"237":[4,27,34],"238":[18,47,49],"239":[19,21,26],"240":[0,9,11],"241":[38,40,44],"242":[3,25,45],"243":[5,31,36],"244":[23,24,29],"245":[28,37,43],"246":[16,30,41],"247":[8,32,39],"248":[2,7,20],"249":[14,15,48],"250":[4,43,45],"251":[2,28,49],"252":[10,13,23],"253":[15,20,31],"254":[32,34,36],"255":[0,9,41],"256":[8,35,48],"257":[17,19,37],"258":[5,18,24],"259":[12,14,22],"260":[7,29,33],"261":[11,26,42],"262":[1,25,40],"263":[30,39,47],"264":[27,44,46],"265":[3,21,38],"266":[6,15,16],"267":[11,25,43],"268":[18,24,41],"269":[6,20,23],"270":[17,32,49],"271":[3,29,48],"272":[21,34,35],"273":[8,45,47],"274":[0,10,28],"275":[1,16,30],"276":[27,31,42],"277":[9,22,37],"278":[26,36,46],"279":[5,13,19],"280":[2,4,7],"281":[12,14,44],"282":[33,38,40],"283":[0,39,47],"284":[2,17,34],"285":[11,27,37],"286":[3,9,23],"287":[15,32,41],"288":[6,20,28],"289":[13,39,48],"290":[16,30,33],"291":[1,21,31],"292":[10,14,42],"293":[7,8,40],"294":[4,18,46],"295":[38,44,45],"296":[12,29,43],"297":[5,24,25],"298":[26,35,49],"299":[19,22,36],"300":[9,14,25],"301":[11,26,37],"302":[4,40,43],"303":[2,3,42],"304":[12,46,49],"305":[0,18,31],"306":[15,16,44],"307":[29,30,48],"308":[20,32,39],"309":[24,34,47],"310":[6,7,38],"311":[10,19,33],"312":[21,23,27],"313":[1,17,45],"314":[5,22,35],"315":[13,36,41],"316":[1,8,28],"317":[13,20,38],"318":[11,22,34],"319":[0,18,29],"320":[10,27,44],"321":[12,42,45],"322":[5,25,40],"323":[41,43,46],"324":[3,30,48],"325":[2,31,36],"326":[16,17,35],"327":[23,26,49],"328":[14,19,32],"329":[4,7,33],"330":[8,9,15],"331":[6,21,39],"332":[28,37,47],"333":[0,22,24],"334":[6,33,39],"335":[11,43,47],"336":[13,19,27],"337":[7,24,34],"338":[4,35,40],"339":[3,16,48],"340":[2,31,44],"341":[10,17,41],"342":[12,30,46],"343":[14,20,36],"344":[21,28,32],"345":[8,29,49],"346":[1,38,42],"347":[5,15,37],"348":[9,23,26],"349":[18,25,45],"350":[0,3,26],"351":[9,16,29],"352":[23,37,41],"353":[6,35,39],"354":[14,28,33],"355":[20,36,48],"356":[8,43,46],"357":[2,21,32],"358":[11,12,13],"359":[27,30,31],"360":[10,17,18],"361":[22,44,47],"362":[7,19,34],"363":[25,42,49],"364":[15,24,45],"365":[1,4,38],"366":[5,15,40],"367":[1,18,49],"368":[17,26,33],"369":[3,22,36],"370":[16,29,45],"371":[4,25,42],"372":[24,28,31],"373":[20,23,46],"374":[0,12,19],"375":[38,47,48],"376":[14,34,37],"377":[7,9,13],"378":[10,30,43],"379":[27,35,39],"380":[8,11,44],"381":[2,21,32],"382":[5,6,41],"383":[13,33,40],"384":[2,19,39],"385":[3,37,43],"386":[7,8,48],"387":[4,14,49],"388":[23,24,40],"389":[5,9,20],"390":[10,18,46],"391":[22,38,44],"392":[0,29,47],"393":[21,25,32],"394":[6,11,30],"395":[16,28,41],"396":[1,26,27],"397":[12,35,42],"398":[17,34,36],"399":[15,31,45],"400":[6,36,47],"401":[3,4,16],"402":[14,23,43],"403":[38,40,46],"404":[7,30,39],"405":[0,18,41],"406":[1,2,31],"407":[22,29,49],"408":[9,10,20],"409":[13,33,35],"410":[19,32,44],"411":[17,37,45],"412":[12,42,48],"413":[5,24,28],"414":[11,25,26],"415":[15,21,27],"416":[1,8,34],"417":[8,14,19],"418":[10,33,35],"419":[12,26,27],"420":[13,37,40],"421":[34,36,46],"422":[31,38,45],"423":[11,23,29],"424":[0,21,32],"425":[7,16,20],"426":[2,24,49],"427":[25,30,39],"428":[5,6,18],"429":[42,47,48],"430":[3,17,28],"431":[9,41,43],"432":[4,15,44],"433":[21,22,26],"434":[27,39,49],"435":[15,41,44],"436":[11,23,28],"437":[7,12,48],"438":[17,40,42],"439":[14,16,20],"440":[29,32,34],"441":[2,19,45],"442":[6,9,31],"443":[5,18,24],"444":[22,43,46],"445":[0,33,47],"446":[1,10,37],"447":[3,4,25],"448":[13,30,35],"449":[8,36,38],"450":[13,15,30],"451":[1,3,44],"452":[21,22,35],"453":[11,31,33],"454":[4,47,48],"455":[19,39,45],"456":[18,29,38],"457":[6,23,25],"458":[0,12,42],"459":[17,27,41],"460":[2,7,46],"461":[9,24,26],"462":[5,10,36],"463":[16,32,34],"464":[8,20,28],"465":[14,37,43],"466":[12,40,49],"467":[28,36,49],"468":[11,15,43],"469":[1,5,46],"470":[9,30,44],"471":[8,13,39],"472":[35,37,40],"473":[3,18,20],"474":[7,47,48],"475":[16,17,27],"476":[2,23,25],"477":[0,32,34],"478":[14,21,38],"479":[19,22,41],"480":[10,31,42],"481":[4,24,33],"482":[6,29,45],"483":[6,26,49],"484":[16,39,47],"485":[5,31,48],"486":[18,42,43],"487":[15,27,34],"488":[3,20,29],"489":[12,35,45],"490":[8,10,40],"491":[1,22,24],"492":[4,13,23],"493":[37,38,46],"494":[2,14,41],"495":[21,25,32],"496":[7,11,30],"497":[19,36,44],"498":[0,28,33],"499":[9,17,26],"500":[22,28,30],"501":[23,39,49],"502":[4,20,43],"503":[25,32,44],"504":[33,34,47],"505":[0,13,36],"506":[12,27,29],"507":[9,18,42],"508":[7,8,17],"509":[14,15,37],"510":[10,31,38],"511":[3,16,41],"512":[2,40,48],"513":[5,26,35],"514":[1,6,21],"515":[11,19,46],"516":[10,24,45],"517":[4,6,11],"518":[0,20,48],"519":[3,24,39],"520":[37,38,49],"521":[7,8,32],"522":[14,26,27],"523":[16,43,45],"524":[12,23,40],"525":[25,34,36],"526":[13,42,46],"527":[17,18,19],"528":[9,21,29],"529":[30,35,47],"530":[5,31,33],"531":[15,22,41],"532":[1,2,44],"533":[0,11,28],"534":[10,19,36],"535":[34,44,45],"536":[1,23,39],"537":[13,24,31],"538":[7,18,21],"539":[4,5,26],"540":[14,27,33],"541":[29,32,37],"542":[3,15,35],"543":[9,20,41],"544":[2,8,42],"545":[6,43,46],"546":[17,30,40],"547":[12,47,49],"548":[22,25,38],"549":[16,28,48],"550":[8,22,46],"551":[13,25,37],"552":[9,18,27],"553":[12,15,40],"554":[20,33,35],"555":[2,5,23],"556":[10,42,43],"557":[16,41,45],"558":[1,21,49],"559":[17,28,32],"560":[24,39,44],"561":[4,26,30],"562":[29,38,48],"563":[7,36,47],"564":[3,6,31],"565":[0,11,19],"566":[14,34,42],"567":[3,8,19],"568":[10,30,32],"569":[4,40,47],"570":[2,28,46],"571":[0,1,27],"572":[7,17,44],"573":[12,23,29],"574":[11,22,31],"575":[5,9,43],"576":[16,21,26],"577":[18,34,39],"578":[14,25,37],"579":[20,24,38],"580":[6,35,41],"581":[13,36,48],"582":[15,45,49],"583":[33,43,46],"584":[18,36,44],"585":[1,14,45],"586":[26,28,40],"587":[7,22,30],"588":[24,25,35],"589":[5,41,49],"590":[15,17,20],"591":[8,9,31],"592":[16,32,39],"593":[6,19,21],"594":[2,13,48],"595":[11,42,47],"596":[23,34,38],"597":[3,4,27],"598":[0,29,37],"599":[10,12,33],"600":[0,20,49],"601":[24,27,47],"602":[17,26,29],"603":[7,15,21],"604":[1,2,22],"605":[14,39,45],"606":[9,10,34],"607":[11,31,40],"608":[28,32,33],"609":[12,19,35],"610":[36,41,48],"611":[5,13,37],"612":[4,44,46],"613":[18,38,42],"614":[16,30,43],"615":[6,23,25],"616":[3,8,17],"617":[13,33,42],"618":[22,31,43],"619":[20,47,48],"620":[4,21,37],"621":[24,25,44],"622":[18,28,32],"623":[5,10,19],"624":[2,34,49],"625":[9,16,30],"626":[0,12,45],"627":[23,40,41],"628":[15,29,36],"629":[3,26,39],"630":[6,11,46],"631":[7,8,35],"632":[1,14,38],"633":[17,24,27],"634":[11,32,34],"635":[30,39,41],"636":[3,21,22],"637":[5,20,36],"638":[0,23,35],"639":[26,28,40],"640":[14,15,33],"641":[18,29,38],"642":[7,42,44],"643":[2,4,31],"644":[9,16,43],"645":[6,10,12],"646":[1,8,49],"647":[45,47,48],"648":[13,25,46],"649":[19,27,37],"650":[20,29,32],"651":[14,28,47],"652":[8,27,39],"653":[3,4,40],"654":[0,24,46],"655":[15,16,38],"656":[11,36,43],"657":[7,12,49],"658":[21,30,35],"659":[6,13,41],"660":[17,19,23],"661":[5,18,22],"662":[1,25,42],"663":[2,33,37],"664":[10,26,44],"665":[9,45,48],"666":[31,34,36],"667":[12,13,43],"668":[1,20,22],"669":[16,18,29],"670":[6,35,47],"671":[2,40,48],"672":[32,34,42],"673":[7,8,39],"674":[0,28,38],"675":[4,5,44],"676":[15,24,45],"677":[9,19,23],"678":[11,26,33],"679":[21,31,37],"680":[3,14,25],"681":[10,17,27],"682":[30,41,46],"683":[36,37,49],"684":[1,14,32],"685":[11,13,42],"686":[18,44,47],"687":[12,25,39],"688":[0,33,45],"689":[20,31,35],"690":[3,10,21],"691":[15,23,30],"692":[6,8,41],"693":[26,27,28],"694":[2,16,43],"695":[7,22,34],"696":[24,40,49],"697":[19,38,46],"698":[4,9,17],"699":[5,29,48],"700":[6,9,22],"701":[2,3,28],"702":[15,41,47],"703":[10,13,48],"704":[4,17,19],"705":[1,30,33],"706":[11,20,37],"707":[16,38,39],"708":[25,34,45],"709":[5,27,42],"710":[7,36,49],"711":[29,31,32],"712":[0,23,44],"713":[12,40,46],"714":[8,14,21],"715":[18,26,43],"716":[23,24,35],"717":[12,29,40],"718":[4,46,47],"719":[11,24,27],"720":[28,36,43],"721":[0,15,37],"722":[7,9,21],"723":[16,30,39],"724":[22,41,49],"725":[6,32,33],"726":[5,10,20],"727":[25,34,45],"728":[1,2,42],"729":[18,19,48],"730":[26,31,35],"731":[8,14,44],"732":[13,17,38],"733":[3,43,44],"734":[24,33,45],"735":[20,27,38],"736":[16,18,36],"737":[6,14,21],"738":[7,22,40],"739":[0,13,19],"740":[34,48,49],"741":[1,9,32],"742":[8,39,46],"743":[17,31,47],"744":[12,25,30],"745":[26,37,41],"746":[5,23,28],"747":[2,4,35],"748":[15,29,42],"749":[3,10,11],"750":[2,30,49],"751":[1,24,25],"752":[33,44,48],"753":[21,36,45],"754":[27,35,46],"755":[3,6,20],"756":[5,11,47],"757":[7,9,38],"758":[13,15,34],"759":[16,40,41],"760":[12,29,39],"761":[4,19,42],"762":[17,18,22],"763":[10,23,43],"764":[8,26,28],"765":[31,32,37],"766":[0,10,14],"767":[4,41,44],"768":[15,36,49],"769":[12,13,23],"770":[11,29,45],"771":[2,3,19],"772":[7,18,37],"773":[9,21,33],"774":[0,14,30],"775":[17,31,48],"776":[8,24,39],"777":[26,28,38],"778":[1,6,46],"779":[16,22,27],"780":[5,32,42],"781":[34,35,47],"782":[20,25,40],"783":[2,41,43],"784":[8,25,29],"785":[16,26,43],"786":[10,11,48],"787":[28,34,47],"788":[3,31,38],"789":[17,32,35],"790":[1,21,39],"791":[13,42,45],"792":[5,14,23],"793":[18,36,49],"794":[30,37,40],"795":[0,44,46],"796":[9,15,24],"797":[7,12,22],"798":[6,20,27],"799":[4,19,33],"800":[9,21,24],"801":[1,48,49],"802":[8,17,44],"803":[16,25,29],"804":[14,34,36],"805":[0,19,45],"806":[3,12,13],"807":[5,6,41],"808":[35,42,43],"809":[7,26,28],"810":[15,27,37],"811":[20,31,33],"812":[4,23,30],"813":[18,32,38],"814":[2,39,40],"815":[22,46,47],"816":[0,10,11],"817":[37,45,49],"818":[13,33,48],"819":[10,23,25],"820":[1,9,43],"821":[3,4,5],"822":[18,19,46],"823":[17,30,35],"824":[11,24,41],"825":[2,40,44],"826":[15,16,47],"827":[26,29,38],"828":[22,27,32],"829":[6,8,14],"830":[20,28,39],"831":[31,34,42],"832":[12,21,36],"833":[6,7,43],"834":[9,12,48],"835":[0,42,46],"836":[11,36,44],"837":[4,16,23],"838":[17,39,40],"839":[15,35,38],"840":[20,22,25],"841":[5,10,34],"842":[14,37,45],"843":[2,8,41],"844":[3,7,13],"845":[21,33,47],"846":[24,28,30],"847":[26,27,29],"848":[1,18,32],"849":[19,31,49],"850":[10,25,33],"851":[21,22,40],"852":[17,19,24],"853":[15,28,47],"854":[5,13,44],"855":[0,26,31],"856":[4,36,39],"857":[7,23,35],"858":[9,27,38],"859":[14,20,43],"860":[18,42,48],"861":[1,8,37],"862":[2,29,30],"863":[3,12,16],"864":[32,41,46],"865":[6,11,45],"866":[34,43,49],"867":[0,14,47],"868":[3,40,48],"869":[11,22,35],"870":[4,36,41],"871":[23,37,44],"872":[8,26,29],"873":[17,20,42],"874":[24,25,30],"875":[7,10,13],"876":[5,33,38],"877":[9,31,32],"878":[2,21,39],"879":[1,18,34],"880":[12,45,49],"881":[6,16,28],"882":[15,19,27],"883":[2,3,46],"884":[6,28,38],"885":[0,14,36],"886":[13,37,44],"887":[5,30,40],"888":[4,9,10],"889":[8,17,32],"890":[25,42,45],"891":[11,22,35],"892":[12,18,33],"893":[19,26,46],"894":[23,24,48],"895":[15,41,49],"896":[21,27,47],"897":[1,29,34],"898":[7,16,39],"899":[20,31,43],"900":[26,28,45],"901":[7,16,33],"902":[11,14,35],"903":[21,37,49],"904":[2,43,47],"905":[19,46,48],"906":[22,38,42],"907":[18,29,40],"908":[12,39,41],"909":[3,15,23],"910":[5,25,27],"911":[9,24,36],"912":[13,31,44],"913":[1,8,17],"914":[0,4,6],"915":[10,32,34],"916":[20,30,34],"917":[9,37,41],"918":[10,25,29],"919":[4,31,32],"920":[6,15,22],"921":[2,30,38],"922":[27,43,44],"923":[8,26,39],"924":[19,23,24],"925":[3,18,28],"926":[7,48,49],"927":[1,40,42],"928":[0,20,47],"929":[5,13,35],"930":[11,14,16],"931":[33,36,45],"932":[17,21,46],"933":[3,12,23],"934":[11,41,48],"935":[20,31,43],"936":[1,19,26],"937":[18,24,32],"938":[5,10,30],"939":[15,22,29],"940":[9,13,21],"941":[4,7,16],"942":[0,34,38],"943":[44,45,49],"944":[25,33,35],"945":[12,40,47],"946":[2,6,46],"947":[27,37,39],"948":[14,17,42],"949":[8,28,36],"950":[7,8,31],"951":[23,27,37],"952":[33,36,44],"953":[32,39,45],"954":[2,3,34],"955":[4,19,30],"956":[9,24,48],"957":[13,46,47],"958":[0,5,6],"959":[21,22,26],"960":[12,42,49],"961":[10,25,41],"962":[11,15,38],"963":[1,28,40],"964":[14,29,43],"965":[17,18,20],"966":[7,16,35],"967":[2,30,37],"968":[4,11,19],"969":[21,45,47],"970":[0,9,22],"971":[6,12,25],"972":[24,34,38],"973":[17,35,46],"974":[13,15,18],"975":[3,16,42],"976":[32,33,36],"977":[10,14,41],"978":[1,26,48],"979":[8,31,49],"980":[20,27,44],"981":[23,28,29],"982":[5,39,43],"983":[15,33,40],"984":[5,31,40],"985":[1,9,27],"986":[7,17,25],"987":[12,23,45],"988":[37,43,48],"989":[2,8,10],"990":[14,21,30],"991":[34,42,47],"992":[3,32,41],"993":[11,28,36],"994":[18,35,49],"995":[4,39,44],"996":[16,19,26],"997":[0,22,38],"998":[6,24,46],"999":[13,20,29],"1000":[9,29,34],"1001":[12,16,17],"1002":[5,18,21],"1003":[4,32,44],"1004":[30,40,46],"1005":[15,20,25],"1006":[6,37,49],"1007":[22,36,43],"1008":[10,35,41],"1009":[1,13,14],"1010":[26,28,38],"1011":[19,27,33],"1012":[0,3,24],"1013":[31,39,42],"1014":[7,11,47],"1015":[8,23,45],"1016":[2,48,49],"1017":[2,15,17],"1018":[22,34,45],"1019":[4,30,36],"1020":[13,29,42],"1021":[6,11,21],"1022":[16,33,35],"1023":[0,10,26]}
 

 


----------------------------------------------------------------------------
 curl -d '{"partition":"1", "file":"object.ring.gz"}' http://localhost:8080/external?class=Nodes

 Account  	None
 Container	None
 Object   	None


 Partition	1
 Hash     	None

 Server:Port Device	127.0.0.1:6000 127.0.0.1:8103
 Server:Port Device	127.0.0.1:6000 127.0.0.1:8145
 Server:Port Device	127.0.0.1:6000 127.0.0.1:8101
 Server:Port Device	127.0.0.1:6000 127.0.0.1:8128	 [Handoff]
 Server:Port Device	127.0.0.1:6000 127.0.0.1:8134	 [Handoff]
 Server:Port Device	127.0.0.1:6000 127.0.0.1:8147	 [Handoff]


 curl -I -XHEAD "http://127.0.0.1:6000/127.0.0.1:8103/1/None"
 curl -I -XHEAD "http://127.0.0.1:6000/127.0.0.1:8145/1/None"
 curl -I -XHEAD "http://127.0.0.1:6000/127.0.0.1:8101/1/None"
 curl -I -XHEAD "http://127.0.0.1:6000/127.0.0.1:8128/1/None" # [Handoff]
 curl -I -XHEAD "http://127.0.0.1:6000/127.0.0.1:8134/1/None" # [Handoff]
 curl -I -XHEAD "http://127.0.0.1:6000/127.0.0.1:8147/1/None" # [Handoff]


 Use your own device location of servers:
 such as "export DEVICE=/srv/node"
 ssh 127.0.0.1 "ls -lah ${DEVICE:-/srv/node*}/127.0.0.1:8103/objects/1"
 ssh 127.0.0.1 "ls -lah ${DEVICE:-/srv/node*}/127.0.0.1:8145/objects/1"
 ssh 127.0.0.1 "ls -lah ${DEVICE:-/srv/node*}/127.0.0.1:8101/objects/1"
 ssh 127.0.0.1 "ls -lah ${DEVICE:-/srv/node*}/127.0.0.1:8128/objects/1" # [Handoff]
 ssh 127.0.0.1 "ls -lah ${DEVICE:-/srv/node*}/127.0.0.1:8134/objects/1" # [Handoff]
 ssh 127.0.0.1 "ls -lah ${DEVICE:-/srv/node*}/127.0.0.1:8147/objects/1" # [Handoff]

 note: `/srv/node*` is used as default value of `devices`, the real value is set in the config file on each storage node.

------------------------------------------------------------------------------------
curl -d '{"resource":"object"}' http://localhost:8080/external?class=Info

curl -d '{"resource":"container"}' http://localhost:8080/external?class=Info
/swift/sdv/containers/610/8c6/98b01292b094a930f1a0a5a2643f58c6/98b01292b094a930f1a0a5a2643f58c6.db
/swift/sdv/containers/290/c5c/48be213a50267e36e5d29de6ddbaec5c/48be213a50267e36e5d29de6ddbaec5c.db
/swift/sdv/containers/322/d27/508fe2583382232542322fba90443d27/508fe2583382232542322fba90443d27.db
/swift/sdv/containers/808/afb/ca3a354fd916e64e1e0a4e752f2c8afb/ca3a354fd916e64e1e0a4e752f2c8afb.db
/swift/sdv/containers/736/58e/b8220ac4f715aa3cdf068e810f25b58e/b8220ac4f715aa3cdf068e810f25b58e.db
/swift/sdv/containers/854/4cb/d59c610a631fa6acb1a94f81139964cb/d59c610a631fa6acb1a94f81139964cb.db
/swift/sdv/containers/763/ba5/bef450cf45566fe13fa617c754cb9ba5/bef450cf45566fe13fa617c754cb9ba5.db
/swift/sdv/containers/24/931/063948dec68262a1b6c4dd7f63e2f931/063948dec68262a1b6c4dd7f63e2f931.db
/swift/sdv/containers/604/3c3/97397638af37f90cbec2fb437f09b3c3/97397638af37f90cbec2fb437f09b3c3.db
/swift/sdv/containers/468/1f0/75053810bc36966c8f1651f502d0d1f0/75053810bc36966c8f1651f502d0d1f0.db
/swift/sdv/containers/603/9f7/96e90f348f8d45a7288eaeed2473c9f7/96e90f348f8d45a7288eaeed2473c9f7.db

curl -d '{"resource":"account"}' http://localhost:8080/external?class=Info
/swift/sdv/accounts/802/178/c8bcccab3ddbfdc34b08e9223f4f5178/c8bcccab3ddbfdc34b08e9223f4f5178.db

 
 curl -d '{"resource":"account", "file":"/swift/sdv/accounts/802/178/c8bcccab3ddbfdc34b08e9223f4f5178/c8bcccab3ddbfdc34b08e9223f4f5178.db"}' http://localhost:8080/external?class=Info
 Path: /AUTH_test
   Account: AUTH_test
     Account Hash: c8bcccab3ddbfdc34b08e9223f4f5178
     Metadata:
       Created at: 2015-07-15T16:15:57.127740 (1436976957.12774)
         Put Timestamp: 2015-07-15T16:15:57.144980 (1436976957.14498)
	   Delete Timestamp: 1970-01-01T00:00:00.000000 (0)
	     Status Timestamp: 2015-07-15T16:15:57.119550 (1436976957.11955)
	       Container Count: 11
	         Object Count: 10
		   Bytes Used: 120
		     Chexor: 42b7e207f6f3da9d93bd4255abcba156
		       UUID: cbbc43b4-134d-43cc-82c7-5c52901f484c
		       No system metadata found in db file
		       No user metadata found in db file
		       Partition	802
		       Hash     	c8bcccab3ddbfdc34b08e9223f4f5178

		       Server:Port Device	127.0.0.1:6002 sdv


		       curl -I -XHEAD "http://127.0.0.1:6002/sdv/802/AUTH_test"


		       Use your own device location of servers:
		       such as "export DEVICE=/srv/node"
		       ssh 127.0.0.1 "ls -lah ${DEVICE:-/srv/node*}/sdv/accounts/802/178/c8bcccab3ddbfdc34b08e9223f4f5178"

		       note: `/srv/node*` is used as default value of `devices`, the real value is set in the config file on each storage node.

 curl -d '{"resource":"container", "file":"/swift/sdv/containers/603/9f7/96e90f348f8d45a7288eaeed2473c9f7/96e90f348f8d45a7288eaeed2473c9f7.db"}' http://localhost:8080/external?class=Info
 Path: /AUTH_test/dispersion_objects
   Account: AUTH_test
     Container: dispersion_objects
       Container Hash: 96e90f348f8d45a7288eaeed2473c9f7
       Metadata:
         Created at: 2015-07-15T16:15:57.422400 (1436976957.42240)
	   Put Timestamp: 2015-07-16T18:40:58.664400 (1437072058.66440)
	     Delete Timestamp: 1970-01-01T00:00:00.000000 (0)
	       Status Timestamp: 2015-07-15T16:15:57.414170 (1436976957.41417)
	         Object Count: 10
		   Bytes Used: 120
		     Storage Policy: Policy-0 (0)
		       Reported Put Timestamp: 2015-07-16T18:40:58.664400 (1437072058.66440)
		         Reported Delete Timestamp: 1970-01-01T00:00:00.000000 (0)
			   Reported Object Count: 10
			     Reported Bytes Used: 120
			       Chexor: e2a6b13edd3c57767c9d6a75dc34a583
			         UUID: 60e91652-28c3-4144-9085-79941e26832c
				   X-Container-Sync-Point2: -1
				     X-Container-Sync-Point1: -1
				     No system metadata found in db file
				     No user metadata found in db file
				     Partition	603
				     Hash     	96e90f348f8d45a7288eaeed2473c9f7

				     Server:Port Device	127.0.0.1:6001 sdv


				     curl -I -XHEAD "http://127.0.0.1:6001/sdv/603/AUTH_test/dispersion_objects"


				     Use your own device location of servers:
				     such as "export DEVICE=/srv/node"
				     ssh 127.0.0.1 "ls -lah ${DEVICE:-/srv/node*}/sdv/containers/603/9f7/96e90f348f8d45a7288eaeed2473c9f7"

				     note: `/srv/node*` is used as default value of `devices`, the real value is set in the config file on each storage node.

----------------------------------------------------------------------------------------------------
curl -d '{"command":"list", "resource":"dispersion_0", "url":"http://127.0.0.1:8080/auth/v1.0", "user":"test:tester", "key":"testing"}' http://localhost:9090/external?class=Swift 
8100/.wwn
8100/leveldb/leveldb.ldb/000003.log
8100/leveldb/leveldb.ldb/CURRENT
8100/leveldb/leveldb.ldb/LOCK
8100/leveldb/leveldb.ldb/LOG
8100/leveldb/leveldb.ldb/MANIFEST-000002
8101/.wwn
8101/leveldb/leveldb.ldb/000003.log
8101/leveldb/leveldb.ldb/CURRENT
8101/leveldb/leveldb.ldb/LOCK
8101/leveldb/leveldb.ldb/LOG
8101/leveldb/leveldb.ldb/MANIFEST-000002
8102/.wwn
8102/leveldb/leveldb.ldb/000003.log
8102/leveldb/leveldb.ldb/CURRENT
8102/leveldb/leveldb.ldb/LOCK
8102/leveldb/leveldb.ldb/LOG
8102/leveldb/leveldb.ldb/MANIFEST-000002
8103/.wwn
8103/leveldb/leveldb.ldb/000003.log
8103/leveldb/leveldb.ldb/CURRENT
8103/leveldb/leveldb.ldb/LOCK
8103/leveldb/leveldb.ldb/LOG
8103/leveldb/leveldb.ldb/MANIFEST-000002
8104/.wwn
8104/leveldb/leveldb.ldb/000003.log
8104/leveldb/leveldb.ldb/CURRENT
8104/leveldb/leveldb.ldb/LOCK
8104/leveldb/leveldb.ldb/LOG
8104/leveldb/leveldb.ldb/MANIFEST-000002
8105/.wwn
8105/leveldb/leveldb.ldb/000003.log
8105/leveldb/leveldb.ldb/CURRENT
8105/leveldb/leveldb.ldb/LOCK
8105/leveldb/leveldb.ldb/LOG
8105/leveldb/leveldb.ldb/MANIFEST-000002
8106/.wwn
8106/leveldb/leveldb.ldb/000003.log
8106/leveldb/leveldb.ldb/CURRENT
8106/leveldb/leveldb.ldb/LOCK
8106/leveldb/leveldb.ldb/LOG
8106/leveldb/leveldb.ldb/MANIFEST-000002
8107/.wwn
8107/leveldb/leveldb.ldb/000003.log
8107/leveldb/leveldb.ldb/CURRENT
8107/leveldb/leveldb.ldb/LOCK
8107/leveldb/leveldb.ldb/LOG
8107/leveldb/leveldb.ldb/MANIFEST-000002
8108/.wwn
8108/leveldb/leveldb.ldb/000003.log
8108/leveldb/leveldb.ldb/CURRENT
8108/leveldb/leveldb.ldb/LOCK
8108/leveldb/leveldb.ldb/LOG
8108/leveldb/leveldb.ldb/MANIFEST-000002
8109/.wwn
8109/leveldb/leveldb.ldb/000003.log
8109/leveldb/leveldb.ldb/CURRENT
8109/leveldb/leveldb.ldb/LOCK
8109/leveldb/leveldb.ldb/LOG
8109/leveldb/leveldb.ldb/MANIFEST-000002
8110/.wwn
8110/leveldb/leveldb.ldb/000003.log
8110/leveldb/leveldb.ldb/CURRENT
8110/leveldb/leveldb.ldb/LOCK
8110/leveldb/leveldb.ldb/LOG
8110/leveldb/leveldb.ldb/MANIFEST-000002
8111/.wwn
8111/leveldb/leveldb.ldb/000003.log
8111/leveldb/leveldb.ldb/CURRENT
8111/leveldb/leveldb.ldb/LOCK
8111/leveldb/leveldb.ldb/LOG
8111/leveldb/leveldb.ldb/MANIFEST-000002
8112/.wwn
8112/leveldb/leveldb.ldb/000003.log
8112/leveldb/leveldb.ldb/CURRENT
8112/leveldb/leveldb.ldb/LOCK
8112/leveldb/leveldb.ldb/LOG
8112/leveldb/leveldb.ldb/MANIFEST-000002
8113/.wwn
8113/leveldb/leveldb.ldb/000003.log
8113/leveldb/leveldb.ldb/CURRENT
8113/leveldb/leveldb.ldb/LOCK
8113/leveldb/leveldb.ldb/LOG
8113/leveldb/leveldb.ldb/MANIFEST-000002
8114/.wwn
8114/leveldb/leveldb.ldb/000003.log
8114/leveldb/leveldb.ldb/CURRENT
8114/leveldb/leveldb.ldb/LOCK
8114/leveldb/leveldb.ldb/LOG
8114/leveldb/leveldb.ldb/MANIFEST-000002
8115/.wwn
8115/leveldb/leveldb.ldb/000003.log
8115/leveldb/leveldb.ldb/CURRENT
8115/leveldb/leveldb.ldb/LOCK
8115/leveldb/leveldb.ldb/LOG
8115/leveldb/leveldb.ldb/MANIFEST-000002
8116/.wwn
8116/leveldb/leveldb.ldb/000003.log
8116/leveldb/leveldb.ldb/CURRENT
8116/leveldb/leveldb.ldb/LOCK
8116/leveldb/leveldb.ldb/LOG
8116/leveldb/leveldb.ldb/MANIFEST-000002
8117/.wwn
8117/leveldb/leveldb.ldb/000003.log
8117/leveldb/leveldb.ldb/CURRENT
8117/leveldb/leveldb.ldb/LOCK
8117/leveldb/leveldb.ldb/LOG
8117/leveldb/leveldb.ldb/MANIFEST-000002
8118/.wwn
8118/leveldb/leveldb.ldb/000003.log
8118/leveldb/leveldb.ldb/CURRENT
8118/leveldb/leveldb.ldb/LOCK
8118/leveldb/leveldb.ldb/LOG
8118/leveldb/leveldb.ldb/MANIFEST-000002
8119/.wwn
8119/leveldb/leveldb.ldb/000003.log
8119/leveldb/leveldb.ldb/CURRENT
8119/leveldb/leveldb.ldb/LOCK
8119/leveldb/leveldb.ldb/LOG
8119/leveldb/leveldb.ldb/MANIFEST-000002
8120/.wwn
8120/leveldb/leveldb.ldb/000003.log
8120/leveldb/leveldb.ldb/CURRENT
8120/leveldb/leveldb.ldb/LOCK
8120/leveldb/leveldb.ldb/LOG
8120/leveldb/leveldb.ldb/MANIFEST-000002
8121/.wwn
8121/leveldb/leveldb.ldb/000003.log
8121/leveldb/leveldb.ldb/CURRENT
8121/leveldb/leveldb.ldb/LOCK
8121/leveldb/leveldb.ldb/LOG
8121/leveldb/leveldb.ldb/MANIFEST-000002
8122/.wwn
8122/leveldb/leveldb.ldb/000003.log
8122/leveldb/leveldb.ldb/CURRENT
8122/leveldb/leveldb.ldb/LOCK
8122/leveldb/leveldb.ldb/LOG
8122/leveldb/leveldb.ldb/MANIFEST-000002
8123/.wwn
8123/leveldb/leveldb.ldb/000003.log
8123/leveldb/leveldb.ldb/CURRENT
8123/leveldb/leveldb.ldb/LOCK
8123/leveldb/leveldb.ldb/LOG
8123/leveldb/leveldb.ldb/MANIFEST-000002
8124/.wwn
8124/leveldb/leveldb.ldb/000003.log
8124/leveldb/leveldb.ldb/CURRENT
8124/leveldb/leveldb.ldb/LOCK
8124/leveldb/leveldb.ldb/LOG
8124/leveldb/leveldb.ldb/MANIFEST-000002
8125/.wwn
8125/leveldb/leveldb.ldb/000003.log
8125/leveldb/leveldb.ldb/CURRENT
8125/leveldb/leveldb.ldb/LOCK
8125/leveldb/leveldb.ldb/LOG
8125/leveldb/leveldb.ldb/MANIFEST-000002
8126/.wwn
8126/leveldb/leveldb.ldb/000003.log
8126/leveldb/leveldb.ldb/CURRENT
8126/leveldb/leveldb.ldb/LOCK
8126/leveldb/leveldb.ldb/LOG
8126/leveldb/leveldb.ldb/MANIFEST-000002
8127/.wwn
8127/leveldb/leveldb.ldb/000003.log
8127/leveldb/leveldb.ldb/CURRENT
8127/leveldb/leveldb.ldb/LOCK
8127/leveldb/leveldb.ldb/LOG
8127/leveldb/leveldb.ldb/MANIFEST-000002
8128/.wwn
8128/leveldb/leveldb.ldb/000003.log
8128/leveldb/leveldb.ldb/CURRENT
8128/leveldb/leveldb.ldb/LOCK
8128/leveldb/leveldb.ldb/LOG
8128/leveldb/leveldb.ldb/MANIFEST-000002
8129/.wwn
8129/leveldb/leveldb.ldb/000003.log
8129/leveldb/leveldb.ldb/CURRENT
8129/leveldb/leveldb.ldb/LOCK
8129/leveldb/leveldb.ldb/LOG
8129/leveldb/leveldb.ldb/MANIFEST-000002
8130/.wwn
8130/leveldb/leveldb.ldb/000003.log
8130/leveldb/leveldb.ldb/CURRENT
8130/leveldb/leveldb.ldb/LOCK
8130/leveldb/leveldb.ldb/LOG
8130/leveldb/leveldb.ldb/MANIFEST-000002
8131/.wwn
8131/leveldb/leveldb.ldb/000003.log
8131/leveldb/leveldb.ldb/CURRENT
8131/leveldb/leveldb.ldb/LOCK
8131/leveldb/leveldb.ldb/LOG
8131/leveldb/leveldb.ldb/MANIFEST-000002
8132/.wwn
8132/leveldb/leveldb.ldb/000003.log
8132/leveldb/leveldb.ldb/CURRENT
8132/leveldb/leveldb.ldb/LOCK
8132/leveldb/leveldb.ldb/LOG
8132/leveldb/leveldb.ldb/MANIFEST-000002
8133/.wwn
8133/leveldb/leveldb.ldb/000003.log
8133/leveldb/leveldb.ldb/CURRENT
8133/leveldb/leveldb.ldb/LOCK
8133/leveldb/leveldb.ldb/LOG
8133/leveldb/leveldb.ldb/MANIFEST-000002
8134/.wwn
8134/leveldb/leveldb.ldb/000003.log
8134/leveldb/leveldb.ldb/CURRENT
8134/leveldb/leveldb.ldb/LOCK
8134/leveldb/leveldb.ldb/LOG
8134/leveldb/leveldb.ldb/MANIFEST-000002
8135/.wwn
8135/leveldb/leveldb.ldb/000003.log
8135/leveldb/leveldb.ldb/CURRENT
8135/leveldb/leveldb.ldb/LOCK
8135/leveldb/leveldb.ldb/LOG
8135/leveldb/leveldb.ldb/MANIFEST-000002
8136/.wwn
8136/leveldb/leveldb.ldb/000003.log
8136/leveldb/leveldb.ldb/CURRENT
8136/leveldb/leveldb.ldb/LOCK
8136/leveldb/leveldb.ldb/LOG
8136/leveldb/leveldb.ldb/MANIFEST-000002
8137/.wwn
8137/leveldb/leveldb.ldb/000003.log
8137/leveldb/leveldb.ldb/CURRENT
8137/leveldb/leveldb.ldb/LOCK
8137/leveldb/leveldb.ldb/LOG
8137/leveldb/leveldb.ldb/MANIFEST-000002
8138/.wwn
8138/leveldb/leveldb.ldb/000003.log
8138/leveldb/leveldb.ldb/CURRENT
8138/leveldb/leveldb.ldb/LOCK
8138/leveldb/leveldb.ldb/LOG
8138/leveldb/leveldb.ldb/MANIFEST-000002
8139/.wwn
8139/leveldb/leveldb.ldb/000003.log
8139/leveldb/leveldb.ldb/CURRENT
8139/leveldb/leveldb.ldb/LOCK
8139/leveldb/leveldb.ldb/LOG
8139/leveldb/leveldb.ldb/MANIFEST-000002
8140/.wwn
8140/leveldb/leveldb.ldb/000003.log
8140/leveldb/leveldb.ldb/CURRENT
8140/leveldb/leveldb.ldb/LOCK
8140/leveldb/leveldb.ldb/LOG
8140/leveldb/leveldb.ldb/MANIFEST-000002
8141/.wwn
8141/leveldb/leveldb.ldb/000003.log
8141/leveldb/leveldb.ldb/CURRENT
8141/leveldb/leveldb.ldb/LOCK
8141/leveldb/leveldb.ldb/LOG
8141/leveldb/leveldb.ldb/MANIFEST-000002
8142/.wwn
8142/leveldb/leveldb.ldb/000003.log
8142/leveldb/leveldb.ldb/CURRENT
8142/leveldb/leveldb.ldb/LOCK
8142/leveldb/leveldb.ldb/LOG
8142/leveldb/leveldb.ldb/MANIFEST-000002
8143/.wwn
8143/leveldb/leveldb.ldb/000003.log
8143/leveldb/leveldb.ldb/CURRENT
8143/leveldb/leveldb.ldb/LOCK
8143/leveldb/leveldb.ldb/LOG
8143/leveldb/leveldb.ldb/MANIFEST-000002
8144/.wwn
8144/leveldb/leveldb.ldb/000003.log
8144/leveldb/leveldb.ldb/CURRENT
8144/leveldb/leveldb.ldb/LOCK
8144/leveldb/leveldb.ldb/LOG
8144/leveldb/leveldb.ldb/MANIFEST-000002
8145/.wwn
8145/leveldb/leveldb.ldb/000003.log
8145/leveldb/leveldb.ldb/CURRENT
8145/leveldb/leveldb.ldb/LOCK
8145/leveldb/leveldb.ldb/LOG
8145/leveldb/leveldb.ldb/MANIFEST-000002
8146/.wwn
8146/leveldb/leveldb.ldb/000003.log
8146/leveldb/leveldb.ldb/CURRENT
8146/leveldb/leveldb.ldb/LOCK
8146/leveldb/leveldb.ldb/LOG
8146/leveldb/leveldb.ldb/MANIFEST-000002
8147/.wwn
8147/leveldb/leveldb.ldb/000003.log
8147/leveldb/leveldb.ldb/CURRENT
8147/leveldb/leveldb.ldb/LOCK
8147/leveldb/leveldb.ldb/LOG
8147/leveldb/leveldb.ldb/MANIFEST-000002
8148/.wwn
8148/leveldb/leveldb.ldb/000003.log
8148/leveldb/leveldb.ldb/CURRENT
8148/leveldb/leveldb.ldb/LOCK
8148/leveldb/leveldb.ldb/LOG
8148/leveldb/leveldb.ldb/MANIFEST-000002
8149/.wwn
8149/leveldb/leveldb.ldb/000003.log
8149/leveldb/leveldb.ldb/CURRENT
8149/leveldb/leveldb.ldb/LOCK
8149/leveldb/leveldb.ldb/LOG
8149/leveldb/leveldb.ldb/MANIFEST-000002
swift.conf
----------------------------------------------------------------------------------------
curl -d '{"command":"stat", "resource":"dispersion_0", "url":"http://127.0.0.1:8080/auth/v1.0", "user":"test:tester", "key":"testing"}' http://localhost:9090/external?class=Swift

         Account: AUTH_test
       Container: dispersion_0
         Objects: 301
           Bytes: 3289854
        Read ACL:
       Write ACL:
         Sync To:
        Sync Key:
   Accept-Ranges: bytes
X-Storage-Policy: Policy-0
     X-Timestamp: 1436976957.30105
      X-Trans-Id: tx0d6dad2de32b4683bcd5a-0055bad5a1
    Content-Type: text/plain; charset=utf-8
----------------------------------------------------------------------------------------------------


curl -d  http://localhost:9090/external/swift?class=Discovery
[{"IPAddrss":"192.168.32.15","Board Mfg":"Supermicro","Board Serial":"","FRU Device Description":"Builtin FRU Device (ID 0)","Board Mfg Date":"Sun Dec 31 16","Product Serial":""},{"IPAddrss":"192.168.32.14","Board Mfg":"Supermicro","Board Serial":"","FRU Device Description":"Builtin FRU Device (ID 0)","Board Mfg Date":"Sun Dec 31 16","Product Serial":""},{"IPAddrss":"192.168.32.11","Board Mfg":"Supermicro","Board Serial":"","FRU Device Description":"Builtin FRU Device (ID 0)","Board Mfg Date":"Sun Dec 31 16","Product Serial":""},{"IPAddrss":"192.168.32.10","Product Manufacturer":"Supermicro","Chassis Serial":"C801LAD48A50054","Board Mfg":"Supermicro","Board Serial":"","Product Part Number":"SSG-K1048-RT-ST20B","FRU Device Description":"Builtin FRU Device (ID 0)","Board Mfg Date":"Sun Dec 31 16","Product Serial":"S191109X5627804","Chassis Type":"Unspecified"},{"IPAddrss":"192.168.32.12","Board Mfg":"Supermicro","Board Serial":"","FRU Device Description":"Builtin FRU Device (ID 0)","Board Mfg Date":"Sun Dec 31 16","Product Serial":""},{"IPAddrss":"192.168.32.13","Board Mfg":"Supermicro","Board Serial":"","FRU Device Description":"Builtin FRU Device (ID 0)","Board Mfg Date":"Sun Dec 31 16","Product Serial":""}]

