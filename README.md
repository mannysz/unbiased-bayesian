# Analyzer Service

In order to run the analyzer service, you should pre-install the following software.

- Python 3.4+
- Python-Dev
- MariaDB or MySQL
- Foreman (the ruby gem) or Honcho (the python package) if you are running in development environment
- Virtualenv

After installing these dependencies, create a virtual environment on the project root folder.

```bash
$ virtualenv -p python3 venv
```

Activate the virtualenv and install all dependencies listed in requirements.txt

```bash
$ source venv/bin/activate
$ pip install -r requirements.txt
```

## Environment Variables

Declare the following environment variables before running any task on analyzer service:

- DATABASE_URL: Database URI in the following format - mysql://user:password@host:port/db_name

Create a file named .env in the root project folder exporting the environment variables to avoid
environment clashing running foreman or honcho, and load it with "source" everytime you need to
run it locally or on command line.

## Migrating the Database

In order to create necessary table and fill it with pre-loaded data, run the following command.

```bash
$ python manage.py migrate
```

## Running the Service

To run the service in development mode, just spawn a web process with foreman or honcho.

Starting with Foreman

```bash
$ foreman start
```

Starting with Honcho
```bash
$ honcho start
```
You can install honcho on the given virtual environment to avoid additional dependencies (like ruby, ruby-gem and foreman).

