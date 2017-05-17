import os
from analyzer.services import Analyzer

data_dir = os.getenv('ANALYZER_DATA')

if __name__ == '__main__':
    an = Analyzer(data_dir)

    # adiciona 'intenção' como uma feature a ser analisada
    # por exemplo, 'comprar', 'vender', 'alugar', etc
    # nesse caso vamos utilizar apenas 'buy' ou 'sell'.
    an.add_feature_set("doctype")

    train_set = []

    # get training data from files
    for fname in os.listdir("data/trainset/"):
        fname = os.path.join("data/trainset/", fname)
        if os.path.isfile(fname):
            with open(fname, 'r') as fp:
                print(fname)
                content = fp.read()
                train_data = {
                    'doctype': 'Article of Incorporation',
                }
                print(train_data)
                train_set.append((content, train_data))

    # get training data from trash
    for fname in os.listdir("data/trashset/"):
        fname = os.path.join("data/trashset/", fname)
        if os.path.isfile(fname):
            with open(fname, 'r') as fp:
                print(fname)
                content = fp.read()
                train_data = {
                    'doctype': 'Unknown',
                }
                print(train_data)
                train_set.append((content, train_data))

    for data in train_set:
        sentence, feature_labels = data
        an.train(sentence, feature_labels)

    an.build()

    print("Done!")
