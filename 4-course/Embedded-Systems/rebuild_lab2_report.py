import docx
from docx.shared import Pt, Inches, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import OxmlElement, parse_xml
from docx.oxml.ns import nsdecls, qn
import os

LAB1_DOCX = '/Users/devianted/Desktop/Учёба/ПВС/ПВС_ЛР1_P3432_Носов_Карандашева.docx'
OUT_DOCX  = '/Users/devianted/Desktop/Учёба/ПВС/ПВС_ЛР2_P3432_Носов_Карандашева.docx'
SRC_DIR   = '/Users/devianted/STM32CubeIDE/workspace_1.9.0/lab2'

# 1. Открываем документ Lab1 как чистую основу, сохраняя все его внутренние стили и тему
doc = docx.Document(LAB1_DOCX)

# 2. Обновляем титульный лист (параграфы 7 и 9)
p7 = doc.paragraphs[7]
p7.text = 'ЛАБОРАТОРНАЯ РАБОТА № 2'
p7.runs[0].font.name = 'Times New Roman'
p7.runs[0].font.size = Pt(16)
p7.runs[0].bold = True

p9 = doc.paragraphs[9]
p9.text = '«Проектирование вычислительных систем»\n\nТема: «Последовательный интерфейс UART»\nВариант № 5 («Кодовый замок»)'
for r in p9.runs:
    r.font.name = 'Times New Roman'
    r.font.size = Pt(14)
    r.bold = True

# 3. Удаляем старое тело первой лабораторной работы (начиная с элемента 19), оставляя шапку и блок Содержания
body = doc._body._body
to_delete = body[19:-1]
for el in to_delete:
    body.remove(el)

# 4. Вспомогательные функции для добавления контента строго в стиле Lab1
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
    r.italic = italic
    return p

def add_bullet(text, bold_prefix=""):
    p = doc.add_paragraph(style='List Paragraph')
    p.paragraph_format.space_before = Pt(0)
    p.paragraph_format.space_after = Pt(2)
    p.paragraph_format.line_spacing = 1.15
    if bold_prefix:
        r_pre = p.add_run(bold_prefix)
        r_pre.font.name = 'Times New Roman'
        r_pre.bold = True
    r = p.add_run(text)
    r.font.name = 'Times New Roman'
    return p

def add_caption(text):
    p = doc.add_paragraph()
    p.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p.paragraph_format.space_before = Pt(6)
    p.paragraph_format.space_after = Pt(6)
    r = p.add_run(text)
    r.font.name = 'Times New Roman'
    r.font.size = Pt(11)
    return p

def add_code(code_text):
    p = doc.add_paragraph()
    p.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.LEFT
    p.paragraph_format.space_before = Pt(3)
    p.paragraph_format.space_after = Pt(6)
    p.paragraph_format.line_spacing = 1.0
    
    # XML свойства как в Lab1: серый фон F8F9FA, левый отступ
    shd_xml = f'<w:shd {nsdecls("w")} w:fill="F8F9FA"/>'
    p._p.get_or_add_pPr().append(parse_xml(shd_xml))
    p.paragraph_format.left_indent = Inches(0.2)
    
    r = p.add_run(code_text)
    r.font.name = 'Consolas'
    r.font.size = Pt(9)
    return p

def add_custom_table(headers, data, col_widths=None):
    table = doc.add_table(rows=len(data) + 1, cols=len(headers))
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    table.autofit = False
    
    # Заголовок таблицы
    for c_idx, h in enumerate(headers):
        cell = table.cell(0, c_idx)
        cell.text = h
        # Серая заливка EAEAEA как в Lab1
        shd_xml = f'<w:shd {nsdecls("w")} w:fill="EAEAEA"/>'
        cell._tc.get_or_add_tcPr().append(parse_xml(shd_xml))
        
        # Внутренние отступы ячейки
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
            
    # Строки данных
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
                
    # Установка ширин колонок
    if col_widths:
        for row in table.rows:
            for c_idx, w in enumerate(col_widths):
                row.cells[c_idx].width = Inches(w)
                tcW_xml = f'<w:tcW {nsdecls("w")} w:w="{int(w*1440)}" w:type="dxa"/>'
                row.cells[c_idx]._tc.get_or_add_tcPr().append(parse_xml(tcW_xml))
                
    # Пустой параграф после таблицы для визуального отделения
    p_after = doc.add_paragraph()
    p_after.paragraph_format.space_before = Pt(0)
    p_after.paragraph_format.space_after = Pt(4)

# ==================== СОДЕРЖИМОЕ ОТЧЕТА ====================

# 1. ЗАДАНИЕ
add_h1('1. Задание')
add_body_p('Изучить протокол передачи данных по последовательному интерфейсу UART, получить базовые знания об организации системы прерываний в микроконтроллерах на примере семейства STM32 (контроллер прерываний NVIC архитектуры ARM Cortex-M4), изучить устройство и принципы функционирования контроллера USART, а также приобрести практические навыки организации дуплексного обмена данными по UART в режимах программного опроса флагов и по аппаратным прерываниям.', bold_prefix='Цели работы: ')

add_h2('Общие требования к драйверу UART')
add_body_p('В соответствии с методическими указаниями к лабораторному практикуму, разработка программного обеспечения включает реализацию двух взаимозаменяемых вариантов драйвера интерфейса UART:')
add_bullet('Разработка и реализация двух вариантов драйверов UART для учебного стенда SDK-1.1M: с использованием аппаратных прерываний и без использования прерываний (метод циклического опроса флагов).')
add_bullet('Драйвер, использующий прерывания, должен функционировать в строго неблокирующем режиме (мгновенный возврат из функций приема/передачи без циклов ожидания) и обеспечивать кольцевую буферизацию (FIFO) для предотвращения потери байт.')
add_bullet('В драйвере без прерываний функция приёма данных также должна быть строго неблокирующей: опрос флага RXNE выполняется без зависания при отсутствии поступающих данных.')
add_bullet('При активации режима без прерываний аппаратные прерывания от блока USART должны быть полностью запрещены в контроллере прерываний NVIC.')
add_bullet('Переключение между режимами «с прерываниями» и «без прерываний» должно производиться оперативно во время работы стенда по нажатию пользовательской кнопки.')
add_bullet('Каждый принимаемый стендом символ должен отсылаться обратно в терминал для реализации аппаратного эхо-вывода в консоли ПК.')
add_bullet('Каждое информационное сообщение стенда должно выводиться с новой строки (символы \\r\\n).')

add_h2('Спецификация варианта № 5 («Кодовый замок»)')
add_body_p('Требуется модернизировать алгоритм кодового замка, разработанный в лабораторной работе № 1:')
add_bullet('Ввод пароля: осуществляется передачей символов с компьютера через терминал по последовательному каналу UART со скоростью 9600 бит/с (8 бит данных, 1 стоп-бит, без контроля четности).')
add_bullet('Отпирающая комбинация: последовательность из восьми латинских букв без учета регистра и цифр (в исходной конфигурации задан пароль «1234abcd»).')
add_bullet('Индикация правильного символа: ввод каждого верного очередного символа сопровождается одиночной вспышкой желтого светодиода стенда.')
add_bullet('Индикация ошибочного символа: ошибка сопровождается одиночной вспышкой красного светодиода, сбросом цепочки ввода в начальное состояние и инкрементом счетчика ошибок.')
add_bullet('Открытие замка: при безошибочном вводе всех восьми символов загорается зеленый светодиод («замок открыт»), горит 3 секунды, после чего гаснет, и замок переходит в режим ожидания.')
add_bullet('Блокировка при 3 ошибках: после трех неудачных попыток ввода подряд активируется аварийный режим — циклически мигает красный светодиод в течение 5 секунд, ввод в терминале на это время блокируется.')
add_bullet('Сброс по таймауту: если ввод комбинации начат, но очередное значение не поступило в течение 5 секунд, происходит автоматический сброс в начало.')
add_bullet('Смена отпирающей последовательности: инициируется отправкой символа «+». Вводится новая строка до 8 символов (завершение по Enter или по достижении 8 знаков). Стенд запрашивает подтверждение («Make this code active? (y/n): »). После ввода символа «y» новый код становится активным.')
add_bullet('Переключение прерываний: нажатие тактовой кнопки стенда (PC15) переключает режим работы драйвера с отправкой сообщения «[Mode switched: INTERRUPTS ON]» или «[Mode switched: INTERRUPTS OFF (Polling)]» и сбросом текущего ввода замка.')

# 2. АРХИТЕКТУРНОЕ ОПИСАНИЕ
add_h1('2. Архитектурное описание программы и драйверов UART')
add_body_p('Программный комплекс спроектирован на базе модульной неблокирующей архитектуры в рамках кооперативного суперцикла (Super-Loop). Применение блокирующих функций задержки (HAL_Delay) и циклов с активным ожиданием флагов готовности полностью исключено. Синхронизация временных интервалов (таймауты ввода, длительности вспышек и мигания светодиодов) реализована с использованием системного миллисекундного таймера SysTick (функция HAL_GetTick()).')
add_body_p('Архитектура программного обеспечения включает следующие структурные модули:')
add_bullet('Модуль кольцевого буфера (ring_buffer.h): легковесный кольцевой FIFO-буфер фиксированного размера (128 байт), обеспечивающий безопасный прием байт в прерывании без гонок данных.', bold_prefix='• ')
add_bullet('Модуль драйвера UART (uart_driver.h / uart_driver.c): аппаратно-зависимый уровень управления контроллером USART6. Реализует неблокирующее чтение в режимах опроса и прерываний, отправку данных и обработку прерываний NVIC.', bold_prefix='• ')
add_bullet('Модуль кодового замка (lock_fsm.h / lock_fsm.c): конечный автомат прикладной задачи Варианта № 5. Обеспечивает посимвольную обработку потока данных, эхо-вывод, смену пароля и генерацию событий световой индикации.', bold_prefix='• ')
add_bullet('Модуль светодиодной индикации (led.h / led.c): неблокирующий драйвер управления зеленым светодиодом (PD13) и двухцветным светодиодом (PD14/PD15).', bold_prefix='• ')
add_bullet('Модуль тактовой кнопки (button.h / button.c): цифровой автомат опроса физической кнопки стенда (PC15) с подавлением дребезга контактов (35 мс) для переключения режима прерываний UART.', bold_prefix='• ')
add_bullet('Главный модуль (main.c): центральный суперцикл, осуществляющий координацию работы модулей в реальном времени.', bold_prefix='• ')

# 3. АППАРАТНЫЕ ОСОБЕННОСТИ
add_h1('3. Аппаратные особенности интерфейса UART на стенде SDK-1.1M')
add_body_p('На лабораторном стенде SDK-1.1M (микроконтроллер STM32F427VIT6) отладочный интерфейс UART подключен ко встроенному аппаратному программатору-отладчику на базе специализированного моста FTDI FT2232. Мост преобразует сигналы интерфейса UART в виртуальный COM-порт USB управляющего персонального компьютера.')
add_body_p('В соответствии с электрической принципиальной схемой стенда, линии отладочного последовательного порта заведены на периферийный блок USART6 микроконтроллера:')
add_bullet('Вывод PC6 (TX): сконфигурирован в режим альтернативной функции AF8 (USART6_TX) с двухтактным выходным каскадом (Push-Pull). Служит для передачи данных со стенда в терминал ПК.', bold_prefix='• ')
add_bullet('Вывод PC7 (RX): сконфигурирован в режим альтернативной функции AF8 (USART6_RX) с внутренней подтяжкой к шине питания (Pull-Up). Служит для приема символов из терминала ПК.', bold_prefix='• ')

add_caption('Таблица 1. Аппаратные выводы стенда SDK-1.1M, задействованные в работе')
t1_headers = ['Обозначение', 'Вывод МК', 'Конфигурация вывода', 'Функциональное назначение']
t1_data = [
    ['USART6_TX', 'PC6', 'AF8 (USART6), Push-Pull, High Speed', 'Передача текстовых данных в консоль ПК'],
    ['USART6_RX', 'PC7', 'AF8 (USART6), Input Pull-Up', 'Прием командных символов от пользователя'],
    ['Кнопка SB1', 'PC15', 'GPIO Input, Pull-Up', 'Переключение режима прерываний UART'],
    ['Зеленый LED', 'PD13', 'GPIO Output Push-Pull', 'Индикация отпирания замка (горит 3 сек)'],
    ['Желтый LED', 'PD14', 'GPIO Output Push-Pull', 'Одиночная вспышка при верном символе'],
    ['Красный LED', 'PD15', 'GPIO Output Push-Pull', 'Вспышка при ошибке / тревога блокировки']
]
add_custom_table(t1_headers, t1_data, [1.3, 1.0, 2.3, 2.2])

# 4. РЕАЛИЗАЦИЯ ДРАЙВЕРА UART
add_h1('4. Реализация драйвера UART в режимах опроса и прерываний')
add_body_p('Разработанный драйвер uart_driver реализует строгое разделение и динамическое переключение между двумя принципами ввода-вывода данных.')

add_h2('Режим циклического опроса флагов (Polling, без прерываний)')
add_body_p('В режиме опроса прерывания контроллера USART6 отключены на аппаратном уровне ядра процессора с помощью вызова функции NVIC_DisableIRQ(USART6_IRQn), а также в регистре управления USART_CR1 путем сброса бита RXNEIE (прерывание по флагу RXNE запрещено).')
add_body_p('Неблокирующий прием реализован путем мгновенной проверки флага RXNE в статусном регистре USART_SR: если флаг равен нулю (данные еще не поступили), функция uart_driver_read_char немедленно возвращает false, передавая управление суперциклу. Если флаг равен единице, значение байта считывается из регистра данных USART_DR и возвращается true.')

add_h2('Режим прерываний с кольцевой буферизацией FIFO')
add_body_p('При вызове функции uart_driver_set_mode(UART_MODE_INTERRUPT) драйвер переводится в асинхронный режим:')
add_bullet('Очищается кольцевой буфер приема s_rx_ring и регистр USART_DR для устранения остаточных байт.')
add_bullet('В контрольном регистре USART_CR1 аппаратно взводится бит RXNEIE (разрешение прерывания по приему байта).')
add_bullet('В контроллере прерываний NVIC активируется вектор прерывания USART6_IRQn (NVIC_EnableIRQ).')
add_body_p('При приеме очередного байта микроконтроллер прерывает основную программу и передает управление в обработчик USART6_IRQHandler. Обработчик считывает байт из регистра данных DR и помещает его в кольцевой буфер через функцию ring_buffer_put. Дополнительно обрабатываются и очищаются флаги аппаратных ошибок приемника (ORE — переполнение, NE — шумы, FE — ошибка кадра), что исключает зависание приемопередатчика.')

# 5. КОНЕЧНЫЙ АВТОМАТ КОДОВОГО ЗАМКА
add_h1('5. Конечный автомат кодового замка по варианту № 5')
add_body_p('Прикладная логика кодового замка структурирована в виде конечного автомата, включающего следующие состояния:')
add_bullet('STATE_INPUT_CODE: дежурный режим ожидания и валидации символов пароля. Принимаемые символы зеркалируются обратно в терминал (эхо). Регистронезависимое сравнение (tolower) сопоставляет ввод с активным паролем (по умолчанию «1234abcd»). При верном символе формируется вспышка желтого светодиода (200 мс). При ошибке генерируется вспышка красного светодиода (300 мс), цепочка ввода сбрасывается в начало, а счетчик ошибок увеличивается. При отсутствии активности в течение 5 секунд происходит сброс по таймауту. Символ «+» переводит автомат в режим смены пароля.')
add_bullet('STATE_UNLOCKED: состояние открытого замка при успешном вводе 8 символов. Загорается зеленый светодиод PD13 на 3 секунды, в терминал выводится «*** ACCESS GRANTED (LOCK OPEN) ***». По истечении 3 секунд замок автоматически запирается.')
add_bullet('STATE_BLOCKED_ALARM: аварийная блокировка после 3 неверных попыток подряд. Активируется циклическое мигание красного светодиода PD15 длительностью 5 секунд, выводя предупреждение. Прием символов на время блокировки игнорируется.')
add_bullet('STATE_NEW_CODE_INPUT: режим ввода новой последовательности до 8 символов. Ввод завершается нажатием Enter (символы \\r или \\n) либо при вводе 8 символов.')
add_bullet('STATE_NEW_CODE_CONFIRM: интерактивный запрос подтверждения «Make this code active? (y/n): ». При нажатии «y» или «Y» новый пароль вступает в силу. При любом другом символе смена отменяется.')

add_h2('Результаты практического тестирования программы на стенде')
add_body_p('Программа была загружена во Flash-память стенда SDK-1.1M. Взаимодействие со стендом производилось через утилиту screen в среде macOS на скорости 9600 бод (/dev/cu.usbserial-1101 9600). В ходе испытаний успешно подтверждены все сценарии:')
add_bullet('1. Корректный ввод пароля «1234abcd» приводит к открытию замка и индикации зелёным светодиодом.')
add_bullet('2. Незавершенный ввод («1234a») корректно сбрасывается по истечении таймаута 5 секунд с выдачей сообщения «[Input timeout! Reset to start]».')
add_bullet('3. Три ошибочных попытки подряд вызывают срабатывание защитной блокировки «!!! 3 WRONG ATTEMPTS! SYSTEM LOCKED !!!» и 5-секундное мигание красного светодиода.')
add_bullet('4. Смена пароля через комбинацию «+» $\\rightarrow$ «12345678» $\\rightarrow$ «y» успешно обновляет отпирающую комбинацию.')
add_bullet('5. Нажатие тактовой кнопки стенда (PC15) безошибочно переключает режим работы драйвера между опросом и прерываниями с отправкой сервисных уведомлений.')

# Вставляем скриншоты терминала, если они доступны
img1_p = '/Users/devianted/.gemini/antigravity/brain/d6bbad83-9e1d-4508-9847-805f39c01a34/.user_uploaded/media_1790935963064.png'
img2_p = '/Users/devianted/.gemini/antigravity/brain/d6bbad83-9e1d-4508-9847-805f39c01a34/.user_uploaded/media_1790936069564.png'

if os.path.exists(img1_p):
    p_img1 = doc.add_paragraph()
    p_img1.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_img1.paragraph_format.space_before = Pt(6)
    p_img1.paragraph_format.space_after = Pt(2)
    p_img1.add_run().add_picture(img1_p, width=Inches(4.2))
    add_caption('Рисунок 1. Протокол тестирования: таймаут ввода, успешное открытие и срабатывание тревоги')

if os.path.exists(img2_p):
    p_img2 = doc.add_paragraph()
    p_img2.paragraph_format.alignment = WD_ALIGN_PARAGRAPH.CENTER
    p_img2.paragraph_format.space_before = Pt(6)
    p_img2.paragraph_format.space_after = Pt(2)
    p_img2.add_run().add_picture(img2_p, width=Inches(4.2))
    add_caption('Рисунок 2. Протокол тестирования: процедура смены пароля и переключение режима прерываний')

# 6. ИСХОДНЫЕ КОДЫ
add_h1('6. Исходные коды программы собственной разработки')
add_body_p('Ниже приведены полные исходные коды программных модулей собственной разработки, созданных в рамках выполнения лабораторной работы № 2.')

files_to_print = [
    ('Core/Inc/ring_buffer.h', 'Файл Core/Inc/ring_buffer.h (Кольцевой буфер FIFO)'),
    ('Core/Inc/uart_driver.h', 'Файл Core/Inc/uart_driver.h (Заголовочный файл драйвера UART)'),
    ('Core/Src/uart_driver.c', 'Файл Core/Src/uart_driver.c (Реализация драйвера UART)'),
    ('Core/Inc/lock_fsm.h',    'Файл Core/Inc/lock_fsm.h (Заголовочный файл конечного автомата замка)'),
    ('Core/Src/lock_fsm.c',    'Файл Core/Src/lock_fsm.c (Реализация конечного автомата замка)'),
    ('Core/Src/main.c',        'Файл Core/Src/main.c (Фрагмент главного суперцикла программы)'),
    ('Core/Src/stm32f4xx_it.c', 'Файл Core/Src/stm32f4xx_it.c (Обработчик прерывания USART6_IRQHandler)')
]

for rel_p, title in files_to_print:
    add_h2(title)
    full_p = os.path.join(SRC_DIR, rel_p)
    if os.path.exists(full_p):
        with open(full_p, 'r', encoding='utf-8', errors='ignore') as f:
            lines = f.readlines()
        if 'main.c' in rel_p:
            start_i = 0
            for i, l in enumerate(lines):
                if 'int main(void)' in l:
                    start_i = i
                    break
            code_txt = "".join(lines[start_i:start_i+65])
        elif 'stm32f4xx_it.c' in rel_p:
            start_i = 0
            for i, l in enumerate(lines):
                if 'USART6_IRQHandler' in l:
                    start_i = max(0, i-5)
                    break
            code_txt = "".join(lines[start_i:start_i+25])
        else:
            code_txt = "".join(lines)
        add_code(code_txt)

# 7. ВЫВОДЫ
add_h1('7. Выводы')
add_body_p('В ходе выполнения лабораторной работы № 2 были глубоко изучены принципы организации последовательного интерфейса передачи данных UART и системы векторных прерываний NVIC на базе микроконтроллера STM32F427VIT6.')
add_body_p('Основные практические результаты работы:')
add_bullet('1. Практически освоен аппаратный контроллер USART6 учебного стенда SDK-1.1M, настроенный на скорость обмена 9600 бит/с с передачей данных через виртуальный COM-порт моста FTDI FT2232.')
add_bullet('2. Разработан и всесторонне протестирован модульный драйвер UART, поддерживающий работу в двух взаимозаменяемых режимах: по программному опросу флагов (Polling) и по прерываниям (Interrupt) с кольцевой буферизацией FIFO на 128 байт. Все операции выполнены строго неблокирующими.')
add_bullet('3. Реализовано безопасное переключение режимов прерываний UART во время работы программы по нажатию тактовой кнопки стенда (PC15) с трансляцией статуса в терминал.')
add_bullet('4. Модернизирован конечный автомат прикладной задачи «Кодовый замок» (Вариант № 5): ввод переведен на посимвольный поток UART с эхо-выводом, регистронезависимым распознаванием пароля, защитой от перебора (блокировка при 3 ошибках на 5 с), контролем таймаута (5 с) и поддержкой процедуры смены пароля по символу «+».')
add_bullet('5. Программа успешно протестирована на реальном оборудовании стенда SDK-1.1M в терминале macOS, продемонстрировав высокую стабильность и полное соответствие техническому заданию.')

# Сохраняем обновленный файл
doc.save(OUT_DOCX)
print("Lab2 report successfully rebuilt and saved!")
