# Initialise Firestore
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

cred = credentials.Certificate("servicekey.json")
firebase_admin.initialize_app(cred)
db = firestore.client()

from flask import Flask,request

app = Flask(__name__)

@app.route('/<meth>',methods = ['GET','POST'])
def handle(meth):
	data = request.get_json(force=True)
	if meth=='post':
		data['time'] = firestore.SERVER_TIMESTAMP
		db.collection('test').add(data)
		return 'Added'
# if __name__ == '__main__':
#     app.run(host = '0.0.0.0',port = 5000,threaded = True)
if __name__ == '__main__':
	from gevent.pywsgi import WSGIServer
	server = WSGIServer(('127.0.0.1', 5000), app)
	server.serve_forever()