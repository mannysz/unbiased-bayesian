import os
import falcon

from wsgiref import simple_server

from analyzer import settings

from analyzer.services import Analyzer

from analyzer.exceptions import AnalyzerError

from analyzer.resources import FeatureResource
from analyzer.resources import FeatureDetailResource
from analyzer.resources import TrainResource
from analyzer.resources import BuildResource
from analyzer.resources import ClassifyResource

from analyzer.middlewares import AuthMiddleware
from analyzer.middlewares import RequireJSON
from analyzer.middlewares import JSONTranslator

# initializing the analyzer
analyzer = Analyzer(settings.DATA_PATH)

# initializing api and middlewares
api = falcon.API(middleware=[
    AuthMiddleware(),
    RequireJSON(),
    JSONTranslator(),
])

# setting error handler
api.add_error_handler(AnalyzerError, AnalyzerError.handle)

# adding routes
api.add_route('/feature', FeatureResource(analyzer))
api.add_route('/feature/{feat}', FeatureDetailResource(analyzer))
api.add_route('/train', TrainResource(analyzer))
api.add_route('/classify', ClassifyResource(analyzer))
api.add_route('/build', BuildResource(analyzer))

if __name__ == '__main__':
    httpd = simple_server.make_server('127.0.0.1',
                                      os.getenv('PORT', 8000), api)
    httpd.serve_forever()
