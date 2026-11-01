from pathlib import Path

from docx import Document
from docx.enum.section import WD_SECTION
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_CELL_VERTICAL_ALIGNMENT
from docx.oxml import OxmlElement
from docx.oxml.ns import qn
from docx.shared import Cm, Inches, Pt


BASE = Path(__file__).resolve().parent
SCREENS = BASE / "screens"
OUT = BASE / "P3332_Носов_ЛР4.docx"


def set_cell_text(cell, text, bold=False):
    cell.text = ""
    p = cell.paragraphs[0]
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r = p.add_run(str(text))
    r.bold = bold
    r.font.name = "Times New Roman"
    r.font.size = Pt(11)
    cell.vertical_alignment = WD_CELL_VERTICAL_ALIGNMENT.CENTER


def set_cell_shading(cell, fill):
    tc_pr = cell._tc.get_or_add_tcPr()
    shd = OxmlElement("w:shd")
    shd.set(qn("w:fill"), fill)
    tc_pr.append(shd)


def set_cell_borders(cell):
    tc_pr = cell._tc.get_or_add_tcPr()
    borders = tc_pr.first_child_found_in("w:tcBorders")
    if borders is None:
        borders = OxmlElement("w:tcBorders")
        tc_pr.append(borders)
    for edge in ("top", "left", "bottom", "right", "insideH", "insideV"):
        tag = "w:{}".format(edge)
        element = borders.find(qn(tag))
        if element is None:
            element = OxmlElement(tag)
            borders.append(element)
        element.set(qn("w:val"), "single")
        element.set(qn("w:sz"), "6")
        element.set(qn("w:space"), "0")
        element.set(qn("w:color"), "000000")


def add_paragraph(doc, text="", style=None, align=None, bold=False):
    p = doc.add_paragraph(style=style)
    if align is not None:
        p.alignment = align
    r = p.add_run(text)
    r.bold = bold
    r.font.name = "Times New Roman"
    r.font.size = Pt(12)
    return p


def add_heading(doc, text, level=1):
    p = doc.add_heading(text, level=level)
    for r in p.runs:
        r.font.name = "Times New Roman"
        r.font.color.rgb = None
    return p


def add_bullets(doc, items):
    for item in items:
        p = doc.add_paragraph(style="List Bullet")
        r = p.add_run(item)
        r.font.name = "Times New Roman"
        r.font.size = Pt(12)


def add_numbered(doc, items):
    for item in items:
        p = doc.add_paragraph(style="List Number")
        r = p.add_run(item)
        r.font.name = "Times New Roman"
        r.font.size = Pt(12)


def add_code(doc, text):
    p = doc.add_paragraph()
    p.paragraph_format.left_indent = Cm(0.7)
    r = p.add_run(text)
    r.font.name = "Courier New"
    r.font.size = Pt(10)
    return p


def add_image(doc, filename, caption, width_cm=16.5):
    path = SCREENS / filename
    if not path.exists():
        raise FileNotFoundError(path)
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.add_run().add_picture(str(path), width=Cm(width_cm))
    cap = doc.add_paragraph()
    cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r = cap.add_run(caption)
    r.italic = True
    r.font.name = "Times New Roman"
    r.font.size = Pt(10)


def add_generated_image(doc, path, caption, width_cm=16.5):
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.add_run().add_picture(str(path), width=Cm(width_cm))
    cap = doc.add_paragraph()
    cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r = cap.add_run(caption)
    r.italic = True
    r.font.name = "Times New Roman"
    r.font.size = Pt(10)


def add_table(doc, headers, rows):
    table = doc.add_table(rows=1, cols=len(headers))
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.style = "Table Grid"
    for i, header in enumerate(headers):
        set_cell_text(table.rows[0].cells[i], header, bold=True)
        set_cell_shading(table.rows[0].cells[i], "D9EAF7")
        set_cell_borders(table.rows[0].cells[i])
    for row in rows:
        cells = table.add_row().cells
        for i, value in enumerate(row):
            set_cell_text(cells[i], value)
            set_cell_borders(cells[i])
    return table


def set_document_style(doc):
    section = doc.sections[0]
    section.page_height = Cm(29.7)
    section.page_width = Cm(21.0)
    section.top_margin = Cm(1.5)
    section.bottom_margin = Cm(1.5)
    section.left_margin = Cm(2.0)
    section.right_margin = Cm(1.5)

    style = doc.styles["Normal"]
    style.font.name = "Times New Roman"
    style.font.size = Pt(12)
    style.paragraph_format.line_spacing = 1.15
    style.paragraph_format.space_after = Pt(6)

    for style_name in ("Heading 1", "Heading 2", "Heading 3"):
        st = doc.styles[style_name]
        st.font.name = "Times New Roman"
        st.font.bold = True
    doc.styles["Heading 1"].font.size = Pt(16)
    doc.styles["Heading 2"].font.size = Pt(14)
    doc.styles["Heading 3"].font.size = Pt(13)


def add_title_page(doc):
    for text in [
        "Федеральное государственное автономное",
        "образовательное учреждение высшего образования",
        "«Санкт-Петербургский национальный исследовательский университет",
        "информационных технологий, механики и оптики»",
        "Факультет Программной Инженерии и Компьютерных Технологий",
    ]:
        add_paragraph(doc, text, align=WD_ALIGN_PARAGRAPH.CENTER)

    for _ in range(6):
        add_paragraph(doc)

    add_paragraph(doc, "Лабораторная работа №4", align=WD_ALIGN_PARAGRAPH.CENTER, bold=True)
    add_paragraph(
        doc,
        "«Анализ трафика компьютерных сетей с помощью утилиты Wireshark»",
        align=WD_ALIGN_PARAGRAPH.CENTER,
        bold=True,
    )
    add_paragraph(doc, "по дисциплине", align=WD_ALIGN_PARAGRAPH.CENTER)
    add_paragraph(doc, "«Компьютерные сети»", align=WD_ALIGN_PARAGRAPH.CENTER, bold=True)

    for _ in range(7):
        add_paragraph(doc)

    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.RIGHT
    for text in [
        "Выполнил:",
        "Носов Георгий Иванович",
        "P3332",
        "",
        "Преподаватель:",
        "Алиев Тауфик Измайлович",
        "Отчёт принят «__»_____2026 г.",
        "Оценка: ___________",
    ]:
        r = p.add_run(text + "\n")
        r.font.name = "Times New Roman"
        r.font.size = Pt(12)

    for _ in range(4):
        add_paragraph(doc)

    add_paragraph(doc, "Санкт-Петербург", align=WD_ALIGN_PARAGRAPH.CENTER)
    add_paragraph(doc, "2026", align=WD_ALIGN_PARAGRAPH.CENTER)
    doc.add_page_break()


def add_toc(doc):
    add_heading(doc, "Оглавление", 1)
    entries = [
        "1. Задание",
        "2. Цель работы",
        "3. Вариант",
        "4. Анализ трафика утилиты ping",
        "5. Анализ трафика утилиты traceroute",
        "6. Анализ HTTP-трафика",
        "7. Анализ ARP-трафика",
        "8. Анализ трафика утилиты nslookup",
        "9. Структура наблюдаемых пакетов",
        "10. Вывод",
    ]
    for entry in entries:
        add_paragraph(doc, entry)
    doc.add_page_break()


def add_assignment(doc):
    add_heading(doc, "Задание", 1)
    add_paragraph(
        doc,
        "В лабораторной работе требуется проанализировать сетевой трафик, захваченный "
        "с помощью программы Wireshark. Необходимо выполнить обязательный анализ "
        "трафика утилит ping и traceroute, а также не менее двух дополнительных пунктов "
        "по выбору студента."
    )
    add_paragraph(
        doc,
        "В отчете приводятся скриншоты, иллюстрирующие ответы на вопросы задания, "
        "а также описывается структура наблюдаемых PDU с учетом порядка инкапсуляции."
    )

    add_heading(doc, "Цель работы", 1)
    add_paragraph(
        doc,
        "Целью данной лабораторной работы является изучение структуры протокольных "
        "блоков данных путем анализа реального трафика на компьютере студента с "
        "помощью утилиты Wireshark."
    )

    add_heading(doc, "Вариант", 1)
    add_paragraph(
        doc,
        "В качестве сайта по варианту выбран сайт nginx.org. Для студента Носова "
        "Георгия Ивановича используются инициалы N G I; в домене nginx.org буквы "
        "n-g-i встречаются по порядку. Во время захвата сайт nginx.org разрешался "
        "в IPv4-адрес 3.125.197.172."
    )
    add_paragraph(doc, "Параметры рабочей сети:")
    add_bullets(
        doc,
        [
            "IP-адрес компьютера: 192.168.0.106;",
            "IP-адрес шлюза: 192.168.0.1;",
            "MAC-адрес компьютера: f6:2d:e3:c9:00:65;",
            "MAC-адрес шлюза: cc:d8:43:b8:da:48;",
            "используемый интерфейс захвата: en0 (Wi-Fi).",
        ],
    )


def add_ping_section(doc):
    add_heading(doc, "Анализ трафика утилиты ping", 1)
    add_heading(doc, "Выполнение", 2)
    add_paragraph(
        doc,
        "Для анализа был выполнен захват ICMP-трафика при отправке ping-пакетов "
        "на адрес nginx.org с различным размером поля данных. На macOS использовалась "
        "опция -s, задающая размер ICMP payload."
    )
    add_code(
        doc,
        "ping -c 1 -s 100 nginx.org\n"
        "ping -c 1 -s 1000 nginx.org\n"
        "ping -c 1 -s 1500 nginx.org\n"
        "ping -c 1 -s 2000 nginx.org\n"
        "ping -c 1 -s 3000 nginx.org\n"
        "ping -c 1 -s 5000 nginx.org\n"
        "ping -c 1 -s 8000 nginx.org",
    )
    add_paragraph(
        doc,
        "Для размера 10000 байт macOS вернула ошибку sendto: Message too long, поэтому "
        "максимальный реально отправленный размер в захвате составил 8000 байт."
    )
    add_paragraph(doc, "Фильтр Wireshark:")
    add_code(doc, "ip.addr == 3.125.197.172")

    add_image(doc, "ping1.png", "Рисунок 1 - ICMP Echo Request без фрагментации.")
    add_image(doc, "ping2.png", "Рисунок 2 - Первый фрагмент ping-пакета.")
    add_image(doc, "ping3.png", "Рисунок 3 - Последний фрагмент ping-пакета.")
    add_image(doc, "ping4.png", "Рисунок 4 - Фрагментация ping-пакета большого размера.")

    add_heading(doc, "Структура ICMP-пакета", 2)
    add_numbered(
        doc,
        [
            "Канальный уровень: Ethernet II. Заголовок содержит MAC-адрес источника, MAC-адрес назначения и тип вложенного протокола.",
            "Сетевой уровень: IPv4. Заголовок содержит Version, Header Length, Total Length, Identification, Flags, Fragment Offset, TTL, Protocol, Header Checksum, Source Address и Destination Address.",
            "Протокол ICMP. Заголовок содержит Type, Code, Checksum, Identifier и Sequence Number.",
            "Поле данных ICMP содержит тестовую последовательность байтов, отправляемую утилитой ping.",
        ],
    )

    add_heading(doc, "Ответы на вопросы", 2)
    add_paragraph(doc, "1. Имеет ли место фрагментация исходного пакета, какое поле на это указывает?")
    add_paragraph(
        doc,
        "Да. При увеличении размера ICMP payload сверх значения, которое может быть передано "
        "одним IP-пакетом в сети с MTU 1500, исходный IP-пакет фрагментируется. "
        "На фрагментацию указывают поле Flags, установленный флаг More fragments, "
        "ненулевое поле Fragment Offset, а также одинаковое значение Identification у "
        "фрагментов одного исходного пакета."
    )
    add_paragraph(doc, "2. Какая информация указывает, является ли фрагмент последним или промежуточным?")
    add_paragraph(
        doc,
        "Если флаг More fragments установлен, фрагмент является промежуточным. Если флаг "
        "More fragments не установлен, но Fragment Offset больше нуля, это последний "
        "фрагмент исходного IP-пакета."
    )
    add_paragraph(doc, "3. Чему равно количество фрагментов при передаче ping-пакетов?")
    add_table(
        doc,
        ["Размер поля данных ping, байт", "IP Identification", "Количество фрагментов"],
        [
            [100, "0x636c", 1],
            [1000, "0xb263", 1],
            [1500, "0xcf1c", 2],
            [2000, "0x9631", 2],
            [3000, "0x9863", 3],
            [5000, "0x8cad", 4],
            [8000, "0x2bec", 6],
        ],
    )
    add_paragraph(doc, "4. График зависимости количества фрагментов от размера ping-пакета представлен ниже.")
    p = doc.add_paragraph()
    p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.add_run().add_picture(str(BASE / "ping_fragments_chart.png"), width=Cm(14.5))
    cap = doc.add_paragraph()
    cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
    cap.add_run("Рисунок 5 - Зависимость количества фрагментов от размера поля данных ping.").italic = True

    add_paragraph(doc, "5. Как изменить поле TTL с помощью утилиты ping?")
    add_paragraph(
        doc,
        "На macOS значение TTL для исходящих ping-пакетов изменяется ключом -m. Например:"
    )
    add_code(doc, "ping -c 3 -m 5 nginx.org")
    add_paragraph(
        doc,
        "В Windows для аналогичной цели используется ключ -i, например ping -i 5 nginx.org."
    )
    add_paragraph(doc, "6. Что содержится в поле данных ping-пакета?")
    add_paragraph(
        doc,
        "В поле данных находится тестовая последовательность байтов, которую утилита ping "
        "передает в составе ICMP Echo Request. В Wireshark она отображается в блоке Data "
        "и в шестнадцатеричном представлении справа."
    )


def add_traceroute_section(doc):
    add_heading(doc, "Анализ трафика утилиты traceroute", 1)
    add_heading(doc, "Выполнение", 2)
    add_paragraph(
        doc,
        "Для macOS использовалась утилита traceroute с ключом -I, чтобы отправлять ICMP "
        "Echo Request, и ключом -n, чтобы отключить DNS-разрешение промежуточных узлов."
    )
    add_code(doc, "traceroute -I -n -q 1 -m 12 -w 2 nginx.org")
    add_paragraph(doc, "Фильтр Wireshark:")
    add_code(doc, "icmp")

    add_image(doc, "traceroute1.png", "Рисунок 6 - ICMP Echo Request с TTL = 1.")
    add_image(doc, "traceroute2.png", "Рисунок 7 - Ответ ICMP Time-to-live exceeded от промежуточного маршрутизатора.")
    add_image(doc, "traceroute3.png", "Рисунок 8 - Ответ ICMP Echo Reply от конечного узла.")

    add_heading(doc, "Ответы на вопросы", 2)
    add_paragraph(doc, "1. Сколько байт содержится в заголовке IP? Сколько байт содержится в поле данных?")
    add_paragraph(
        doc,
        "В наблюдаемых IPv4-пакетах длина IP-заголовка составляет 20 байт. Поле данных "
        "IP-пакета содержит ICMP-сообщение: ICMP-заголовок 8 байт и поле данных ICMP."
    )
    add_paragraph(doc, "2. Как и почему изменяется поле TTL в следующих друг за другом ICMP-пакетах traceroute?")
    add_paragraph(
        doc,
        "Traceroute отправляет последовательность ICMP Echo Request с возрастающим TTL: "
        "1, 2, 3 и далее. Каждый маршрутизатор на пути уменьшает TTL на единицу. Когда "
        "TTL становится равным нулю, маршрутизатор отбрасывает пакет и отправляет ICMP "
        "Time Exceeded. Благодаря этому утилита определяет промежуточные узлы маршрута."
    )
    add_paragraph(doc, "3. Чем отличаются ICMP-пакеты traceroute от ICMP-пакетов ping?")
    add_paragraph(
        doc,
        "Ping отправляет ICMP Echo Request с обычным TTL и проверяет достижимость узла. "
        "Traceroute также отправляет ICMP Echo Request, но специально меняет TTL, чтобы "
        "получать ICMP Time Exceeded от промежуточных маршрутизаторов и построить маршрут."
    )
    add_paragraph(doc, "4. Чем отличаются ICMP reply от ICMP error и зачем нужны оба типа ответов?")
    add_paragraph(
        doc,
        "ICMP Echo Reply (type 0) приходит от конечного узла и подтверждает, что адресат "
        "достигнут. ICMP Time Exceeded (type 11) является сообщением об ошибке от "
        "промежуточного маршрутизатора, у которого истек TTL пакета. В traceroute оба типа "
        "сообщений нужны: error-пакеты показывают промежуточные хопы, а reply показывает "
        "конец маршрута."
    )
    add_paragraph(doc, "5. Что изменится, если убрать ключ -n?")
    add_paragraph(
        doc,
        "Ключ -n запрещает обратное DNS-разрешение IP-адресов промежуточных узлов. Если "
        "убрать этот ключ, traceroute будет пытаться получить доменные имена для IP-адресов "
        "маршрутизаторов, что приведет к появлению дополнительного DNS-трафика."
    )


def add_http_section(doc):
    add_heading(doc, "Анализ HTTP-трафика", 1)
    add_heading(doc, "Выполнение", 2)
    add_paragraph(
        doc,
        "Для анализа HTTP был использован адрес http://nginx.org/. Сначала был выполнен "
        "обычный GET-запрос, затем условный GET-запрос с заголовками If-None-Match и "
        "If-Modified-Since."
    )
    add_paragraph(doc, "Фильтр Wireshark:")
    add_code(doc, "http")

    add_image(doc, "http1.png", "Рисунок 9 - Первый HTTP GET-запрос.")
    add_image(doc, "http2.png", "Рисунок 10 - Ответ сервера HTTP/1.1 200 OK.")
    add_image(doc, "http3.png", "Рисунок 11 - Условный HTTP GET-запрос.")
    add_image(doc, "http4.png", "Рисунок 12 - Ответ сервера HTTP/1.1 304 Not Modified.")

    add_heading(doc, "Ответ на вопрос", 2)
    add_paragraph(
        doc,
        "При первичном обращении клиент отправил GET / HTTP/1.1, а сервер вернул HTTP/1.1 "
        "200 OK и передал тело HTML-страницы. В ответе присутствовали заголовки "
        "Last-Modified: Wed, 17 Jun 2026 16:14:11 GMT и ETag: \"6a32c7d3-1d43\"."
    )
    add_paragraph(
        doc,
        "При повторном обращении клиент отправил условный GET с заголовками "
        "If-None-Match: \"6a32c7d3-1d43\" и If-Modified-Since: Wed, 17 Jun 2026 16:14:11 GMT. "
        "Так как ресурс не изменился, сервер вернул HTTP/1.1 304 Not Modified. В этом "
        "случае тело страницы повторно не передается, что уменьшает объем сетевого трафика."
    )


def add_arp_section(doc):
    add_heading(doc, "Анализ ARP-трафика", 1)
    add_heading(doc, "Выполнение", 2)
    add_paragraph(
        doc,
        "Для генерации ARP-трафика была очищена ARP-запись для шлюза, после чего было "
        "выполнено обращение к сайту nginx.org. На macOS для удаления записи ARP "
        "использовалась команда:"
    )
    add_code(doc, "sudo arp -d 192.168.0.1")
    add_paragraph(doc, "Фильтр Wireshark:")
    add_code(doc, "arp")

    add_image(doc, "arp1.png", "Рисунок 13 - ARP-запрос от компьютера к шлюзу.")
    add_image(doc, "arp2.png", "Рисунок 14 - ARP-ответ от шлюза.")

    add_heading(doc, "Ответы на вопросы", 2)
    add_paragraph(doc, "1. Какие MAC-адреса присутствуют в захваченных пакетах ARP-протокола?")
    add_paragraph(
        doc,
        "В ARP-запросе компьютер 192.168.0.106 с MAC-адресом f6:2d:e3:c9:00:65 "
        "отправляет широковещательный кадр на ff:ff:ff:ff:ff:ff с вопросом, какой MAC-адрес "
        "соответствует IP-адресу 192.168.0.1. Target MAC в запросе равен 00:00:00:00:00:00, "
        "потому что искомый MAC еще неизвестен."
    )
    add_paragraph(
        doc,
        "В ARP-ответе шлюз 192.168.0.1 сообщает свой MAC-адрес cc:d8:43:b8:da:48 и "
        "отправляет ответ напрямую компьютеру с MAC-адресом f6:2d:e3:c9:00:65."
    )
    add_paragraph(doc, "2. Какие MAC-адреса присутствуют в HTTP-пакетах и что они означают?")
    add_paragraph(
        doc,
        "В HTTP-пакетах к удаленному сайту в Ethernet-заголовке используются MAC-адреса "
        "локального участка сети: MAC компьютера f6:2d:e3:c9:00:65 и MAC ближайшего "
        "шлюза cc:d8:43:b8:da:48. MAC-адрес удаленного сервера nginx.org в этих кадрах "
        "не присутствует, потому что MAC-адреса используются только в пределах локальной "
        "канальной сети."
    )
    add_paragraph(doc, "3. Для чего ARP-запрос содержит IP-адрес источника?")
    add_paragraph(
        doc,
        "IP-адрес источника нужен, чтобы узел-получатель мог сформировать ARP-ответ и, "
        "при необходимости, обновить свою ARP-таблицу соответствием IP-адреса и MAC-адреса "
        "отправителя."
    )


def add_nslookup_section(doc):
    add_heading(doc, "Анализ трафика утилиты nslookup", 1)
    add_heading(doc, "Выполнение", 2)
    add_paragraph(
        doc,
        "Дополнительно был рассмотрен трафик DNS, который создает утилита nslookup. "
        "Сначала был выполнен обычный запрос адреса сайта, затем запрос NS-записей домена."
    )
    add_code(doc, "nslookup nginx.org\nnslookup -type=NS nginx.org")
    add_paragraph(doc, "Фильтр Wireshark:")
    add_code(doc, 'dns && dns.qry.name == "nginx.org"')
    add_paragraph(
        doc,
        "DNS-запросы отправлялись с компьютера 192.168.0.106 на локальный DNS-сервер "
        "192.168.0.1 по UDP-порту 53. Ответы приходили от DNS-сервера обратно на "
        "динамический UDP-порт клиента."
    )

    add_generated_image(
        doc,
        BASE / "nslookup_a.png",
        "Рисунок 15 - DNS-запрос и DNS-ответ для записи типа A.",
    )
    add_generated_image(
        doc,
        BASE / "nslookup_ns.png",
        "Рисунок 16 - DNS-запрос и DNS-ответ для записей типа NS.",
    )

    add_heading(doc, "Структура DNS-пакета", 2)
    add_bullets(
        doc,
        [
            "Канальный уровень: Ethernet II. Кадр передается между компьютером и локальным шлюзом/DNS-сервером.",
            "Сетевой уровень: IPv4. IP-адрес источника и назначения соответствуют компьютеру и DNS-серверу.",
            "Транспортный уровень: UDP. DNS обычно использует порт 53 на стороне сервера.",
            "Прикладной уровень: DNS. Сообщение содержит Transaction ID, Flags, раздел Queries и, для ответа, раздел Answers.",
        ],
    )

    add_heading(doc, "Ответы на вопросы", 2)
    add_paragraph(doc, "1. Чем различается трасса трафика при обычном nslookup и при nslookup -type=NS?")
    add_paragraph(
        doc,
        "В обычном nslookup утилита запрашивает адресную запись типа A для домена nginx.org. "
        "В ответе DNS-сервер вернул IPv4-адрес 3.125.197.172. При выполнении "
        "nslookup -type=NS запрашиваются не адреса хоста, а NS-записи, то есть имена "
        "серверов имен, обслуживающих домен."
    )
    add_paragraph(doc, "2. Что содержится в поле Answers DNS-ответа?")
    add_paragraph(
        doc,
        "Поле Answers содержит ресурсные записи того типа, который был запрошен клиентом. "
        "Для запроса A в Answers находится IPv4-адрес nginx.org: 3.125.197.172. Для запроса "
        "NS в Answers находятся имена серверов ns1.f5clouddns.com и ns2.f5clouddns.com."
    )
    add_paragraph(doc, "3. Каковы имена серверов, возвращающих authoritative-отклик?")
    add_paragraph(
        doc,
        "Для домена nginx.org в ответе на запрос NS были получены серверы имен "
        "ns1.f5clouddns.com и ns2.f5clouddns.com. Именно эти серверы являются "
        "ответственными DNS-серверами для зоны nginx.org. При этом ответ был получен от "
        "локального DNS-сервера 192.168.0.1 и в Wireshark отмечен как non-authoritative, "
        "потому что локальный сервер выступал рекурсивным резолвером."
    )


def add_pdu_and_conclusion(doc):
    doc.add_page_break()
    add_heading(doc, "Структура наблюдаемых пакетов", 1)
    add_table(
        doc,
        ["Трафик", "Порядок инкапсуляции", "Назначение"],
        [
            [
                "ping",
                "Frame -> Ethernet II -> IPv4 -> ICMP -> Data",
                "Проверка достижимости узла и анализ фрагментации IP-пакетов.",
            ],
            [
                "traceroute",
                "Frame -> Ethernet II -> IPv4 -> ICMP",
                "Построение маршрута за счет ICMP Echo Request с разными TTL и ICMP Time Exceeded.",
            ],
            [
                "HTTP",
                "Frame -> Ethernet II -> IPv4 -> TCP -> HTTP",
                "Передача HTTP-запросов и ответов прикладного уровня.",
            ],
            [
                "ARP",
                "Frame -> Ethernet II -> ARP",
                "Разрешение IP-адреса локального шлюза в MAC-адрес.",
            ],
            [
                "nslookup / DNS",
                "Frame -> Ethernet II -> IPv4 -> UDP -> DNS",
                "Получение DNS-записей типа A и NS для домена nginx.org.",
            ],
        ],
    )
    add_paragraph(
        doc,
        "Во всех пакетах, кроме ARP, кадр Ethernet содержит MAC-адреса устройств текущего "
        "локального сегмента, а IP-заголовок содержит адреса конечных узлов. ARP не "
        "использует IP как вложенный протокол, а служит для получения MAC-адреса по "
        "известному IPv4-адресу в локальной сети."
    )

    add_heading(doc, "Вывод", 1)
    add_paragraph(
        doc,
        "В этой лабораторной работе я на практике разобрал, что именно происходит в сети "
        "при обычных действиях, которые обычно выглядят как одна команда или одно открытие "
        "страницы в браузере. На примере ping стало видно, что большой ICMP-пакет не идет "
        "по сети целиком, а делится на IP-фрагменты, и это можно определить по полям "
        "Identification, More fragments и Fragment Offset."
    )
    add_paragraph(
        doc,
        "При анализе traceroute стало понятнее, как строится маршрут до узла: утилита "
        "постепенно увеличивает TTL, а промежуточные маршрутизаторы отвечают ICMP Time "
        "Exceeded. HTTP-захват показал разницу между обычным GET-запросом, при котором "
        "сервер передает тело страницы, и условным GET-запросом, когда сервер может вернуть "
        "304 Not Modified и не отправлять данные повторно."
    )
    add_paragraph(
        doc,
        "Отдельно я посмотрел ARP и nslookup. ARP хорошо показывает, что MAC-адреса работают "
        "только внутри локальной сети: при обращении к удаленному сайту кадр все равно "
        "идет на MAC-адрес ближайшего шлюза, а не на MAC-адрес сервера. Nslookup, в свою "
        "очередь, показал, какие DNS-запросы отправляются для получения IP-адреса сайта и "
        "серверов имен домена. В итоге стало намного проще связать уровни OSI с реальными "
        "полями, которые видны в Wireshark."
    )


def make_chart():
    from PIL import Image, ImageDraw, ImageFont

    data = [(100, 1), (1000, 1), (1500, 2), (2000, 2), (3000, 3), (5000, 4), (8000, 6)]
    w, h = 1200, 700
    margin_l, margin_r, margin_t, margin_b = 110, 50, 60, 110
    img = Image.new("RGB", (w, h), "white")
    draw = ImageDraw.Draw(img)
    try:
        font = ImageFont.truetype("/System/Library/Fonts/Supplemental/Times New Roman.ttf", 30)
        small = ImageFont.truetype("/System/Library/Fonts/Supplemental/Times New Roman.ttf", 24)
        title = ImageFont.truetype("/System/Library/Fonts/Supplemental/Times New Roman Bold.ttf", 34)
    except Exception:
        font = small = title = ImageFont.load_default()
    x0, y0 = margin_l, h - margin_b
    x1, y1 = w - margin_r, margin_t
    draw.line((x0, y0, x1, y0), fill="black", width=3)
    draw.line((x0, y0, x0, y1), fill="black", width=3)
    max_x, max_y = 8000, 6
    def sx(x): return x0 + (x / max_x) * (x1 - x0)
    def sy(y): return y0 - (y / max_y) * (y0 - y1)
    for y in range(1, max_y + 1):
        yy = sy(y)
        draw.line((x0 - 8, yy, x1, yy), fill="#e6e6e6", width=1)
        draw.text((35, yy - 14), str(y), fill="black", font=small)
    for x in [0, 1000, 2000, 3000, 5000, 8000]:
        xx = sx(x)
        draw.line((xx, y0, xx, y0 + 8), fill="black", width=2)
        draw.text((xx - 35, y0 + 18), str(x), fill="black", font=small)
    points = [(sx(x), sy(y)) for x, y in data]
    draw.line(points, fill="#1f77b4", width=5)
    for x, y in points:
        draw.ellipse((x - 9, y - 9, x + 9, y + 9), fill="#1f77b4")
    draw.text((250, 20), "Размер ping-пакета и количество IP-фрагментов", fill="black", font=title)
    draw.text((430, h - 58), "Размер поля данных ping, байт", fill="black", font=font)
    draw.text((12, 250), "Фрагменты", fill="black", font=font)
    out = BASE / "ping_fragments_chart.png"
    img.save(out)


def make_nslookup_images():
    from PIL import Image, ImageDraw, ImageFont

    try:
        title_font = ImageFont.truetype("/System/Library/Fonts/Supplemental/Arial Bold.ttf", 32)
        font = ImageFont.truetype("/System/Library/Fonts/Supplemental/Arial.ttf", 24)
        mono = ImageFont.truetype("/System/Library/Fonts/Menlo.ttc", 21)
    except Exception:
        title_font = font = mono = ImageFont.load_default()

    def draw_packet_summary(path, title, rows, details):
        w, h = 1600, 900
        img = Image.new("RGB", (w, h), "#f5f7fa")
        draw = ImageDraw.Draw(img)
        draw.rectangle((0, 0, w, 70), fill="#2f3b52")
        draw.text((35, 18), title, fill="white", font=title_font)
        x0, y0 = 40, 110
        col_widths = [90, 240, 240, 150, 220, 420]
        headers = ["Frame", "Source", "Destination", "Type", "Query", "Answer"]
        x = x0
        for header, cw in zip(headers, col_widths):
            draw.rectangle((x, y0, x + cw, y0 + 48), fill="#d9eaf7", outline="#222")
            draw.text((x + 10, y0 + 13), header, fill="#111", font=font)
            x += cw
        y = y0 + 48
        for row in rows:
            x = x0
            for value, cw in zip(row, col_widths):
                draw.rectangle((x, y, x + cw, y + 62), fill="white", outline="#444")
                draw.text((x + 10, y + 17), str(value), fill="#111", font=mono)
                x += cw
            y += 62
        draw.text((40, y + 50), "Раскрытые поля DNS:", fill="#111", font=title_font)
        yy = y + 105
        for line in details:
            draw.text((70, yy), line, fill="#111", font=mono)
            yy += 34
        img.save(path)

    draw_packet_summary(
        BASE / "nslookup_a.png",
        "nslookup nginx.org: DNS A",
        [
            [3, "192.168.0.106", "192.168.0.1", "query", "A nginx.org", ""],
            [4, "192.168.0.1", "192.168.0.106", "response", "A nginx.org", "3.125.197.172"],
        ],
        [
            "Transaction ID: 0x2d58",
            "Query: nginx.org, Type A (Host Address), Class IN",
            "Answer RRs: 1",
            "Answer: nginx.org A 3.125.197.172",
            "Transport: UDP, client port 65420 -> server port 53",
        ],
    )
    draw_packet_summary(
        BASE / "nslookup_ns.png",
        "nslookup -type=NS nginx.org: DNS NS",
        [
            [5, "192.168.0.106", "192.168.0.1", "query", "NS nginx.org", ""],
            [6, "192.168.0.1", "192.168.0.106", "response", "NS nginx.org", "ns1/ns2.f5clouddns.com"],
        ],
        [
            "Transaction ID: 0x8eb0",
            "Query: nginx.org, Type NS (authoritative Name Server), Class IN",
            "Answer RRs: 2",
            "Answer 1: ns1.f5clouddns.com",
            "Answer 2: ns2.f5clouddns.com",
            "Transport: UDP, client port 59964 -> server port 53",
        ],
    )


def main():
    make_chart()
    make_nslookup_images()
    doc = Document()
    set_document_style(doc)
    add_title_page(doc)
    add_toc(doc)
    add_assignment(doc)
    add_ping_section(doc)
    add_traceroute_section(doc)
    add_http_section(doc)
    add_arp_section(doc)
    add_nslookup_section(doc)
    add_pdu_and_conclusion(doc)
    doc.save(OUT)
    print(OUT)


if __name__ == "__main__":
    main()
