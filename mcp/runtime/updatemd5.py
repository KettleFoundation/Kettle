#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Fri May 22 23:32:36 2011

@author: Searge
@version: v1.0
"""

import sys
import logging
from optparse import OptionParser

from commands import Commands, CLIENT, SERVER, CalledProcessError
from mcp import updatemd5_side


def main():
    parser = OptionParser(version='MCP %s' % Commands.fullversion())
    parser.add_option('--client', dest='only_client', action='store_true', help='only process client', default=False)
    parser.add_option('--server', dest='only_server', action='store_true', help='only process server', default=False)
    parser.add_option('-f', '--force', action='store_true', dest='force', help='force update', default=False)
    parser.add_option('-c', '--config', dest='config', help='additional configuration file')
    options, _ = parser.parse_args()
    updatemd5(options.config, options.force, options.only_client, options.only_server)


def updatemd5(conffile, force, only_client, only_server):
    try:
        commands = Commands(conffile)

        # client or server
        process_client = True
        process_server = True
        if only_client and not only_server:
            process_server = False
        if only_server and not only_client:
            process_client = False

        if ((process_client and commands.checkmd5s(CLIENT) or process_server and commands.checkmd5s(SERVER))
                and not force):
            print 'WARNING:'
            print 'The updatemd5 script is unsupported and should only be run in special'
            print 'cases, such as if there were compile errors in the last decompile which'
            print 'have now been corrected. It will reset the changed status of all classes'
            print 'for reobfuscation, and only classes modified afterwards will end up in'
            print 'the reobf directory.'
            print 'Only use this script if you absolutely know what you are doing, or when a'
            print 'MCP team member asks you to do so.'
            answer = raw_input('If you really want to update all classes, enter "Yes" ')
            if answer.lower() not in ['yes']:
                print 'You have not entered "Yes", aborting the update process'
                sys.exit(1)

        if process_client:
            try:
                updatemd5_side(commands, CLIENT)
            except CalledProcessError:
                commands.logger.error('Client recompile failed, correct source then rerun updatemd5')
        if process_server:
            try:
                updatemd5_side(commands, SERVER)
            except CalledProcessError:
                commands.logger.error('Server recompile failed, correct source then rerun updatemd5')
    except Exception:  # pylint: disable-msg=W0703
        logging.exception('FATAL ERROR')
        sys.exit(1)


if __name__ == '__main__':
    main()
