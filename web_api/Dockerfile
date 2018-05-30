FROM python:3.6

WORKDIR /app

COPY requirements.txt /app
COPY app.py /app
COPY neo4j_handler.py /app
COPY public /app/public


RUN pip install -r requirements.txt

ENV HOST=localhost
ENV NEO4J_USERNAME=neo4j
ENV NEO4J_PASSWORD=password
ENV max_nodes=10000

CMD ["python", "app.py"]