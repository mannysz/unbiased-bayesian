import os
import random
from analyzer.services import Analyzer

data_dir = os.getenv('ANALYZER_DATA')

if __name__ == '__main__':
    an = Analyzer(data_dir)

    # adiciona 'intenção' como uma feature a ser analisada
    # por exemplo, 'comprar', 'vender', 'alugar', etc
    # nesse caso vamos utilizar apenas 'buy' ou 'sell'.
    an.add_feature_set("intent")

    # adiciona 'entity' como uma feature a ser analisada
    # por exemplo, 'iphone', 'galaxy note', 'cg titãn', etc.
    # nesse caso vamos usar apenas smartphones como exemplo.
    an.add_feature_set("entity")

    # adiciona 'brand' (marca) como uma feature a ser analisada
    # por exemplo 'apple', 'motorola', 'samsung'
    an.add_feature_set("brand")

    # adiciona 'model' como uma feature a ser analisada
    # por exemplo '5c 16gb' '6s 32gb', 'note 2 8gb' 'zenfone 5', etc.
    an.add_feature_set("model")

    # geração dos dados de treino no seguinte formato:
    # <intent> <entity> <model> <price> <contact>
    train_set = []
    intents = (
        ("compro", "buy"),
        ("vendo", "sell"),
        ("to vendendo", "sell"),
        ("ofereço", "sell"),
        ("supimpa", "sell"),
        ("alguém tem", "buy"),
        ("to procurando", "buy"),
        ("estou procurando", "buy"),
        ("procuro por", "buy"),
    )
    entities = (
        ('iphone', 'apple', ('4', '4s', '5', '5c', '5s', '6', '6c', '6s')),
        ('galaxy', 'samsung',
            ('young', 's', 's2', 's3', 's4', 's5', 's6', 'express', 'duos',
             'note', 'note 2')),
        ('zenfone', 'asus', ('2', 'one', '5')),
        ('lumia', 'nokia', ('1000', '2000', '3035', 's60')),
        ('nokia', 'nokia', ('1100', '1120')),
    )
    gossip = (
        'baratinho',
        'acessível',
        'novinho em folha',
        'zerado',
        'na caixa',
        'com pouco uso',
    )
    contact_phrases = (
        'manda mensagem pra {contact} e fala com {name}',
        'manda inbox pra {contact} pra falar com {name}',
        'manda inbox!',
        'tratar com {name}',
        'só ligar {contact}',
    )
    contacts = (
        ('Zé Ruela', '4722829812'),
        ('Joao Ninguem', '(66) 2538261'),
        ('Shanvers', 'shanvers@gmail.com'),
        ('Waterson', 'wat.is@gmail.com'),
    )

    # geração dos posts
    post_format = "{intent} {entity} {model} {gossip}\n{contact}"
    for i in range(1000):
        intent = random.choice(intents)
        product = random.choice(entities)
        entity = product[0]
        brand = product[1]
        model = random.choice(product[2])
        contact = random.choice(contacts)
        profile_name = contact[0]
        mail_phone = contact[1]

        # finaliza com a ultima frase de contato, que pode ter campos opcionais
        # logo temos que tratar se é possivel ou nao fazer o replace
        cphrase = random.choice(contact_phrases)
        cphrase = cphrase.replace('{name}', profile_name)
        cphrase = cphrase.replace('{contact}', mail_phone)

        post = post_format.format(
            intent=intent[0],
            entity=entity,
            model=random.choice([model, ""]),  # opcional
            gossip=random.choice(gossip),
            contact=cphrase
        )

        train_data = {
            'intent': intent[1],
            'entity': entity,
            'brand': brand,
            'model': model,
        }

        print(train_data)

        train_set.append((post, train_data))

    # agora que temos um set de treino, podemos treinar o analizador
    for data in train_set:
        sentence, feature_labels = data
        an.train(sentence, feature_labels)

    # e compliar a maquina treinada
    an.build()

    print("Done!")
