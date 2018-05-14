FROM python:3.6-alpine

ADD . /src

WORKDIR /src

ADD requirements.txt .

RUN pip install -r requirements.txt

env NEO4J_USERNAME=username
env NEO4J_PASSWORD=password

CMD ["python", "app.py"]