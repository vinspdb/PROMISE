import os
import subprocess
def mining_structure(path):
    print(" -- Mining Process Structure --")
    # Event log file_name
    file_name = path
    input_route = path + '.xes'
    # Mining structure definition
    args = ['Discover.bat', input_route, file_name]
    subprocess.call(args)

