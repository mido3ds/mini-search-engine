#!/bin/env python3
'''
Plot the performance analysis results.
Usage: python3 plot.py /path/to/output/dir /path/to/input1.json [/path/to/input2.json /path/to/input2.json ...]
Example: python3 plot.py $PWD perf*.json
Requrements: python3.7+, matplotlib
'''
import json
import math
import sys

import matplotlib.pyplot as plt

if len(sys.argv) < 2:
    print('Wrong number of arguments')
    print(__doc__.strip())
    exit(1)


def json_load(f):
    with open(f) as ff:
        return json.load(ff)


out_dir = sys.argv[1] + '/'
inps = [json_load(f) for f in sys.argv[2:]]
print(f'read {len(inps)} files')


def collect(label: str, xs: list) -> list:
    return [math.inf if x[label] == 'NaN' else x[label] for x in xs]


avgTimeWithRanking = collect('avgTimeWithRanking', inps)
successPercentageWithRanking = collect('successPercentageWithRanking', inps)
avgTimeWithoutRanking = collect('avgTimeWithoutRanking', inps)
successPercentageWithoutRanking = collect(
    'successPercentageWithoutRanking',
    inps
)
numCrawledPages = collect('numCrawledPages', inps)
numIndexedKeywords = collect('numIndexedKeywords', inps)


def plot(w, wt, xs, xs2, xlabel, xlabel2, ylabel, f):
    plt.clf()
    plt.subplots_adjust(
        top=0.91,
        bottom=0.095,
        left=0.09,
        right=0.93,
        hspace=0.215,
        wspace=0.155
    )

    fig = plt.figure()
    ax1 = fig.add_subplot(111)
    ax2 = ax1.twiny()

    ax1.plot(
        xs,
        w,
        label='ranking enabled'
    )
    ax1.plot(
        xs,
        wt,
        label='ranking disabled'
    )

    ax1.set_ylabel(ylabel)
    ax1.legend()

    ax1.set_xlim(xs[0]-10, xs[-1]+10)
    ax1.set_xlabel(xlabel)
    ax2.set_xlim(xs2[0]-10, xs2[-1]+10)
    ax2.set_xlabel(xlabel2)

    ax1.grid()
    ax2.grid()

    plt.savefig(out_dir + f)

    print(f'plotted "{out_dir + f}"')


plot(
    w=avgTimeWithRanking,
    wt=avgTimeWithoutRanking,
    xs=numCrawledPages,
    xlabel='# Crawled Pages',
    xs2=numIndexedKeywords,
    xlabel2='# Indexed Pages',
    ylabel='Average Time (milliseconds)',
    f='avgtime.png',
)

plot(
    w=successPercentageWithRanking,
    wt=successPercentageWithoutRanking,
    xs=numCrawledPages,
    xlabel='# Crawled Pages',
    xs2=numIndexedKeywords,
    xlabel2='# Indexed Pages',
    ylabel='Success Percentage',
    f='secPrec.png',
)
