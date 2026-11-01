import docx
from docx.shared import Pt, Inches, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import OxmlElement, parse_xml
from docx.oxml.ns import nsdecls, qn
import os

LAB1_DOCX = '/Users/devianted/Desktop/Учёба/ПВС/ПВС_ЛР1_P3432_Носов_Карандашева.docx'
OUT_DOCX  = '/Users/devianted/Desktop/Учёба/ПВС/ПВС_ЛР3_P3432_Носов_Карандашева.docx'
SRC_DIR   = '/Users/devianted/STM32CubeIDE/workspace_1.9.0/lab3'
IMG_DIR   = '/Users/devianted/Desktop/Учёба/ПВС/img'

# 1. Открываем документ Lab1 как шаблон
doc = docx.Document(LAB1_DOCX)

# 2. Обновляем титульный лист
p7 = doc.paragraphs[7]
p7.text = 'ЛАБОРАТОРНАЯ РАБОТА № 3'
p7.runs[0].font.name = 'Times New Roman'
p7.runs[0].font.size = Pt(16)
p7.runs[0].bold = True

p9 = doc.paragraphs[9]
p9.text = '«Проектирование вычислительных систем»\n\nТема: «OLED-дисплей и устройства I2C»\nВариант № 5 («Кодовый замок»)'
for r in p9.runs:
    r.font.name = 'Times New Roman'
    r.font.size = Pt(14)
    r.bold = True

# 3. Удаляем тело первой лабораторной, оставляя титульный и блок Содержания
body = doc._body._body
to_delete = body[19:-1]
for el in to_delete:
    body.remove(el)

# 4. Вспомогательные функции
def add_h1(text):
    p = doc.add_paragraph(style='Heading 1')
    r = p.add_run(text)
    r.font.name = 'Times New Roman'
    return p

def add_h2(text):
    p = doc.add_paragraph(style='Heading 2')
    r = p.add_run(text)
    r.font.name = 'Times New Roman'
    return p

def add_body_p(text, bold_prefix="", italic=False):
    p = doc.add_paragraph()
    p.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
    p.paragraph_format.space_before = Pt(0)
    p.paragraph_format.space_after = Pt(4)
    p.paragraph_format.line_spacing = 1.15
    p.paragraph_format.first_line_indent = docx.shared.Mm(10)
    
    if bold_prefix:
        r_pre = p.add_run(bold_prefix)
        r_pre.font.name = 'Times New Roman'
        r_pre.bold = True
    r = p.add_run(text)
    r.font.name = 'Times New Roman'
    r.font.size = Pt(12)
    r.italic = italic
    return p

def add_bullet(text, bold_prefix=""):
    p = doc.add_paragraph(style='List Paragraph')
    p.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.JUSTIFY
    p.paragraph_format.space_before = Pt(0)
    p.paragraph_format.space_after = Pt(2)
    p.paragraph_format.line_spacing = 1.15
    
    if bold_prefix:
        r_pre = p.add_run(bold_prefix)
        r_pre.font.name = 'Times New Roman'
        r_pre.bold = True
    r = p.add_run(text)
    r.font.name = 'Times New Roman'
    r.font.size = Pt(12)
    return p

def add_image_with_caption(img_path, caption_text, width_inches=5.2):
    if os.path.exists(img_path):
        p_img = doc.add_paragraph()
        p_img.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_img.paragraph_format.space_before = Pt(6)
        p_img.paragraph_format.space_after = Pt(2)
        p_img.paragraph_format.keep_with_next = True
        run_img = p_img.add_run()
        run_img.add_picture(img_path, width=Inches(width_inches))

        p_cap = doc.add_paragraph()
        p_cap.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER
        p_cap.paragraph_format.space_before = Pt(0)
        p_cap.paragraph_format.space_after = Pt(6)
        run_cap = p_cap.add_run(caption_text)
        run_cap.font.name = 'Times New Roman'
        run_cap.font.size = Pt(10.5)
        run_cap.italic = True

def add_code_block(code_text, filename=""):
    if filename:
        p_fn = doc.add_paragraph()
        p_fn.paragraph_format.space_before = Pt(8)
        p_fn.paragraph_format.space_after = Pt(2)
        p_fn.paragraph_format.keep_with_next = True
        r_fn = p_fn.add_run(f"Листинг: {filename}")
        r_fn.font.name = 'Times New Roman'
        r_fn.font.size = Pt(10)
        r_fn.italic = True
        r_fn.bold = True

    table = doc.add_table(rows=1, cols=1)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.autofit = False
    
    cell = table.cell(0, 0)
    cell.width = Inches(6.5)
    
    shading_xml = f'<w:shd {nsdecls("w")} w:fill="F5F5F5"/>'
    cell._tc.get_or_add_tcPr().append(parse_xml(shading_xml))
    
    border_xml = f'''<w:tcBorders {nsdecls("w")}>
        <w:top w:val="single" w:sz="4" w:space="0" w:color="CCCCCC"/>
        <w:left w:val="single" w:sz="18" w:space="0" w:color="002060"/>
        <w:bottom w:val="single" w:sz="4" w:space="0" w:color="CCCCCC"/>
        <w:right w:val="single" w:sz="4" w:space="0" w:color="CCCCCC"/>
    </w:tcBorders>'''
    cell._tc.get_or_add_tcPr().append(parse_xml(border_xml))
    
    tcMar_xml = f'<w:tcMar {nsdecls("w")}><w:top w:w="120" w:type="dxa"/><w:left w:w="180" w:type="dxa"/><w:bottom w:w="120" w:type="dxa"/><w:right w:w="180" w:type="dxa"/></w:tcMar>'
    cell._tc.get_or_add_tcPr().append(parse_xml(tcMar_xml))
    
    p = cell.paragraphs[0]
    p.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.LEFT
    p.paragraph_format.space_before = Pt(0)
    p.paragraph_format.space_after = Pt(0)
    p.paragraph_format.line_spacing = 1.0
    
    run = p.add_run(code_text.strip())
    run.font.name = 'Courier New'
    run.font.size = Pt(8.5)
    
    p_after = doc.add_paragraph()
    p_after.paragraph_format.space_before = Pt(0)
    p_after.paragraph_format.space_after = Pt(4)

def add_clean_table(headers, data, col_widths=None):
    table = doc.add_table(rows=len(data) + 1, cols=len(headers))
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.autofit = False
    
    tblPr = table._tbl.tblPr
    tblBorders_xml = f'''<w:tblBorders {nsdecls("w")}>
        <w:top w:val="single" w:sz="6" w:space="0" w:color="002060"/>
        <w:bottom w:val="single" w:sz="6" w:space="0" w:color="002060"/>
        <w:left w:val="none"/>
        <w:right w:val="none"/>
        <w:insideH w:val="single" w:sz="4" w:space="0" w:color="CCCCCC"/>
        <w:insideV w:val="none"/>
    </w:tblBorders>'''
    tblPr.append(parse_xml(tblBorders_xml))
    
    for c_idx, h_text in enumerate(headers):
        cell = table.cell(0, c_idx)
        cell.text = h_text
        shd_xml = f'<w:shd {nsdecls("w")} w:fill="E9EEF4"/>'
        cell._tc.get_or_add_tcPr().append(parse_xml(shd_xml))
        
        tcMar_xml = f'<w:tcMar {nsdecls("w")}><w:top w:w="120" w:type="dxa"/><w:left w:w="150" w:type="dxa"/><w:bottom w:w="120" w:type="dxa"/><w:right w:w="150" w:type="dxa"/></w:tcMar>'
        cell._tc.get_or_add_tcPr().append(parse_xml(tcMar_xml))
        
        p = cell.paragraphs[0]
        p.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.LEFT
        p.paragraph_format.space_before = Pt(0)
        p.paragraph_format.space_after = Pt(0)
        for r in p.runs:
            r.font.name = 'Times New Roman'
            r.font.size = Pt(11)
            r.bold = True
            
    for r_idx, row in enumerate(data, start=1):
        for c_idx, val in enumerate(row):
            cell = table.cell(r_idx, c_idx)
            cell.text = str(val)
            tcMar_xml = f'<w:tcMar {nsdecls("w")}><w:top w:w="120" w:type="dxa"/><w:left w:w="150" w:type="dxa"/><w:bottom w:w="120" w:type="dxa"/><w:right w:w="150" w:type="dxa"/></w:tcMar>'
            cell._tc.get_or_add_tcPr().append(parse_xml(tcMar_xml))
            
            p = cell.paragraphs[0]
            p.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.LEFT
            p.paragraph_format.space_before = Pt(0)
            p.paragraph_format.space_after = Pt(0)
            for r in p.runs:
                r.font.name = 'Times New Roman'
                r.font.size = Pt(10.5)
                
    if col_widths:
        for row in table.rows:
            for c_idx, w in enumerate(col_widths):
                row.cells[c_idx].width = Inches(w)
                tcW_xml = f'<w:tcW {nsdecls("w")} w:w="{int(w*1440)}" w:type="dxa"/>'
                row.cells[c_idx]._tc.get_or_add_tcPr().append(parse_xml(tcW_xml))
                
    p_after = doc.add_paragraph()
    p_after.paragraph_format.space_before = Pt(0)
    p_after.paragraph_format.space_after = Pt(4)

# ==================== СОДЕРЖИМОЕ ОТЧЕТА ====================

# 1. ЗАДАНИЕ
add_h1('1. Задание')
add_body_p('Получить базовые знания об интерфейсе I2C и особенностях передачи данных по данному интерфейсу; изучить устройство и принципы функционирования контроллера интерфейса I2C в микроконтроллерах семейства STM32; изучить устройство и принципы работы графического OLED-дисплея и энергонезависимой памяти EEPROM на стенде SDK-1.1M; разработать прикладную программу для микроконтроллера и клиентское графическое приложение для персонального компьютера.', bold_prefix='Цели работы: ')

add_h2('Спецификация варианта № 5 («Кодовый замок»)')
add_body_p('В соответствии с обновленной редакцией методических указаний к лабораторному практикуму (версия 26101101), в рамках варианта № 5 требуется модернизировать программный комплекс кодового замка:')
add_bullet('Обеспечить хранение мастер-пароля в микросхеме энергонезависимой памяти EEPROM (MCP79411), подключенной к микроконтроллеру по интерфейсу I2C.')
add_bullet('При старте микроконтроллера считывать актуальный пароль из EEPROM; при смене пароля сохранять новое значение в энергонезависимую память с подтверждением сохранности после полного обесточивания стенда.')
add_bullet('Осуществлять вывод в реальном времени на графический OLED-дисплей (контроллер SSD1306, 128x64) вводимого пароля (в виде маски) и текущего статуса системы (LOCKED, ACCESS GRANTED, WRONG CODE, BLOCKED).')
add_bullet('Разработать на ПК клиентскую программу с графическим интерфейсом пользователя (GUI на языке Python / Tkinter), осуществляющую связь со стендом по последовательному порту UART.')
add_bullet('Реализовать протокол обмена данными между ПК и стендом, включающий автоматическую команду «пинг» при подключении, ввод пароля, получение статусов и команду удаленного задания нового пароля.')
add_bullet('Обеспечить сохранение всего сеанса передачи данных по UART в локальный файл журнала на диске ПК с фиксацией точных меток времени.')

# 2. АППАРАТНАЯ ЧАСТЬ И СХЕМОТЕХНИКА
add_h1('2. Аппаратная часть и схема включения')
add_body_p('Лабораторный комплекс SDK-1.1M объединяет процессорный модуль STM32F427VIT6 и периферийные узлы, подключенные по шинам I2C1 и USART6:')

headers_hw = ['Периферийный узел', 'Микросхема', 'Интерфейс', 'Адрес / Выводы', 'Функциональное назначение']
data_hw = [
    ['OLED-дисплей', 'SSD1306 (128x64)', 'I2C1', '0x78 (PB8=SCL, PB9=SDA)', 'Отображение маски пароля и статуса замка'],
    ['Память EEPROM', 'MCP79411 (1 Кбит)', 'I2C1', '0xAE (PB8=SCL, PB9=SDA)', 'Энергонезависимое хранение мастер-пароля'],
    ['Связь с ПК', 'FT2232 Port B', 'USART6', 'PC6 (TX), PC7 (RX)', 'Дуплексный UART-обмен командами (9600 бод)'],
    ['Индикация', 'VD1 (Зеленый LED)', 'GPIO', 'PD13', 'Световой индикатор открытия замка'],
    ['Индикация', 'VD2/VD3 (Желтый/Красный)', 'GPIO', 'PD14, PD15', 'Индикация ввода символа и ошибок']
]
add_clean_table(headers_hw, data_hw, col_widths=[1.2, 1.3, 0.9, 1.4, 2.0])

add_body_p('Шина I2C1 использует выводы PB8 (линия синхронизации SCL) и PB9 (линия данных SDA). Обе линии аппаратно подтянуты к потенциалу +3.3 В резисторами подтяжки и работают в режиме открытого стока (Open-Drain).')

# 3. ПРОТОКОЛ ВЗАИМОДЕЙСТВИЯ И ДРАЙВЕРЫ
add_h1('3. Программный комплекс и протокол обмена')
add_body_p('Для обеспечения надежного взаимодействия стенда с графическим клиентом на ПК был спроектирован текстовый протокол на базе пакетов с разделителем перевода строки (\\r\\n):')

headers_proto = ['Направление', 'Команда / Ответ', 'Описание и назначение']
data_proto = [
    ['ПК -> Стенд', 'PING\\n', 'Проверка связи при запуске клиентской программы'],
    ['Стенд -> ПК', 'PONG\\n', 'Подтверждение готовности микроконтроллера'],
    ['ПК -> Стенд', 'AUTH:<пароль>\\n', 'Попытка авторизации (ввод символов пароля)'],
    ['Стенд -> ПК', 'STATUS:OK\\n', 'Пароль верный, замок открыт на 3 секунды (зеленый LED)'],
    ['Стенд -> ПК', 'STATUS:ERR\\n', 'Ошибка в пароле (красная вспышка LED)'],
    ['Стенд -> ПК', 'STATUS:BLOCKED\\n', 'Превышен лимит ошибок (3 раза): блокировка на 5 с'],
    ['ПК -> Стенд', 'SET:<пароль>\\n', 'Команда перезаписи мастер-пароля в EEPROM'],
    ['Стенд -> ПК', 'STATUS:SAVED\\n', 'Новый пароль успешно прошит в микросхему MCP79411'],
    ['Стенд -> ПК', 'DISPLAY:<маска>\\n', 'Передача текущей строки отображения дисплея']
]
add_clean_table(headers_proto, data_proto, col_widths=[1.3, 1.7, 3.5])

add_h2('Архитектура встроенного ПО (STM32)')
add_body_p('Прошивка микроконтроллера разделена на независимые функциональные модули:')
add_bullet('oled: инициализация контроллера SSD1306, работа с постраничной видеопамятью GDDRAM (1024 байта), графические примитивы и вывод текстовых строк шрифтом 11x18 и 7x10.')
add_bullet('eeprom: чтение и запись ячеек микросхемы MCP79411 с соблюдением обязательного технологического таймаута записи (5 мс на байт) и процедурой автоматической первичной инициализации.')
add_bullet('lock_fsm: конечный автомат кодового замка с контролем таймаута бездействия (10 с), блокировкой после 3 ошибок (5 с) и генерацией управляющих сигналов на светодиоды.')
add_bullet('uart_driver: кольцевой буфер приема на базе аппаратного прерывания RXNE, исключающий потерю байт при сетевом взаимодействии.')

# 4. ТЕСТИРОВАНИЕ И РЕЗУЛЬТАТЫ РАБОТЫ
add_h1('4. Тестирование и результаты работы')
add_body_p('Тестирование программного комплекса проводилось совместно с разработанным GUI-клиентом client_gui.py на персональном компьютере и аппаратным стендом SDK-1.1M.')

add_h2('4.1. Внешний вид графического клиента ПК')
add_body_p('Графический интерфейс клиента обеспечивает отображение текущего статуса замка, строку синхронного отображения символов OLED-дисплея, поля ввода пароля и формы программирования энергонезависимой памяти EEPROM:')

add_image_with_caption(
    os.path.join(IMG_DIR, 'gui_opened.png'),
    'Рисунок 1 — Главное окно клиентской программы: замок открыт (STATUS:OK)',
    width_inches=4.4
)

add_image_with_caption(
    os.path.join(IMG_DIR, 'gui_blocked.png'),
    'Рисунок 2 — Клиентская программа: срабатывание временной блокировки после ошибок (STATUS:BLOCKED)',
    width_inches=4.4
)

add_image_with_caption(
    os.path.join(IMG_DIR, 'gui_eeprom.png'),
    'Рисунок 3 — Клиентская программа: подтверждение успешной записи пароля в EEPROM MCP79411',
    width_inches=4.4
)

add_h2('4.2. Функционирование стенда SDK-1.1M')
add_body_p('В процессе работы стенда графический OLED-дисплей и светодиоды боковой панели наглядно отражают текущее состояние системы:')

add_image_with_caption(
    os.path.join(IMG_DIR, 'stand_locked.jpg'),
    'Рисунок 4 — Стенд SDK-1.1M: режим ожидания ввода (Status: LOCKED, маска пароля)',
    width_inches=4.8
)

add_image_with_caption(
    os.path.join(IMG_DIR, 'stand_opened.jpg'),
    'Рисунок 5 — Стенд SDK-1.1M: пароль принят (ACCESS OK), зажжен зеленый индикатор VD1',
    width_inches=4.8
)

add_image_with_caption(
    os.path.join(IMG_DIR, 'stand_error.jpg'),
    'Рисунок 6 — Стенд SDK-1.1M: ввод неверного пароля (WRONG CODE, сброс ввода)',
    width_inches=4.8
)

add_h2('4.3. Сохранение нового пароля в EEPROM и проверка энергонезависимости')
add_body_p('Через форму смены пароля в клиентском приложении был задан новый пароль. После подтверждения стенд записал данные в память MCP79411:')
add_code_block('''[2026-10-23 17:43:22.680] [TX] SET:2
[2026-10-23 17:43:22.706] [RX] STATUS:BLOCKED
[2026-10-23 17:43:22.722] [RX] DISPLAY:ERROR
[2026-10-23 17:43:22.866] [RX] STATUS:SAVED''', filename="Фрагмент журнала uart_session_*.log (Запись в EEPROM)")
add_body_p('Для проверки энергонезависимости стенд был полностью обесточен (отключен кабель питания USB). После повторного включения стенд успешно авторизовал доступ по новому паролю, что экспериментально подтвердило корректное сохранение ячеек EEPROM.')

# 5. ВЫВОДЫ
add_h1('5. Выводы')
add_body_p('В ходе выполнения лабораторной работы № 3 были успешно решены все поставленные задачи:')
add_bullet('Освоен интерфейс I2C и реализовано бесконфликтное взаимодействие с графическим OLED-дисплеем (SSD1306) и микросхемой энергонезависимой памяти EEPROM (MCP79411).')
add_bullet('Разработан надежный драйвер памяти EEPROM с побайтовым программированием, выдержкой технологических пауз записи и динамической поддержкой длины пароля.')
add_bullet('Реализована графическая визуализация состояний замка и вводимых символов на OLED-дисплее высокого разрешения 128x64.')
add_bullet('Создано кроссплатформенное клиентское приложение с графическим интерфейсом на языке Python, обеспечивающее удобное удаленное управление стендом и протоколирование всех транзакций с микросекундной точностью.')

# 6. ПРИЛОЖЕНИЕ (ИСХОДНЫЙ КОД)
add_h1('6. Приложение: Исходный код разработанных модулей')

def read_src(rel_path):
    p = os.path.join(SRC_DIR, rel_path)
    if os.path.exists(p):
        with open(p, 'r', encoding='utf-8', errors='ignore') as f:
            return f.read()
    return f"/* File not found: {rel_path} */"

add_code_block(read_src('Core/Inc/eeprom.h'), filename="Core/Inc/eeprom.h")
add_code_block(read_src('Core/Src/eeprom.c'), filename="Core/Src/eeprom.c")
add_code_block(read_src('Core/Inc/oled.h'), filename="Core/Inc/oled.h")
add_code_block(read_src('Core/Src/oled.c'), filename="Core/Src/oled.c")
add_code_block(read_src('Core/Inc/lock_fsm.h'), filename="Core/Inc/lock_fsm.h")
add_code_block(read_src('Core/Src/lock_fsm.c'), filename="Core/Src/lock_fsm.c")

with open('/Users/devianted/Desktop/Учёба/ПВС/client_gui.py', 'r', encoding='utf-8') as f:
    add_code_block(f.read(), filename="client_gui.py (Клиентская программа для ПК)")

# Сохранение документа
doc.save(OUT_DOCX)
print(f"Report successfully saved to: {OUT_DOCX}")
