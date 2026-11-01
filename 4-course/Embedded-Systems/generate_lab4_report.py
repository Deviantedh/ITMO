import docx
from docx.shared import Pt, Inches, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import OxmlElement, parse_xml
from docx.oxml.ns import nsdecls, qn
import os
import subprocess

LAB1_DOCX = '/Users/devianted/Desktop/Учёба/ПВС/ПВС_ЛР1_P3432_Носов_Карандашева.docx'
OUT_DOCX  = '/Users/devianted/Desktop/Учёба/ПВС/ПВС_ЛР4_P3432_Носов_Карандашева.docx'
OUT_PDF   = '/Users/devianted/Desktop/Учёба/ПВС/ПВС_ЛР4_P3432_Носов_Карандашева.pdf'
SRC_DIR   = '/Users/devianted/STM32CubeIDE/workspace_1.9.0/lab4'
IMG_DIR   = '/Users/devianted/Desktop/Учёба/ПВС/img'

# 1. Открываем документ Lab1 как шаблон оформления
doc = docx.Document(LAB1_DOCX)

# 2. Обновляем титульный лист
p7 = doc.paragraphs[7]
p7.text = 'ЛАБОРАТОРНАЯ РАБОТА № 4'
p7.runs[0].font.name = 'Times New Roman'
p7.runs[0].font.size = Pt(16)
p7.runs[0].bold = True

p9 = doc.paragraphs[9]
p9.text = '«Проектирование вычислительных систем»\n\nТема: «Операционные системы реального времени (FreeRTOS) в STM32»\nВариант № 5 («Кодовый замок» на базе FreeRTOS)'
for r in p9.runs:
    r.font.name = 'Times New Roman'
    r.font.size = Pt(14)
    r.bold = True

# 3. Удаляем тело первой лабораторной, оставляя титульный и блок Содержания
body = doc._body._body
to_delete = body[19:-1]
for el in to_delete:
    body.remove(el)

# 4. Вспомогательные функции оформления
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

def add_image_with_caption(img_path, caption_text, width_inches=4.8):
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
add_body_p('Получить базовые знания об операционной системе реального времени (ОС РВ) FreeRTOS и стандарте CMSIS-RTOS v2; приобрести практические навыки применения ОС РВ для создания многозадачного программного обеспечения микроконтроллеров STM32; освоить механизмы вытесняющего планирования, синхронизации задач через мьютексы и межзадачного обмена данными через очереди сообщений.', bold_prefix='Цели работы: ')

add_h2('Общие требования к лабораторной работе')
add_body_p('В соответствии с регламентом дисциплины «Проектирование вычислительных систем» (раздел 5.4 обновленного методического пособия):')
add_bullet('Разработать программу для учебного стенда SDK-1.1M, которая выполняет те же функции, что и программа лабораторной работы № 3 (Вариант № 5: «Кодовый замок»), но реализована на базе ОС РВ FreeRTOS.')
add_bullet('Клиентская программа для ПК с графическим интерфейсом на языке Python (client_gui.py) используется напрямую из предыдущей лабораторной работы без изменений.')
add_bullet('В проекте должно быть реализовано как минимум две задачи (Tasks), переключаемых планировщиком:')
add_bullet('displayTask: задача обновления графического интерфейса на OLED-дисплее;', bold_prefix='– ')
add_bullet('lockTask: задача обработки команд, логики кодового замка и работы с EEPROM.', bold_prefix='– ')
add_bullet('В системе должен присутствовать как минимум один обработчик аппаратного прерывания (USART6_IRQHandler), передающий данные в задачи ОС через очередь сообщений.')
add_bullet('Изображение на дисплее не должно зависать или подвисать при выполнении других задач. Должно стабильно обеспечиваться не менее 20 кадров в секунду (FPS).')
add_bullet('Для визуального контроля плавности и отсутствия зависаний в углу дисплея мелким шрифтом должен постоянно инкрементироваться динамический счетчик от 0 до 99 (по кругу).')

# 2. АППАРАТНАЯ ЧАСТЬ И СХЕМОТЕХНИКА
add_h1('2. Аппаратная часть стенда SDK-1.1M')
add_body_p('Стенд SDK-1.1M построен на базе микроконтроллера STM32F427VIT6 (LQFP-100, Cortex-M4F, 60 МГц). В проекте задействованы следующие аппаратные компоненты стенда:')

headers_hw = ['Узел стенда', 'Обозначение', 'Интерфейс / Выводы МК', 'Адрес / Канал', 'Назначение и параметры']
data_hw = [
    ['OLED-дисплей', 'HG12864-096-W', 'I2C1 (PB8-SCL, PB9-SDA)', '0x78 (SSD1306)', '128×64 монохромный экран, отрисовка >= 20 FPS и счетчик 0..99'],
    ['Память EEPROM', 'MCP79411', 'I2C1 (PB8-SCL, PB9-SDA)', '0xAE (EEPROM)', 'Хранение мастер-пароля замка при отключении питания'],
    ['Интерфейс UART', 'FT2232HL Port B', 'USART6 (PC6-TX, PC7-RX)', '9600 бод, 8N1', 'Связь с клиентским GUI на ПК, прерывания RXNE'],
    ['Зеленый LED', 'VD1', 'TIM4_CH2 (PD13)', 'GPIO Out / PWM', 'Индикация открытого состояния замка'],
    ['Желтый LED', 'VD2', 'TIM4_CH3 (PD14)', 'GPIO Out / PWM', 'Индикация верного ввода очередного символа'],
    ['Красный LED', 'VD3', 'TIM4_CH4 (PD15)', 'GPIO Out / PWM', 'Индикация ошибки пароля и режима блокировки'],
    ['Таймер Timebase', 'TIM6', 'Внутренний таймер', 'APB1 (30 МГц)', 'Аппаратный источник тиков для HAL_GetTick()']
]
add_clean_table(headers_hw, data_hw, col_widths=[1.2, 1.1, 1.3, 1.1, 1.8])

# 3. АРХИТЕКТУРА И МЕХАНИЗМЫ FREERTOS
add_h1('3. Архитектура программного обеспечения на базе FreeRTOS')

add_h2('3.1. Разделение timebase между FreeRTOS и HAL')
add_body_p('При использовании FreeRTOS системный таймер ядра Cortex-M SysTick монопольно захватывается планировщиком операционной системы для отсчета системных квантов времени (квантование 1 мс при configTICK_RATE_HZ = 1000). Для исключения конфликтов приоритетов и взаимных блокировок источник системного времени для библиотеки HAL перенесен на базовый аппаратный таймер TIM6 (HAL_TIM_PeriodElapsedCallback -> HAL_IncTick).')

add_h2('3.2. Распределение задач и приоритетов')
add_body_p('В системе созданы две независимые задачи со следующими параметрами:')

headers_tasks = ['Имя задачи', 'Функция входа', 'Приоритет', 'Размер стека', 'Период / Назначение']
data_tasks = [
    ['displayTask', 'StartDisplayTask', 'osPriorityNormal (24)', '512 слов (2 КБ)', 'Период 40 мс (25 FPS). Отрисовка экрана и счетчика 0..99'],
    ['lockTask', 'StartLockTask', 'osPriorityAboveNormal (32)', '512 слов (2 КБ)', 'Событийная. Валидация пароля, тайм-ауты, EEPROM, UART']
]
add_clean_table(headers_tasks, data_tasks, col_widths=[1.2, 1.4, 1.3, 1.1, 1.5])

add_h2('3.3. Межзадачное взаимодействие (IPC) и защита ресурсов')
add_body_p('Для безопасной многозадачной работы реализованы следующие средства CMSIS-RTOS v2:')
add_bullet('Очередь сообщений uartQueue (размер 64 элемента по 1 байту): аппаратное прерывание USART6_IRQHandler помещает принятые байты в очередь вызовом osMessageQueuePut(). Задача lockTask ожидает поступления данных в заблокированном состоянии osMessageQueueGet(), не расходуя такты ядра.', bold_prefix='– ')
add_bullet('Мьютекс шины I2C i2cMutex: контроллер I2C1 является общим аппаратным ресурсом для дисплея SSD1306 (displayTask) и памяти EEPROM MCP79411 (lockTask). Для предотвращения срыва транзакций и аппаратных коллизий перед каждым обменом задача обязана захватить мьютекс osMutexAcquire(i2cMutexHandle, osWaitForever) и освободить его после завершения передачи.', bold_prefix='– ')
add_bullet('Механизм наследования приоритетов (Priority Inheritance) в мьютексе полностью исключает возникновение инверсии приоритетов.', bold_prefix='– ')

# 4. СРАВНИТЕЛЬНЫЙ АНАЛИЗ РЕАЛИЗАЦИЙ
add_h1('4. Сравнительный анализ реализаций: Bare-Metal (ЛР3) vs FreeRTOS (ЛР4)')
add_body_p('В соответствии с заданием приведен подробный анализ различий между реализацией кодового замка на «голом железе» (суперцикл while(1) с ручными проверками времени) и с использованием операционной системы реального времени FreeRTOS:')

headers_cmp = ['Характеристика / Критерий', 'Реализация на «голом железе» (ЛР3)', 'Реализация на FreeRTOS (ЛР4)']
data_cmp = [
    ['Архитектурный подход', 'Суперцикл while(1) с неблокирующим FSM', 'Вытесняющая многозадачность (2 независимые задачи)'],
    ['Реакция на ввод UART', 'Опрос буфера в теле цикла, возможны задержки', 'Мгновенное вытеснение дисплея задачей замка по прерыванию'],
    ['Отрисовка дисплея', 'Вызывалась по флагу dirty, могла давать микрофризы', 'Строго детерминированная: 25 кадров/с (40 мс) со счетчиком'],
    ['Использование ЦП (CPU load)', 'Ядро постоянно загружено на 100% кручением цикла', 'Энергоэффективно: в паузах процессор уходит в Idle Task'],
    ['Расход оперативной памяти', 'Минимальный (один общий стек порядка 1 КБ)', 'Выше: каждой задаче выделен стек по 2 КБ + куча FreeRTOS'],
    ['Сложность масштабирования', 'Высокая (добавление функций усложняет суперцикл)', 'Низкая (новые модули оформляются отдельными задачами)']
]
add_clean_table(headers_cmp, data_cmp, col_widths=[1.5, 2.4, 2.6])

# 5. ПРАКТИЧЕСКИЕ РЕЗУЛЬТАТЫ РАБОТЫ
add_h1('5. Практические результаты работы и тестирование с GUI')

add_h2('5.1. Графический интерфейс клиента на ПК')
add_body_p('Стенд успешно взаимодействует с клиентской программой client_gui.py на персональном компьютере через виртуальный COM-порт (FT2232HL). Графический интерфейс обеспечивает визуализацию состояния замка, потоковый ввод пароля и программирование энергонезависимой памяти EEPROM:')

add_image_with_caption(
    os.path.join(IMG_DIR, 'gui_opened.png'),
    'Рисунок 1 — Главное окно клиентской программы: замок открыт (STATUS:OK)',
    width_inches=4.4
)

add_image_with_caption(
    os.path.join(IMG_DIR, 'gui_eeprom.png'),
    'Рисунок 2 — Клиентская программа: подтверждение успешной записи нового пароля в EEPROM MCP79411',
    width_inches=4.4
)

add_h2('5.2. Функционирование стенда SDK-1.1M под управлением FreeRTOS')
add_body_p('В процессе работы стенда OLED-дисплей и светодиодная индикация наглядно отражают текущее состояние системы. Для аппаратного контроля отсутствия задержек планировщика FreeRTOS в правом верхнем углу дисплея непрерывно работает инкрементный счетчик кадров 00..99:')

add_image_with_caption(
    os.path.join(IMG_DIR, 'stand_locked_rtos.jpg'),
    'Рисунок 3 — Стенд SDK-1.1M: режим ожидания ввода пароля (Status: LOCKED, контрольный счетчик «13» в углу экрана)',
    width_inches=4.8
)

add_image_with_caption(
    os.path.join(IMG_DIR, 'stand_opened_rtos.jpg'),
    'Рисунок 4 — Стенд SDK-1.1M: авторизация доступа (>> OPENED! <<, ACCESS OK, включен зеленый светодиод VD1)',
    width_inches=4.8
)

add_image_with_caption(
    os.path.join(IMG_DIR, 'stand_error_rtos.jpg'),
    'Рисунок 5 — Стенд SDK-1.1M: индикация ошибки ввода (>> ERROR! <<, WRONG CODE, контрольный счетчик «61»)',
    width_inches=4.8
)

add_image_with_caption(
    os.path.join(IMG_DIR, 'stand_blocked_rtos.jpg'),
    'Рисунок 6 — Стенд SDK-1.1M: временная блокировка после трех ошибок (!! BLOCKED !!, Wait: 4 s, счетчик «11»)',
    width_inches=4.8
)

add_h2('5.3. Анализ детерминированности кадровой частоты (FPS)')
add_body_p('Протокол взаимодействия по последовательному порту UART (9600 бит/с) синхронизирован с конечным автоматом замка lockTask через очередь FreeRTOS:')
add_code_block('''[PC -> СТЕНД] PING
[СТЕНД -> PC] PONG
[СТЕНД -> PC] STATUS:LOCKED
[СТЕНД -> PC] DISPLAY:____

--- Пользователь вводит пароль через клиентский GUI ---
[PC -> СТЕНД] AUTH:1234
[СТЕНД -> PC] STATUS:OK
[СТЕНД -> PC] DISPLAY:OPENED

--- Стенд открывает замок на 3 секунды, загорается зеленый LED ---
[СТЕНД -> PC] STATUS:LOCKED
[СТЕНД -> PC] DISPLAY:____

--- Тест смены мастер-пароля с сохранением в EEPROM ---
[PC -> СТЕНД] SET:9876
[СТЕНД -> PC] STATUS:SAVED
[СТЕНД -> PC] DISPLAY:____''', filename="Журнал взаимодействия стенда с клиентом client_gui.py")

add_body_p('Экспериментальное наблюдение за счетчиком кадров в правом верхнем углу дисплея (00..99) показало полное отсутствие зависаний и задержек при приеме пакетов по UART и операциях записи в память EEPROM. Время отрисовки одного кадра составляет 40 мс, что в точности соответствует частоте 25 кадров в секунду (при нормативном требовании технического задания не менее 20 FPS).')

# 6. ВЫВОДЫ
add_h1('6. Выводы')
add_body_p('В ходе выполнения лабораторной работы № 4 были практически освоены принципы разработки встраиваемого программного обеспечения на базе операционной системы реального времени FreeRTOS (CMSIS-RTOS v2) для микроконтроллера STM32F427VIT6. Успешно решены следующие научно-технические задачи:')
add_bullet('Освоена архитектура FreeRTOS: вытесняющее планирование на основе приоритетов, контекстное переключение задач через механизм PendSV и разделение источников тактирования ядра (SysTick) и периферии HAL (TIM6).')
add_bullet('Разработаны две взаимодействующие задачи: задача отрисовки OLED-дисплея displayTask (стабильные 25 кадров в секунду со служебным счетчиком 00..99) и высокоприоритетная задача логики кодового замка lockTask.')
add_bullet('Реализованы потокобезопасные механизмы IPC: очередь сообщений uartQueue для обработки прерываний последовательного порта и мьютекс i2cMutex с наследованием приоритета для бесконфликтного доступа к шине I2C1.')
add_bullet('Проведен сравнительный анализ системной производительности и архитектурных особенностей Bare-Metal и FreeRTOS решений, подтвердивший высокую масштабируемость и детерминированность RTOS-подхода.')

# 7. ПРИЛОЖЕНИЕ (ИСХОДНЫЙ КОД)
add_h1('7. Приложение: Исходный код разработанных модулей')

def read_src(rel_path):
    p = os.path.join(SRC_DIR, rel_path)
    if os.path.exists(p):
        with open(p, 'r', encoding='utf-8', errors='ignore') as f:
            return f.read()
    return f"/* File not found: {rel_path} */"

add_code_block(read_src('Core/Inc/lock_fsm.h'), filename="Core/Inc/lock_fsm.h")
add_code_block(read_src('Core/Src/lock_fsm.c'), filename="Core/Src/lock_fsm.c")
add_code_block(read_src('Core/Src/uart_driver.c'), filename="Core/Src/uart_driver.c")
add_code_block(read_src('Core/Src/main.c'), filename="Core/Src/main.c (Задачи FreeRTOS)")

# Сохраняем DOCX
doc.save(OUT_DOCX)
print(f"Report successfully saved to: {OUT_DOCX}")

# Конвертируем в PDF через soffice
try:
    cmd = ['/opt/homebrew/bin/soffice', '--headless', '--convert-to', 'pdf', OUT_DOCX, '--outdir', os.path.dirname(OUT_DOCX)]
    res = subprocess.run(cmd, capture_output=True, text=True)
    if res.returncode == 0:
        print(f"PDF successfully generated: {OUT_PDF}")
    else:
        print("Soffice error:", res.stderr)
except Exception as e:
    print("PDF conversion failed:", e)

