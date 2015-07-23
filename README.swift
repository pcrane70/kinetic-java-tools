Environment Variables:
SWIFT_DIR: This variable change the default Swift Dir from /etc/swift to user
defined values.
Example:
 export SWIFT_DIR =/mydir

Alternatively, the following K/V in Json format can be used to change the directory for each REST call
"dir":"/home/my-swift-dir"
Example:
curl -d '{"msg":"proxy", "dir":"/home/my-swift-dir"}' http://localhost:8080/external?class=Config


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
curl -d '{"msg":"object"}' http://localhost:8080/external?class=Partitions

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

curl -d '{"msg":"populate"}' http://localhost:8080/external?class=Dispersion
Created 10 containers for dispersion reporting, 0s, 0 retries
Created 10 objects for dispersion reporting, 0s, 0 retries
----------------------------------------------------------------------------
curl -d '{"msg":"report"}' http://localhost:8080/external?class=Dispersion
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

curl -d '{"msg":"container"}' http://localhost:8080/external?class=Init
container-server running (3647 - /etc/swift/container-server.conf)
-----------------------------------------------------------------------
curl -d '{"msg":"proxy"}' http://localhost:8080/external?class=Init
proxy-server running (3646 - /etc/swift/proxy-server.conf)

------------------------------------------------------------------------
curl -d '{"msg":"account"}' http://localhost:8080/external?class=Init
account-server running (3648 - /etc/swift/account-server.conf)

-------------------------------------------------------------------------
curl -d '{"msg":"object"}' http://localhost:8080/external?class=Init

object-server running (3649 - /etc/swift/object-server.conf)
----------------------------------------------------------------------------
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
-------------------------------------------------------------------------------------------------------


To get the Object Ring:
curl -d '{"msg":"object"}' http://localhost:8080/external?class=Partitions
Sample output:
0 = 2,5,16
1 = 1,3,45
2 = 18,44,46
3 = 10,20,27
4 = 21,23,31
5 = 12,14,41
6 = 19,38,49
7 = 15,39,48
8 = 24,25,32
9 = 9,26,30
10 = 13,35,36
11 = 0,6,8
12 = 11,28,47
13 = 7,40,42
14 = 29,33,34
15 = 4,17,22
16 = 1,37,43
17 = 25,44,47
18 = 4,17,19
19 = 33,37,39
20 = 14,27,43
21 = 7,36,48
22 = 18,30,41
23 = 9,28,42
24 = 5,38,49
25 = 8,20,35
26 = 21,22,34
27 = 6,11,29
28 = 12,13,46
29 = 3,16,40
30 = 2,26,45
31 = 15,24,31
32 = 0,10,23
33 = 11,32,34
34 = 5,24,40
35 = 6,8,27
36 = 29,37,42
37 = 12,13,19
38 = 1,20,47
39 = 2,15,18
40 = 17,21,38
41 = 9,10,48
42 = 7,22,25
43 = 32,35,43
44 = 3,23,26
45 = 28,31,39
46 = 0,41,45
47 = 4,16,46
48 = 14,33,49
49 = 30,36,44
50 = 6,29,42
51 = 18,31,49
52 = 1,5,46
53 = 14,38,47
54 = 13,21,36
55 = 3,16,45
56 = 11,19,26
57 = 17,33,44
58 = 10,35,40
59 = 12,24,39
60 = 20,25,32
61 = 7,27,37
62 = 0,4,34
63 = 8,23,43
64 = 2,22,41
65 = 15,28,30
66 = 3,9,48
67 = 19,22,32
68 = 0,9,47
69 = 28,37,40
70 = 7,27,33
71 = 8,15,17
72 = 12,36,39
73 = 31,35,38
74 = 2,18,46
75 = 25,26,45
76 = 29,41,48
77 = 16,20,30
78 = 4,23,49
79 = 11,24,44
80 = 5,6,10
81 = 14,42,43
82 = 1,13,34
83 = 21,23,43
84 = 2,26,41
85 = 21,24,31
86 = 18,32,44
87 = 22,25,30
88 = 27,40,46
89 = 10,34,38
90 = 0,13,17
91 = 6,15,36
92 = 42,45,49
93 = 20,37,47
94 = 4,5,39
95 = 3,29,35
96 = 9,11,12
97 = 7,19,48
98 = 1,14,28
99 = 8,16,33
100 = 13,17,30
101 = 7,11,19
102 = 5,23,49
103 = 2,29,36
104 = 15,25,41
105 = 9,35,42
106 = 24,40,43
107 = 32,46,47
108 = 1,3,45
109 = 27,28,39
110 = 26,33,48
111 = 8,18,21
112 = 20,22,37
113 = 16,31,44
114 = 0,10,34
115 = 6,12,14
116 = 4,22,38
117 = 11,21,42
118 = 8,44,45
119 = 7,15,26
120 = 30,38,43
121 = 5,33,41
122 = 0,20,37
123 = 16,34,36
124 = 2,13,35
125 = 3,14,25
126 = 19,31,49
127 = 23,40,48
128 = 17,32,47
129 = 1,9,27
130 = 6,12,29
131 = 4,28,39
132 = 10,24,46
133 = 0,18,49
134 = 2,3,44
135 = 9,38,40
136 = 12,17,18
137 = 8,34,39
138 = 6,30,47
139 = 13,15,37
140 = 4,14,28
141 = 22,41,43
142 = 16,19,24
143 = 21,25,35
144 = 10,11,48
145 = 1,23,45
146 = 5,42,46
147 = 7,20,32
148 = 27,31,33
149 = 26,29,36
150 = 15,28,32
151 = 1,3,27
152 = 30,33,45
153 = 13,39,41
154 = 9,40,46
155 = 2,26,43
156 = 17,34,44
157 = 24,35,36
158 = 7,10,21
159 = 12,18,42
160 = 8,19,22
161 = 14,23,48
162 = 6,20,31
163 = 5,25,29
164 = 11,16,47
165 = 4,38,49
166 = 0,11,37
167 = 3,39,43
168 = 29,30,45
169 = 2,22,32
170 = 20,31,46
171 = 14,19,33
172 = 5,7,42
173 = 13,34,35
174 = 15,16,37
175 = 10,18,24
176 = 8,28,48
177 = 21,47,49
178 = 6,25,27
179 = 0,9,12
180 = 4,23,26
181 = 17,38,41
182 = 1,36,44
183 = 11,24,40
184 = 37,39,40
185 = 2,8,29
186 = 12,14,43
187 = 10,23,34
188 = 28,33,36
189 = 31,41,49
190 = 1,47,48
191 = 5,7,22
192 = 4,13,27
193 = 9,38,46
194 = 18,19,30
195 = 0,17,45
196 = 3,15,21
197 = 6,16,26
198 = 32,35,44
199 = 20,25,42
200 = 3,24,46
201 = 2,22,35
202 = 19,38,45
203 = 14,18,21
204 = 28,33,37
205 = 9,31,39
206 = 32,40,44
207 = 13,16,43
208 = 4,6,42
209 = 10,15,27
210 = 7,20,49
211 = 12,23,48
212 = 11,17,26
213 = 8,29,30
214 = 0,25,34
215 = 1,36,47
216 = 5,41,42
217 = 9,12,38
218 = 31,33,40
219 = 2,5,44
220 = 0,17,30
221 = 1,7,23
222 = 32,39,43
223 = 14,35,37
224 = 4,16,49
225 = 3,25,28
226 = 10,18,21
227 = 22,24,41
228 = 8,26,29
229 = 13,36,48
230 = 15,20,47
231 = 19,45,46
232 = 11,27,34
233 = 6,12,22
234 = 17,35,42
235 = 6,10,46
236 = 1,13,33
237 = 4,27,34
238 = 18,47,49
239 = 19,21,26
240 = 0,9,11
241 = 38,40,44
242 = 3,25,45
243 = 5,31,36
244 = 23,24,29
245 = 28,37,43
246 = 16,30,41
247 = 8,32,39
248 = 2,7,20
249 = 14,15,48
250 = 4,43,45
251 = 2,28,49
252 = 10,13,23
253 = 15,20,31
254 = 32,34,36
255 = 0,9,41
256 = 8,35,48
257 = 17,19,37
258 = 5,18,24
259 = 12,14,22
260 = 7,29,33
261 = 11,26,42
262 = 1,25,40
263 = 30,39,47
264 = 27,44,46
265 = 3,21,38
266 = 6,15,16
267 = 11,25,43
268 = 18,24,41
269 = 6,20,23
270 = 17,32,49
271 = 3,29,48
272 = 21,34,35
273 = 8,45,47
274 = 0,10,28
275 = 1,16,30
276 = 27,31,42
277 = 9,22,37
278 = 26,36,46
279 = 5,13,19
280 = 2,4,7
281 = 12,14,44
282 = 33,38,40
283 = 0,39,47
284 = 2,17,34
285 = 11,27,37
286 = 3,9,23
287 = 15,32,41
288 = 6,20,28
289 = 13,39,48
290 = 16,30,33
291 = 1,21,31
292 = 10,14,42
293 = 7,8,40
294 = 4,18,46
295 = 38,44,45
296 = 12,29,43
297 = 5,24,25
298 = 26,35,49
299 = 19,22,36
300 = 9,14,25
301 = 11,26,37
302 = 4,40,43
303 = 2,3,42
304 = 12,46,49
305 = 0,18,31
306 = 15,16,44
307 = 29,30,48
308 = 20,32,39
309 = 24,34,47
310 = 6,7,38
311 = 10,19,33
312 = 21,23,27
313 = 1,17,45
314 = 5,22,35
315 = 13,36,41
316 = 1,8,28
317 = 13,20,38
318 = 11,22,34
319 = 0,18,29
320 = 10,27,44
321 = 12,42,45
322 = 5,25,40
323 = 41,43,46
324 = 3,30,48
325 = 2,31,36
326 = 16,17,35
327 = 23,26,49
328 = 14,19,32
329 = 4,7,33
330 = 8,9,15
331 = 6,21,39
332 = 28,37,47
333 = 0,22,24
334 = 6,33,39
335 = 11,43,47
336 = 13,19,27
337 = 7,24,34
338 = 4,35,40
339 = 3,16,48
340 = 2,31,44
341 = 10,17,41
342 = 12,30,46
343 = 14,20,36
344 = 21,28,32
345 = 8,29,49
346 = 1,38,42
347 = 5,15,37
348 = 9,23,26
349 = 18,25,45
350 = 0,3,26
351 = 9,16,29
352 = 23,37,41
353 = 6,35,39
354 = 14,28,33
355 = 20,36,48
356 = 8,43,46
357 = 2,21,32
358 = 11,12,13
359 = 27,30,31
360 = 10,17,18
361 = 22,44,47
362 = 7,19,34
363 = 25,42,49
364 = 15,24,45
365 = 1,4,38
366 = 5,15,40
367 = 1,18,49
368 = 17,26,33
369 = 3,22,36
370 = 16,29,45
371 = 4,25,42
372 = 24,28,31
373 = 20,23,46
374 = 0,12,19
375 = 38,47,48
376 = 14,34,37
377 = 7,9,13
378 = 10,30,43
379 = 27,35,39
380 = 8,11,44
381 = 2,21,32
382 = 5,6,41
383 = 13,33,40
384 = 2,19,39
385 = 3,37,43
386 = 7,8,48
387 = 4,14,49
388 = 23,24,40
389 = 5,9,20
390 = 10,18,46
391 = 22,38,44
392 = 0,29,47
393 = 21,25,32
394 = 6,11,30
395 = 16,28,41
396 = 1,26,27
397 = 12,35,42
398 = 17,34,36
399 = 15,31,45
400 = 6,36,47
401 = 3,4,16
402 = 14,23,43
403 = 38,40,46
404 = 7,30,39
405 = 0,18,41
406 = 1,2,31
407 = 22,29,49
408 = 9,10,20
409 = 13,33,35
410 = 19,32,44
411 = 17,37,45
412 = 12,42,48
413 = 5,24,28
414 = 11,25,26
415 = 15,21,27
416 = 1,8,34
417 = 8,14,19
418 = 10,33,35
419 = 12,26,27
420 = 13,37,40
421 = 34,36,46
422 = 31,38,45
423 = 11,23,29
424 = 0,21,32
425 = 7,16,20
426 = 2,24,49
427 = 25,30,39
428 = 5,6,18
429 = 42,47,48
430 = 3,17,28
431 = 9,41,43
432 = 4,15,44
433 = 21,22,26
434 = 27,39,49
435 = 15,41,44
436 = 11,23,28
437 = 7,12,48
438 = 17,40,42
439 = 14,16,20
440 = 29,32,34
441 = 2,19,45
442 = 6,9,31
443 = 5,18,24
444 = 22,43,46
445 = 0,33,47
446 = 1,10,37
447 = 3,4,25
448 = 13,30,35
449 = 8,36,38
450 = 13,15,30
451 = 1,3,44
452 = 21,22,35
453 = 11,31,33
454 = 4,47,48
455 = 19,39,45
456 = 18,29,38
457 = 6,23,25
458 = 0,12,42
459 = 17,27,41
460 = 2,7,46
461 = 9,24,26
462 = 5,10,36
463 = 16,32,34
464 = 8,20,28
465 = 14,37,43
466 = 12,40,49
467 = 28,36,49
468 = 11,15,43
469 = 1,5,46
470 = 9,30,44
471 = 8,13,39
472 = 35,37,40
473 = 3,18,20
474 = 7,47,48
475 = 16,17,27
476 = 2,23,25
477 = 0,32,34
478 = 14,21,38
479 = 19,22,41
480 = 10,31,42
481 = 4,24,33
482 = 6,29,45
483 = 6,26,49
484 = 16,39,47
485 = 5,31,48
486 = 18,42,43
487 = 15,27,34
488 = 3,20,29
489 = 12,35,45
490 = 8,10,40
491 = 1,22,24
492 = 4,13,23
493 = 37,38,46
494 = 2,14,41
495 = 21,25,32
496 = 7,11,30
497 = 19,36,44
498 = 0,28,33
499 = 9,17,26
500 = 22,28,30
501 = 23,39,49
502 = 4,20,43
503 = 25,32,44
504 = 33,34,47
505 = 0,13,36
506 = 12,27,29
507 = 9,18,42
508 = 7,8,17
509 = 14,15,37
510 = 10,31,38
511 = 3,16,41
512 = 2,40,48
513 = 5,26,35
514 = 1,6,21
515 = 11,19,46
516 = 10,24,45
517 = 4,6,11
518 = 0,20,48
519 = 3,24,39
520 = 37,38,49
521 = 7,8,32
522 = 14,26,27
523 = 16,43,45
524 = 12,23,40
525 = 25,34,36
526 = 13,42,46
527 = 17,18,19
528 = 9,21,29
529 = 30,35,47
530 = 5,31,33
531 = 15,22,41
532 = 1,2,44
533 = 0,11,28
534 = 10,19,36
535 = 34,44,45
536 = 1,23,39
537 = 13,24,31
538 = 7,18,21
539 = 4,5,26
540 = 14,27,33
541 = 29,32,37
542 = 3,15,35
543 = 9,20,41
544 = 2,8,42
545 = 6,43,46
546 = 17,30,40
547 = 12,47,49
548 = 22,25,38
549 = 16,28,48
550 = 8,22,46
551 = 13,25,37
552 = 9,18,27
553 = 12,15,40
554 = 20,33,35
555 = 2,5,23
556 = 10,42,43
557 = 16,41,45
558 = 1,21,49
559 = 17,28,32
560 = 24,39,44
561 = 4,26,30
562 = 29,38,48
563 = 7,36,47
564 = 3,6,31
565 = 0,11,19
566 = 14,34,42
567 = 3,8,19
568 = 10,30,32
569 = 4,40,47
570 = 2,28,46
571 = 0,1,27
572 = 7,17,44
573 = 12,23,29
574 = 11,22,31
575 = 5,9,43
576 = 16,21,26
577 = 18,34,39
578 = 14,25,37
579 = 20,24,38
580 = 6,35,41
581 = 13,36,48
582 = 15,45,49
583 = 33,43,46
584 = 18,36,44
585 = 1,14,45
586 = 26,28,40
587 = 7,22,30
588 = 24,25,35
589 = 5,41,49
590 = 15,17,20
591 = 8,9,31
592 = 16,32,39
593 = 6,19,21
594 = 2,13,48
595 = 11,42,47
596 = 23,34,38
597 = 3,4,27
598 = 0,29,37
599 = 10,12,33
600 = 0,20,49
601 = 24,27,47
602 = 17,26,29
603 = 7,15,21
604 = 1,2,22
605 = 14,39,45
606 = 9,10,34
607 = 11,31,40
608 = 28,32,33
609 = 12,19,35
610 = 36,41,48
611 = 5,13,37
612 = 4,44,46
613 = 18,38,42
614 = 16,30,43
615 = 6,23,25
616 = 3,8,17
617 = 13,33,42
618 = 22,31,43
619 = 20,47,48
620 = 4,21,37
621 = 24,25,44
622 = 18,28,32
623 = 5,10,19
624 = 2,34,49
625 = 9,16,30
626 = 0,12,45
627 = 23,40,41
628 = 15,29,36
629 = 3,26,39
630 = 6,11,46
631 = 7,8,35
632 = 1,14,38
633 = 17,24,27
634 = 11,32,34
635 = 30,39,41
636 = 3,21,22
637 = 5,20,36
638 = 0,23,35
639 = 26,28,40
640 = 14,15,33
641 = 18,29,38
642 = 7,42,44
643 = 2,4,31
644 = 9,16,43
645 = 6,10,12
646 = 1,8,49
647 = 45,47,48
648 = 13,25,46
649 = 19,27,37
650 = 20,29,32
651 = 14,28,47
652 = 8,27,39
653 = 3,4,40
654 = 0,24,46
655 = 15,16,38
656 = 11,36,43
657 = 7,12,49
658 = 21,30,35
659 = 6,13,41
660 = 17,19,23
661 = 5,18,22
662 = 1,25,42
663 = 2,33,37
664 = 10,26,44
665 = 9,45,48
666 = 31,34,36
667 = 12,13,43
668 = 1,20,22
669 = 16,18,29
670 = 6,35,47
671 = 2,40,48
672 = 32,34,42
673 = 7,8,39
674 = 0,28,38
675 = 4,5,44
676 = 15,24,45
677 = 9,19,23
678 = 11,26,33
679 = 21,31,37
680 = 3,14,25
681 = 10,17,27
682 = 30,41,46
683 = 36,37,49
684 = 1,14,32
685 = 11,13,42
686 = 18,44,47
687 = 12,25,39
688 = 0,33,45
689 = 20,31,35
690 = 3,10,21
691 = 15,23,30
692 = 6,8,41
693 = 26,27,28
694 = 2,16,43
695 = 7,22,34
696 = 24,40,49
697 = 19,38,46
698 = 4,9,17
699 = 5,29,48
700 = 6,9,22
701 = 2,3,28
702 = 15,41,47
703 = 10,13,48
704 = 4,17,19
705 = 1,30,33
706 = 11,20,37
707 = 16,38,39
708 = 25,34,45
709 = 5,27,42
710 = 7,36,49
711 = 29,31,32
712 = 0,23,44
713 = 12,40,46
714 = 8,14,21
715 = 18,26,43
716 = 23,24,35
717 = 12,29,40
718 = 4,46,47
719 = 11,24,27
720 = 28,36,43
721 = 0,15,37
722 = 7,9,21
723 = 16,30,39
724 = 22,41,49
725 = 6,32,33
726 = 5,10,20
727 = 25,34,45
728 = 1,2,42
729 = 18,19,48
730 = 26,31,35
731 = 8,14,44
732 = 13,17,38
733 = 3,43,44
734 = 24,33,45
735 = 20,27,38
736 = 16,18,36
737 = 6,14,21
738 = 7,22,40
739 = 0,13,19
740 = 34,48,49
741 = 1,9,32
742 = 8,39,46
743 = 17,31,47
744 = 12,25,30
745 = 26,37,41
746 = 5,23,28
747 = 2,4,35
748 = 15,29,42
749 = 3,10,11
750 = 2,30,49
751 = 1,24,25
752 = 33,44,48
753 = 21,36,45
754 = 27,35,46
755 = 3,6,20
756 = 5,11,47
757 = 7,9,38
758 = 13,15,34
759 = 16,40,41
760 = 12,29,39
761 = 4,19,42
762 = 17,18,22
763 = 10,23,43
764 = 8,26,28
765 = 31,32,37
766 = 0,10,14
767 = 4,41,44
768 = 15,36,49
769 = 12,13,23
770 = 11,29,45
771 = 2,3,19
772 = 7,18,37
773 = 9,21,33
774 = 0,14,30
775 = 17,31,48
776 = 8,24,39
777 = 26,28,38
778 = 1,6,46
779 = 16,22,27
780 = 5,32,42
781 = 34,35,47
782 = 20,25,40
783 = 2,41,43
784 = 8,25,29
785 = 16,26,43
786 = 10,11,48
787 = 28,34,47
788 = 3,31,38
789 = 17,32,35
790 = 1,21,39
791 = 13,42,45
792 = 5,14,23
793 = 18,36,49
794 = 30,37,40
795 = 0,44,46
796 = 9,15,24
797 = 7,12,22
798 = 6,20,27
799 = 4,19,33
800 = 9,21,24
801 = 1,48,49
802 = 8,17,44
803 = 16,25,29
804 = 14,34,36
805 = 0,19,45
806 = 3,12,13
807 = 5,6,41
808 = 35,42,43
809 = 7,26,28
810 = 15,27,37
811 = 20,31,33
812 = 4,23,30
813 = 18,32,38
814 = 2,39,40
815 = 22,46,47
816 = 0,10,11
817 = 37,45,49
818 = 13,33,48
819 = 10,23,25
820 = 1,9,43
821 = 3,4,5
822 = 18,19,46
823 = 17,30,35
824 = 11,24,41
825 = 2,40,44
826 = 15,16,47
827 = 26,29,38
828 = 22,27,32
829 = 6,8,14
830 = 20,28,39
831 = 31,34,42
832 = 12,21,36
833 = 6,7,43
834 = 9,12,48
835 = 0,42,46
836 = 11,36,44
837 = 4,16,23
838 = 17,39,40
839 = 15,35,38
840 = 20,22,25
841 = 5,10,34
842 = 14,37,45
843 = 2,8,41
844 = 3,7,13
845 = 21,33,47
846 = 24,28,30
847 = 26,27,29
848 = 1,18,32
849 = 19,31,49
850 = 10,25,33
851 = 21,22,40
852 = 17,19,24
853 = 15,28,47
854 = 5,13,44
855 = 0,26,31
856 = 4,36,39
857 = 7,23,35
858 = 9,27,38
859 = 14,20,43
860 = 18,42,48
861 = 1,8,37
862 = 2,29,30
863 = 3,12,16
864 = 32,41,46
865 = 6,11,45
866 = 34,43,49
867 = 0,14,47
868 = 3,40,48
869 = 11,22,35
870 = 4,36,41
871 = 23,37,44
872 = 8,26,29
873 = 17,20,42
874 = 24,25,30
875 = 7,10,13
876 = 5,33,38
877 = 9,31,32
878 = 2,21,39
879 = 1,18,34
880 = 12,45,49
881 = 6,16,28
882 = 15,19,27
883 = 2,3,46
884 = 6,28,38
885 = 0,14,36
886 = 13,37,44
887 = 5,30,40
888 = 4,9,10
889 = 8,17,32
890 = 25,42,45
891 = 11,22,35
892 = 12,18,33
893 = 19,26,46
894 = 23,24,48
895 = 15,41,49
896 = 21,27,47
897 = 1,29,34
898 = 7,16,39
899 = 20,31,43
900 = 26,28,45
901 = 7,16,33
902 = 11,14,35
903 = 21,37,49
904 = 2,43,47
905 = 19,46,48
906 = 22,38,42
907 = 18,29,40
908 = 12,39,41
909 = 3,15,23
910 = 5,25,27
911 = 9,24,36
912 = 13,31,44
913 = 1,8,17
914 = 0,4,6
915 = 10,32,34
916 = 20,30,34
917 = 9,37,41
918 = 10,25,29
919 = 4,31,32
920 = 6,15,22
921 = 2,30,38
922 = 27,43,44
923 = 8,26,39
924 = 19,23,24
925 = 3,18,28
926 = 7,48,49
927 = 1,40,42
928 = 0,20,47
929 = 5,13,35
930 = 11,14,16
931 = 33,36,45
932 = 17,21,46
933 = 3,12,23
934 = 11,41,48
935 = 20,31,43
936 = 1,19,26
937 = 18,24,32
938 = 5,10,30
939 = 15,22,29
940 = 9,13,21
941 = 4,7,16
942 = 0,34,38
943 = 44,45,49
944 = 25,33,35
945 = 12,40,47
946 = 2,6,46
947 = 27,37,39
948 = 14,17,42
949 = 8,28,36
950 = 7,8,31
951 = 23,27,37
952 = 33,36,44
953 = 32,39,45
954 = 2,3,34
955 = 4,19,30
956 = 9,24,48
957 = 13,46,47
958 = 0,5,6
959 = 21,22,26
960 = 12,42,49
961 = 10,25,41
962 = 11,15,38
963 = 1,28,40
964 = 14,29,43
965 = 17,18,20
966 = 7,16,35
967 = 2,30,37
968 = 4,11,19
969 = 21,45,47
970 = 0,9,22
971 = 6,12,25
972 = 24,34,38
973 = 17,35,46
974 = 13,15,18
975 = 3,16,42
976 = 32,33,36
977 = 10,14,41
978 = 1,26,48
979 = 8,31,49
980 = 20,27,44
981 = 23,28,29
982 = 5,39,43
983 = 15,33,40
984 = 5,31,40
985 = 1,9,27
986 = 7,17,25
987 = 12,23,45
988 = 37,43,48
989 = 2,8,10
990 = 14,21,30
991 = 34,42,47
992 = 3,32,41
993 = 11,28,36
994 = 18,35,49
995 = 4,39,44
996 = 16,19,26
997 = 0,22,38
998 = 6,24,46
999 = 13,20,29
1000 = 9,29,34
1001 = 12,16,17
1002 = 5,18,21
1003 = 4,32,44
1004 = 30,40,46
1005 = 15,20,25
1006 = 6,37,49
1007 = 22,36,43
1008 = 10,35,41
1009 = 1,13,14
1010 = 26,28,38
1011 = 19,27,33
1012 = 0,3,24
1013 = 31,39,42
1014 = 7,11,47
1015 = 8,23,45
1016 = 2,48,49
1017 = 2,15,17
1018 = 22,34,45
1019 = 4,30,36
1020 = 13,29,42
1021 = 6,11,21
1022 = 16,33,35
1023 = 0,10,26


