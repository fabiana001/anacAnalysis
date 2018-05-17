FROM python:3.6-alpine

WORKDIR /app

ADD requirements.txt /app
ADD app.py /app
ADD neo4j_handler.py /app
ADD public /app/public


RUN pip install -r requirements.txt

ENV HOST=localhost
ENV NEO4J_USERNAME=neo4j
ENV NEO4J_PASSWORD=password
ENV max_nodes=10000

CMD ["python", "app.py"]