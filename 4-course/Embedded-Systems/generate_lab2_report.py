import docx
from docx.shared import Pt, Inches, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import OxmlElement, parse_xml
from docx.oxml.ns import nsdecls, qn
import os

base_doc = docx.Document('/Users/devianted/Desktop/Учёба/ПВС/ПВС_ЛР1_P3432_Носов_Карандашева.docx')
doc = docx.Document()

# Copy page margins
for section in doc.sections:
    base_sec = base_doc.sections[0]
    section.top_margin = base_sec.top_margin
    section.bottom_margin = base_sec.bottom_margin
    section.left_margin = base_sec.left_margin
    section.right_margin = base_sec.right_margin

# Set base style font
doc.styles['Normal'].font.name = 'PT Serif'
doc.styles['Normal'].font.size = Pt(12)

def set_cell_border(cell, **kwargs):
    """
    Set cell borders
    kwargs: top, bottom, left, right
    values: dict(sz=12, val='single', color='FF0000', space='0')
    """
    tc = cell._tc
    tcPr = tc.get_or_add_tcPr()
    tcBorders = tcPr.first_child_found_in("w:tcBorders")
    if tcBorders is None:
        tcBorders = OxmlElement('w:tcBorders')
        tcPr.append(tcBorders)
    for edge in ('top', 'left', 'bottom', 'right', 'insideH', 'insideV'):
        edge_data = kwargs.get(edge)
        if edge_data:
            tag = 'w:{}'.format(edge)
            element = tcBorders.find(qn(tag))
            if element is None:
                element = OxmlElement(tag)
                tcBorders.append(element)
            for key, val in edge_data.items():
                element.set(qn('w:{}'.format(key)), str(val))

def set_cell_shading(cell, color):
    shading_xml = f'<w:shd {nsdecls("w")} w:fill="{color}"/>'
    cell._tc.get_or_add_tcPr().append(parse_xml(shading_xml))

def add_heading_1(text):
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(18)
    p.paragraph_format.space_after = Pt(8)
    p.paragraph_format.keep_with_next = True
    run = p.add_run(text)
    run.font.name = 'PT Serif'
    run.font.size = Pt(16)
    run.bold = True
    return p

def add_heading_2(text):
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(14)
    p.paragraph_format.space_after = Pt(6)
    p.paragraph_format.keep_with_next = True
    run = p.add_run(text)
    run.font.name = 'PT Serif'
    run.font.size = Pt(14)
    run.bold = True
    return p

def add_paragraph_text(text, space_after=6, bold=False, italic=False):
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(0)
    p.paragraph_format.space_after = Pt(space_after)
    p.paragraph_format.line_spacing = 1.15
    run = p.add_run(text)
    run.font.name = 'PT Serif'
    run.font.size = Pt(12)
    run.bold = bold
    run.italic = italic
    return p

def add_bullet_point(text, bold_prefix=""):
    p = doc.add_paragraph(style='List Paragraph')
    p.paragraph_format.space_before = Pt(0)
    p.paragraph_format.space_after = Pt(4)
    p.paragraph_format.line_spacing = 1.15
    if bold_prefix:
        r_pre = p.add_run(bold_prefix)
        r_pre.font.name = 'PT Serif'
        r_pre.font.size = Pt(12)
        r_pre.bold = True
    r_txt = p.add_run(text)
    r_txt.font.name = 'PT Serif'
    r_txt.font.size = Pt(12)
    return p

def add_code_block(code_text):
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(4)
    p.paragraph_format.space_after = Pt(8)
    p.paragraph_format.line_spacing = 1.0
    run = p.add_run(code_text)
    run.font.name = 'Consolas'
    run.font.size = Pt(9)
    return p

# 1. ТИТУЛЬНЫЙ ЛИСТ
def make_title_page():
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(0)
    p.paragraph_format.space_after = Pt(12)
    p.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER
    run = p.add_run('Факультет ИНФОРМАТИКИ И ВЫЧИСЛИТЕЛЬНОЙ ТЕХНИКИ')
    run.font.name = 'PT Serif'
    run.font.size = Pt(12)
    run.bold = True

    for _ in range(5):
        doc.add_paragraph()

    p = doc.add_paragraph()
    p.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.paragraph_format.space_after = Pt(6)
    run = p.add_run('ЛАБОРАТОРНАЯ РАБОТА № 2')
    run.font.name = 'Times New Roman'
    run.font.size = Pt(16)
    run.bold = True

    p = doc.add_paragraph()
    p.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.paragraph_format.space_after = Pt(6)
    run = p.add_run('по дисциплине')
    run.font.name = 'PT Serif'
    run.font.size = Pt(14)

    p = doc.add_paragraph()
    p.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.paragraph_format.space_after = Pt(24)
    run = p.add_run('«Проектирование вычислительных систем»\n\nТема: «Последовательный интерфейс UART»\nВариант № 5 («Кодовый замок»)')
    run.font.name = 'Times New Roman'
    run.font.size = Pt(14)
    run.bold = True

    for _ in range(4):
        doc.add_paragraph()

    table = doc.add_table(rows=3, cols=1)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    row_cell = table.rows[1].cells[0]
    p_tab = row_cell.paragraphs[0]
    p_tab.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.RIGHT
    run = p_tab.add_run('Выполнили:\nСтуденты группы P3432\nНосов Георгий Иванович\nКарандашева Анастасия Денисовна\n\nПреподаватель:\nКлючев Владислав Аркадьевич\n')
    run.font.name = 'PT Serif'
    run.font.size = Pt(12)

    doc.add_page_break()

make_title_page()

# 2. СОДЕРЖАНИЕ
add_heading_1('Содержание')
toc_items = [
    '1. Задание',
    '2. Архитектурное описание программы и драйверов UART',
    '3. Аппаратные особенности интерфейса UART на стенде SDK-1.1M',
    '4. Реализация драйвера UART в режимах опроса и прерываний',
    '5. Конечный автомат кодового замка по варианту № 5',
    '6. Исходные коды программы собственной разработки',
    '7. Выводы'
]
for item in toc_items:
    p = doc.add_paragraph()
    p.paragraph_format.space_before = Pt(2)
    p.paragraph_format.space_after = Pt(4)
    r = p.add_run(item)
    r.font.name = 'PT Serif'
    r.font.size = Pt(12)

doc.add_page_break()

# 3. РАЗДЕЛ 1. ЗАДАНИЕ
add_heading_1('1. Задание')
add_paragraph_text('Цели работы:', bold=True)
add_bullet_point('Изучить протокол передачи данных по последовательному интерфейсу UART.')
add_bullet_point('Получить базовые знания об организации системы прерываний в микроконтроллерах на примере семейства STM32 (контроллер NVIC архитектуры ARM Cortex-M4).')
add_bullet_point('Изучить устройство и принципы функционирования аппаратного контроллера интерфейса USART/UART, приобрести навыки организации обмена данными по UART в режимах опроса флагов (polling) и по прерываниям (interrupts).')

add_paragraph_text('Общие требования к драйверу UART:')
add_bullet_point('Разработать и реализовать два варианта драйверов UART для стенда SDK-1.1M: с использованием прерываний и без использования прерываний (по опросу флагов).')
add_bullet_point('Драйвер, использующий прерывания, должен обеспечивать работу в строго неблокирующем режиме (возврат из функции происходит сразу же, без ожидания окончания приема или отправки), а также буферизацию данных (кольцевой буфер FIFO) для исключения случайной потери байт при интенсивном потоке.')
add_bullet_point('В драйвере, не использующем прерывания, функция приёма данных также должна быть строго неблокирующей — она проверяет готовность флага RXNE и не должна зависать в цикле ожидания, если байт не поступил.')
add_bullet_point('При использовании режима «без прерываний» прерывания от соответствующего блока UART должны быть аппаратно запрещены в контроллере прерываний NVIC.')
add_bullet_point('Для всех вариантов должно быть реализовано оперативное переключение между режимами «с прерываниями» и «без прерываний» (по нажатию кнопки на стенде).')
add_bullet_point('Каждый принимаемый стендом символ должен отсылаться обратно в терминал для обеспечения эхо-вывода в консоли ПК.')
add_bullet_point('Каждое новое текстовое сообщение от стенда должно выводиться с новой строки (\\r\\n).')

add_heading_2('Спецификация варианта № 5 («Кодовый замок»)')
add_paragraph_text('В соответствии с вариантом № 5 требуется доработать программу кодового замка из лабораторной работы № 1:')
add_bullet_point('Ввод отпирающей последовательности: осуществляется не с кнопки стенда, а путем передачи символов с компьютера через терминал по интерфейсу UART на фиксированной скорости 9600 бит/с (8 бит данных, 1 стоп-бит, без контроля четности).')
add_bullet_point('Формат отпирающего кода: последовательность из восьми латинских букв без учета регистра и цифр (по умолчанию в программе установлен код «1234abcd»).')
add_bullet_point('Индикация правильного символа: ввод каждого правильного символа последовательности сопровождается кратковременной вспышкой желтого светодиода стенда.')
add_bullet_point('Индикация ошибки: ввод неверного символа сопровождается одиночной вспышкой красного светодиода, сбросом цепочки ввода в исходное состояние и инкрементом счетчика неверных попыток.')
add_bullet_point('Успешное открытие: после верного ввода всех восьми символов загорается зеленый светодиод («замок открыт»), горит фиксированное время (3 с), после чего гаснет, и система вновь переходит в дежурный режим ожидания ввода.')
add_bullet_point('Блокировка при 3 ошибках: после трех неверных вводов подряд активируется циклический тревожный сигнал красного светодиода (5 с) с блокировкой повторного ввода, после чего система возвращается в дежурный режим.')
add_bullet_point('Сброс по таймауту: если ввод кода начат, но не завершен до конца в течение 5 секунд, происходит автоматический сброс в исходное состояние с индикацией.')
add_bullet_point('Смена отпирающей последовательности: инициируется отправкой символа «+». Далее пользователь вводит новую строку (до 8 символов), завершая ввод нажатием Enter или достижением 8 символов. Стенд запрашивает подтверждение («Make this code active? (y/n): »). После ввода символа «y» новый пароль становится активным.')
add_bullet_point('Переключение режима прерываний: осуществляется нажатием тактовой кнопки стенда (PC15). При переключении в порт отправляется информационное сообщение («[Mode switched: INTERRUPTS ON]» или «[Mode switched: INTERRUPTS OFF (Polling)]»), а текущее состояние ввода замка сбрасывается в исходное.')

# 4. РАЗДЕЛ 2. АРХИТЕКТУРНОЕ ОПИСАНИЕ
add_heading_1('2. Архитектурное описание программы и драйверов UART')
add_paragraph_text('Программный комплекс построен по модульному принципу в соответствии с требованиями кооперативной многозадачности (суперцикл с неблокирующими вызовами). Применение блокирующих функций (HAL_Delay, бесконечные циклы while(!RXNE)) полностью исключено, что гарантирует мгновенную реакцию системы на внешние события.')
add_paragraph_text('В состав проекта входят следующие ключевые компоненты:')
add_bullet_point('Кольцевой буфер (ring_buffer.h): легковесная реализация циклического буфера FIFO фиксированного размера (128 байт), обеспечивающая безопасную передачу принятых байт из обработчика аппаратных прерываний в основной поток программы без гонок данных.', bold_prefix='• ')
add_bullet_point('Драйвер UART (uart_driver.h / uart_driver.c): универсальный аппаратно-независимый уровень управления модулем USART6 стенда SDK-1.1M. Инкапсулирует переключение между режимом опроса флагов и режимом кольцевой буферизации по прерываниям.', bold_prefix='• ')
add_bullet_point('Конечный автомат кодового замка (lock_fsm.h / lock_fsm.c): прикладная логика замка, адаптированная под посимвольную обработку потока из UART с реализацией эхо-вывода, распознаванием команд смены пароля, отслеживанием тайм-аутов и управлением световой сигнализацией.', bold_prefix='• ')
add_bullet_point('Модуль управления светодиодами (led.h / led.c): неблокирующий драйвер светодиодов (зеленый PD13, желтый PD14, красный PD15), обеспечивающий одиночные вспышки заданной длительности и периодическое мигание на базе счетчика HAL_GetTick().', bold_prefix='• ')
add_bullet_point('Модуль тактовой кнопки (button.h / button.c): конечный автомат обработки физической кнопки стенда PC15 с цифровой фильтрацией дребезга контактов, используемый для переключения режима прерываний UART.', bold_prefix='• ')
add_bullet_point('Главный суперцикл (main.c): центральный диспетчер, синхронно вызывающий неблокирующий опрос кнопки, чтение байт из UART и обновление состояний таймеров индикации.', bold_prefix='• ')

# 5. РАЗДЕЛ 3. АППАРАТНЫЕ ОСОБЕННОСТИ
add_heading_1('3. Аппаратные особенности интерфейса UART на стенде SDK-1.1M')
add_paragraph_text('На отладочном лабораторном стенде SDK-1.1M (микроконтроллер STM32F427VIT6) коммуникация с управляющим компьютером организована через встроенный USB-мост/отладчик на базе микросхемы FTDI FT2232. Согласно принципиальной электрической схеме стенда, сигналы отладочного виртуального COM-порта выведены на линии приемопередатчика USART6 микроконтроллера:')
add_bullet_point('Вывод PC6 (TXD): настроен в режим альтернативной функции AF8 (USART6_TX). Обеспечивает передачу данных от стенда к ПК.', bold_prefix='• ')
add_bullet_point('Вывод PC7 (RXD): настроен в режим альтернативной функции AF8 (USART6_RX). Обеспечивает прием командных символов от ПК в стенд.', bold_prefix='• ')

add_paragraph_text('Таблица 1. Аппаратные ресурсы стенда SDK-1.1M, задействованные в лабораторной работе')
table1 = doc.add_table(rows=6, cols=3)
table1.alignment = WD_TABLE_ALIGNMENT.CENTER
headers = ['Элемент стенда', 'Вывод STM32F427', 'Функциональный режим и назначение']
for c_idx, h in enumerate(headers):
    cell = table1.rows[0].cells[c_idx]
    cell.text = h
    set_cell_shading(cell, 'E0E0E0')
    set_cell_border(cell, top=dict(sz=6, val='single', color='000000'),
                          bottom=dict(sz=6, val='single', color='000000'),
                          left=dict(sz=6, val='single', color='000000'),
                          right=dict(sz=6, val='single', color='000000'))
    p = cell.paragraphs[0]
    p.runs[0].bold = True
    p.runs[0].font.name = 'PT Serif'
    p.runs[0].font.size = Pt(10)

t1_data = [
    ('USART6_TX', 'PC6', 'Альтернативная функция AF8, Push-Pull, High Speed. Передача в терминал.'),
    ('USART6_RX', 'PC7', 'Альтернативная функция AF8, Pull-Up. Прием символов с терминала ПК.'),
    ('Кнопка SB1', 'PC15', 'Вход GPIO, режим Pull-Up. Переключение режимов прерываний UART.'),
    ('Зеленый LED VD6', 'PD13', 'Выход Push-Pull. Индикация открытого состояния замка (3 сек).'),
    ('Двухцветный LED VD7', 'PD14 / PD15', 'Выходы Push-Pull. Желтый (PD14) – успех символа; Красный (PD15) – ошибка/тревога.')
]
for r_idx, row in enumerate(t1_data, start=1):
    for c_idx, val in enumerate(row):
        cell = table1.rows[r_idx].cells[c_idx]
        cell.text = val
        set_cell_border(cell, top=dict(sz=4, val='single', color='CCCCCC'),
                              bottom=dict(sz=4, val='single', color='CCCCCC'),
                              left=dict(sz=4, val='single', color='CCCCCC'),
                              right=dict(sz=4, val='single', color='CCCCCC'))
        p = cell.paragraphs[0]
        p.runs[0].font.name = 'PT Serif'
        p.runs[0].font.size = Pt(10)

add_paragraph_text('', space_after=6)

# 6. РАЗДЕЛ 4. РЕАЛИЗАЦИЯ ДРАЙВЕРА UART
add_heading_1('4. Реализация драйвера UART в режимах опроса и прерываний')
add_paragraph_text('В соответствии с заданием лабораторной работы разработана архитектура драйвера, позволяющая динамически переключаться между двумя взаимоисключающими принципами организации ввода-вывода.')

add_heading_2('Режим опроса флагов (Polling, без прерываний)')
add_paragraph_text('В режиме опроса прерывания контроллера USART6 аппаратно заблокированы в NVIC (NVIC_DisableIRQ(USART6_IRQn)) и в регистре управления USART_CR1 (сброшен бит RXNEIE).')
add_paragraph_text('Чтение данных реализовано через неблокирующий анализ статусных регистров: функция uart_driver_read_char() опрашивает флаг RXNE (Read Data Register Not Empty) в регистре USART_SR. Если флаг сброшен (байт в сдвиговый регистр приемника еще не прибыл), функция незамедлительно возвращает false, не блокируя выполнение программы. При установленном флаге RXNE байт считывается из регистра USART_DR и возвращается значение true.')

add_heading_2('Режим прерываний с кольцевым буфером FIFO')
add_paragraph_text('При активации режима прерываний вызывается функция uart_driver_set_mode(UART_MODE_INTERRUPT):')
add_bullet_point('Очищается кольцевой буфер FIFO и регистр данных DR для сброса возможных остаточных байт.')
add_bullet_point('В регистре USART_CR1 аппаратно взводится бит RXNEIE (прерывание по флагу заполнения приемного буфера).')
add_bullet_point('В контроллере прерываний ядра ARM NVIC активируется линия прерывания USART6_IRQn.')
add_paragraph_text('При поступлении байта процессор переходит в векторный обработчик USART6_IRQHandler. Обработчик считывает поступивший байт из USART_DR (что аппаратно сбрасывает флаг RXNE) и немедленно помещает его в кольцевой буфер s_rx_ring через функцию ring_buffer_put(). Дополнительно обработчик диагностирует и сбрасывает флаги аппаратных ошибок приемника (ORE – Overrun Error, NE – Noise Error, FE – Framing Error), предотвращая зависание периферийного блока.')
add_paragraph_text('В этом режиме функция uart_driver_read_char() извлекает данные напрямую из FIFO буфера через функцию ring_buffer_get(), обеспечивая нулевую вероятность потери байт даже при кратковременной занятости процессора другими прикладными задачами.')

# 7. РАЗДЕЛ 5. КОНЕЧНЫЙ АВТОМАТ КОДОВОГО ЗАМКА
add_heading_1('5. Конечный автомат кодового замка по варианту № 5')
add_paragraph_text('Конечный автомат кодового замка (FSM) управляет жизненным циклом системы и включает в себя пять состояний:')
add_bullet_point('STATE_INPUT_CODE (Ожидание и ввод кода): основное состояние. Символы, поступающие из UART, транслируются обратно (эхо). Осуществляется посимвольное сравнение без учета регистра символов (функция tolower) с эталонным паролем. При совпадении формируется вспышка желтого LED (200 мс). При ошибке генерируется вспышка красного LED (300 мс), позиция сбрасывается в 0, а счетчик ошибок инкрементируется. Если за 5 секунд ввод не завершен, происходит сброс по таймауту. При получении символа «+» автомат переходит в режим смены пароля.')
add_bullet_point('STATE_UNLOCKED (Замок открыт): наступает при верном вводе всех 8 символов кода. Зажигается зеленый светодиод PD13 на 3 секунды, в терминал выводится строка «*** ACCESS GRANTED (LOCK OPEN) ***». По истечении интервала замок автоматически запирается.')
add_bullet_point('STATE_BLOCKED_ALARM (Режим тревоги/блокировки): активируется после 3 неверных вводов подряд. Запускается циклическое мигание красного светодиода PD15 длительностью 5 секунд, в порт выводится предупреждение. Прием символов на время блокировки полностью игнорируется.')
add_bullet_point('STATE_NEW_CODE_INPUT (Ввод нового пароля): ввод новой последовательности длиной до 8 символов. Завершается либо по нажатию клавиши Enter (\\r или \\n), либо по достижении 8 символов.')
add_bullet_point('STATE_NEW_CODE_CONFIRM (Подтверждение нового пароля): запрос к пользователю «Make this code active? (y/n): ». При получении «y» или «Y» новый пароль сохраняется в оперативной памяти и становится активным. При вводе любого другого символа изменение отменяется.')

# 8. РАЗДЕЛ 6. ИСХОДНЫЕ КОДЫ
add_heading_1('6. Исходные коды программы собственной разработки')
add_paragraph_text('Ниже представлены исходные коды модулей, разработанных авторами в рамках выполнения лабораторной работы № 2.')

src_dir = '/Users/devianted/STM32CubeIDE/workspace_1.9.0/lab2'
files_to_include = [
    ('Core/Inc/ring_buffer.h', 'Заголовочный файл кольцевого буфера FIFO (ring_buffer.h)'),
    ('Core/Inc/uart_driver.h', 'Заголовочный файл драйвера UART (uart_driver.h)'),
    ('Core/Src/uart_driver.c', 'Реализация драйвера UART (uart_driver.c)'),
    ('Core/Inc/lock_fsm.h', 'Заголовочный файл конечного автомата замка (lock_fsm.h)'),
    ('Core/Src/lock_fsm.c', 'Реализация конечного автомата замка (lock_fsm.c)'),
    ('Core/Src/main.c', 'Фрагмент главного модуля программы (main.c, суперцикл)'),
    ('Core/Src/stm32f4xx_it.c', 'Фрагмент обработчика прерывания USART6 (stm32f4xx_it.c)')
]

for rel_path, title in files_to_include:
    add_heading_2(f'Файл {rel_path} ({title})')
    full_p = os.path.join(src_dir, rel_path)
    if os.path.exists(full_p):
        with open(full_p, 'r', encoding='utf-8', errors='ignore') as f:
            lines = f.readlines()
        
        # Для main.c и stm32f4xx_it.c берем только релевантные куски
        if 'main.c' in rel_path:
            # Извлекаем main() и суперцикл
            start_idx = 0
            for i, line in enumerate(lines):
                if 'int main(void)' in line:
                    start_idx = i
                    break
            code_fragment = "".join(lines[start_idx:start_idx+65])
        elif 'stm32f4xx_it.c' in rel_path:
            start_idx = 0
            for i, line in enumerate(lines):
                if 'USART6_IRQHandler' in line:
                    start_idx = max(0, i - 5)
                    break
            code_fragment = "".join(lines[start_idx:start_idx+25])
        else:
            code_fragment = "".join(lines)
        
        add_code_block(code_fragment)

# 9. РАЗДЕЛ 7. ВЫВОДЫ
add_heading_1('7. Выводы')
add_paragraph_text('В ходе выполнения лабораторной работы № 2 были детально изучены особенности последовательного интерфейса передачи данных UART и архитектура системы прерываний NVIC микроконтроллеров семейства STM32F4.')
add_paragraph_text('Основные практические результаты работы:')
add_bullet_point('1. Изучен и практически освоен аппаратный контроллер USART6 стенда SDK-1.1M, настроенный на скорость 9600 бит/с с передачей данных через интерфейс виртуального COM-порта микросхемы FTDI.')
add_bullet_point('2. Разработан и отлажен модульный драйвер UART, поддерживающий работу в двух режимах: по опросу флагов (polling) и по прерываниям (interrupts) с кольцевым буфером FIFO на 128 байт. Все операции чтения/записи выполнены строго неблокирующими.')
add_bullet_point('3. Реализовано безопасное переключение режимов прерываний UART на лету нажатием физической кнопки стенда PC15 с выдачей сервисных сообщений в терминал ПК.')
add_bullet_point('4. Успешно модернизирован конечный автомат прикладной задачи «Кодовый замок» (Вариант № 5): ввод кода переведен с физической кнопки на текстовый поток UART с поддержкой эхо-вывода, регистронезависимого сравнения 8-значного пароля, тайм-аута незавершенного ввода (5 с), блокировки при 3 ошибках (5 с) и интерактивной смены пароля по символу «+».')
add_bullet_point('5. Сохранена кооперативная многозадачная архитектура без использования блокирующих задержек HAL_Delay(), что обеспечивает высокую стабильность и отзывчивость встроенного программного обеспечения.')

doc.save('/Users/devianted/Desktop/Учёба/ПВС/ПВС_ЛР2_P3432_Носов_Карандашева.docx')
print('Report successfully created!')
