import os
import sys
from analyzer.services import Analyzer

data_dir = os.getenv('ANALYZER_DATA')

if __name__ == '__main__':
    print(sys.argv[1])
    if os.path.isfile(sys.argv[1]):
        print("Classifying..")
    an = Analyzer(data_dir)
    print(an.classify(" ".join(sys.argv)))
