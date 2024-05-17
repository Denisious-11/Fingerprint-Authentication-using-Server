import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

cred = credentials.Certificate("fingerprint-3b17d-68252e41a0c2.json")
firebase_admin.initialize_app(cred)


db = firestore.client() 

collection1 = db.collection('user_table')

def store_to_db(email,password,sha256_hash,encrypted_image_string):
    try:
        create_time, newdoc = collection1.add(
            {
                "Email": email,
                "Password": password,
                "Sha256_hash": sha256_hash,
                "Encrypted_Image": encrypted_image_string,
                
            }
        )
        #iULIxHn6BtWvdr1iA64C
        print(newdoc.id)
        print(create_time)
        return "yes"
    except:
        return "no"