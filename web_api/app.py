import numpy as np
import flask
from flask import request
from flask_cors import CORS
import os
import json
from neo4j_handler import *
from flask_compress import Compress
import os


# create the app
app = flask.Flask("api-server", static_folder='public')
CORS(app)
Compress(app)

# read these parameters from env
host = '131.1.252.119'
if 'HOST' in os.environ:
    host = os.environ['HOST']

print("connecting to host {}".format(host))

user = 'neo4j'
password = 'password'

max_nodes = 20000
if 'max_nodes' in os.environ:
    max_nodes = os.environ['max_nodes']

py2neo_handler = Py2NeoHandler(host=host, user=user, pwd=password)
py2neo_handler.query_by_relevant_terms("", max_nodes)


@app.route('/', defaults={'path': ''})
@app.route('/<path:path>')
def serve(path):
    if path != "" and os.path.exists("public" + path):
        print(path)
        return flask.send_from_directory('public', path)
    else:
        return flask.send_from_directory('public', 'index.html')


@app.route('/healtz', methods=['GET'])
def healtz():
    return flask.jsonify({'healtz': 'ok'})


@app.route('/graph', methods=['POST'])
def predict():
    response = {'success': False}
    post_object = request.get_json()

    if post_object is not None:
        if 'queryterms' in post_object:
            queryterms = post_object['queryterms']
            try:
                results = py2neo_handler.query_by_relevant_terms(queryterms, max_nodes)
                response['queryterms'] = queryterms
                response['success'] = True
                response['result'] = results.to_json()
            except Exception as e:
                response['success'] = False
                response['error'] = "Error executing query {}".format(e)

        else:
            response['message'] = 'missing queryterms'
    else:
        response['message'] = 'missing json body'

    return flask.jsonify(response)


if __name__ == "__main__":
    print(("* Loading Flask starting server..."
           "please wait until server has fully started"))
    app.run(debug=False, host='0.0.0.0')
