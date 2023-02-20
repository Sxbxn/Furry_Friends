import os
from PIL import Image
from PIL import ImageFile
import numpy as np
import tensorflow as tf

def padding(img):
    # img = Image.open(img_path)
    img = img.convert('RGB')
    w, h = img.size
    img = np.array(img)
    
    margin = [np.abs(w - h) // 2 , np.abs(w - h) // 2]
    if w > h:
        margin_list = [margin, [0,0]]
    else:
        margin_list = [[0,0], margin]
        
    if len(img.shape) == 3:
        margin_list.append([0,0])
        
    output = np.pad(img, margin_list, mode='constant')
    img = Image.fromarray(output)
    # 이미지 크기
    img = img.resize((224, 224))
    img = np.array(img)
    return img

def mk_img(img_path):
    np_img = padding(img_path)
    np_img = np.expand_dims(np_img, axis = 0)
    return np_img

def predict_result(model_path, img):
    model = tf.keras.models.load_model(model_path, compile = False)
    result = model.predict(img)
    if result > 0.5:
        str_result = "abnormal"
    else:
        str_result = "normal"
    return str_result

    """path_list = []
        img_path = "C:/venvs/testproject/Scripts/png"
       img = mk_img(img_path)
       for path in path_list:
            print(path_list) 
    """
