import os
import string
import pickle
import math
from collections import defaultdict
from nltk.classify.naivebayes import NaiveBayesClassifier
from nltk.tokenize import RegexpTokenizer


class Analyzer(object):
    """
    Spinver Analyzer

    Service used to classify new sentences given the train set.

    Glossarium
    ---
    :feature:       attribute used to calculate the probability of a
                    given label on the given document.

    :label:         characteristic that describes a feature.

    :document:      a set of words used in a sentence.

    :sentence:      term to be classified on the given output.

    :feature_set:   set of all possible features with its labels, used
                    to train the naive bayesian classifier.
    """

    ####################################
    # Public Interface                 #
    ####################################

    def __init__(self, data_path):
        """
        Initializes the analyzer instance loading data from
        data directory path.

        :data type:         str
        :data description:  Path to the data directory. Should be
                            writable and readable by the analyzer
                            user.
        """
        self.data = data_path
        self.feature_sets = {}
        self.tokenizer = RegexpTokenizer('\w+|\$[\d\.]+|\S+')
        try:
            self._load()
        except:
            self._persist(self.feature_sets)

    def add_feature_set(self, feature):
        """
        Adds a new feature_set to the analyzer.

        :feature type:          str
        :feature description:   Feature name.
        """
        if feature not in self.feature_sets.keys():
            self.feature_sets[feature] = {
                'classifier': None,
                'documents': [],
                'word_features': set(),
            }

    def remove_feature_set(self, fset):
        """
        Removes the given feature set.
        """
        if fset in self.feature_sets.keys():
            del self.feature_sets[fset]

    def list_feature_sets(self):
        """
        Lists all features registered in feature_sets.
        """
        return self.feature_sets.keys()

    def train(self, sentence, feature_labels):
        """
        Add new sentence to the train set.

        :sentence type: str
        :sentence desc: the sentence itself.

        :feature_labels type: dict
        :featyre_labels desc: dicitonary mapping the key as feature to
                              a value as label or tag to classify the
                              sentence.
        """
        # process sentence
        sentence = self._process_sentence(sentence)
        # extract document from sentence
        document = self._process_document(sentence)
        # for each feature
        for feat in feature_labels.keys():
            if feat in self.feature_sets.keys():
                # check if word features is initialized for the given fset
                fset = self.feature_sets[feat]
                fset['word_features'] = fset['word_features'] or set()
                # merge words from document to feature word_features
                fset['word_features'] = fset['word_features'].union(document)
                # classify the document with given label on this feature
                labeled_doc = (document, feature_labels[feat])
                # add document to feature documents.
                fset['documents'] = fset['documents'] or []
                fset['documents'].append(labeled_doc)
            else:
                raise Exception("Invalid feature: {}".format(feat))

    def build(self):
        """
        Rebuild all naive bayesian classifiers with the previous train set.
        """
        for fset in self.feature_sets.keys():
            featuresets = [(self._document_features(d), c)
                           for (d, c) in self.feature_sets[fset]['documents']]
            self.feature_sets[fset]["classifier"] = NaiveBayesClassifier.train(
                featuresets)
        self._persist(self.feature_sets)

    def classify(self, sentence):
        """
        Classify the given sentence based in previous trained data set.
        """
        sentence = self._process_sentence(sentence)
        document = self._process_document(sentence)
        word_features = self._document_features(document)
        labels = []
        for feat in self.feature_sets.keys():
            classifier = self.feature_sets[feat].get('classifier', None)
            if classifier:
                labels.append({
                    'feature': feat,
                    'label': classifier.classify(word_features)
                })
        return labels

    def _load(self):
        if not os.path.exists(self.data):
            raise Exception("Data file does not exists!")
        try:
            self.feature_sets = pickle.load(open(self.data, "rb"))
        except:
            raise Exception("Error loading data file: {}".format(
                "Please check if the data file is readable."
            ))

    def _persist(self, data):
        try:
            pickle.dump(self.feature_sets, open(self.data, "wb"))
        except:
            raise Exception("Error persisting data file: {}".format(
                "Please check if the data file is writable."
            ))

    def _document_features(self, document):
        """
        Extracts all existing features in documents given
        the feature set.

        :document type:         list
        :document description:  List of normalized words extracted
                                from a sentence.
        """
        document_words = set(document)
        features = {}
        for fset in self.feature_sets.keys():
            for word in document_words:
                if self.feature_sets[fset].get('word_features', None):
                    wfeats = self.feature_sets[fset]['word_features']
                    features[word] = word in wfeats
        return features

    def _process_document(self, document):
        """
        Process a sentence to a document.
        """
        # get all words on the sentence
        words = set(self.tokenizer.tokenize(document))
        word_dict = defaultdict(int)
        # iterate over all words and count them
        for word in words:
            word_dict[word.lower()] += 1
        # ignore all words with low occurrence
        cleaned_words = []
        for key in word_dict:
            if math.log(word_dict[key]) > 0:
                cleaned_words.append(key)
        print("Cleaned Words: ")
        print(cleaned_words)
        return set(cleaned_words)

    def _process_sentence(self, sentence):
        """
        Strips punctuations from given sentence.
        """
        sentence = sentence.translate(
            sentence.maketrans("", "", string.punctuation)
        )
        return sentence

    def _get_classifier(self, feature):
        """
        Returns a feature classifier if its already built.
        """
        # implement featuresets
        if self.feature_set and self.feature_set.get(feature, None) and\
           self.feature_set[feature].get("classifier"):
            return self.feature_set[feature]["classifier"]
