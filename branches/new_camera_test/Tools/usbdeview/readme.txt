


USBDeview v1.82
Copyright (c) 2006 - 2010 Nir Sofer
Web Site: http://www.nirsoft.net



Description
===========

USBDeview is a small utility that lists all USB devices that currently
connected to your computer, as well as all USB devices that you
previously used.
For each USB device, exteneded information is displayed: Device
name/description, device type, serial number (for mass storage devices),
the date/time that device was added, VendorID, ProductID, and more...
USBDeview also allows you to uninstall USB devices that you previously
used, and disconnect USB devices that are currently connected to your
computer.
You can also use USBDeview on a remote computer, as long as you login to
that computer with admin user.

License
=======

This utility is released as freeware. You are allowed to freely
distribute this utility via floppy disk, CD-ROM, Internet, or in any
other way, as long as you don't charge anything for this. If you
distribute this utility, you must include all files in the distribution
package, without any modification !



Disclaimer
==========

The software is provided "AS IS" without any warranty, either expressed
or implied, including, but not limited to, the implied warranties of
merchantability and fitness for a particular purpose. The author will not
be liable for any special, incidental, consequential or indirect damages
due to loss of data or any other reason.



System Requirement
==================

This utility works on Windows 2000, Windows XP, Windows 2003, Windows
Vista, Windows Server 2008, and Windows 7. Windows 98/ME is not
supported. In order to disable/enable/uninstall USB items.
Under Windows Vista/7/2008 with UAC turned on, you should right click on
USBDeview and choose 'Run As Administrator'. In order to disable/enable
USB items on x64 systems, you must use the x64 version of USBDeview.



Versions History
================


* Version 1.82
  o The properties window now contains 2 properties columns, instead
    of one very long properties column in the previous versions.

* Version 1.81
  o Added new variables to the execute command option: %driver_file%,
    %driver_version%, and %power%

* Version 1.80
  o Added 'Firmware Revision' column.

* Version 1.77
  o Added 'Display Tray Balloon When Device Is Disconnected' option.

* Version 1.76
  o Added 'Add Header Line To CSV/Tab-Delimited File' option. When
    this option is turned on, the column names are added as the first
    line when you export to csv or tab-delimited file.

* Version 1.75
  o Added 'Driver Description' and 'Driver Version' columns.
  o The status and version of usb.ids (The VendorID/ProductID file)
    is now displayed in the right side of the bottom status bar. Clicking
    this usb.ids status will open http://www.linux-usb.org/usb.ids in
    your default Web browser.

* Version 1.70
  o Added option to execute a command when you unplug a USB device
    (In 'Advanced Options' window)

* Version 1.67
  o You can now send the USB devices information to stdout by
    specifying an empty filename ("") in the command-line. (For example:
    usbdeview.exe /stext "" >> c:\temp\usb.txt)
  o Added new variables to the execute command option: %device_type%,
    %service_name%, %device_class%, and %device_mfg%

* Version 1.65
  o Added 'Power' column, which displays the power required by the
    device in mA. (Works only for local computer)

* Version 1.60
  o Added 2 new columns: 'Device Class' and 'Device Mfg'.

* Version 1.56
  o Fix bug: when saving USB devices list from command-line,
    USBDeview saved all items, without considering the last saved
    configuration.
  o Added command-line options that controls what devices to save or
    display (/DisplayDisconnected, /DisplayHubs, /DisplayNoPortSerial,
    /DisplayNoDriver).

* Version 1.55
  o Added 3 new columns of service/driver information: Service Name,
    Service Description, Driver Filename.

* Version 1.50
  o Improved drive letter detection: USBDeview now also detect the
    drive letter of hard-disk drives connected to USB. (This improvement
    only works only for local computer)

* Version 1.48
  o Fixed bug: USBDeview didn't allow you to make a speed test when
    the drive letter of the USB drive was A: or B:

* Version 1.47
  o Fixed bug: USBDeview displayed wrong drive letters when the
    serial number or ParentId Prefix contained only one character.
  o Added a separated version for x64 systems, in order to allow you
    to disable/enable items on x64 systems.

* Version 1.46
  o Added 'ParentId Prefix' column, which shows the ID string of the
    parent device.

* Version 1.45
  o Fixed bug: On Windows Vista/7, USBDeview failed to detect that
    USB devices are disabled.
  o Added support for sorting the USB devices list from command-line.

* Version 1.43
  o Fixed issue with Windows 7/Vista: The description field displayed
    unneeded prefix string like '@oem4.inf,%athr.devicedesc.5523%'.

* Version 1.42
  o Added a few reliability checks for flash drive speed tests. If
    USBDeview detect that the final speed test result might not be
    reliable, an error message will be displayed.

* Version 1.41
  o Fixed issue: When USBDeview window is hidden and there is an icon
    in the taskbar, running USBDeview again will open the existing
    instance of USBDeview, instead of creating another one.

* Version 1.40
  o Added 'Speed Test' option for testing the read/write speed of
    your USB flash drive. Also, Added an option to publish your speed
    test results in http://usbspeed.nirsoft.net.

* Version 1.36
  o Fixed bug: USBDeview didn't display some partly installed devices.

* Version 1.35
  o Added 'Vendor Name' and 'Product Name' columns. (Requires to
    download an external USB IDs file)
  o New variables in command-string: %vid_hex% and %pid_hex% (vid and
    pid values as hexadecimal number)
  o Add icons legend in the help file and in the Web page.

* Version 1.30
  o Added new option: Show Time In GMT.

* Version 1.29
  o Fixed bug: For USB flash drives with multiple drives (like
    SanDisk Cruzer With U3), USBDeview displayed only one drive letter
    randomly. Now it can display multiple drive letters.

* Version 1.28
  o Added command-line options: /stop_by_pid, /disable_by_pid,
    /enable_by_pid, /remove_by_pid

* Version 1.27
  o Added new option: Open In RegEdit.

* Version 1.26
  o The 'Advanced Options' dialog-box is now resizable.
  o Added 'USB Hub' column (yes/no).

* Version 1.25
  o Added Disable+Enable option - gives you the affect similar to
    unplugging and then plugging back the device.
  o Added 'Open Drive' option (For USB flash drives).

* Version 1.22
  o Added new command line options: /remove_all_connected,
    /remove_all_disconnected
  o Fixed bug: using /remove_all removed also the USB hubs and caused
    the USB to stop working until restarting the computer.

* Version 1.21
  o Added new option: Select Another Font.

* Version 1.20
  o Added new command-line options to disable/enable/remove USB
    devices.

* Version 1.18
  o Fixed bug: The main window lost the focus when the user switched
    to another application and then returned back to USBDeview.

* Version 1.17
  o Fixed bug in saving as comma-delimited file when description or
    name fields contained comma character.

* Version 1.16
  o Added support for saving comma-delimited (.csv) files.
  o Added new command-line option: /scomma

* Version 1.15
  o New options: Disable/enable the selected USB devices (Works only
    with admin rights)
  o New Option: Start USBDeview As Hidden (Only when 'Put Icon On
    Tray' is turned on)

* Version 1.12
  o The configuration is now saved to a file, instead of the Registry.
  o New option: Display a balloon in the tray icon when a new device
    is connected (only for Windows XP or above).

* Version 1.11
  o Fixed bug: Changing the status of 'Put Icon On Tray' also changed
    the status of 'Show Tooltips'.

* Version 1.10
  o New Option: Put Icon On Tray
  o New Option: AutoPlay on selected devices.
  o New Option: AutoPlay When Device Is Connected.
  o New Option: Execute a command when you insert a USB device. (In
    Advanced Options)
  o New Option: Disconnect All Devices.

* Version 1.06
  o Added Disconnect/Uninstall buttons to the toolbar.

* Version 1.05
  o New command-line option: /regfile
  o New option: Display USB Hubs.

* Version 1.03 - New command-line option: /stop_by_class
* Version 1.02 - New option: Display devices without drivers.
* Version 1.01 - Fixed bug: In some computers, the USB devices list was
  loaded from the wrong hardware configuration.
* Version 1.00 - First release.



Using USBDeview
===============

USBDeview doesn't require any installation process or additional DLL
files. Just copy the executable file (USBDeview.exe) to any folder you
like, and run it.
The main window of USBDeview displays all USB devices installed on your
system. You can select one or more items, and then disconnect (unplug)
them , uninstall them, or just save the information into text/xml/html
file.



USBDeview Columns Description
=============================


* Device Name: Specifies the device name. For some device, this column
  may display meaningless name, like "USB Device". If the device name is
  meaningless, try to look at the Description column.
* Device Description: The description of the device.
* Device Type: The device type, according to USB class code. For more
  information about USB classes: USB Class Codes.
* Connected: Specifies whether the device is currently connected to
  your computer. If the device is connected, you can use the 'Disconnect
  Selected Devices' option (F9) to disconnect the device.
* Safe To Unplug: Specifies whether it's safe to unplug the device from
  the USB plug without disconnecting it first. If the value of this
  column is false, and you want to unplug this device, you must first
  disconnect this device by using the 'Disconnect Selected Devices'
  option (F9) of USBDeview utility, or by using the 'Unplug or Eject
  Hardware' utility of Windows operating system.
* Drive Letter: Specifies the drive letter of the USB device. This
  column is only relevant to USB flash memory devices and to USB CD/DVD
  drives. Be aware that USBDeview cannot detect drive letters of USB
  hard-disks.
* Serial Number: Specifies the serial number of the device. This column
  is only relevant to mass storage devices (flash memory devices, CD/DVD
  drives, and USB hard-disks).
* Created Date: Specifies the date/time that the device was installed.
  In most cases, this date/time value represents the time that you first
  plugged the device to the USB port. However, be aware that in some
  circumstances this value may be wrong.
* Last Plug/Unplug Date: Specifies the last time that you
  plugged/unplugged the device. This date value is lost when you restart
  the computer.
* VendorID/ProductID: Specifies the VendorID and ProductID of the
  device. For unofficial list of VendorID/ProductID, click here.
* USB Class/Subclass/Protocol: Specifies the Class/Subclass/Protocol of
  the device according to USB specifications. For more information about
  USB classes: USB Class Codes.
* Hub/Port: Specifies the hub number and port number that the device
  was plugged into. This value is empty for mass storage devices.



USBDeview Icons Legend
======================




The device is not connected.


The device is connected. It's safe to physically unplug the device
without disconnecting it.


The device is connected. You must disconnect the device from USBDeview or
from Windows "Safely Remove Hardware" option before you physically unplug
it.


The device is disabled.



'Vendor Name' And 'Product Name' Columns
========================================

Starting from version 1.35, you can view the vendor name and product name
of your USB devices. In order to use this feature, you have to download
the USB ID's list of Stephen J. Gowdy (http://www.linux-usb.org/usb.ids)
and put the file in the same folder of USBDeview.exe
The name of the file must remain as 'usb.ids'.



Speed Test for USB Flash Drives
===============================

Starting from version 1.40, USBDeview allows you to test the read/write
speed of your USB flash drive. The test is made by writing a large file
(named $speed_test_nirsoft$.dat) into your USB flash drive, and then
reading it back for testing the read speed. Be aware that you need at
least 100 MB of free disk space in order to successfully make this speed
test. Also, be aware that this test is made with sequential read and
write operations. When using multiple small files, the read/write
performances are usually much lower than sequential read/write.

In order to make a speed test to your flash drive, simply select the
desired item in the main window, and then choose the 'Speed Test' option
from the File menu, or press Ctrl+T. In the speed test window, verify
that the selected drive letter is the right one, and then press the
'Start Test' button. Wait a few seconds until the write and read tests
are finished.



Publishing Your Speed Test Result
=================================

If you want to share your speed test results, so other people that want
to purchase a USB flash drive will be able to easily compare the speed of
different flash drives, you can use the 'Publish Test Result' option to
do that.

When you successfully finish a speed test, the 'Publish Test Result'
button is automatically enabled. Clicking this button opens a new window
that displays all the data that will be sent to
http://usbspeed.nirsoft.net Web site, and ask you the confirm the speed
test publishing. Be aware that only the data displayed in this window is
transmitted to http://usbspeed.nirsoft.net Web site. USBDeview doesn't
send the serial number of your USB device or any other information that
may violate your privacy.



AutoPlay
========

Starting from version 1.10, USBDeview allows you to automatically run a
file on USB flash memory devices, according to AutoRun.inf filename. You
can manually activate the AutoPlay feature by selecting the desired
device and pressing F8. Alternatively, You can check the "AutoPlay When
Device Is Connected" option, and then the AutoPlay feature will be
automatically activated when you insert a USB flash memory device.
Be aware that this feature only works if USBDeview detects the drive
letter of the device.



Execute a command when you insert a USB device
==============================================

Starting from version 1.10, USBDeview allows you to execute a command
from exe/cmd/bat file in every time that insert a USB device to your
system. In order to use this feature, go to 'Options->Advanced Options',
select the 'Execute..' check-box, and type the exe/cmd file that you want
to run. You can also use the following special variables in this command
string: %device_name%, %device_desc%, %drive%, %serial_number%, %vid%,
%pid%, %vid_hex%, %pid_hex% , %device_type%, %service_name%,
%device_class%, %device_mfg%
USBDeview will automatically replace these variables with the appropriate
data of the device that you inserted. For example, if you want to pass to
your exe file 2 parameters - the serial number of the device, and the
drive letter:
c:\temp\test.exe "%serial_number%" "%drive%"



Known Limitations/Issues
========================


* For all devices except 'Mass Storage' Devices: If you plug a device
  to one USB port, and then you plug the same device to another USB port,
  a new record of the same device will be created for each USB port that
  you plug. So if you see multiple lines for exactly the same device, it
  means that you plugged it to different USB ports. The USB ports that
  you plugged are specified in 'Hub/Port' column.
  However, for 'Mass Storage' device, only one record is created for each
  device, even if you plug it to different USB port.
* When a USB device is added/removed on your local computer, UDBDeview
  utility automatically updates the list of USB devices.
  But... If you UDBDeview is connected to a remote computer, the list
  won't refresh automatically when a device is added or removed. You must
  refresh it manually, by pressing F5 key.



Connecting To Remote Computer
=============================

The following command-line options allows you to connect to remote
computers. You must login to the remote computer with admin user in order
to use these options.
* /remote <\\Computer Name>
  Allows you to connect a single remote computer.
  For Example:
  USBDeview.exe /remote \\MyComp
* /remotefile <Computers List File>
  Allows you to connect multiple computers, and view all their USB
  activity in one window. The computers list file should be a simple
  Ascii text file with computer names separated by colon, semicolon,
  space, tab characters or CRLF.
  For Example:
  USBDeview.exe /remotefile "c:\temp\comp.txt"
In order to successfully get full admin access to the remote computer,
read this Blog post: How to connect a remote Windows 7/Vista/XP computer
with NirSoft utilities.



Connecting To external SYSTEM registry file
===========================================

If you have the 'SYSTEM' registry file of external operating system, you
can use the following command-line option to read the USB devices list
from it:
/regfile <SYSTEM Registry File>

For Example:
USBDeview.exe /regfile "c:\temp\regfiles\SYSTEM"
USBDeview.exe /regfile "d:\windows\system32\config\SYSTEM"

This option has some limitations:
* You cannot read a Registry file of Windows XP/2003/Vista from Windows
  2000 Machine.
* USBDeview works in read-only mode. (You cannot uninstall a device
  from external file)



Stop/Disconnect Command-Line Options
====================================


* /stop {/showmsg} {\\RemoteComputer} <Device Name>
  Stop/disconnect a USB device by specifying its name or description.
  You don't have to specify the whole name or description. You can also
  specify only a part of the name or description string, and USBDeview
  will identify it. For example, If the device name is 'Kingston
  DataTraveler 2.0', you can simply put 'kingston' or 'DataTraveler' in
  the device name parameter, and USBDeview will disconnect the right
  device.
  If you want that a message/balloon will be displayed after the device
  is disconnected, add the '/showmsg' parameter. (Windows 2000 displays a
  message-box, Windows XP displays a balloon)
  If you want to disconnect a device in a remote computer, specify the
  remote computer name, with \\ prefix. You must login to the remote
  computer with admin rights in order to do that.
  Examples:
  USBDeview.exe /stop /showmsg "DVD RW"
  USBDeview.exe /stop "DataTraveler"
  USBDeview.exe /stop \\comp01 "SanDisk Cruzer"


* /stop_by_serial {/showmsg} {\\RemoteComputer} <Device Name>
  Similar to /stop command, but instead of specifying a device name, you
  should specify the serial number of the device. Works only with mass
  storage devices.
  Examples:
  USBDeview.exe /stop_by_serial ea051261
  USBDeview.exe /stop_by_serial /showmsg 876534513


* /stop_by_drive {/showmsg} {\\RemoteComputer} <Device Name>
  Stop/disconnect a USB device by specifying its drive letter. Works only
  with USB flash drives and CD/DVD devices.
  Examples:
  USBDeview.exe /stop_by_drive g:
  USBDeview.exe /stop_by_drive /showmsg f:
  USBDeview.exe /stop_by_drive /showmsg \\comp02 t:


* /stop_by_class {/showmsg} {\\RemoteComputer} <USB Class;USB
  SubClass;USB Protocol>
  Stop/disconnect a USB device by specifying the class/subclass/protocol
  combination.
  For example, if you want to stop all 'Mass Storage' devices, you should
  specify the following class/subclass/protocol combination: 08;06;50
  Examples:
  USBDeview.exe /stop_by_class /showmsg 08;06;50
  USBDeview.exe /stop_by_class 03;01;02


* /stop_by_pid {/showmsg} {\\RemoteComputer} <VendorID;ProductID>
  Stop/disconnect a USB device by specifying the VendorID/ProductID. You
  can specify only the VendorID if you want to disconnect all devices
  with that VendorID.
  Examples:
  USBDeview.exe /stop_by_pid /showmsg 13fe;1a00
  USBDeview.exe /stop_by_pid 04f9


* /stop_all {/showmsg} {\\RemoteComputer}
  Stop/disconnect all USB devices currently connected to your computer.





Disable/Enable/Remove Command-Line Options
==========================================

Starting from version 1.20, you can also use the following commands to
disable, enable or remove USB devices from command-line:
* /disable {\\RemoteComputer} <Device Name>
* /disable_by_serial {\\RemoteComputer} <Device Name>
* /disable_by_drive {\\RemoteComputer} <Device Name>
* /disable_by_class {\\RemoteComputer} <USB Class;USB SubClass;USB
  Protocol>
* /disable_by_pid {\\RemoteComputer} <VendorID;ProductID>
* /disable_all {\\RemoteComputer}
* /enable {\\RemoteComputer} <Device Name>
* /enable_by_serial {\\RemoteComputer} <Device Name>
* /enable_by_drive {\\RemoteComputer} <Device Name>
* /enable_by_class {\\RemoteComputer} <USB Class;USB SubClass;USB
  Protocol>
* /enable_by_pid {\\RemoteComputer} <VendorID;ProductID>
* /enable_all {\\RemoteComputer}
* /remove {\\RemoteComputer} <Device Name>
* /remove_by_serial {\\RemoteComputer} <Device Name>
* /remove_by_drive {\\RemoteComputer} <Device Name>
* /remove_by_class {\\RemoteComputer} <USB Class;USB SubClass;USB
  Protocol>
* /remove_by_pid {\\RemoteComputer} <VendorID;ProductID>
* /remove_all {\\RemoteComputer}
* /remove_all_connected - Remove all connected USB devices.
* /remove_all_disconnected - Remove all disconnected USB devices.



Save Command-Line Options
=========================



/stext <Filename>
Save the list of all USB devices into a regular text file.

/stab <Filename>
Save the list of all USB devices into a tab-delimited text file.

/scomma <Filename>
Save the list of all USB devices into a comma-delimited text file.

/stabular <Filename>
Save the list of all USB devices into a tabular text file.

/shtml <Filename>
Save the list of all USB devices into HTML file (Horizontal).

/sverhtml <Filename>
Save the list of all USB devices into HTML file (Vertical).

/sxml <Filename>
Save the list of all USB devices to XML file.

/sort <column>
This command-line option can be used with other save options for sorting
by the desired column. If you don't specify this option, the list is
sorted according to the last sort that you made from the user interface.
The <column> parameter can specify the column index (0 for the first
column, 1 for the second column, and so on) or the name of the column,
like "Device Name" and "Description". You can specify the '~' prefix
character (e.g: "~Description") if you want to sort in descending order.
You can put multiple /sort in the command-line if you want to sort by
multiple columns.

Examples:
USBDeview.exe /shtml "f:\temp\usb-list.html" /sort 2 /sort ~1
USBDeview.exe /shtml "f:\temp\usb-list.html" /sort "Device Type" /sort
"Device Name"

/nosort
When you specify this command-line option, the list will be saved without
any sorting.

/DisplayDisconnected <0 | 1>
Specifies whether to show disconnected devices. 0 = No, 1 = Yes.

/DisplayNoPortSerial <0 | 1>
Specifies whether to show devices without port/serial number. 0 = No, 1 =
Yes.

/DisplayNoDriver <0 | 1>
Specifies whether to show devices without driver. 0 = No, 1 = Yes.

/DisplayHubs <0 | 1>
Specifies whether to show USB hubs. 0 = No, 1 = Yes.



Translating USBDeview to other languages
========================================

In order to translate USBDeview to other language, follow the
instructions below:
1. Run USBDeview with /savelangfile parameter:
   USBDeview.exe /savelangfile
   A file named USBDeview_lng.ini will be created in the folder of
   USBDeview utility.
2. Open the created language file in Notepad or in any other text
   editor.
3. Translate all string entries to the desired language. Optionally,
   you can also add your name and/or a link to your Web site.
   (TranslatorName and TranslatorURL values) If you add this information,
   it'll be used in the 'About' window.
4. After you finish the translation, Run USBDeview, and all translated
   strings will be loaded from the language file.
   If you want to run USBDeview without the translation, simply rename
   the language file, or move it to another folder.



Feedback
========

If you have any problem, suggestion, comment, or you found a bug in my
utility, you can send a message to nirsofer@yahoo.com
