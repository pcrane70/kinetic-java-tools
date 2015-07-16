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

