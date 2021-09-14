import os
import subprocess
def mining_structure(path):
    # Event log file_name
    file_name = path
    input_route =  path + '.xes'
    # Mining structure definition
    print(input_route)
    args = ['Discover.bat', input_route, file_name]
    subprocess.call(args)

