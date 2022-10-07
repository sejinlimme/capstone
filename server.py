from flask import Flask, jsonify
from flask_restful import Api
from transformers import PreTrainedTokenizerFast
import torch


Q_TKN = "<usr>"
A_TKN = "<sys>"
BOS = '</s>'
EOS = '</s>'
MASK = '<unused0>'
SENT = '<unused1>'
PAD = '<pad>'

save_ckpt_path = f"./kogpt2.pth" # 경로 설정

device = torch.device("cuda" if torch.cuda.is_available() else "cpu") # 모델 gpu 사용 여부

model = torch.load(save_ckpt_path, map_location=device) # 저장한 모델 불러오기
# 모델 구조 불러오기

#학습한 모델 불러오기
model.eval()

sent = '0'

app = Flask(__name__)  # Flask 객체 선언, 파라미터로 어플리케이션 패키지의 이름을 넣어줌

api = Api(app)

# 서버로 입력 들어온 텍스트 토큰화 시키기 위한 토크나이저
koGPT2_TOKENIZER = PreTrainedTokenizerFast.from_pretrained("skt/kogpt2-base-v2",
            bos_token=BOS, eos_token=EOS, unk_token='<unk>',
            pad_token=PAD, mask_token=MASK)

@app.route('/echo_call/<param>') #get echo api
def get_echo_call(param): #param = 입력으로 들어오는 텍스트
    input_ids = torch.LongTensor(koGPT2_TOKENIZER.encode(Q_TKN + param + SENT + sent + A_TKN)).unsqueeze(dim=0)
    pred = model(input_ids)
    pred = pred.logits
    gen = koGPT2_TOKENIZER.convert_ids_to_tokens(torch.argmax(pred, dim=-1).squeeze().numpy().tolist())[-1]
    gen = gen[:gen.find(".")]

    return jsonify({"param": gen})

if __name__ == "__main__":
    app.run("192.168.219.105", port=5000, debug=True)