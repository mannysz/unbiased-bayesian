import os
import sys
from analyzer.services import Analyzer

data_dir = os.getenv('ANALYZER_DATA')

if __name__ == '__main__':
    print(sys.argv[1])
    if os.path.isfile(sys.argv[1]):
        with open(sys.argv[1], 'r') as fp:
            doc = fp.read()
            print("Classifying..")
            an = Analyzer(data_dir)
            print(an.classify(doc))
    else:
        print("File not found: {}".format(
            sys.argv[1]
        ))
