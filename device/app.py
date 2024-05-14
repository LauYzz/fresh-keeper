import streamlit as st
import cv2
import numpy as np
from PIL import Image

# GPT-4V 食材识别函数
def identify_ingredients(image):
    # 这里是一个模拟的食材识别函数，你需要用实际的GPT-4V API来替换
    # 传入的图像应进行适当处理，然后发送给GPT-4V进行识别
    # 返回的结果是识别到的食材列表
    ingredients = ["Tomato", "Lettuce", "Cheese"]  # 示例结果
    return ingredients

# 设置页面标题
st.title("实时摄像头食材识别")

# 初始化摄像头
cap = cv2.VideoCapture(0)

# 创建一个按钮，用于拍摄图像
if st.button("拍摄图像"):
    ret, frame = cap.read()
    if ret:
        # 显示拍摄的图像
        st.image(frame, channels="BGR")

        # 将图像从BGR转换为RGB
        image_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        image_pil = Image.fromarray(image_rgb)

        # 调用食材识别函数
        ingredients = identify_ingredients(image_pil)
        
        # 显示识别结果
        st.write("识别到的食材：")
        st.write(ingredients)

# 显示实时摄像头图像
frame_placeholder = st.empty()
while True:
    ret, frame = cap.read()
    if not ret:
        break
    frame_placeholder.image(frame, channels="BGR")

# 释放摄像头资源
cap.release()
