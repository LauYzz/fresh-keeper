import streamlit as st
import cv2
import numpy as np
from PIL import Image
import base64
import requests
import json
from io import BytesIO

# OpenAI API Key
api_key = ""

# Function to encode the image
def encode_image(image):
    buffered = BytesIO()
    image.save(buffered, format="JPEG")
    return base64.b64encode(buffered.getvalue()).decode('utf-8')

# GPT-4V 食材识别函数
def identify_ingredients(image):
    # Encode the image
    base64_image = encode_image(image)
    
    headers = {
        "Content-Type": "application/json",
        "Authorization": f"Bearer {api_key}"
    }

    prompt = '''
    You are a very helpful assistant with a strong understanding of various ingredients.
    请识别图片中的食材，返回食材种类和个数。
    输出格式是：食材种类, 个数，不用输出其它信息。
    例子：苹果，3
    '''

    payload = {
        "model": "gpt-4-turbo",
        "messages": [
            {
                "role": "user",
                "content": [
                    {
                        "type": "text",
                        "text": prompt
                    },
                    {
                        "type": "image_url",
                        "image_url": {
                            "url": f"data:image/jpeg;base64,{base64_image}"
                        }
                    }
                ]
            }
        ],
        "temperature": 0,
        "max_tokens": 100
    }

    response = requests.post("https://api.openai.com/v1/chat/completions", headers=headers, json=payload).json()
    return response['choices'][0]['message']['content']

# 设置页面标题
st.title("实时摄像头食材识别")

# 初始化占位符
frame_placeholder = st.empty()
button_container = st.container()
result_placeholder = st.empty()

# 初始化摄像头
cap = cv2.VideoCapture(cv2.CAP_DSHOW)

def capture_image():
    ret, frame = cap.read()
    return ret, frame

# 初始化状态
if 'captured_image' not in st.session_state:
    st.session_state['captured_image'] = None

with button_container:
    col1, col2 = st.columns(2)
    with col1:
        if st.button("拍摄图像"):
            ret, frame = cap.read()
            if ret:
                # 显示拍摄的图像
                st.session_state['captured_image'] = frame
                frame_placeholder.image(frame, channels="BGR")
    with col2:
        if st.button("重置"):
            st.session_state['captured_image'] = None
            result_placeholder.empty()
            frame_placeholder.empty()
            st.experimental_rerun()

# 显示实时图像或定格图像
if st.session_state['captured_image'] is not None:
    frame_placeholder.image(st.session_state['captured_image'], channels="BGR")
    # 调用食材识别函数并显示识别结果
    with st.spinner('正在识别...'):
        # 将图像从BGR转换为RGB
        image_rgb = cv2.cvtColor(st.session_state['captured_image'], cv2.COLOR_BGR2RGB)
        image_pil = Image.fromarray(image_rgb)
        ingredients = identify_ingredients(image_pil)
        result_text = "识别到的食材：" + ingredients  # 合并文本
        result_placeholder.write(result_text)  # 使用单个 write 调用显示合并后的文本
else:
    while True:
        ret, frame = cap.read()
        if not ret:
            st.write("无法读取摄像头帧，请检查设备连接。")
            break
        frame_placeholder.image(frame, channels="BGR")

# 释放摄像头资源
cap.release()
