import os
import sys
from analyzer.services import Analyzer

data_dir = os.getenv('ANALYZER_DATA')

if __name__ == '__main__':
    an = Analyzer(data_dir)
    print(an.classify(" ".join(sys.argv)))
