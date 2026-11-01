#!/usr/bin/env python3
from __future__ import annotations

import html
import re
import sys
import argparse
from dataclasses import dataclass
from pathlib import Path

from lxml import etree


@dataclass
class Answer:
    weight: str
    body_html: str


@dataclass
class Question:
    title: str
    qtype: str
    score: str
    body_html: str
    direct_answer: str
    answers: list[Answer]


def clean_text(value: str | None) -> str:
    if not value:
        return ""
    return re.sub(r"\n{3,}", "\n\n", value.replace("\r\n", "\n")).strip()


def case_insensitive_pic_map(pics_dir: Path) -> dict[str, str]:
    result: dict[str, str] = {}
    if not pics_dir.exists():
        return result
    for path in pics_dir.iterdir():
        if path.is_file():
            result[path.name.casefold()] = path.name
    return result


def inner_html(node: etree._Element, pic_names: dict[str, str]) -> str:
    chunks: list[str] = []
    if node.text:
        chunks.append(html.escape(clean_text(node.text)).replace("\n", "<br>"))
    for child in node:
        tag = str(child.tag).lower()
        if tag == "img":
            src = child.attrib.get("src", "").replace("\\", "/")
            src_name = Path(src).name
            resolved_name = pic_names.get(src_name.casefold(), src_name)
            alt = html.escape(resolved_name)
            chunks.append(f'<img src="pics/{html.escape(resolved_name, quote=True)}" alt="{alt}">')
        elif tag == "br":
            chunks.append("<br>")
        else:
            chunks.append(
                etree.tostring(child, encoding="unicode", method="html", with_tail=False)
            )
        if child.tail:
            chunks.append(html.escape(clean_text(child.tail)).replace("\n", "<br>"))
    return "".join(chunks).strip()


def text_of(parent: etree._Element, tag: str) -> str:
    child = parent.find(tag)
    return clean_text("".join(child.itertext())) if child is not None else ""


def parse_questions(xml_path: Path) -> list[Question]:
    raw_text = xml_path.read_text(encoding="cp1251")
    raw_text = re.sub(
        r"<\?xml\s+version=(['\"])1\.0\1\s+encoding=(['\"])[^'\"]+\2\s*\?>",
        '<?xml version="1.0" encoding="UTF-8"?>',
        raw_text,
        count=1,
        flags=re.IGNORECASE,
    )
    raw_text = re.sub(r"<br\s*>", "<br/>", raw_text, flags=re.IGNORECASE)
    raw_text = re.sub(r"<img\b([^>/]*?)>", r"<img\1/>", raw_text, flags=re.IGNORECASE)
    raw_text = re.sub(r"</img\s*>", "", raw_text, flags=re.IGNORECASE)
    parser = etree.XMLParser(recover=True)
    root = etree.fromstring(raw_text.encode("utf-8"), parser=parser)
    pic_names = case_insensitive_pic_map(xml_path.parent / "pics")

    questions: list[Question] = []
    for item in root.findall("вопрос_теста"):
        body = item.find("вопрос")
        direct = text_of(item, "правильный_ответ")
        answers: list[Answer] = []
        for answer_node in item.findall("вариант_ответа"):
            wording = answer_node.find("формулировка")
            answers.append(
                Answer(
                    weight=text_of(answer_node, "вес"),
                    body_html=inner_html(wording, pic_names) if wording is not None else "",
                )
            )
        questions.append(
            Question(
                title=text_of(item, "заголовок"),
                qtype=text_of(item, "тип"),
                score=text_of(item, "оценка"),
                body_html=inner_html(body, pic_names) if body is not None else "",
                direct_answer=direct,
                answers=answers,
            )
        )
    return questions


def render_html(questions: list[Question], title: str) -> str:
    rows: list[str] = []
    for index, question in enumerate(questions, 1):
        answer_rows: list[str] = []
        for answer in question.answers:
            try:
                weight_value = float(answer.weight.replace("+", ""))
            except ValueError:
                weight_value = 0
            is_correct = weight_value > 0
            cls = "answer correct" if is_correct else "answer"
            mark = "✓" if is_correct else " "
            answer_rows.append(
                f"""
                <li class="{cls}">
                  <span class="mark">{mark}</span>
                  <div class="answer-body">{answer.body_html}</div>
                  <span class="weight">{html.escape(answer.weight)}</span>
                </li>
                """
            )
        direct_answer = ""
        if question.direct_answer:
            direct_answer = f"""
            <div class="direct-answer">
              <div class="label">Правильный ответ</div>
              <div>{html.escape(question.direct_answer).replace(chr(10), "<br>")}</div>
            </div>
            """
        meta_bits = [
            f"тип {html.escape(question.qtype)}" if question.qtype else "",
            f"оценка {html.escape(question.score)}" if question.score else "",
            html.escape(question.title) if question.title and question.title != "Deleted!" else "",
        ]
        meta = " · ".join(bit for bit in meta_bits if bit)
        rows.append(
            f"""
            <article class="question">
              <header>
                <div class="number">Вопрос {index}</div>
                <div class="meta">{meta}</div>
              </header>
              <div class="question-text">{question.body_html}</div>
              {direct_answer}
              <ol class="answers">
                {''.join(answer_rows)}
              </ol>
            </article>
            """
        )

    return f"""<!doctype html>
<html lang="ru">
<head>
  <meta charset="utf-8">
  <title>{html.escape(title)}</title>
  <style>
    @page {{ size: A4; margin: 14mm 12mm; }}
    * {{ box-sizing: border-box; }}
    body {{
      margin: 0;
      color: #172026;
      background: #f6f7f8;
      font: 14px/1.45 -apple-system, BlinkMacSystemFont, "Segoe UI", Arial, sans-serif;
    }}
    main {{
      max-width: 980px;
      margin: 0 auto;
      padding: 28px 18px 44px;
    }}
    h1 {{
      margin: 0 0 8px;
      font-size: 28px;
      line-height: 1.15;
      font-weight: 750;
    }}
    .summary {{
      margin: 0 0 24px;
      color: #52606d;
    }}
    .question {{
      break-inside: avoid;
      page-break-inside: avoid;
      margin: 0 0 16px;
      padding: 16px 18px;
      background: #fff;
      border: 1px solid #d9dee4;
      border-radius: 8px;
    }}
    .question header {{
      display: flex;
      gap: 10px;
      align-items: baseline;
      justify-content: space-between;
      margin-bottom: 10px;
      border-bottom: 1px solid #e7eaee;
      padding-bottom: 8px;
    }}
    .number {{
      font-size: 16px;
      font-weight: 750;
      color: #0c3b5d;
    }}
    .meta {{
      color: #6b7680;
      font-size: 12px;
      text-align: right;
    }}
    .question-text {{
      margin: 8px 0 12px;
      font-size: 15px;
      font-weight: 650;
    }}
    .answers {{
      list-style: none;
      margin: 0;
      padding: 0;
      display: grid;
      gap: 6px;
    }}
    .answer {{
      display: grid;
      grid-template-columns: 24px 1fr auto;
      gap: 8px;
      align-items: start;
      padding: 8px 10px;
      border: 1px solid #e1e5e9;
      border-radius: 6px;
      background: #fafbfc;
    }}
    .answer.correct {{
      border-color: #7bb38d;
      background: #eff8f1;
    }}
    .mark {{
      width: 20px;
      height: 20px;
      border-radius: 50%;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      color: #126128;
      font-weight: 800;
    }}
    .weight {{
      color: #66727c;
      font-size: 12px;
      white-space: nowrap;
    }}
    .direct-answer {{
      margin: 10px 0;
      padding: 10px 12px;
      border-left: 4px solid #24814a;
      background: #eff8f1;
    }}
    .label {{
      margin-bottom: 3px;
      color: #52606d;
      font-size: 12px;
      text-transform: uppercase;
      letter-spacing: .04em;
    }}
    img {{
      display: block;
      max-width: 100%;
      max-height: 360px;
      height: auto;
      margin: 10px 0;
      object-fit: contain;
    }}
    .answer img {{ max-height: 260px; }}
    @media print {{
      body {{ background: #fff; }}
      main {{ max-width: none; padding: 0; }}
      .question {{ border-radius: 0; }}
    }}
  </style>
</head>
<body>
  <main>
    <h1>{html.escape(title)}</h1>
    <p class="summary">Всего вопросов: {len(questions)}. Ответы с положительным весом отмечены галочкой.</p>
    {''.join(rows)}
  </main>
</body>
</html>
"""


def render_compact_html(questions: list[Question], title: str) -> str:
    rows: list[str] = []
    for index, question in enumerate(questions, 1):
        answer_rows: list[str] = []
        for answer_index, answer in enumerate(question.answers, 1):
            try:
                weight_value = float(answer.weight.replace("+", ""))
            except ValueError:
                weight_value = 0
            cls = "answer correct" if weight_value > 0 else "answer"
            answer_rows.append(
                f'<div class="{cls}"><span class="answer-num">{answer_index}&gt;</span>'
                f'<span class="answer-body">{answer.body_html}</span></div>'
            )

        direct_answer = ""
        if question.direct_answer:
            direct_answer = (
                '<div class="direct-answer"><b>Правильный ответ:</b> '
                f'{html.escape(question.direct_answer).replace(chr(10), "<br>")}</div>'
            )

        meta_bits = [
            f"тип {html.escape(question.qtype)}" if question.qtype else "",
            f"оценка {html.escape(question.score)}" if question.score else "",
            html.escape(question.title) if question.title and question.title != "Deleted!" else "",
        ]
        meta = ", ".join(bit for bit in meta_bits if bit)
        rows.append(
            f"""
            <section class="question">
              <div class="q-head"><b>{index}. </b>{question.body_html}</div>
              <div class="meta">{meta}</div>
              {direct_answer}
              <div class="answers">{''.join(answer_rows)}</div>
            </section>
            """
        )

    return f"""<!doctype html>
<html lang="ru">
<head>
  <meta charset="utf-8">
  <title>{html.escape(title)}</title>
  <style>
    @page {{ size: A4; margin: 8mm 10mm; }}
    * {{ box-sizing: border-box; }}
    body {{
      margin: 0;
      color: #000;
      background: #fff;
      font: 11.5px/1.16 "Times New Roman", Times, serif;
    }}
    main {{
      max-width: none;
      margin: 0;
      padding: 0;
    }}
    h1 {{
      margin: 0 0 5px;
      font-size: 14px;
      line-height: 1.1;
      font-weight: 700;
    }}
    .summary {{
      margin: 0 0 6px;
      font-size: 10px;
    }}
    .question {{
      break-inside: avoid;
      page-break-inside: avoid;
      margin: 0;
      padding: 4px 0 5px;
      border-top: 1px solid #999;
    }}
    .q-head {{
      margin: 0 0 2px;
      font-size: 11.8px;
    }}
    .meta {{
      margin: 0 0 2px;
      color: #555;
      font-size: 9px;
    }}
    .answers {{
      margin: 0;
      padding: 0;
    }}
    .answer {{
      margin: 0;
      padding: 0;
    }}
    .answer.correct {{
      font-weight: 700;
    }}
    .answer-num {{
      font-weight: 700;
    }}
    .answer-body {{
      display: inline;
    }}
    .direct-answer {{
      margin: 1px 0;
      font-size: 10.5px;
    }}
    br {{
      line-height: 1.05;
    }}
    img {{
      display: block;
      max-width: 100%;
      max-height: 155px;
      width: auto;
      height: auto;
      margin: 3px 0;
      object-fit: contain;
    }}
    .answer img {{
      display: inline-block;
      max-height: 115px;
      max-width: 48%;
      vertical-align: top;
      margin: 2px 4px 2px 0;
    }}
  </style>
</head>
<body>
  <main>
    <h1>{html.escape(title)}</h1>
    <p class="summary">Всего вопросов: {len(questions)}. Жирным отмечены ответы с положительным весом.</p>
    {''.join(rows)}
  </main>
</body>
</html>
"""


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("input_xml")
    parser.add_argument("output_html")
    parser.add_argument("--compact", action="store_true")
    args = parser.parse_args()
    xml_path = Path(args.input_xml).resolve()
    output_path = Path(args.output_html).resolve()
    questions = parse_questions(xml_path)
    renderer = render_compact_html if args.compact else render_html
    output_path.write_text(
        renderer(questions, "КС25 Тест ИТОГ - вопросы и ответы"),
        encoding="utf-8",
    )
    print(f"questions={len(questions)}")
    print(output_path)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
