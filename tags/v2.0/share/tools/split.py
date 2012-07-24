#!/usr/bin/env python
# coding: utf-8
#
# @(#) share/tools/split.py
#
# @see http://code.activestate.com/recipes/224800-simple-file-splittercombiner-module/
#
# USAGE
#
#   shell>  split.py --input infile --output outfile_prefix --chunks 2
#   # will split "infile" into "outfile_prefix0" and "outfile_prefix1"
#   # you can recreate infile by running the following command:
#   # shell>  cat outfile_prefix0 outfile_prefix1 > infile
#
# DESCRIPTION
#
#   Split an input file into a number of pieces
#

import optparse
import os

#--------------------------------------- constants

# there is a file size limit of 1048576 bytes
# for resource files in raw and also in assets
UNCOMPRESS_DATA_MAX = 1048576

#--------------------------------------- get_options

def get_options():
    """
       parse the command-line options
    """
    parser = optparse.OptionParser()
    parser.add_option('-i', '--input', dest = 'input_name',
                      help = 'input FILE', metavar = 'FILE')
    parser.add_option('-o', '--output', dest = 'output_name',
                      help = 'output FILE')
    parser.add_option('-n', '--chunks', dest = 'chunks', type = 'int',
                      default = 10, help = 'number of chunks')
    options, args = parser.parse_args()
    return options

#--------------------------------------- split_file

def split_file(input_name, output_name, chunks):
    """
        split input file with name 'input_name',
        into 'chunks' output files, with
        prefix 'output_name' and postfixes "0",
        "1", up to str(chunks-1)
    """
    # open input file
    input_file = open(input_name, 'rb')
    input_size = os.path.getsize(input_name)
    chunk_size = int((input_size + chunks - 1) / chunks)
    # split input file into 'chunks' files
    total_bytes = 0
    for n in range(chunks):
        # read nbytes at once
        nbytes = chunk_size
        if input_size < total_bytes + nbytes:
            nbytes = input_size - total_bytes
        if int(UNCOMPRESS_DATA_MAX - 1024) <= nbytes:
            raise Exception('File %s too big: increase number of chunks' % input_name)
        input_data = input_file.read(nbytes)
        # write chunk
        output_file = open(output_name + str(n), 'wb')
        output_file.write(input_data)
        output_file.flush()
        output_file.close()
        # update counters
        total_bytes += len(input_data)
    # all done
    input_file.close()

#--------------------------------------- main

# main code
options = get_options()
split_file(
    options.input_name, options.output_name,
    int(options.chunks))

#--------------------------------------- The End
