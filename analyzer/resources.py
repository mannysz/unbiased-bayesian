from analyzer.services import Analyzer
from analyzer.exceptions import AnalyzerError
from analyzer import settings


analyzer = Analyzer(settings.DATA_PATH)

class TrainResource(object):

    def on_post(self, req, resp):
        pass
