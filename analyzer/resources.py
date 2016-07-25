import falcon

from analyzer.exceptions import AnalyzerError


class BaseResource(object):
    """
    Basic abstract resource.
    """

    abstract = True

    def __init__(self, analyzer_obj):
        self.analyzer = analyzer_obj

    def on_get(self, req, resp):
        raise AnalyzerError("Not implemented!")

    def on_post(self, req, resp):
        raise AnalyzerError("Not implemented!")

    def on_patch(self, req, resp):
        raise AnalyzerError("Not implemented!")

    def on_put(self, req, resp):
        raise AnalyzerError("Not implemented!")

    def on_delete(self, req, resp):
        raise AnalyzerError("Not implemented!")


class FeatureResource(BaseResource):

    def __init__(self, analyzer):
        super().__init__(analyzer)

    def on_get(self, req, resp):
        fsets = list(self.analyzer.list_feature_sets())
        req.context['result'] = {
            "objects": fsets
        }

    def on_post(self, req, resp):
        body = req.context.get("body", False)
        if not body or not body.get('feature', None):
            raise falcon.HTTPError(falcon.HTTP_400,
                                   'Bad Request',
                                   'Missing attribute "feature"')
        feature = body.get('feature')
        self.analyzer.add_feature_set(feature)
        resp.status = falcon.HTTP_201
        resp.location = '/feature/{}'.format(feature)


class FeatureDetailResource(BaseResource):

    def __init__(self, analyzer):
        super().__init__(analyzer)

    def on_get(self, req, resp, feat):
        if feat in self.analyzer.list_feature_sets():
            req.context['result'] = {
                "feature": feat
            }
            resp.status = falcon.HTTP_200
        else:
            resp.status = falcon.HTTP_404

    def on_delete(self, req, resp, feat):
        if feat in self.analyzer.list_feature_sets():
            self.analyzer.remove_feature_set(feat)
            resp.status = falcon.HTTP_202
        else:
            resp.status = falcon.HTTP_404


class TrainResource(BaseResource):
    pass


class ClassifyResource(BaseResource):
    pass


class BuildResource(BaseResource):
    pass
