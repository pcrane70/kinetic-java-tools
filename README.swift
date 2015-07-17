Environment Variables:
SWIFT_DIR: This variable change the default Swift Dir from /etc/swift to user
defined values.
Example:
 export SWIFT_DIR =/mydir

For extracting configurations use Config class. 
Examples:
curl -d '{"msg":"proxy"}' http://localhost:8080/external?class=Config
curl -d '{"msg":"account"}' http://localhost:8080/external?class=Config
curl -d '{"msg":"object"}' http://localhost:8080/external?class=Config
curl -d '{"msg":"container"}' http://localhost:8080/external?class=Config

For extracting populating dispersion  use Dispersion class. 
Examples:
curl -d '{"msg":"populate"}' http://localhost:8080/external?class=Dispersion
curl -d '{"msg":"report"}' http://localhost:8080/external?class=Dispersion

To check the status of the each server use the Init class
Examples:
curl -d '{"msg":"container"}' http://localhost:8080/external?class=Init
curl -d '{"msg":"proxy"}' http://localhost:8080/external?class=Init
curl -d '{"msg":"account"}' http://localhost:8080/external?class=Init
curl -d '{"msg":"object"}' http://localhost:8080/external?class=Init

To get the Recon status of each server use the Recon class
Examples:
curl -d '{"msg":"account"}' http://localhost:8080/external?class=Recon
curl -d '{"msg":"container"}' http://localhost:8080/external?class=Recon
curl -d '{"msg":"object"}' http://localhost:8080/external?class=Recon
curl -d '{"msg":"proxy"}' http://localhost:8080/external?class=Recon

To find the status of each ring use the Ring class
Examples:
curl -d '{"msg":"account"}' http://localhost:8080/external?class=Ring
curl -d '{"msg":"container"}' http://localhost:8080/external?class=Ring
curl -d '{"msg":"object"}' http://localhost:8080/external?class=Ring

============================  SAMPLE OUTPUT========================

curl -d '{"msg":"proxy"}' http://localhost:8080/external?class=Config
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

curl -d '{"msg":"account"}' http://localhost:8080/external?class=Config

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

curl -d '{"msg":"container"}' http://localhost:8080/external?class=Config

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
curl -d '{"msg":"object"}' http://localhost:8080/external?class=Config
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




curl -d '{"msg":"account"}' http://localhost:8080/external?class=Ring
account.builder, build version 1
1024 partitions, 3.000000 replicas, 1 regions, 1 zones, 1 devices, 0.00 balance, 0.00 dispersion
The minimum number of hours before a partition can be reassigned is 1
The overload factor is 0.00% (0.000000)
Devices:    id  region  zone      ip address  port  replication ip  replication port      name weight partitions balance meta
             0       1     1       127.0.0.1  6002       127.0.0.1              6002       sdv   1.00       3072    0.00
	    
 ----------------------------------------
 curl -d '{"msg":"conatiner"}' http://localhost:9090/external?class=Ring
container.builder, build version 1
1024 partitions, 3.000000 replicas, 1 regions, 1 zones, 1 devices, 0.00 balance, 0.00 dispersion
The minimum number of hours before a partition can be reassigned is 1
The overload factor is 0.00% (0.000000)
Devices:    id  region  zone      ip address  port  replication ip  replication port      name weight partitions balance meta
             0       1     1       127.0.0.1  6001       127.0.0.1              6001       sdv   1.00       3072    0.00 

 ----------------------------------------
 curl -d '{"msg":"object"}' http://localhost:9090/external?class=Ring 
object.builder, build version 50
1024 partitions, 3.000000 replicas, 1 regions, 1 zones, 50 devices, 0.91 balance, 0.00 dispersion
The minimum number of hours before a partition can be reassigned is 1
The overload factor is 0.00% (0.000000)
Devices:    id  region  zone      ip address  port  replication ip  replication port      name weight partitions balance meta
             0       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8100   1.00         62    0.91 
             1       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8101   1.00         61   -0.72 
             2       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8102   1.00         62    0.91 
             3       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8103   1.00         61   -0.72 
             4       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8104   1.00         62    0.91 
             5       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8105   1.00         61   -0.72 
             6       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8106   1.00         62    0.91 
             7       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8107   1.00         61   -0.72 
             8       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8108   1.00         61   -0.72 
             9       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8109   1.00         61   -0.72 
            10       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8110   1.00         62    0.91 
            11       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8111   1.00         62    0.91 
            12       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8112   1.00         61   -0.72 
            13       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8113   1.00         62    0.91 
            14       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8114   1.00         61   -0.72 
            15       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8115   1.00         62    0.91 
            16       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8116   1.00         62    0.91 
            17       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8117   1.00         62    0.91 
            18       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8118   1.00         61   -0.72 
            19       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8119   1.00         61   -0.72 
            20       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8120   1.00         61   -0.72 
            21       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8121   1.00         62    0.91 
            22       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8122   1.00         62    0.91 
            23       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8123   1.00         61   -0.72 
            24       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8124   1.00         61   -0.72 
            25       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8125   1.00         61   -0.72 
            26       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8126   1.00         62    0.91 
            27       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8127   1.00         61   -0.72 
            28       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8128   1.00         61   -0.72 
            29       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8129   1.00         62    0.91 
            30       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8130   1.00         62    0.91 
            31       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8131   1.00         61   -0.72 
            32       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8132   1.00         61   -0.72 
            33       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8133   1.00         62    0.91 
            34       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8134   1.00         62    0.91 
            35       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8135   1.00         62    0.91 
            36       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8136   1.00         62    0.91 
            37       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8137   1.00         61   -0.72 
            38       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8138   1.00         61   -0.72 
            39       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8139   1.00         61   -0.72 
            40       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8140   1.00         61   -0.72 
            41       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8141   1.00         61   -0.72 
            42       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8142   1.00         62    0.91 
            43       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8143   1.00         61   -0.72 
            44       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8144   1.00         61   -0.72 
            45       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8145   1.00         62    0.91 
            46       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8146   1.00         61   -0.72 
            47       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8147   1.00         61   -0.72 
            48       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8148   1.00         61   -0.72 
            49       1     1       127.0.0.1  6000       127.0.0.1              6000 127.0.0.1:8149   1.00         62    0.91 

