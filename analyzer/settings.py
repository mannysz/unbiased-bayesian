import os

DATA_PATH = os.getenv("ANALYZER_DATA", False)

if not DATA_PATH:
    raise Exception("ANALYZER_DATA env variable not set!")
