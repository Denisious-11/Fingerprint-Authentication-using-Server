from django.shortcuts import render
from .models import *
import json
from django.core import serializers
from django.http import HttpResponse, JsonResponse
from django.db.models import Q
from django.db.models import Count
import re
from django.views.decorators.cache import never_cache
from django.core.files.storage import FileSystemStorage
import random
from django.views.decorators.csrf import csrf_exempt
import os
from datetime import datetime
from datetime import date
import base64
import numpy as np
import cv2
from PIL import Image
import hashlib
from .perform_aes import *
from .storefire import *



# Create your views here.
dirr="Files/"
enc_key="c242594280a587cbdde8c7aba35bbed7671d374255e7f3b70104a406623b57ae"
@csrf_exempt
def register(request):
	email=request.POST.get("email")
	password=request.POST.get("password")
	image_file=os.listdir('Files')
	print(image_file)
	response_data={}
	try:
		image_name=image_file[0]
		print(image_name)
		im_path=dirr+image_name
		# Read the BMP image
		image = Image.open(im_path)

		# Convert the image to bytes
		image_bytes = image.tobytes()

		# Calculate SHA-256 hash
		sha256_hash = hashlib.sha256(image_bytes).hexdigest()

		print(f"SHA-256 Hash of the image: {sha256_hash}")

		new_en=enc_key[:16]

		# Encrypt the image
		encrypted_image_string = encrypt_image(im_path, new_en)
		# print(encrypted_image_string)
		print(len(encrypted_image_string))

		result=store_to_db(email,password,sha256_hash,encrypted_image_string)

		if result=="yes":
			response_data['msg'] = "yes"
		else:
			response_data['msg'] = "no"

	except:
		response_data['msg'] = "no"

	return JsonResponse(response_data)

@csrf_exempt
def remove(request):
	files = os.listdir(dirr)
	response_data={}
	try:
		for file in files:
			file_path = os.path.join(dirr, file)
			if os.path.isfile(file_path):
				os.remove(file_path)

		response_data['msg'] = "yes"
	except:
		response_data['msg'] = "no"

	return JsonResponse(response_data)


def match_fingerprints(image_path1, image_path2):
	print("$$$$$$$$$$$$$$$$$$")
	best_score=0

	# Load the fingerprint images
	fingerprint1 = cv2.imread(dirr+image_path1, cv2.IMREAD_GRAYSCALE)
	fingerprint2 = cv2.imread(dirr+image_path2, cv2.IMREAD_GRAYSCALE)

	sift=cv2.SIFT_create()

	keypoints1,descriptors1=sift.detectAndCompute(fingerprint1,None)
	keypoints2,descriptors2=sift.detectAndCompute(fingerprint2,None)

	# Create FLANN based matcher
	FLANN_INDEX_KDTREE = 1
	index_params = dict(algorithm=FLANN_INDEX_KDTREE, trees=5)
	search_params = dict(checks=50)
	flann = cv2.FlannBasedMatcher(index_params, search_params)

	matches = flann.knnMatch(descriptors1, descriptors2, k=2)

	match_points = []
	print("^^^^^^^^^^^^^")
	for a, b in matches:
		if a.distance < 0.7 * b.distance:  # Adjust the threshold as needed
			match_points.append(a)

	print(match_points)
	keypoints = min(len(keypoints1), len(keypoints2))

	print("***************")
	print(keypoints)
	if keypoints > 0:
		matching_score = len(match_points) / keypoints * 100
		print("matching_score:", matching_score)
		return matching_score
	else:
		print("No keypoints found. Cannot calculate similarity.")
		return 0


@csrf_exempt
def login(request):
	image_file=os.listdir('Files')
	print(image_file)
	response_data={}

	try:
		image_name=image_file[0]
		print(image_name)
		im_path=dirr+image_name
		# Read the BMP image
		image = Image.open(im_path)

		# Convert the image to bytes
		image_bytes = image.tobytes()

		# Calculate SHA-256 hash
		sha256_hash = hashlib.sha256(image_bytes).hexdigest()

		print(f"SHA-256 Hash of the image: {sha256_hash}")


		resplist=[]
		respdata={}
		docs = db.collection(u'user_table').stream()
		for doc in docs:
			data={}
			docdict=doc.to_dict()
			# print(docdict)
			#print(docdict['Encrypted_Image'])
			encrypted_image=docdict['Encrypted_Image']

			new_dec=enc_key[:16]
			decrypted_image_bytes = decrypt_image(encrypted_image,new_dec)


			# Do further processing or save the decrypted image bytes as an image file
			with open("Files/temp.bmp", "wb") as image_file:
				image_file.write(decrypted_image_bytes)


		# Paths to your fingerprint image files
		all_files=os.listdir('Files')
		print(all_files)

		##Paths to your fingerprint images
		image_path1 = all_files[0]
		image_path2 = all_files[1]

		matching_score = match_fingerprints(image_path1, image_path2)
		if matching_score > 1:	
			print("\nAuthentication Successful")
			response_data['msg'] = "success"
		else:
			print("\nAuthentication Failed")
			response_data['msg'] = "fail"
	except:
		response_data['msg'] = "no"
			
	return JsonResponse(response_data)




