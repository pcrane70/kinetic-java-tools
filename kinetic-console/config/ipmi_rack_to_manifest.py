# This python script builds a Kinetic hardware manifest for a storage
# rack. Each chassis will be queried for its Kinetic drives.
#
# Assumptions:
# (1) IPMI interface for chassis
# (2) existence of 'ipmi' command-line utility
# (3) same (matching) admin userid/password values for all chassis in rack
# (4) 12 drives per chassis
#
# For use with REAL hardware, set the appropriate values for the
# following variables (at bottom of file):
#
#    real_user  (user id of admin user on chassis)
#    real_password  (password of admin user on chassis)
#    real_host_list  (list of IP addresses; 1 IP address per chassis)
#    use_real_hardware = True
#
# This script can also create a manifest for a SIMULATED rack. This may
# be useful to see what the format would be for a rack of 6 storage
# chassis, with each chassis containing 12 Kinetic drives.
#
#    use_real_hardware = False
#
# The script will output Kinetic hardware manifest in JSON format
#

import json
import subprocess
import sys


IPMI_CHASSIS_DRIVE_RESPONSE_SIZE = 284*2
KINETIC_DRIVE_PORT = 8123
KINETIC_DRIVE_TLS_PORT = 8443

SAMPLE_DRIVE = "01 50 00 c5 00 79 87 e7 98 00 00 00 00 00 00 00 " \
                 "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 " \
                 "00 00 00 1c 00 00 00 00 00 00 02 06 00 00 01 02 " \
                 "04 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 " \
                 "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 " \
                 "00 00 53 65 61 67 61 74 65 00 00 00 00 00 00 00 " \
                 "00 00 53 54 34 30 30 30 4e 4b 30 30 31 2d 31 4e " \
                 "58 36 15 ac 10 00 01 ff ff 00 00 00 00 00 00 00 " \
                 "00 00 00 00 ac 10 11 46 ff ff 00 00 00 00 00 00 " \
                 "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 " \
                 "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 " \
                 "00 00 00 00 00 00 00 15 ac 11 00 01 ff ff 00 00 " \
                 "00 00 00 00 00 00 00 00 00 ac 11 02 8f ff ff 00 " \
                 "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 " \
                 "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 " \
                 "00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 00 " \
                 "01 00 00 00 00 00 00 00 00 00 11 c6 02 24 cc 00 " \
                 "11 c6 02 24 cd 01 01 03 00 05 00 00"


# bytes 1 to 8
def get_wwn(drive_data):
    return drive_data[1 * 2 : 9 * 2]
	    
# bytes 265 to 270
def get_mac_1(drive_data):
    return drive_data[265 * 2 : 271 * 2]
        
def convert_to_ip(drive_data):
    IP = ''
    i = 0
    for j in range(0,4):
        if len(IP) > 0:
            IP += "."
        rc = drive_data[i : i+2]
        i += 2
        IP += "%d" % int(rc, 16)
    return IP
            
# bytes 132 to 135
def get_ip_1(drive_data):
    return convert_to_ip(drive_data[132 * 2 : 136 * 2])

# bytes 271 to 276
def get_mac_2(drive_data):
    return drive_data[271 * 2: 277 * 2]

# bytes 201 to 204
def get_ip_2(drive_data):
    return convert_to_ip(drive_data[201 * 2 : 205 * 2])


class Rack:
    
    def __init__(self, identifier):
        self._list_chassis = []
        self._identifier = identifier
        self._x = ''
        self._y = ''
        self._z = ''
        
    def set_identifier(self, identifier):
        self._identifier = identifier
        
    def set_coordinate(self, x, y, z):
        self._x = x
        self._y = y
        self._z = z

    def add_chassis(self, chassis):
        if chassis is not None:
            self._list_chassis.append(chassis)
        
    def update_values(self, is_sample):
        if self._identifier is None or len(self._identifier) == 0:
            self._identifier = '1'
            
        # to make wwn and IP addresses unique
        starting_drive_number = 1
            
        chassis_number = 1
        for chassis in self._list_chassis:
            chassis.set_identifier(str(chassis_number))
            chassis.set_coordinate(str(chassis_number), '', '')
            chassis.update_values(is_sample, starting_drive_number)
            chassis_number += 1
            starting_drive_number += chassis.drive_count()
        
    def is_empty(self):
        return 0 == len(self._list_chassis)
        
    def to_dict(self):
        dict_rack = {}
        dict_rack['id'] = self._identifier
        coord = {}
        coord['x'] = self._x
        coord['y'] = self._y
        coord['z'] = self._z
        dict_rack['coordinate'] = coord
        chassis_list = []
        for chassis in self._list_chassis:
            if not chassis.is_empty():
                chassis_list.append(chassis.to_dict())
            
        dict_rack['chassis'] = chassis_list
        return dict_rack


class Chassis:
    
    def __init__(self):
        self._list_devices = []
        self._identifier = None
        self._x = ''
        self._y = ''
        self._z = ''
        
    def set_identifier(self, identifier):
        self._identifier = identifier
        
    def set_coordinate(self, x, y, z):
        self._x = x
        self._y = y
        self._z = z

    def add_device(self, device):
        if device is not None:
            self._list_devices.append(device)
        
    def update_values(self, is_sample, starting_drive_number):
        if self._identifier is None or len(self._identifier) == 0:
            self._identifier = '1'
            
        drive_number = 1
        for drive in self._list_devices:
            drive.set_coordinate(str(drive_number), '', '')
            drive.update_values(starting_drive_number + drive_number - 1)
            drive_number += 1

    def to_dict(self):
        dict_chassis = {}
        coord = {}
        coord['x'] = self._x
        coord['y'] = self._y
        coord['z'] = self._z
        dict_chassis['coordinate'] = coord
        device_list = []
        for device in self._list_devices:
            device_list.append(device.to_dict())
            
        dict_chassis['devices'] = device_list
        return dict_chassis
        
    def is_empty(self):
        return 0 == len(self._list_devices)
        
    def drive_count(self):
        return len(self._list_devices)


class Device:

    @staticmethod
    def parse_drive_string(drive_string):
        # remove whitespace
        drive_data = drive_string.replace('\n','')
        drive_data = drive_data.replace(' ','')

        if len(drive_data) != IPMI_CHASSIS_DRIVE_RESPONSE_SIZE:
            print "invalid IPMI chassis data, len=%d" % len(drive_data)
            return None
              
        ips = []
        ips.append(get_ip_1(drive_data))
        ips.append(get_ip_2(drive_data))
        device = Device()
        device_id = DeviceId()
        device_id.set_port(KINETIC_DRIVE_PORT)
        device_id.set_tls_port(KINETIC_DRIVE_TLS_PORT)
        device_id.set_wwn(get_wwn(drive_data))
        device_id.set_ips(ips)
        device.set_device_id(device_id)
        return device
    
    def __init__(self):
        self._device_id = None
        self._x = ''
        self._y = ''
        self._z = ''
        
    def set_coordinate(self, x, y, z):
        self._x = x
        self._y = y
        self._z = z

    def set_device_id(self, device_id):
        self._device_id = device_id
        
    def update_values(self, device_number):
        self._device_id.update_values(device_number)

    def print_info(self):
        print "WWN: %s" % self._device_id.get_wwn()
        print "IP-1: %s" % self._device_id.get_ips()[0]
        print "IP-2: %s" % self._device_id.get_ips()[1]
        
    def to_dict(self):
        dict_device = {}
        dict_device['deviceId'] = self._device_id.to_dict()
        coords = {}
        coords['x'] = self._x
        coords['y'] = self._y
        coords['z'] = self._z
        dict_device['coordinate'] = coords
        return dict_device


class DeviceId:
    
    def __init__(self):
        self._port = KINETIC_DRIVE_PORT
        self._tls_port = KINETIC_DRIVE_TLS_PORT
        self._wwn = None
        self._ips = []
        
    def get_port(self):
        return self._port
        
    def set_port(self, port):
        self._port = port
        
    def get_tls_port(self):
        return self._tls_port
        
    def set_tls_port(self, tls_port):
        self._tls_port = tls_port
        
    def get_wwn(self):
        return self._wwn
        
    def set_wwn(self, wwn):
        self._wwn = wwn
        
    def get_ips(self):
        return self._ips
        
    def set_ips(self, ips):
        self._ips = ips

    def update_values(self, device_number):
        # force wwn and ip addresses to be unique
        device_number_string = str(device_number)
        device_number_digits = len(device_number_string)
        
        wwn_prefix = self._wwn[0:len(self._wwn)-device_number_digits]
        wwn_suffix = device_number_string
        self._wwn = wwn_prefix + wwn_suffix
        
        updated_ips = []
        for ip in self._ips:
            pos_last_dot = ip.rfind('.')
            ip_prefix = ip[0:pos_last_dot+1]
            ip_suffix = device_number_string
            updated_ip = ip_prefix + ip_suffix
            updated_ips.append(updated_ip)
            
        self._ips = updated_ips

    def to_dict(self):
        dict_device_id = {}
        dict_device_id['ips'] = self._ips
        dict_device_id['port'] = self._port
        dict_device_id['tlsPort'] = self._tls_port
        dict_device_id['wwn'] = self._wwn
        return dict_device_id


class IPMI:
    
    def __init__(self, user_id, password, host):
        self._user_id = user_id
        self._password = password
        self._host = host
        
    def run_command(self, command):
        try:
            return run_os_command(command)
        except OSError:
            print "error: unable to run ipmitool utility"
            sys.exit(1)

    def query_cmd_chassis_drive_info(self, drive_number):
        return IPMI.cmd_for_retrieve_chassis_drive_info(self._user_id, self._password, self._host, drive_number)
        
    def query_chassis_drive(self, drive_number):
        if (self._user_id is not None and self._password is not None and self._host is not None):
            cmd = self.query_cmd_chassis_drive_info(drive_number)
            resp = self.run_command(cmd)
        else:
            resp = (0, SAMPLE_DRIVE)
        
        exit_code = resp[0]
        cmd_response = resp[1]
        drive_dict = {}
        if exit_code == 0 and len(cmd_response) > 0:
            drive_dict = Device.parse_drive_string(cmd_response)
        
        return (exit_code, drive_dict)
        
    @staticmethod
    def cmd_for_retrieve_chassis_drive_info(user_id, pword, host, drive_number):
        return 'ipmitool,-Ilan,-U%s,-P%s,-H%s,raw,0x30,0x70,0x86,%d' % \
                (user_id, pword, host, drive_number)


def run_os_command(command):
    cmd = [command]
    p = subprocess.Popen(cmd, stdout=subprocess.PIPE)
    cmd_output = ''
    for line in p.stdout:
        cmd_output += line
    p.wait()
    return (p.returncode, cmd_output)

def query_chassis(connect_info, chassis_number):
    
    user_id = connect_info[0]
    password = connect_info[1]
    host = connect_info[2]
    
    if user_id is None and password is None and host is None:
        is_sample = True
    else:
        is_sample = False

    ipmi = IPMI(user_id, password, host)
    chassis = Chassis()
    
    for i in range(1, 13):
        drive_number = i
        device_tuple = ipmi.query_chassis_drive(drive_number)
        exit_code = device_tuple[0]
        device = device_tuple[1]
    
        if exit_code == 0 and device is not None:
            chassis.add_device(device)
        else:
            print "unable to retrieve drive information"
            
    if chassis.is_empty():
        return None
    else:
        return chassis


def query_rack(rack_id, rack_x, rack_y, rack_z, user, password, host_list):
    
    if user is None and password is None:
        is_sample = True
    else:
        is_sample = False
    
    chassis_number = 1
    rack = Rack(rack_id)
    rack.set_coordinate(rack_x, rack_y, rack_z)
    
    if len(rack_host_list) > 0:
        for host in rack_host_list:
            connect_info = (user, password, host)
            chassis = query_chassis(connect_info, chassis_number)
            if chassis is not None:
                rack.add_chassis(chassis)
            chassis_number += 1
        
        rack.update_values(is_sample)

    return rack


if __name__=='__main__':
    
    rack_id = '1'
    rack_x = 'acme'
    rack_y = 'lab'
    rack_z = 'rack'
    
    # real chassis hardware values go here
    real_user = 'ADMIN'
    real_password = 'ADMIN'
    # list of chassis mgmt IP addresses in rack
    real_host_list = ['192.168.1.99']
    
    sample_user = None
    sample_password = None
    # list of chassis mgmt IP addresses in rack
    sample_host_list = [None, None, None, None, None, None]

    # ****  Toggle between real and simulated hardware    
    use_real_hardware = True
    
    if use_real_hardware:
        params = (real_user, real_password, real_host_list)
    else:
        params = (sample_user, sample_password, sample_host_list)
    
    user = params[0]
    password = params[1]
    rack_host_list = params[2]  

    rack = query_rack(rack_id, rack_x, rack_y, rack_z, user, password, rack_host_list)
    if rack.is_empty():
        print "empty rack"
    else:
        print json.dumps(rack.to_dict())

