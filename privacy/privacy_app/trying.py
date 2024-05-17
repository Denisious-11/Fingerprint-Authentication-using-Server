import numpy as np
import pandas as pd
import cv2
from tensorflow.keras import backend as b
from tensorflow.keras.layers import Input, Lambda, Dense, Dropout, Convolution2D, MaxPooling2D, Flatten,Activation
from tensorflow.keras import optimizers
from tensorflow.keras.callbacks import ModelCheckpoint, LearningRateScheduler
import os
from tensorflow.keras.models import Model
import matplotlib.pyplot as plt
from tensorflow.keras.models import Sequential
import tensorflow as tf
import pickle
import numpy as np
from tensorflow.keras.applications.vgg16 import VGG16, preprocess_input
from tensorflow.keras.preprocessing import image
from tensorflow.keras.models import Model
from sklearn.metrics.pairwise import cosine_similarity
from tkinter.filedialog import askopenfilename
base_model = VGG16(weights='imagenet')
model = Model(inputs=base_model.input, outputs=base_model.get_layer('fc1').output)
#
def model_predict_():
	def euclidean_distance(vects):
		x, y = vects
		sumq = b.sum(b.square(x - y), axis=1, keepdims=True)
		return b.sqrt(b.maximum(sumq, b.epsilon()))

	dimension = (105,105,1)

	def build_base_network(input_shape):
		seq = Sequential()
		
		kernel_size = 3
		
		#convolutional layer 1
		seq.add(Convolution2D(64, (kernel_size, kernel_size), input_shape=input_shape))
		seq.add(Activation('relu'))
		seq.add(MaxPooling2D(pool_size=(2, 2)))  
		seq.add(Dropout(.25))
		
		seq.add(Convolution2D(32, (kernel_size, kernel_size)))
		seq.add(Activation('relu'))
		seq.add(MaxPooling2D(pool_size=(2, 2))) 
		seq.add(Dropout(.25))
		
		#convolutional layer 2
		seq.add(Convolution2D(32, (kernel_size, kernel_size)))
		seq.add(Activation('relu'))
		seq.add(MaxPooling2D(pool_size=(2, 2))) 
		seq.add(Dropout(.25))

		#flatten 
		seq.add(Flatten())
		seq.add(Dense(128, activation='relu'))
		seq.add(Dropout(0.1))
		seq.add(Dense(50, activation='relu'))
		return seq

	base_network = build_base_network(dimension)

	img_a = Input(shape=dimension)
	img_b = Input(shape=dimension)

	feat_a = base_network(img_a)
	feat_b = base_network(img_b)

	output =Lambda(euclidean_distance, name="output_layer", output_shape=(1,))([feat_a, feat_b])

	#prediction = Dense(1,activation='sigmoid')(output)

	model1 = Model([img_a, img_b],output)
	model1.summary()

	model1.compile(loss="binary_crossentropy", optimizer=optimizers.Adam(learning_rate=0.0001),metrics=["accuracy"])

	model1.load_weights("tr_model1.h5")

	print("loaded")
	return model1


def model_predict(im1,im2):
    # Load the pre-trained VGG16 model without the top classification layer
    def image_to_feature_vector(image_path):
        img = image.load_img(image_path, target_size=(224, 224))
        x = image.img_to_array(img)
        x = np.expand_dims(x, axis=0)
        x = preprocess_input(x)
        features = model.predict(x)
        return features.flatten()

    # Path to the two images you want to compare
    image1_path = im1
    image2_path = im2

    # Convert the images to feature vectors
    image1_features = image_to_feature_vector(image1_path)
    image2_features = image_to_feature_vector(image2_path)

    # Reshape the feature vectors to be 2D arrays
    image1_features = image1_features.reshape(1, -1)
    image2_features = image2_features.reshape(1, -1)

    # Calculate cosine similarity
    similarity_score = cosine_similarity(image1_features, image2_features)[0][0]
    return similarity_score