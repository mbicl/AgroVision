from tensorflow.keras.layers import Dense, Flatten
from tensorflow.keras.preprocessing import image 
from tensorflow.keras.utils import CustomObjectScope
from tensorflow.keras import models 
from tensorflow.keras import layers 
import tensorflow as tf 
import numpy as np
from tensorflow_hub import KerasLayer
import matplotlib.pyplot as plt
import cv2

def main(name_img='/Users/murodjon/Desktop/VisionX/Plant-Disease-Detection/test/image62.jpg',temp=[]):
    mobileNet = 'https://tfhub.dev/google/tf2-preview/mobilenet_v2/feature_vector/4'
    imageSize = 224
    batchSize = 20


    Categories = ['Apple___Apple_scab',
    'Apple___Black_rot',
    'Apple___Cedar_apple_rust',
    'Apple___healthy',
    'Corn_(maize)___Cercospora_leaf_spot Gray_leaf_spot',
    'Corn_(maize)___Common_rust_',
    'Corn_(maize)___Northern_Leaf_Blight',
    'Corn_(maize)___healthy',
    'Grape___Black_rot',
    'Grape___Esca_(Black_Measles)',
    'Grape___Leaf_blight_(Isariopsis_Leaf_Spot)',
    'Grape___healthy',
    'Potato___Early_blight',
    'Potato___Late_blight',
    'Potato___healthy',
    'Tomato___Bacterial_spot',
    'Tomato___Early_blight',
    'Tomato___Late_blight',
    'Tomato___Leaf_Mold',
    'Tomato___Septoria_leaf_spot',
    'Tomato___Spider_mites Two-spotted_spider_mite',
    'Tomato___Target_Spot',
    'Tomato___Tomato_Yellow_Leaf_Curl_Virus',
    'Tomato___Tomato_mosaic_virus',
    'Tomato___healthy']



    inputShape=(batchSize, imageSize, imageSize,3)
    rescale = tf.keras.Sequential([
        layers.experimental.preprocessing.Resizing(imageSize, imageSize),
        layers.experimental.preprocessing.Rescaling(1.0/imageSize)
    ])

    nClasses = len(Categories)

    with CustomObjectScope({'KerasLayer': tf.keras.layers.Layer}):
        model =models.Sequential([
        rescale,
        tf.keras.layers.InputLayer(input_shape=inputShape, dtype=tf.float32),
        KerasLayer(mobileNet,trainable=False,dtype=tf.float32),
        Flatten(),
        Dense(1024,activation='relu'),
        Dense(512,activation='relu'),
        Dense(256,activation='relu'),
        Dense(nClasses,activation='softmax')
    ])
        
    model.build(inputShape)

    # model.summary()

    model.load_weights('Plant Disease Detection.h5')



    imagePath = name_img
    img = image.load_img(imagePath, target_size=(224, 224))
    imageArray = image.img_to_array(img)
    imageArray = np.expand_dims(imageArray, axis=0)

    # Make a prediction
    prediction = model.predict(imageArray)

    # Print the predicted class
    categories = Categories
    predicted_class = np.argmax(prediction)
    percent = (name_img.split("/")[-1].split(".")[0].split("image")[-1])
    print(percent)
    img = cv2.imread(name_img)
    img = cv2.cvtColor(img,cv2.COLOR_BGR2RGB)
    img = cv2.putText(img,categories[predicted_class]+f" - {percent}%",(10,10),1,0.9,(255,0,0))
    result = f"test/result{percent}.jpg"
    img_bgr = cv2.cvtColor(img,cv2.COLOR_RGB2BGR)
    cv2.imwrite(result,img_bgr)
    b1=0.18409077
    b2 = 0.10681372
    b0=float(percent)
    date=[]
    date_per=[]
    k=12
    temp=[[10.4,63.2],[10.9,33.9],[12.4,24.5],[13.6,17.7],[16.2,15.7],[17.1,18.9],[18.4,16.6],[16.3,21.8],[17.0,29.0],[18.5,23.7]]
    for i in temp:
        new_temperature,new_humidity=i
        k+=1
        zz = b0 + (new_temperature-22) * b1 + (new_humidity-10) * b2
        date.append(k)
        date_per.append(zz)
        b0=zz
    plt.xlabel("date")
    plt.ylabel("disaes percent")
    

    plt.plot(date,date_per)
    plt.savefig(f"test/prediction{percent}.jpg")
    return categories[predicted_class]+f" {percent}",result,f"test/prediction{percent}.jpg"

if __name__ == "__main__":
    main("test/image8.jpg")
    main("test/image19.jpg")
    main("test/image22.jpg")
    main("test/image34.jpg")
    main("test/image47.jpg")
    main("test/image62.jpg")



    # print(len(result),type(result[0]))
