#
# Copyright (C) 2015 Seagate Technology.
#
# This library is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 2.1 of the License, or (at your option) any later version.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with this library; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
#


# 3rd-Party Dependencies
#=======================
# Urwid (Python console user interface library) - http://urwid.org
# Github repo: https://github.com/wardi/urwid.git

# Seagate Dependencies
#=====================
# kinetic-java
# Github repo: https://github.com/Seagate/kinetic-java


# Prioritized To-Do Items (all subject to change)
#========================

# High
#------------------------

# Medium
#------------------------
# cluster ping
# support for optional args (use ssl, identity, hmac, clversion, etc.)
# add ktool secureerase
# audit/log file
# hook up form field tooltips
# hook up form field validators
# drive ops: put, get, delete, getnext, getprev, getkeyrange
# nicer looking UI (colors)
# status panel at bottom of screen
# menu item enable/disable visual indicator

# Low
#------------------------
# menu fastpath from command line
# option to be in 'non-execute' (-x) mode
# media scan, media optimize
# ability to view Kinetic protocol definition file from github
# hook up form field max length specifiers

# Not Prioritized
#------------------------
# ability to feed command output (while running) to update UI
# ability to read 'cluster' of drives from file (maybe not needed with ktool?)


import commands
import datetime
import os.path
import sys
import time

import urwid   # python console user interface library



# Java Kinetic Admin CLI source
#https://github.com/Seagate/kinetic-java/blob/master/kinetic-client/src/main/java/com/seagate/kinetic/admin/cli/KineticAdminCLI.java
#https://github.com/Seagate/kinetic-java/blob/master/kinetic-client/src/main/java/com/seagate/kinetic/admin/cli/DiscoverDevice.java

# Python Kinetic CLI tools
#https://github.com/Seagate/kinetic-py-tools/blob/master/scripts/cluster_update.py
#https://github.com/Seagate/kinetic-py-tools/blob/master/scripts/discover.py
#https://github.com/Seagate/kinetic-py-tools/blob/master/scripts/update.py


SHELL_NAME                 = "bash"

PATH_DISCOVERED_FILES      = "discovered_drives.txt"

PROGRAM_NAME               = "Kinetic Tool"
PROGRAM_VERSION            = "0.1"
PROGRAM_COPYRIGHT          = "(c) Seagate Technologies, 2015"

# required and optional indicators for form fields
REQ                        = "* "
OPT                        = "  "

UI_TEXT_YES                = "Yes"
UI_TEXT_NO                 = "No"
UI_TEXT_OK                 = "OK"

LBL_ADMIN_ERASE            = "Instant Erase"
LBL_ADMIN_LOCK             = "Lock Drive"
LBL_ADMIN_SECURE_ERASE     = "Secure Erase"
LBL_ADMIN_UNLOCK           = "Unlock Drive"
LBL_CLSTR_FILE             = "Cluster Name"
LBL_DISCOVER_DRIVES        = "Discover Drives"
LBL_DRIVE_FILE             = "Drives File"
LBL_FIRMWARE_FILE          = "Firmware file"
LBL_FOUND_DRIVES           = "Found Drives"
LBL_GETLOG_CAPACITY        = "GetLog Capacity"
LBL_GETLOG_CONFIGURATION   = "GetLog Configuration"
LBL_GETLOG_LIMITS          = "GetLog Limits"
LBL_GETLOG_MESSAGES        = "GetLog Messages"
LBL_GETLOG_STATISTICS      = "GetLog Statistics"
LBL_GETLOG_TEMPERATURE     = "GetLog Temperature"
LBL_GETLOG_UTILIZATION     = "GetLog Utilization"
LBL_HOST                   = "Host"
LBL_JAVA_INFO              = "Java Info"
LBL_KEY                    = "Key"
LBL_META                   = "Metadata only"
LBL_NEW_CLUSTER_VERSION    = "New version"
LBL_NEW_PIN                = "New PIN"
LBL_OLD_PIN                = "Old PIN"
LBL_OPEN_CLUSTER           = "Open Cluster"
LBL_PIN                    = "PIN"
LBL_REALLY_QUIT            = "Really Quit?"
LBL_SEC_FILE               = "Security File"
LBL_SET_CLUSTER_VERSION    = "Set Cluster Version"
LBL_SET_ERASE_PIN          = "Set Erase PIN"
LBL_SET_LOCK_PIN           = "Set Lock PIN"
LBL_SET_SECURITY           = "Set Security/ACL"
LBL_SUBNET                 = "Subnet"
LBL_UPDATE_FIRMWARE        = "Update Firmware"
LBL_VALUE                  = "Value"
LBL_VERSION                = "Version"

MNU_PROGRAM                = "Program"
MNU_DRIVE_LOG              = "Drive Logs"
MNU_DRIVE_OPERATION        = "Drive Operations"
MNU_DRIVE_ADMIN            = "Drive Admin"
MNU_DRIVE_SECURITY         = "Drive Security"
MNU_CLUSTER                = "Cluster"
MNU_HELP                   = "Help"

MNU_ADMIN_DISCOVER         = "Discover Drives"
MNU_ADMIN_LOCK_DEVICE      = "Lock Device"
MNU_ADMIN_UNLOCK_DEVICE    = "Unlock Device"
MNU_ADMIN_ERASE            = "Instant Erase"
MNU_ADMIN_SECURE_ERASE     = "Secure Erase"
MNU_ADMIN_SET_CLUSTER_VER  = "Set Cluster Version"
MNU_ADMIN_UPDATE_FIRMWARE  = "Update Firmware"
MNU_ADMIN_MEDIA_SCAN       = "Media Scan"
MNU_ADMIN_MEDIA_OPTIMIZE   = "Media Optimize"

MNU_LOG_CAPACITY           = "Capacity"
MNU_LOG_CONFIG             = "Configuration"
MNU_LOG_DEVICE             = "Device"
MNU_LOG_LIMITS             = "Limits"
MNU_LOG_MESSAGES           = "Messages"
MNU_LOG_STATS              = "Statistics"
MNU_LOG_TEMPS              = "Temperatures"
MNU_LOG_UTILS              = "Utilizations"

MNU_OP_PING                = "Ping"
MNU_OP_PUT                 = "Put"
MNU_OP_GET                 = "Get"
MNU_OP_GET_NEXT            = "Get Next"
MNU_OP_GET_PREV            = "Get Previous"
MNU_OP_GET_KEY_RANGE       = "Get Key Range"
MNU_OP_DELETE              = "Delete"

MNU_PROG_ABOUT             = "About"
MNU_PROG_SYS_INFO          = "Sys Info"
MNU_PROG_JAVA_INFO         = "Java Info"
MNU_PROG_PYTHON_INFO       = "Python Info"
MNU_PROG_QUIT              = "Quit"

MNU_SEC_SET_ACL            = "Set Security/ACL"
MNU_SEC_SET_ERASE_PIN      = "Set Erase PIN"
MNU_SEC_SET_LOCK_PIN       = "Set Lock PIN"

MNU_CLSTR_DISCOVER         = "Discover Drives"
MNU_CLSTR_OPEN             = "Open Cluster"
MNU_CLSTR_CLOSE            = "Close Cluster"
MNU_CLSTR_PING             = "Ping Drives"
MNU_CLSTR_LOG_CAPACITY     = "Drive Capacity"
MNU_CLSTR_FIRMWARE_UPD     = "Firmware Update"
MNU_CLSTR_FIRMWARE_VER     = "Firmware Version"
MNU_CLSTR_LOG_STATS        = "Drive Stats"
MNU_CLSTR_LOG_TEMPS        = "Drive Temperatures"
MNU_CLSTR_LOG_UTILS        = "Drive Utilizations"
MNU_CLSTR_KEY_PUT          = "Put Key"
MNU_CLSTR_KEY_DELETE       = "Delete Key"
MNU_CLSTR_SET_VERSION      = "Set Cluster Version"
MNU_CLSTR_LOCK_DRIVES      = "Lock Drives"
MNU_CLSTR_UNLOCK_DRIVES    = "Unlock Drives"
MNU_CLSTR_ERASE            = "Instant Erase Drives"
MNU_CLSTR_SECURE_ERASE     = "Secure Erase Drives"

MNU_HELP_VIEW_PROTOCOL     = "View Kinetic Protocol"


# TT = 'tool tip' (help text for individual fields)
TT_CLUSTER_VERSION_TARGET  = "drive to receive new cluster version"
TT_DISC_SUBNET             = "subnet to scan (i.e., 192.168.0)"
TT_ERASE_PIN               = "erase PIN"
TT_ERASE_TARGET            = "drive to erase"
TT_FIRMWARE_FILE           = "file containing drive firmware"
TT_FIRMWARE_TARGET         = "drive to receive firmware update"
TT_GETLOG_DRIVE            = "drive to query"
TT_LOCK_PIN                = "lock PIN"
TT_LOCK_HOST               = "drive to lock"
TT_NEW_CLUSTER_VERSION     = "new cluster version"
TT_SECURE_ERASE_TARGET     = "drive to secure erase"
TT_UNLOCK_PIN              = "unlock PIN"
TT_UNLOCK_HOST             = "drive to unlock"

# max characters per field
MAX_FILE                   = 40
MAX_HOST                   = 20
MAX_KEY                    = 40
MAX_VALUE                  = 40
MAX_PIN                    = 10
MAX_SUBNET                 = 11

F_CHK = 'checkbox'
F_TXT = 'text'       # text entry/edit field

field_mgr = None
cluster_open = False
cluster_drive_file = ''
cluster_drive_list = []


#####################################################################
def system_time_millis():
    return int(time.time() * 1000)

#####################################################################
def filter_java_output(output_lines):
    filtered_lines = []
    for output_line in output_lines:
        pos_com_seagate = output_line.find("com.seagate.")
        pos_log_info = output_line.find("INFO: ")
        pos_set_timeout = output_line.find("setRequestTimeoutMillis")
        pos_kinetic_client = output_line.find("kinetic.client")
        if -1 == pos_com_seagate and 0 != pos_log_info and \
           -1 == pos_set_timeout and -1 == pos_kinetic_client:
            filtered_lines.append(output_line)

    return filtered_lines 

#####################################################################
def close_box(button):
    top.pop()

#####################################################################
def display_output_lines(title, output_lines):
    body = [urwid.Text(title), urwid.Divider()]
    for output_line in output_lines:
        t = urwid.Text(output_line)
        body.append(urwid.AttrMap(t, None, focus_map='reversed'))
    body.append(urwid.Divider())
    btn = urwid.Button('Dismiss')
    urwid.connect_signal(btn, 'click', close_box)
    btn = urwid.AttrMap(btn, None, focus_map='reversed')
    body.append(btn)
    w = urwid.ListBox(urwid.SimpleFocusListWalker(body))
    box = urwid.BoxAdapter(w, height=20)
    top.open_output_box(urwid.Filler(box))

#####################################################################
def drive_ping(drive):
    #TODO: change this to use NOOP
    cmd = "%s %s/kineticAdmin.sh -getlog -type limits -host %s" % (SHELL_NAME, path_kinetic_java_client, drive)
    rc, output = run_command(cmd, "Drive Ping")
    if rc == 0 and len(output) > 0:
        return True
    else:
        return False

#####################################################################
def drive_put(drive, key, value):
    #TODO: implement drive_put
    return True

#####################################################################
def drive_put_file(drive, key, file_path):
    #TODO: implement drive_put_file
    return True

#####################################################################
def drive_delete(drive, key, cl_version=None, identity=None):
    #TODO: implement drive_delete
    return True

#####################################################################
def drive_get(drive, key, metadata_only):
    #TODO: implement drive_get
    return (True, '')

#####################################################################
def set_cluster_drive_file(drive_file):
    global cluster_open
    global cluster_drive_file
    cluster_drive_file = drive_file
    if drive_file is not None and len(drive_file) > 0:
        cluster_open = True
    else:
        cluster_open = False

#####################################################################
def set_cluster_drives(drive_list):
    global cluster_open
    global cluster_drive_list
    cluster_drive_list = drive_list
    if drive_list is not None and len(drive_list) > 0:
        cluster_open = True
    else:
        cluster_open = False

#####################################################################
def read_file_lines(file_path):
    with open(file_path) as f:
        return f.readlines()
    return None

#####################################################################
class CascadingBoxes(urwid.WidgetPlaceholder):
    max_box_levels = 4

    #################################################################
    def __init__(self, box):
        super(CascadingBoxes, self).__init__(urwid.SolidFill(u'/'))
        self.box_level = 0
        self.open_box(box)

    #################################################################
    def open_box(self, box):
        self.original_widget = urwid.Overlay(urwid.LineBox(box),
            self.original_widget,
            align='center', width=('relative', 80),
            valign='middle', height=('relative', 80),
            min_width=24, min_height=8,
            left=self.box_level * 3,
            right=(self.max_box_levels - self.box_level - 1) * 3,
            top=self.box_level * 2,
            bottom=(self.max_box_levels - self.box_level - 1) * 2)
        self.box_level += 1

    #################################################################
    def open_output_box(self, box):
        self.original_widget = urwid.Overlay(urwid.LineBox(box),
            self.original_widget,
            align='center', width=('relative', 80),
            valign='middle', height=('relative', 80),
            min_width=75, min_height=20,
            left=10,
            right=10,
            top=2,
            bottom=3)
        self.box_level += 1

    #################################################################
    def pop(self):
        self.original_widget = self.original_widget[0]
        self.box_level -= 1

    #################################################################
    def keypress(self, size, key):
        if key == 'esc' and self.box_level > 1:
            self.pop()
        else:
            return super(CascadingBoxes, self).keypress(size, key)

#####################################################################
class FieldManager(object):
    """ 
    This class manages the field data without being entangled in the 
    implementation details of the widget set.
    """
    def __init__(self):
        self.getters = {}
        self.required_checkers = {}
        self.label_getters = {}

    def set_required_checker(self, name, function):
        self.required_checkers[name] = function

    def set_label_getter(self, name, function):
        self.label_getters[name] = function

    def set_getter(self, name, function):
        """ 
        This is where we collect all of the field getter functions.
        """
        self.getters[name] = function

    def get_value(self, name):
        """
        This will actually get the value associated with a field name.
        """
        return self.getters[name]()

    def get_value_dict(self):
        """
        Dump everything we've got.
        """
        missing_values = []
        retval = {}
        for key in self.getters:
            fld_required = self.required_checkers[key]()
            fld_value = self.getters[key]()
            if fld_required:
                if 0 == len(fld_value):
                    lbl = self.label_getters[key]()
                    missing_values.append(lbl)

            retval[key] = fld_value
        if len(missing_values) > 0:
            display_output_lines('Missing Required Fields', missing_values)
            return None
        else:
            return retval

#####################################################################
def get_field(field_def, fieldmgr):
    fld_label     = field_def[0]
    fld_name      = field_def[1]
    fld_type      = field_def[2]
    fld_tooltip   = field_def[3]
    fld_max_chars = field_def[4]
    fld_validator = field_def[5]

    # we don't have hanging indent, but we can stick a bullet out into the 
    # left column.
    label = urwid.Text(('label', fld_label))
    colon = urwid.Text(('label', ': '))

    if fld_type == F_TXT:
        field_widget = urwid.Edit('', '')
        def get_label():
            return fld_label
        def is_required():
            # if it starts with our required prefix
            pos_req = fld_label.find(REQ)
            return pos_req == 0
        def getter():
            """ 
            Closure around urwid.Edit.get_edit_text(), which we'll
            use to scrape the value out when we're all done.
            """
            return field_widget.get_edit_text()
        fieldmgr.set_getter(fld_name, getter)
        fieldmgr.set_required_checker(fld_name, is_required)
        fieldmgr.set_label_getter(fld_name, get_label)
    elif fld_type == F_CHK:
        field_widget = urwid.CheckBox('')
        def get_label():
            return fld_label
        def is_required():
            return False

        def getter():
            """ 
            Closure around urwid.CheckBox.get_state(), which we'll
            use to scrape the value out when we're all done. 
            """
            return field_widget.get_state()
        fieldmgr.set_getter(fld_name, getter)
        fieldmgr.set_required_checker(fld_name, is_required)
        fieldmgr.set_label_getter(fld_name, get_label)

    field_widget = urwid.AttrWrap(field_widget, 'field', 'fieldfocus')

    # put everything together.  Each column is either 'fixed' for a fixed width,
    # or given a 'weight' to help determine the relative width of the column
    # such that it can fill the row.
    editwidget = urwid.Columns([
                                ('weight', 1, label),
                                ('fixed',  2, colon),
                                ('weight', 2, field_widget)])

    wrapper = urwid.AttrWrap(editwidget, None, {'label':'labelfocus'})
    return urwid.Padding(wrapper, ('fixed left', 3), ('fixed right', 3))

#####################################################################
def form_ok_handler(extra_args, handler, button):
    fieldmgr = extra_args
    dict_values = fieldmgr.get_value_dict()
    if dict_values is not None:
        handler(dict_values)

#####################################################################
def form_button(caption, callback, fieldmgr):
    button = urwid.Button(caption)
    urwid.connect_signal(button, 'click', form_ok_handler, user_args=[fieldmgr, callback])
    return urwid.AttrMap(button, None, focus_map='reversed')

#####################################################################
def menu_button(caption, callback):
    if callback == not_implemented:
        caption = caption + ' (NI)'
    
    button = urwid.Button(caption)
    urwid.connect_signal(button, 'click', callback)
    btn_attr_map = None
    btn_focus_map = 'reversed'

    return urwid.AttrMap(button, btn_attr_map, focus_map=btn_focus_map)

#####################################################################
def sub_menu(caption, choices):
    contents = menu(caption, choices)
    def open_menu(button):
        return top.open_box(contents)
    return menu_button([caption, u'...'], open_menu)

#####################################################################
def menu(title, choices):
    body = [urwid.Text(title), urwid.Divider()]
    body.extend(choices)
    return urwid.ListBox(urwid.SimpleFocusListWalker(body))

#####################################################################
def not_implemented(button):
    message_box("Not yet implemented")

#####################################################################
def run_form(form_title, fields, on_ok_handler):
    global field_mgr
    title = urwid.Text(form_title)
    form_widgets = [title, urwid.Divider(bottom=2)]
    field_mgr = FieldManager()

    for field_def in fields:
        form_widgets.append(get_field(field_def, field_mgr)) 

    form_widgets.append(urwid.Divider())
    ok = form_button(UI_TEXT_OK, on_ok_handler, field_mgr)
    form_widgets.append(ok)
    listbox = urwid.Pile(form_widgets)
    #listbox = urwid.ListBox(urwid.SimpleFocusListWalker(form_widgets))
    top.open_box(urwid.Filler(listbox))

#####################################################################
def run_command(cmd, title=None):
    show_running_box(title)
    results = commands.getstatusoutput(cmd)
    hide_running_box()
    return results

#####################################################################
def show_running_box(title=None):
    cur_time = time.time()
    ts_format = '%Y-%m-%d %H:%M:%S' 
    sys_time_text = "(started: %s)" % datetime.datetime.fromtimestamp(cur_time).strftime(ts_format)

    list_widgets = []
    if title is not None and len(title) > 0:
        title = " %s %s" % (title, sys_time_text)
    else:
        title = " %s" % sys_time_text

    list_widgets.append(urwid.Text([title, u'\n', u'\n']))

    list_widgets.append(urwid.Text([" Running ...", u'\n']))
    top.open_box(urwid.Filler(urwid.Pile(list_widgets)))
    mainloop.draw_screen()

#####################################################################
def hide_running_box():
    close_box(None)
    mainloop.draw_screen()

#####################################################################
def message_box(message_text):
    text_widget = urwid.Text([message_text, u'\n'])
    ok_button = menu_button(UI_TEXT_OK, close_box)
    top.open_box(urwid.Filler(urwid.Pile([text_widget, ok_button])))

#####################################################################
def program_about(button):
    about_string = "%s\n%s %s\n%s" % (PROGRAM_NAME, LBL_VERSION, PROGRAM_VERSION, PROGRAM_COPYRIGHT)
    message_box(about_string)

#####################################################################
def program_sys_info(button):
    sys_info_string = ''

    if sys.platform == 'linux2':
        # this might not be the best way to do this
        if os.path.isfile('/etc/lsb-release'):
            cmd = "cat /etc/lsb-release | grep DISTRIB_DESCRIPTION | cut -f 2 -d '=' | cut -f 2 -d '\"'"
            rc, output = commands.getstatusoutput(cmd)
            if rc == 0 and len(output) > 0:
                sys_info_string = output

    if len(sys_info_string) > 0:
        message_box(sys_info_string)
    else:
        message_box("System information not available")

#####################################################################
def program_java_info(button):
    rc, output = commands.getstatusoutput('java -version')
    if rc == 0 and len(output) > 0:
        display_output_lines(LBL_JAVA_INFO, output.split('\n'))
    else:
        message_box("Unable to find java executable")

#####################################################################
def program_python_info(button):
    v = sys.version_info
    version_string = "Python %d.%d.%d %s" % (v.major,v.minor,v.micro,v.releaselevel)
    message_box(version_string)

#####################################################################
def program_quit(button):
    raise urwid.ExitMainLoop()

#####################################################################
def log_capacity(button):
    fld_host = 'host'

    def on_ok(form_values):
        if len(form_values) == 0 or \
           fld_host not in form_values:
            return

        host = form_values[fld_host]
        cmd = "%s %s/kineticAdmin.sh -getlog -type capacity -host %s" % (SHELL_NAME, path_kinetic_java_client, host)
        rc, output = run_command(cmd, "Drive Capacity")
        if rc == 0 and len(output) > 0:
            output_lines = filter_java_output(output.split('\n'))
            display_output_lines("Capacity", output_lines)
        else:
            message_box("Unable to retrieve drive capacity")

    fields = [
            # label        field     type   tooltip          max       validator
            (REQ+LBL_HOST, fld_host, F_TXT, TT_GETLOG_DRIVE, MAX_HOST, None)
        ]

    run_form(LBL_GETLOG_CAPACITY, fields, on_ok)

#####################################################################
def log_config(button):
    fld_host = 'host'

    def on_ok(form_values):
        if len(form_values) == 0 or \
           fld_host not in form_values:
            return

        host = form_values[fld_host]
        cmd = "%s %s/kineticAdmin.sh -getlog -type configuration -host %s" % (SHELL_NAME, path_kinetic_java_client, host)
        rc, output = run_command(cmd, "Drive Config")
        if rc == 0 and len(output) > 0:
            output_lines = filter_java_output(output.split('\n'))
            display_output_lines("Configuration", output_lines)
        else:
            message_box("Unable to retrieve drive config")

    fields = [
            # label        field     type   tooltip          max       validator
            (REQ+LBL_HOST, fld_host, F_TXT, TT_GETLOG_DRIVE, MAX_HOST, None)
        ]

    run_form(LBL_GETLOG_CONFIGURATION, fields, on_ok)

#####################################################################
def log_limits(button):
    fld_host = 'host'

    def on_ok(form_values):
        if len(form_values) == 0 or \
           fld_host not in form_values:
            return

        host = form_values[fld_host]
        cmd = "%s %s/kineticAdmin.sh -getlog -type limits -host %s" % (SHELL_NAME, path_kinetic_java_client, host)
        rc, output = run_command(cmd, "Drive Limits")
        if rc == 0 and len(output) > 0:
            output_lines = filter_java_output(output.split('\n'))
            display_output_lines("Limits", output_lines)
        else:
            message_box("Unable to retrieve drive limits")

    fields = [
            # label        field     type   tooltip          max       validator
            (REQ+LBL_HOST, fld_host, F_TXT, TT_GETLOG_DRIVE, MAX_HOST, None)
        ]

    run_form(LBL_GETLOG_LIMITS, fields, on_ok)

#####################################################################
def log_messages(button):
    fld_host = 'host'

    def on_ok(form_values):
        if len(form_values) == 0 or \
           fld_host not in form_values:
            return

        host = form_values[fld_host]
        cmd = "%s %s/kineticAdmin.sh -getlog -type message -host %s" % (SHELL_NAME, path_kinetic_java_client, host)
        rc, output = run_command(cmd, "Drive Messages")
        if rc == 0 and len(output) > 0:
            output_lines = filter_java_output(output.split('\n'))
            display_output_lines("Messages", output_lines)
        else:
            message_box("Unable to retrieve drive messages")

    fields = [
            # label        field     type   tooltip          max       validator
            (REQ+LBL_HOST, fld_host, F_TXT, TT_GETLOG_DRIVE, MAX_HOST, None)
        ]

    run_form(LBL_GETLOG_MESSAGES, fields, on_ok)

#####################################################################
def log_statistics(button):
    fld_host = 'host'

    def on_ok(form_values):
        if len(form_values) == 0 or \
           fld_host not in form_values:
            return

        host = form_values[fld_host]
        cmd = "%s %s/kineticAdmin.sh -getlog -type statistic -host %s" % (SHELL_NAME, path_kinetic_java_client, host)
        rc, output = run_command(cmd, "Drive Stats")
        if rc == 0 and len(output) > 0:
            output_lines = filter_java_output(output.split('\n'))
            display_output_lines("Stats", output_lines)
        else:
            message_box("Unable to retrieve drive stats")

    fields = [
            # label        field     type   tooltip          max       validator
            (REQ+LBL_HOST, fld_host, F_TXT, TT_GETLOG_DRIVE, MAX_HOST, None)
        ]

    run_form(LBL_GETLOG_STATISTICS, fields, on_ok)

#####################################################################
def log_temperatures(button):
    fld_host = 'host'

    def on_ok(form_values):
        if len(form_values) == 0 or \
           fld_host not in form_values:
            return

        host = form_values[fld_host]
        cmd = "%s %s/kineticAdmin.sh -getlog -type temperature -host %s" % (SHELL_NAME, path_kinetic_java_client, host)
        rc, output = run_command(cmd, "Drive Temperatures")
        if rc == 0 and len(output) > 0:
            output_lines = filter_java_output(output.split('\n'))
            display_output_lines("Temperatures", output_lines)
        else:
            message_box("Unable to retrieve drive temperatures")

    fields = [
            # label        field     type   tooltip          max       validator
            (REQ+LBL_HOST, fld_host, F_TXT, TT_GETLOG_DRIVE, MAX_HOST, None)
        ]

    run_form(LBL_GETLOG_TEMPERATURE, fields, on_ok)

#####################################################################
def log_utilizations(button):
    fld_host = 'host'

    def on_ok(form_values):
        if len(form_values) == 0 or \
           fld_host not in form_values:
            return

        host = form_values[fld_host]
        cmd = "%s %s/kineticAdmin.sh -getlog -type utilization -host %s" % (SHELL_NAME, path_kinetic_java_client, host)
        rc, output = run_command(cmd, "Drive Utilizations")
        if rc == 0 and len(output) > 0:
            output_lines = filter_java_output(output.split('\n'))
            display_output_lines("Utilizations", output_lines)
        else:
            message_box("Unable to retrieve drive utilizations")

    fields = [
            # label        field     type   tooltip          max       validator
            (REQ+LBL_HOST, fld_host, F_TXT, TT_GETLOG_DRIVE, MAX_HOST, None)
        ]

    run_form(LBL_GETLOG_UTILIZATION, fields, on_ok)

#####################################################################
def op_ping(button):
    fld_host = 'host'

    def on_ok(form_values):
        if len(form_values) == 0 or \
           fld_host not in form_values:
            return

        host = form_values[fld_host]

        st = system_time_millis()
        success = drive_ping(host)
        if success:
            et = system_time_millis()
            ping_time = et - st
            message_box("ping %s: time=%d ms" % (host, ping_time))
        else:
            message_box("ping failed for %s" % host)

    fields = [
            # label        field     type   tooltip       max       validator
            (REQ+LBL_HOST, fld_host, F_TXT, '',           MAX_HOST, None)
        ]

    run_form(MNU_OP_PING, fields, on_ok)

#####################################################################
def op_put(button):
    fld_key   = 'key'
    fld_value = 'value'
    fld_host  = 'host'

    def on_ok(form_values):
        if len(form_values) == 0 or \
           fld_key not in form_values or \
           fld_value not in form_values or \
           fld_host not in form_values:
            return

        key = form_values[fld_key]
        value = form_values[fld_value]
        host = form_values[fld_host]
        success = drive_put(host, key, value)
        if success:
            message_box("put succeeded for '%s'" % key)
        else:
            message_box("put failed for '%s'" % key)

    fields = [
            # label         field      type   tooltip max        validator
            (REQ+LBL_KEY,   fld_key,   F_TXT, '',     MAX_KEY,   None),
            (OPT+LBL_VALUE, fld_value, F_TXT, '',     MAX_VALUE, None),
            (REQ+LBL_HOST,  fld_host,  F_TXT, '',     MAX_HOST,  None)
        ]

    run_form(MNU_OP_PUT, fields, on_ok)

#####################################################################
def op_get(button):
    fld_key  = 'key'
    fld_host = 'host'
    fld_meta = 'metadata_only'

    def on_ok(form_values):
        if len(form_values) == 0 or \
           fld_key not in form_values or \
           fld_host not in form_values:
            return

        key = form_values[fld_key]
        host = form_values[fld_host]
        success, value = drive_get(key, host)
        if success:
            message_box("get succeeded for '%s'" % key)
        else:
            message_box("get failed for '%s'" % key)

    fields = [
            # label    field     type   tooltip max       validator
            (REQ+LBL_KEY,  fld_key,  F_TXT, '',     MAX_KEY,  None),
            (REQ+LBL_HOST, fld_host, F_TXT, '',     MAX_HOST, None),
            (OPT+LBL_META, fld_meta, F_CHK, '',     0,        None)
        ]

    run_form(MNU_OP_GET, fields, on_ok)

#####################################################################
def op_delete(button):
    fld_key      = 'key'
    fld_host     = 'host'
    fld_cl_ver   = 'cl_ver'
    fld_identity = 'identity'

    def on_ok(form_values):
        if len(form_values) == 0 or \
           fld_key not in form_values or \
           fld_host not in form_values:
            return

        key = form_values[fld_key]
        host = form_values[fld_host]

        if fld_cl_ver in form_values:
            cl_ver = form_values[fld_cl_ver]
        else:
            cl_ver = None

        if fld_identity in form_values:
            identity = form_values[fld_identity]
        else:
            identity = None

        success = drive_delete(host, key)
        if success:
            message_box("Delete succeeded for '%s'" % key)
        else:
            message_box("Delete failed for '%s'" % key)

    fields = [
            # label    field     type   tooltip max       validator
            (REQ+LBL_KEY,  fld_key,  F_TXT, '',     MAX_KEY,  None),
            (REQ+LBL_HOST, fld_host, F_TXT, '',     MAX_HOST, None)
        ]

    run_form(MNU_OP_DELETE, fields, on_ok)

#####################################################################
def admin_lock_device(button):
    fld_pin  = 'pin'
    fld_host = 'host'

    def on_ok(form_values):
        if len(form_values) == 0 or \
           fld_pin not in form_values or \
           fld_host not in form_values:
            return

        lock_pin = form_values[fld_pin]
        host = form_values[fld_host]
        cmd = "%s %s/kineticAdmin.sh -lockdevice -host %s -pin %s" % (SHELL_NAME, path_kinetic_java_client, host, lock_pin)
        rc, output = run_command(cmd, "Lock Device")
        if rc == 0 and len(output) > 0:
            output_lines = filter_java_output(output.split('\n'))
            display_output_lines("Lock Device", output_lines)
        else:
            message_box("Unable to lock drive")

    fields = [
            # label        field     type   tooltip       max       validator
            (REQ+LBL_PIN,  fld_pin,  F_TXT, TT_LOCK_PIN,  MAX_PIN,  None),
            (REQ+LBL_HOST, fld_host, F_TXT, TT_LOCK_HOST, MAX_HOST, None)
        ]

    run_form(LBL_ADMIN_LOCK, fields, on_ok)

#####################################################################
def admin_unlock_device(button):
    fld_pin  = 'pin'
    fld_host = 'host'

    def on_ok(form_values):
        if len(form_values) == 0 or \
           fld_pin not in form_values or \
           fld_host not in form_values:
            return

        unlock_pin = form_values[fld_pin]
        host = form_values[fld_host]
        cmd = "%s %s/kineticAdmin.sh -unlockdevice -host %s -pin %s" % (SHELL_NAME, path_kinetic_java_client, host, unlock_pin)
        rc, output = run_command(cmd, "Unlock Device")
        if rc == 0 and len(output) > 0:
            output_lines = filter_java_output(output.split('\n'))
            display_output_lines("Unlock Device", output_lines)
        else:
            message_box("Unable to unlock drive")

    fields = [
            # label        field     type   tooltip         max       validator
            (REQ+LBL_PIN,  fld_pin,  F_TXT, TT_UNLOCK_PIN,  MAX_PIN,  None),
            (REQ+LBL_HOST, fld_host, F_TXT, TT_UNLOCK_HOST, MAX_HOST, None)
        ]

    run_form(LBL_ADMIN_UNLOCK, fields, on_ok)

#####################################################################
def admin_instant_erase(button):
    fld_pin  = 'pin'
    fld_host = 'host'

    def on_ok(form_values):
        if len(form_values) == 0 or \
           fld_pin not in form_values or \
           fld_host not in form_values:
            return

        erase_pin = form_values[fld_pin]
        host = form_values[fld_host]
        cmd = "%s %s/kineticAdmin.sh -instanterase -host %s -pin %s" % (SHELL_NAME, path_kinetic_java_client, host, erase_pin)
        rc, output = run_command(cmd, "Drive Instant Erase")
        if rc == 0 and len(output) > 0:
            output_lines = filter_java_output(output.split('\n'))
            display_output_lines("Instant Erase", output_lines)
        else:
            message_box("Unable to erase drive")

    fields = [
            # label        field     type    tooltip         max       validator
            (REQ+LBL_PIN,  fld_pin,  F_TXT, TT_ERASE_PIN,    MAX_PIN,  None),
            (REQ+LBL_HOST, fld_host, F_TXT, TT_ERASE_TARGET, MAX_HOST, None)
        ]

    run_form(LBL_ADMIN_ERASE, fields, on_ok)

#####################################################################
def admin_secure_erase(button):
    fld_pin  = 'pin'
    fld_host = 'host'

    def on_ok(form_values):
        if len(form_values) == 0 or \
           fld_pin not in form_values or \
           fld_host not in form_values:
            return

        erase_pin = form_values[fld_pin]
        host = form_values[fld_host]
        cmd = "%s %s/kineticAdmin.sh -secureerase -host %s -pin %s" % (SHELL_NAME, path_kinetic_java_client, host, erase_pin)
        rc, output = run_command(cmd, "Drive Secure Erase")
        if rc == 0 and len(output) > 0:
            output_lines = filter_java_output(output.split('\n'))
            display_output_lines("Secure Erase", output_lines)
        else:
            message_box("Unable to erase drive")

    fields = [
            # label        field     type   tooltip          max       validator
            (REQ+LBL_PIN,  fld_pin,  F_TXT, TT_ERASE_PIN,    MAX_PIN,  None),
            (REQ+LBL_HOST, fld_host, F_TXT, TT_ERASE_TARGET, MAX_HOST, None)
        ]

    run_form(LBL_ADMIN_SECURE_ERASE, fields, on_ok)

#####################################################################
def admin_set_cluster_version(button):
    fld_version = 'version'
    fld_host    = 'host'

    def on_ok(form_values):
        if len(form_values) == 0 or \
           fld_version not in form_values or \
           fld_host not in form_values:
            return

        new_version = form_values[fld_version]
        host = form_values[fld_host]
        cmd = "%s %s/kineticAdmin.sh -setclusterversion -host %s -newclversion %s" % (SHELL_NAME, path_kinetic_java_client, host, new_version)
        rc, output = run_command(cmd, "Drive Set Cluster Version")
        if rc == 0 and len(output) > 0:
            output_lines = filter_java_output(output.split('\n'))
            display_output_lines("Cluster Version", output_lines)
        else:
            message_box("Unable to set cluster version")

    fields = [
            # label                       field        type   tooltip max validator
            (REQ+LBL_NEW_CLUSTER_VERSION, fld_version, F_TXT, TT_NEW_CLUSTER_VERSION,     10, None),
            (REQ+LBL_HOST,                fld_host,    F_TXT, TT_CLUSTER_VERSION_TARGET,     MAX_HOST, None)
        ]

    run_form(LBL_SET_CLUSTER_VERSION, fields, on_ok)

#####################################################################
def admin_update_firmware(button):
    fld_path = 'path'
    fld_host = 'host'

    def on_ok(form_values):
        if len(form_values) == 0 or \
           fld_path not in form_values or \
           fld_host not in form_values:
            return

        firmware_file = form_values[fld_path]
        host = form_values[fld_host]

        # we also have a python utility for firmware updates
        cmd = "%s %s/kineticAdmin.sh -firmware %s -host %s" % (SHELL_NAME, path_kinetic_java_client, firmware_file, host)

        rc, output = run_command(cmd, "Update Drive Firmware")
        if rc == 0 and len(output) > 0:
            output_lines = filter_java_output(output.split('\n'))
            display_output_lines("Update Firmware", output_lines)
        else:
            message_box("Unable to update drive firmware")

    fields = [
            # label                 field     type   tooltip             max       validator
            (REQ+LBL_FIRMWARE_FILE, fld_path, F_TXT, TT_FIRMWARE_FILE,   10,       None),
            (REQ+LBL_HOST,          fld_host, F_TXT, TT_FIRMWARE_TARGET, MAX_HOST, None)
        ]

    run_form(LBL_UPDATE_FIRMWARE, fields, on_ok)

#####################################################################
def sec_set_acl(button):
    fld_sec_file = 'sec_file'
    fld_host     = 'host'

    def on_ok(form_values):
        if len(form_values) == 0 or \
           fld_sec_file not in form_values or \
           fld_host not in form_values:
            return

        sec_file = form_values[fld_sec_file]
        host = form_values[fld_host]
        cmd = "%s %s/kineticAdmin.sh -security %s -host %s" % (SHELL_NAME, path_kinetic_java_client, sec_file, host)
        rc, output = run_command(cmd, "Set Drive Security")
        if rc == 0 and len(output) > 0:
            output_lines = filter_java_output(output.split('\n'))
            display_output_lines("Drive Security", output_lines)
        else:
            message_box("Unable to update drive security")

    fields = [
            # label            field         type   tooltip max       validator
            (REQ+LBL_SEC_FILE, fld_sec_file, F_TXT, '',     MAX_FILE, None),
            (REQ+LBL_HOST,     fld_host,     F_TXT, '',     MAX_HOST, None)
        ]

    run_form(LBL_SET_SECURITY, fields, on_ok)

#####################################################################
def sec_set_erase_pin(button):
    fld_old_pin = 'old_pin'
    fld_new_pin = 'new_pin'
    fld_host    = 'host'

    def on_ok(form_values):
        if len(form_values) == 0 or \
           fld_old_pin not in form_values or \
           fld_new_pin not in form_values or \
           fld_host not in form_values:
            return

        old_pin = form_values[fld_old_pin]
        new_pin = form_values[fld_new_pin]
        host = form_values[fld_host]
        cmd = "%s %s/kineticAdmin.sh -seterasepin -host %s -olderasepin %s -newerasepin %s" % (SHELL_NAME, path_kinetic_java_client, host, old_pin, new_pin)
        rc, output = run_command(cmd, "Set Erase PIN")
        if rc == 0 and len(output) > 0:
            output_lines = filter_java_output(output.split('\n'))
            display_output_lines("Set Erase PIN", output_lines)
        else:
            message_box("Unable to set erase PIN")

    fields = [
            # label           field        type   tooltip max       validator
            (REQ+LBL_OLD_PIN, fld_old_pin, F_TXT, '',     MAX_PIN,  None),
            (REQ+LBL_NEW_PIN, fld_new_pin, F_TXT, '',     MAX_PIN,  None),
            (REQ+LBL_HOST,    fld_host,    F_TXT, '',     MAX_HOST, None)
        ]

    run_form(LBL_SET_ERASE_PIN, fields, on_ok)

#####################################################################
def sec_set_lock_pin(button):
    fld_old_pin = 'old_pin'
    fld_new_pin = 'new_pin'
    fld_host    = 'host'

    def on_ok(form_values):
        if len(form_values) == 0 or \
           fld_old_pin not in form_values or \
           fld_new_pin not in form_values or \
           fld_host not in form_values:
            return

        old_pin = form_values[fld_old_pin]
        new_pin = form_values[fld_new_pin]
        host = form_values[fld_host]
        cmd = "%s %s/kineticAdmin.sh -setlockpin -host %s -oldlockpin %s -newlockpin %s" % (SHELL_NAME, path_kinetic_java_client, host, old_pin, new_pin)
        rc, output = run_command(cmd, "Set Lock PIN")
        if rc == 0 and len(output) > 0:
            output_lines = filter_java_output(output.split('\n'))
            display_output_lines("Set Lock PIN", output_lines)
        else:
            message_box("Unable to set lock PIN")

    fields = [
            # label           field        type   tooltip max       validator
            (REQ+LBL_OLD_PIN, fld_old_pin, F_TXT, '',     MAX_PIN,  None),
            (REQ+LBL_NEW_PIN, fld_new_pin, F_TXT, '',     MAX_PIN,  None),
            (REQ+LBL_HOST,    fld_host,    F_TXT, '',     MAX_HOST, None)
        ]

    run_form(LBL_SET_LOCK_PIN, fields, on_ok)

#####################################################################
def clstr_discover(button):
    if os.path.isfile(PATH_DISCOVERED_FILES):
        tmp_prefix = "tmp_"
    else:
        tmp_prefix = ""

    drives_file = tmp_prefix + PATH_DISCOVERED_FILES 
    cmd = "%s %s/ktool.sh -discover -out %s" % (SHELL_NAME, path_kinetic_java_tools, drives_file)
    rc, output = run_command(cmd, "Discover Drives")
    if rc == 0 and len(output) > 0:
        output_lines = filter_java_output(output.split('\n'))
        result_lines = []
        for output_line in output_lines:
            pos_discovered = output_line.find("Discovered ")
            if pos_discovered == 0:
                result_lines.append(output_line)
                break

        if len(result_lines) == 0:
            result_lines.append("No drives found")
        else:
            if len(tmp_prefix) > 0:
                os.remove(PATH_DISCOVERED_FILES)
                os.rename(drives_file, PATH_DISCOVERED_FILES)
            
            set_cluster_drive_file(PATH_DISCOVERED_FILES)
            result_lines.append('\n')
            result_lines.append('Opened cluster of discovered drives')

        display_output_lines("Drive Discovery", result_lines)
    else:
        message_box("Unable to discover cluster drives")

#####################################################################
def clstr_open(button):
    fld_drive_file = 'drive_file'

    def on_ok(form_values):
        found_drives = False
        drive_file = form_values[fld_drive_file]
        # if drive file not specified and we have discovered drives use it 
        if len(drive_file) == 0:
            if os.path.isfile(PATH_DISCOVERED_FILES):
                set_cluster_drive_file(PATH_DISCOVERED_FILES)
                found_drives = True

        if found_drives:
            result_text = "Cluster opened"
        else:
            result_text = "No cluster found"

        display_output_lines("Cluster Open", [result_text])

    # the drive file is optional because leaving blank means to use
    # the discovered drives file
    fields = [
            # label              field           type   tooltip max       validator
            (OPT+LBL_DRIVE_FILE, fld_drive_file, F_TXT, '',     MAX_FILE, None)
        ]

    run_form(LBL_OPEN_CLUSTER, fields, on_ok)

#####################################################################
def clstr_close(button):
    if without_cluster():
        return

    set_cluster_drives(None)
    set_cluster_drive_file('')
    message_box('Cluster Closed')

#####################################################################
def clstr_sync_operation(op_handler):
    list_success = []
    list_failure = []

    if cluster_drive_list is not None and len(cluster_drive_list) > 0:
        for cluster_drive in cluster_drive_list:
            #TODO: show progress bar?
            success = op_handler(cluster_drive)
            if success:
                list_success.append(cluster_drive)
            else:
                list_failure.append(cluster_drive)

    return (list_success, list_failure)

#####################################################################
def clstr_firmware_ver(button):
    if without_cluster():
        message_box("No cluster open")
        return

    fld_firm_ver = 'firm_ver'

    def on_ok(form_values):
        if len(form_values) == 0 or \
           fld_firm_ver not in form_values:
            return

        firm_ver = form_values[fld_firm_ver]
        cmd = "%s %s/ktool.sh -checkversion -v %s -in %s" % (SHELL_NAME, path_kinetic_java_tools, firm_ver, cluster_drive_file)
        rc, output = run_command(cmd, "Check Cluster Firmware")
        if rc == 0 and len(output) > 0:
            output_lines = filter_java_output(output.split('\n'))
            success_fail_text = "Total(Succeed/Failed)"
            result_lines = []
            for output_line in output_lines:
                pos_success_fail = output_line.find(success_fail_text)
                if 0 == pos_success_fail:
                    result_lines.append(output_line)
                    break

            if len(result_lines) == 0:
                result_lines.append("Unable to determine firmware versions")

            display_output_lines("Cluster Firmware Version", result_lines)
        else:
            message_box("Unable to check cluster version firmware")

    fields = [
            # label              field         type   tooltip max validator
            (REQ+"Firmware Ver", fld_firm_ver, F_TXT, '',     10, None)
        ]

    run_form("Check Cluster Firmware Version", fields, on_ok)

#####################################################################
def clstr_set_version(button):
    if without_cluster():
        message_box("No cluster open")
        return

    fld_new_ver  = 'new_ver'
    fld_cl_ver   = 'cl_ver' 
    fld_identity = 'identity'
    fld_key      = 'key'
    fld_ssl      = 'ssl'

    def on_ok(form_values):
        if len(form_values) == 0 or \
           fld_new_ver not in form_values:
            return

        new_ver = form_values[fld_new_ver]

        #TODO: add optional arguments for setclusterversion

        cmd = "%s %s/ktool.sh -setclusterversion -clversion %s -in %s" % (SHELL_NAME, path_kinetic_java_tools, new_ver, cluster_drive_file)
        rc, output = run_command(cmd, "Set Cluster Version")
        if rc == 0 and len(output) > 0:
            output_lines = filter_java_output(output.split('\n'))
            display_output_lines("Set Cluster Version", output_lines)
        else:
            message_box("Unable to set cluster version")

    fields = [
            # label          field         type   tooltip max validator
            (REQ+"New Ver",  fld_new_ver,  F_TXT, '',     10, None),
            (OPT+"Version",  fld_cl_ver,   F_TXT, '',     10, None),
            (OPT+"Identity", fld_identity, F_TXT, '',     10, None),
            (OPT+"Key",      fld_key,      F_TXT, '',     10, None),
            (OPT+"Use SSL",  fld_ssl,      F_CHK, '',     1,  None)
        ]

    run_form("Set Cluster Version", fields, on_ok)

#####################################################################
def clstr_instant_erase(button):
    if without_cluster():
        message_box("No cluster open")
        return

    fld_pin      = 'pin'
    fld_cl_ver   = 'cl_ver'
    fld_identity = 'identity'
    fld_key      = 'key'
    fld_ssl      = 'ssl'

    def on_ok(form_values):
        if len(form_values) == 0 or \
           fld_pin not in form_values:
            return

        erase_pin = form_values[fld_pin]

        #TODO: add optional arguments for cluster instanterase
        timeout_millis = 180000

        cmd = "%s %s/ktool.sh -instanterase -pin %s -in %s -reqtimeout %d" % (SHELL_NAME, path_kinetic_java_tools, erase_pin, cluster_drive_file, timeout_millis)
        rc, output = run_command(cmd, "Cluster Instant Erase")
        if rc == 0 and len(output) > 0:
            output_lines = filter_java_output(output.split('\n'))
            display_output_lines("Cluster Instant Erase", output_lines)
        else:
            message_box("Unable to erase cluster drives")

    fields = [
            # label          field         type   tooltip       max      validator
            (REQ+LBL_PIN,    fld_pin,      F_TXT, TT_ERASE_PIN, MAX_PIN, None),
            (OPT+"Version",  fld_cl_ver,   F_TXT, '',           10,      None),
            (OPT+"Identity", fld_identity, F_TXT, '',           10,      None),
            (OPT+"Key",      fld_key,      F_TXT, '',           10,      None),
            (OPT+"Use SSL",  fld_ssl,      F_CHK, '',           1,       None)
        ]

    run_form("Cluster Instant Erase", fields, on_ok)

#####################################################################
def with_cluster():
    return cluster_open

#####################################################################
def without_cluster():
    if cluster_open:
        return False
    else:
        return True

#####################################################################
title_string = "%s (v%s)" % (PROGRAM_NAME,PROGRAM_VERSION)

menu_top = menu(title_string, [
    sub_menu(MNU_PROGRAM, [
        menu_button(MNU_PROG_ABOUT,            program_about), 
        menu_button(MNU_PROG_SYS_INFO,         program_sys_info),
        menu_button(MNU_PROG_JAVA_INFO,        program_java_info), 
        menu_button(MNU_PROG_PYTHON_INFO,      program_python_info)
    ]),
    sub_menu(MNU_DRIVE_LOG, [
        menu_button(MNU_LOG_CAPACITY,          log_capacity), 
        menu_button(MNU_LOG_CONFIG,            log_config),
        menu_button(MNU_LOG_DEVICE,            not_implemented),
        menu_button(MNU_LOG_LIMITS,            log_limits),
        menu_button(MNU_LOG_MESSAGES,          log_messages),
        menu_button(MNU_LOG_STATS,             log_statistics),
        menu_button(MNU_LOG_TEMPS,             log_temperatures),
        menu_button(MNU_LOG_UTILS,             log_utilizations)
    ]),
    sub_menu(MNU_DRIVE_OPERATION, [
        menu_button(MNU_OP_PING,               op_ping),
        menu_button(MNU_OP_PUT,                not_implemented),
        menu_button(MNU_OP_GET,                not_implemented),
        menu_button(MNU_OP_GET_NEXT,           not_implemented),
        menu_button(MNU_OP_GET_PREV,           not_implemented),
        menu_button(MNU_OP_GET_KEY_RANGE,      not_implemented),
        menu_button(MNU_OP_DELETE,             not_implemented)
    ]),
    sub_menu(MNU_DRIVE_ADMIN, [
        menu_button(MNU_ADMIN_LOCK_DEVICE,     not_implemented), # not tested
        menu_button(MNU_ADMIN_UNLOCK_DEVICE,   not_implemented), # not tested
        menu_button(MNU_ADMIN_ERASE,           admin_instant_erase),
        menu_button(MNU_ADMIN_SECURE_ERASE,    admin_secure_erase),
        menu_button(MNU_ADMIN_SET_CLUSTER_VER, admin_set_cluster_version),
        menu_button(MNU_ADMIN_UPDATE_FIRMWARE, not_implemented), # not tested
        menu_button(MNU_ADMIN_MEDIA_SCAN,      not_implemented),
        menu_button(MNU_ADMIN_MEDIA_OPTIMIZE,  not_implemented)
    ]),
    sub_menu(MNU_DRIVE_SECURITY, [
        menu_button(MNU_SEC_SET_ACL,           sec_set_acl),
        menu_button(MNU_SEC_SET_ERASE_PIN,     sec_set_erase_pin),
        menu_button(MNU_SEC_SET_LOCK_PIN,      not_implemented)
    ]),
    sub_menu(MNU_CLUSTER, [
        menu_button(MNU_CLSTR_DISCOVER,        clstr_discover),
        menu_button(MNU_CLSTR_OPEN,            clstr_open),
        menu_button(MNU_CLSTR_CLOSE,           clstr_close),
        menu_button(MNU_CLSTR_PING,            not_implemented),
        menu_button(MNU_CLSTR_LOG_CAPACITY,    not_implemented),
        menu_button(MNU_CLSTR_FIRMWARE_VER,    clstr_firmware_ver),
        menu_button(MNU_CLSTR_FIRMWARE_UPD,    not_implemented),
        menu_button(MNU_CLSTR_LOG_STATS,       not_implemented),
        menu_button(MNU_CLSTR_LOG_TEMPS,       not_implemented),
        menu_button(MNU_CLSTR_LOG_UTILS,       not_implemented),
        menu_button(MNU_CLSTR_KEY_PUT,         not_implemented),
        menu_button(MNU_CLSTR_KEY_DELETE,      not_implemented),
        menu_button(MNU_CLSTR_SET_VERSION,     clstr_set_version),
        menu_button(MNU_CLSTR_LOCK_DRIVES,     not_implemented),
        menu_button(MNU_CLSTR_UNLOCK_DRIVES,   not_implemented),
        menu_button(MNU_CLSTR_ERASE,           clstr_instant_erase),
        menu_button(MNU_CLSTR_SECURE_ERASE,    not_implemented)
    ]),
    sub_menu(MNU_HELP, [
        menu_button(MNU_HELP_VIEW_PROTOCOL,    not_implemented),
        menu_button(u'Option 2',               not_implemented),
        menu_button(u'Option 3',               not_implemented)
    ]),
    menu_button(MNU_PROG_QUIT,                 program_quit)
])

#####################################################################
def run(kinetic_java_tools_path, kinetic_java_path):
    global path_kinetic_java_tools
    global path_kinetic_java_client
    global top
    global mainloop
    path_kinetic_java_tools = kinetic_java_tools_path + '/kinetic-tools/bin'
    path_kinetic_java_client = kinetic_java_path + '/bin'
    top = CascadingBoxes(menu_top)
    mainloop = urwid.MainLoop(top, palette=[('reversed', 'standout', '')])
    mainloop.run()

#####################################################################
def main():
    import argparse

    parser = argparse.ArgumentParser(description='CUI Kinetic Tool')
    parser.add_argument('kinetic_java_tools_path',
                       help='Path to kinetic-java-tools')
    parser.add_argument('kinetic_java_path',
                       help='Path to kinetic-java')

    args = parser.parse_args()

    run(args.kinetic_java_tools_path, args.kinetic_java_path)

#####################################################################
#####################################################################
if __name__=='__main__':
    main()

