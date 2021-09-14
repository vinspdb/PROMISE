import os
import subprocess
def mining_structure(path):
    """Execute splitminer for bpmn structure mining.
    Args:
        settings (dict): Path to jar and file names
        epsilon (double): Parallelism threshold (epsilon) in [0,1]
        eta (double): Percentile for frequency threshold (eta) in [0,1]
    """
    print(" -- Mining Process Structure --")
    # Event log file_name
    file_name = path
    input_route = path + '.xes'
    # Mining structure definition
    args = ['java', '-cp', 'splitminer.jar;lib\*', 'au.edu.unimelb.services.ServiceProvider',
            'SMD', '0.1', '0.0', 'false', '.\\'+input_route, '.\\'+file_name]
    subprocess.call(args, stdout=subprocess.DEVNULL, stderr=subprocess.STDOUT)
