import numpy as np
import flask
from flask import request
from flask_cors import CORS
import os
import json


# create the app
app = flask.Flask("api-server", static_folder='public')
CORS(app)


@app.route('/', defaults={'path': ''})
@app.route('/<path:path>')
def serve(path):
    if path != "" and os.path.exists("public" + path):
        return flask.send_from_directory('public/', path)
    else:
        return flask.send_from_directory('public', 'index.html')


@app.route('/healtz', methods=['GET'])
def healtz():
    return flask.jsonify({'healtz': 'ok'})


@app.route('/predict', methods=['POST'])
def predict():
    data = {'msg': 'hello world'}
    return flask.jsonify(data)


if __name__ == "__main__":
    print(("* Loading Flask starting server..."
           "please wait until server has fully started"))
    app.run(debug=False, host='0.0.0.0')
