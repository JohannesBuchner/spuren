#!/usr/bin/env python3
# This file is a part of gnome-pass-search-provider.
#
# gnome-pass-search-provider is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# gnome-pass-search-provider is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with gnome-pass-search-provider. If not, see
# <http://www.gnu.org/licenses/>.

# Copyright (C) 2017 Jonathan Lestrelin
# Author: Jonathan Lestrelin <jonathan.lestrelin@gmail.com>

# This project was based on gnome-shell-search-github-repositories
# Copyright (C) 2012 Red Hat, Inc.
# Author: Ralph Bean <rbean@redhat.com>
# which itself was based on fedmsg-notify
# Copyright (C) 2012 Red Hat, Inc.
# Author: Luke Macken <lmacken@redhat.com>

import os, sys
import re
import gzip
import traceback
import subprocess
import dbus
import dbus.glib
import dbus.service
import gi
gi.require_version('Gtk', '3.0')
from gi.repository import Gtk, Gdk, GLib
import codecs

# Convenience shorthand for declaring dbus interface methods.
# s.b.n. -> search_bus_name
search_bus_name = 'org.gnome.Shell.SearchProvider2'
sbn = dict(dbus_interface=search_bus_name)


class SpurenService(dbus.service.Object):
    """ Spuren Search Results provider daemon.

    This service is started through DBus activation by calling the
    :meth:`Enable` method, and stopped with :meth:`Disable`.

    """
    bus_name = 'org.gnome.Spuren.SearchProvider'

    _object_path = '/' + bus_name.replace('.', '/')

    def __init__(self):
        self.store_path = os.path.expanduser('~/.local/share/spuren/db.gz')
        self.load()
        self.session_bus = dbus.SessionBus()
        bus_name = dbus.service.BusName(self.bus_name, bus=self.session_bus)
        dbus.service.Object.__init__(self, bus_name, self._object_path)
        
    def load(self):
        self.store = []
        for line in gzip.open(self.store_path, mode='rt', encoding='latin1'):
            parts = line.strip().split("\t", maxsplit=2)
            if len(parts) == 3:
                size, path, filename = parts
                self.store.append((path, filename))
        self.last_terms = None
        self.last_results = []

    @dbus.service.method(in_signature='sasu', **sbn)
    def ActivateResult(self, id, terms, timestamp):
        try:
            cmd = str(id)
            action, path = cmd.split(' ', maxsplit=1)
            if action == 'copy':
                self.send_to_clipboard(cmd)
            elif action == 'folder':
                self.launch(os.path.dirname(path))
            elif action == 'launch':
                self.launch(path)
        except Exception as e:
            traceback.print_exc()

    @dbus.service.method(in_signature='as', out_signature='as', **sbn)
    def GetInitialResultSet(self, terms):
        try:
            return self.get_result_set(terms)
        except Exception as e:
            traceback.print_exc()
            return ["error"]

    @dbus.service.method(in_signature='as', out_signature='aa{sv}', **sbn)
    def GetResultMetas(self, ids):
        return [dict(id=id, name=id, gicon="search") for id in ids]

    @dbus.service.method(in_signature='asas', out_signature='as', **sbn)
    def GetSubsearchResultSet(self, previous_results, new_terms):
        try:
            return self.get_result_set(new_terms)
        except Exception as e:
            traceback.print_exc()
            return ["error"]

    @dbus.service.method(in_signature='asu', terms='as', timestamp='u', **sbn)
    def LaunchSearch(self, terms, timestamp):
        pass
    

    def get_result_set(self, terms):
        #print("searching for %s" % terms)
        if terms[-1] == '%copy':
            action = 'copy'
            terms = terms[:-1]
        elif terms[-1] == '%dir' or terms[-1] == '%path':
            action = 'folder'
            terms = terms[:-1]
        else:
            action = 'launch'
        terms = [term.strip().lower() for term in terms if '%' not in term and len(term) > 1]
        termstr = ' '.join(terms)
        if len(termstr) < 3: return []
        
        if self.last_terms is not None and ' '.join(terms).startswith(' '.join(self.last_terms)):
            base_store = self.last_results
            #print("  reusing results")
        else:
            base_store = self.store

        filtered_store = [(path, filename) for path, filename in base_store 
            if all((term in filename.lower()) for term in terms)]
        self.last_terms = terms
        self.last_results = filtered_store
        #print("   %d results for (%s)'%s'" % (len(filtered_store), action, terms) )
        if len(filtered_store) == 0:
            results = ['no results']
        else:
            results = ['%s %s/%s' % (action, path, filename) for path, filename in filtered_store]
        return results

    def send_password_to_native_clipboard(self, base_args, name):
        pass_cmd = subprocess.run(base_args + ['-c', name])
        if pass_cmd.returncode:
            self.notify('Failed to copy password!', error=True)
        elif 'otp' in base_args:
            self.notify('Copied OTP password to clipboard:',
                        body=f'<b>{name}</b>')
        else:
            self.notify('Copied password to clipboard:', body=f'<b>{name}</b>')

    def send_to_clipboard(self, text):
        #print("copying to clipboard")
        cb = Gtk.Clipboard.get(Gdk.SELECTION_CLIPBOARD)
        cb.set_text(text, len(text))
        cb.store()
    
    def launch(self, path):
        #print("trying to launch", path)
        subprocess.call(('xdg-open', path))
    
    """def notify(self, message, body='', error=False):
        try:
            self.session_bus.get_object(
                'org.freedesktop.Notifications',
                '/org/freedesktop/Notifications'
            ).Notify(
                'Spuren',
                0,
                'dialog-spuren',
                message,
                body,
                '',
                {'transient': False if error else True},
                0 if error else 3000,
                dbus_interface='org.freedesktop.Notifications'
            )
        except dbus.DBusException as err:
            print('Got error {} while trying to display message {}.'.format(
                err, message))"""


def main():
    SpurenService()
    GLib.MainLoop().run()


if __name__ == '__main__':
    main()
