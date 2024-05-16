import base64
import requests
import json

# OpenAI API Key
api_key = ""

# Function to encode the image
def encode_image(image_path):
  with open(image_path, "rb") as image_file:
    return base64.b64encode(image_file.read()).decode('utf-8')

# Path to your image
image_path = "device\images\\tomato_4.jpg"

# Getting the base64 string
base64_image = encode_image(image_path)

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
# print(response)
print(response['choices'][0]['message']['content'])