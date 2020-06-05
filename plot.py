#!/bin/env python3
'''
Plot the performance analysis results.
Usage: python3 plot.py /path/to/output/dir /path/to/input1.json [/path/to/input2.json /path/to/input2.json ...]
Example: python3 plot.py $PWD perf*.json
Requrements: python3.7+, matplotlib
'''
import json
import sys

import matplotlib.pyplot as plt

if len(sys.argv) < 2:
    print('Wrong number of arguments')
    print(__doc__.strip())
    exit(1)


def json_load(f):
    with open(f) as ff:
        return json.load(ff)


out_dir = sys.argv[1]
inps = [json_load(f) for f in sys.argv[2:]]

# TODO plot avgTime* against the numCrawledPages and numIndexedKeywords
