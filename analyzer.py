import csv
import string
from nltk.classify.naivebayes import NaiveBayesClassifier
from nltk.tokenize import RegexpTokenizer

from bottle import route, run

"""
Spinver Analyzer

Service used to classify new sentences given the train set.

Glossarium
---
:feature:       attribute used to calculate the probability of a given label on
                the given document.

:document:      a set of words used in a sentence.

:sentence:      term to be classified on the given output.

:feature_set:   set of all possible features with its labels, used to train the
                naive bayesian classifier.
"""

documents = []
word_features = set()
tokenizer = RegexpTokenizer('\w+|\$[\d\.]+|\S+')


def document_features(document):
    """
    Extracts all existing features in documents given
    the feature set.
    """
    document_words = set(document)
    features = {}
    for word in document_words:
        features[word] = word in word_features
    return features


def process_document(document):
    """
    Process a sentence to a document.
    """
    # get all words on the sentence
    words = set(tokenizer.tokenize(document))
    word_features.union(words)
    return words


def process_sentence(sentence):
    # strip punctuations from sentence
    sentence = sentence.translate(
        sentence.maketrans("", "", string.punctuation)
    )
    return sentence


def get_classifier():
    """
    Trains dataset with given processed data and returns
    a new classifier.
    """
    # implement featuresets
    featuresets = [(document_features(d), c) for (d, c) in documents]
    return NaiveBayesClassifier.train(featuresets)


def train(sentence, tags):
    """
    Add sentence to the new base, but not executes the machine learning
    training. Its only done when getting a new classifier instance.
    """
    pass


def process(registry):
    """
    Process the given registry for future train
    """
    if len(registry) < 2:
        raise Exception("Given registry is not valid!")
    sentence = process_sentence(registry[0])
    document = process_document(sentence)
    cls = registry[1]
    # add result to the documents list, that will be used to train set
    documents.append((document, cls))


# load documents from csv
with open('/Users/emanuel/Downloads/vertical_trade_buy_sell.csv',
          'rt', encoding="latin1") as csvfile:
    reader = csv.reader(csvfile, delimiter=';')
    for row in reader:
        if not row[0].startswith("#"):
            process((row[1], row[2]))


classifier = get_classifier()
oferta = """
Alguem tem um iphoni 4s baratinho em florianópolis, alguém tiem aí ow!
=====================
$$$ Valor: R$ 300,00. $$$
"""

prob_dict = classifier.prob_classify(document_features(process_document(process_sentence(oferta))))
for tag in prob_dict.samples():
    print("{}\t{}%".format(tag, prob_dict.prob(tag) * 100))
