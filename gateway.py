import re
import time

import torch
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from transformers import AutoTokenizer, AutoModelForCausalLM
from peft import PeftModel

import asyncio


request_queue = asyncio.Queue()


BASE_MODEL   = "meta-llama/Llama-3.2-1B-Instruct"
ADAPTER_PATH = r"D:\TutorialAI\ReadingMCQ_checkpoint"   


tok = AutoTokenizer.from_pretrained(BASE_MODEL)

base = AutoModelForCausalLM.from_pretrained(
    BASE_MODEL,
    torch_dtype=torch.float32,   
    device_map=None              
)

model = PeftModel.from_pretrained(base, ADAPTER_PATH)

model.eval()


def predict_answer_letter(passage: str, question: str, options: list[str]) -> str | None:
    system_prompt = (
        "You are a reading comprehension MCQ assistant.\n"
        "Your ONLY task is to choose A, B, C, or D.\n"
        "IMPORTANT RULES:\n"
        "- Output MUST be exactly one letter: A, B, C, or D.\n"
        "- Do NOT output anything else.\n"
        "- No explanation. No sentences. No JSON.\n"
        "- Output must be EXACTLY: A or B or C or D.\n"
    )

    user_prompt = (
        f"Passage:\n{passage}\n\n"
        f"Question:\n{question}\n\n"
        "Options:\n"
        f"A. {options[0]}\n"
        f"B. {options[1]}\n"
        f"C. {options[2]}\n"
        f"D. {options[3]}\n\n"
        "Your answer (A/B/C/D):"
    )

    prompt = (
        "<|begin_of_text|>"
        "<|start_header_id|>system<|end_header_id|>\n" + system_prompt + "\n"
        "<|start_header_id|>user<|end_header_id|>\n" + user_prompt + "\n"
        "<|start_header_id|>assistant<|end_header_id|>\n"
    )

    inputs = tok(prompt, return_tensors="pt").to(model.device)

    with torch.no_grad():
        outputs = model.generate(
            **inputs,
            max_new_tokens=4,
            temperature=0.1,
            do_sample=False
        )

    text = tok.decode(outputs[0], skip_special_tokens=True)

    # Extract only the assistant's reply
    if "<|start_header_id|>assistant<|end_header_id|>" in text:
        text = text.split("<|start_header_id|>assistant<|end_header_id|>")[-1].strip()

    # Clean strange characters
    text = text.strip().replace(".", "").upper()

    # Direct answer case
    if text in ["A", "B", "C", "D"]:
        return text

    # Search fallback
    m = re.search(r"\b([A-D])\b", text)
    if m:
        return m.group(1)

    return None




class PredictReq(BaseModel):
    passage: str
    question: str
    options: list[str]   

app = FastAPI(title="Reading MCQ Model API (local)", version="1.0.0")

async def model_worker():
    while True:
        req, fut = await request_queue.get()
        try:
            answer = predict_answer_letter(req.passage, req.question, req.options)
            fut.set_result(answer)
        except Exception as e:
            fut.set_exception(e)
        finally:
            request_queue.task_done()

asyncio.create_task(model_worker())

@app.post("/predict")
async def predict(req: PredictReq):
    loop = asyncio.get_event_loop()
    fut = loop.create_future()
    await request_queue.put((req, fut))
    answer = await fut
    return {"answer_letter": answer}

