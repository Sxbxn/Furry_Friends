import tensorflow as tf
from tqdm import tqdm
m_dog_eye = tf.keras.models.load_model("Furry_Friends/EYE_Model/개_안구질환_DenseNet121.h5", compile = False)
m_cat_eye = tf.keras.models.load_model("Furry_Friends/EYE_Model/고양이_안구질환_DenseNet.h5", compile = False)

print("eye model loaded")

for i in tqdm(range(1,10),postfix='Loading Dog model...'):
    if i==3:
        continue
    exec(f'm_dog_ab0{i}_lateral = tf.keras.models.load_model("Furry_Friends/XRAY_Model/dog_ab_lateral/dog_ab0{i}_lateral.h5", compile = False)')
# m_dog_ab02_lateral = tf.keras.models.load_model("Furry_Friends/XRAY_Model/dog_ab_lateral/dog_ab02_lateral.h5", compile = False)
# m_dog_ab04_lateral = tf.keras.models.load_model("Furry_Friends/XRAY_Model/dog_ab_lateral/dog_ab04_lateral.h5", compile = False)
# m_dog_ab05_lateral = tf.keras.models.load_model("Furry_Friends/XRAY_Model/dog_ab_lateral/dog_ab05_lateral.h5", compile = False)
# m_dog_ab06_lateral = tf.keras.models.load_model("Furry_Friends/XRAY_Model/dog_ab_lateral/dog_ab06_lateral.h5", compile = False)
# m_dog_ab07_lateral = tf.keras.models.load_model("Furry_Friends/XRAY_Model/dog_ab_lateral/dog_ab07_lateral.h5", compile = False)
# m_dog_ab08_lateral = tf.keras.models.load_model("Furry_Friends/XRAY_Model/dog_ab_lateral/dog_ab08_lateral.h5", compile = False)
# m_dog_ab09_lateral = tf.keras.models.load_model("Furry_Friends/XRAY_Model/dog_ab_lateral/dog_ab09_lateral.h5", compile = False)

m_dog_ab_lateral = [m_dog_ab01_lateral,m_dog_ab02_lateral,m_dog_ab04_lateral,m_dog_ab05_lateral, 
                        m_dog_ab06_lateral,m_dog_ab07_lateral,m_dog_ab08_lateral,m_dog_ab09_lateral]

for i in tqdm(range(3,10),postfix='Loading Dog model...'):
    if i == 6 or i == 7:
        continue
    exec(f'm_dog_ab0{i}_VD = tf.keras.models.load_model("Furry_Friends/XRAY_Model/dog_ab_vd/dog_ab0{i}_VD.h5", compile = False)')

# m_dog_ab04_VD = tf.keras.models.load_model("Furry_Friends/XRAY_Model/dog_ab_vd/dog_ab04_VD.h5", compile = False)
# m_dog_ab05_VD = tf.keras.models.load_model("Furry_Friends/XRAY_Model/dog_ab_vd/dog_ab05_VD.h5", compile = False)
# m_dog_ab08_VD = tf.keras.models.load_model("Furry_Friends/XRAY_Model/dog_ab_vd/dog_ab08_VD.h5", compile = False)
# m_dog_ab09_VD = tf.keras.models.load_model("Furry_Friends/XRAY_Model/dog_ab_vd/dog_ab09_VD.h5", compile = False)

m_dog_ab_VD = [m_dog_ab03_VD,m_dog_ab04_VD,m_dog_ab05_VD,m_dog_ab08_VD, m_dog_ab09_VD]

for i in tqdm(range(1,10),postfix='Loading Cat model...'):
    if i==3:
        continue
    exec(f'm_cat_ab0{i}_lateral = tf.keras.models.load_model("Furry_Friends/XRAY_Model/cat_ab_lateral/cat_ab0{i}_lateral.h5", compile = False)')
# m_cat_ab02_lateral = tf.keras.models.load_model("Furry_Friends/XRAY_Model/cat_ab_lateral/cat_ab02_lateral.h5", compile = False)
# m_cat_ab04_lateral = tf.keras.models.load_model("Furry_Friends/XRAY_Model/cat_ab_lateral/cat_ab04_lateral.h5", compile = False)
# m_cat_ab05_lateral = tf.keras.models.load_model("Furry_Friends/XRAY_Model/cat_ab_lateral/cat_ab05_lateral.h5", compile = False)
# m_cat_ab06_lateral = tf.keras.models.load_model("Furry_Friends/XRAY_Model/cat_ab_lateral/cat_ab06_lateral.h5", compile = False)
# m_cat_ab07_lateral = tf.keras.models.load_model("Furry_Friends/XRAY_Model/cat_ab_lateral/cat_ab07_lateral.h5", compile = False)
# m_cat_ab08_lateral = tf.keras.models.load_model("Furry_Friends/XRAY_Model/cat_ab_lateral/cat_ab08_lateral.h5", compile = False)
# m_cat_ab09_lateral = tf.keras.models.load_model("Furry_Friends/XRAY_Model/cat_ab_lateral/cat_ab09_lateral.h5", compile = False)

m_cat_ab_lateral = [m_cat_ab01_lateral, m_cat_ab02_lateral, m_cat_ab04_lateral, m_cat_ab05_lateral, 
                        m_cat_ab06_lateral, m_cat_ab07_lateral, m_cat_ab08_lateral, m_cat_ab09_lateral]


for i in tqdm(range(3,10),postfix='Loading Cat model...'):
    if i== 6 or i == 7:
        continue
    exec(f'm_cat_ab0{i}_VD = tf.keras.models.load_model("Furry_Friends/XRAY_Model/cat_ab_vd/cat_ab0{i}_VD.h5", compile = False)')

# m_cat_ab04_VD = tf.keras.models.load_model("Furry_Friends/XRAY_Model/cat_ab_vd/cat_ab04_VD.h5", compile = False)
# m_cat_ab05_VD = tf.keras.models.load_model("Furry_Friends/XRAY_Model/cat_ab_vd/cat_ab05_VD.h5", compile = False)
# m_cat_ab08_VD = tf.keras.models.load_model("Furry_Friends/XRAY_Model/cat_ab_vd/cat_ab08_VD.h5", compile = False)
# m_cat_ab09_VD = tf.keras.models.load_model("Furry_Friends/XRAY_Model/cat_ab_vd/cat_ab09_VD.h5", compile = False)

m_cat_ab_VD = [m_cat_ab03_VD, m_cat_ab04_VD, m_cat_ab05_VD, m_cat_ab08_VD, m_cat_ab09_VD]


m_dog_ch01_lateral = tf.keras.models.load_model("Furry_Friends/XRAY_Model/dog_ch_lateral/dog_ch01_lateral.h5", compile = False)
m_dog_ch02_lateral = tf.keras.models.load_model("Furry_Friends/XRAY_Model/dog_ch_lateral/dog_ch02_lateral.h5", compile = False)

m_dog_ch_lateral = [m_dog_ch01_lateral, m_dog_ch02_lateral]


m_dog_ch03_VD = tf.keras.models.load_model("Furry_Friends/XRAY_Model/dog_ch_vd/dog_ch03_VD.h5", compile = False)

m_dog_ch_VD = [m_dog_ch03_VD]


m_cat_ch01_lateral = tf.keras.models.load_model("Furry_Friends/XRAY_Model/cat_ch_lateral/cat_ch01_lateral.h5", compile = False)

m_cat_ch_lateral = [m_cat_ch01_lateral]


m_dog_mu02_ap = tf.keras.models.load_model("Furry_Friends/XRAY_Model/dog_mu_ap/dog_mu02_ap.h5", compile = False)
m_dog_mu04_ap = tf.keras.models.load_model("Furry_Friends/XRAY_Model/dog_mu_ap/dog_mu04_ap.h5", compile = False)
m_dog_mu05_ap = tf.keras.models.load_model("Furry_Friends/XRAY_Model/dog_mu_ap/dog_mu05_ap.h5", compile = False)

m_dog_mu_ap = [m_dog_mu02_ap, m_dog_mu04_ap, m_dog_mu05_ap]