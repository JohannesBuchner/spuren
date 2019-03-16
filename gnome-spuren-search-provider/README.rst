=====================================================
Spuren Desktop Search Engine for GNOME
=====================================================

Spuren is a simple desktop file search engine. This allows GNOME shell searches.

Installation
------------

Ensure that python>=3.5 and python-gobject are installed on your system.

Download or clone this repository::

	git clone https://github.com/JohannesBuchner/spuren

Run the installation script as root::

	cd gnome-spuren-search-provider
	sudo ./install.sh

If you need to you can change the installation paths to suit your system::

	sudo SYSCONFDIR=/etc DATADIR=/usr/share LIBDIR=/usr/lib LIBEXECDIR=/usr/lib ./install.sh

How to use
------------

1. Press the Win/Super key and enter your search terms.
2. The file database will be searched for these.
3. By default, the file will be launched if clicked. 
4. Append %dir or %path to your search term to open the folder.
5. Append %copy to copy the full path into the clipboard. 

Compatibility
---------------

This implements the `org.gnome.Shell.SearchProvider2` D-Bus API which seems to be present in GNOME Shell since around 2012 and has been tested with GNOME Shell 3.22-3.30.

Launching uses xdg-open. If this opens the wrong application, you have to set the right default application in the Open Desktop MIME database.

Troubleshooting
----------------

If this does not work for you, make sure to look to wherever GNOME and D-Bus are logging for error messages (in the journal on systemd-using systems).
Don't hesitate to open an issue.


