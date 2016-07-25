import falcon


class AnalyzerError(Exception):

    @staticmethod
    def handle(ex, req, resp, params):
        message = "An error ocurred processing your request: {}".format(
            str(ex)
        )

        raise falcon.HTTPError(falcon.HTTP_400,
                               "Error",
                               message)
