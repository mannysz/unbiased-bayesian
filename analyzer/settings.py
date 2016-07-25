import os

DATA_PATH = os.getenv("ANALYZER_DATA", False)

if not DATA_PATH:
    raise Exception("ANALYZER_DATA environment variable not set!")

API_KEY = os.getenv("API_KEY", False)

if not API_KEY:
    raise Exception("API_KEY environment variable not set!")
