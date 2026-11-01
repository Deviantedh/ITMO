#!/usr/bin/env python3
"""
Клиентская программа для управления стендом SDK-1.1M (Кодовый замок).
Лабораторная работа №4: «Операционные системы реального времени (FreeRTOS)».
Вариант 5: «Кодовый замок».
"""

import sys
import os
import time
import threading
import datetime
import serial
import serial.tools.list_ports
import tkinter as tk
from tkinter import ttk, messagebox, scrolledtext

LOG_FILE = f"uart_session_{datetime.datetime.now().strftime('%Y-%m-%d_%H-%M-%S')}.log"

def log_message(direction: str, text: str):
    timestamp = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S.%f")[:-3]
    entry = f"[{timestamp}] [{direction}] {text.strip()}\n"
    try:
        with open(LOG_FILE, "a", encoding="utf-8") as f:
            f.write(entry)
    except Exception as e:
        print(f"Log error: {e}")
    return entry

class LockClientGUI(tk.Tk):
    def __init__(self, port_arg=None):
        super().__init__()
        self.title("SDK-1.1M Кодовый замок (Лабораторная работа №4 — FreeRTOS)")
        self.geometry("640x700")
        self.minsize(580, 620)

        self.ser = None
        self.rx_thread = None
        self.running = True
        self.connected = False

        self.port_name = tk.StringVar(value=port_arg if port_arg else "")
        self.password_var = tk.StringVar()
        self.new_pass_var = tk.StringVar()
        self.status_var = tk.StringVar(value="НЕТ ПОДКЛЮЧЕНИЯ")
        self.oled_display_var = tk.StringVar(value="____")

        self.setup_ui()

        if self.port_name.get():
            self.after(300, self.connect_serial)

    def setup_ui(self):
        style = ttk.Style(self)
        style.theme_use("clam")

        # --- 1. Панель подключения ---
        conn_frame = ttk.LabelFrame(self, text=" Подключение по UART ", padding=10)
        conn_frame.pack(fill=tk.X, padx=12, pady=8)

        ttk.Label(conn_frame, text="COM-порт:").pack(side=tk.LEFT, padx=5)

        ports = [p.device for p in serial.tools.list_ports.comports()]
        if not self.port_name.get() and ports:
            for p in ports:
                if "1201" in p or "usbserial" in p:
                    self.port_name.set(p)
                    break
            if not self.port_name.get():
                self.port_name.set(ports[0])

        self.port_combo = ttk.Combobox(conn_frame, textvariable=self.port_name, values=ports, width=28)
        self.port_combo.pack(side=tk.LEFT, padx=5)

        self.btn_connect = ttk.Button(conn_frame, text="Подключиться", command=self.toggle_connection)
        self.btn_connect.pack(side=tk.LEFT, padx=5)

        self.btn_ping = ttk.Button(conn_frame, text="Ping (Тест связи)", command=self.send_ping, state=tk.DISABLED)
        self.btn_ping.pack(side=tk.LEFT, padx=5)

        # --- 2. Статус замка (Визуальный индикатор) ---
        status_frame = ttk.LabelFrame(self, text=" Состояние замка на стенде ", padding=12)
        status_frame.pack(fill=tk.X, padx=12, pady=6)

        self.lbl_status = tk.Label(
            status_frame,
            textvariable=self.status_var,
            font=("Helvetica", 17, "bold"),
            bg="#333333",
            fg="#FFFFFF",
            pady=10,
            relief=tk.RIDGE
        )
        self.lbl_status.pack(fill=tk.X, padx=6, pady=4)

        disp_box = ttk.Frame(status_frame)
        disp_box.pack(fill=tk.X, pady=4)
        ttk.Label(disp_box, text="Символы на OLED-дисплее:", font=("Helvetica", 11)).pack(side=tk.LEFT, padx=6)
        self.lbl_oled = tk.Label(
            disp_box,
            textvariable=self.oled_display_var,
            font=("Courier", 16, "bold"),
            bg="#000000",
            fg="#00FF66",
            width=16,
            relief=tk.SUNKEN
        )
        self.lbl_oled.pack(side=tk.LEFT, padx=8)

        # --- 3. Ввод пароля для открытия ---
        auth_frame = ttk.LabelFrame(self, text=" Ввод пароля для доступа ", padding=10)
        auth_frame.pack(fill=tk.X, padx=12, pady=6)

        entry_box = ttk.Frame(auth_frame)
        entry_box.pack(fill=tk.X, pady=4)

        ttk.Label(entry_box, text="Пароль:", font=("Helvetica", 11)).pack(side=tk.LEFT, padx=5)
        self.entry_pass = ttk.Entry(entry_box, textvariable=self.password_var, font=("Courier", 14), width=16, show="*")
        self.entry_pass.pack(side=tk.LEFT, padx=5)
        self.entry_pass.bind("<KeyRelease>", self.on_key_type)
        self.entry_pass.bind("<Return>", lambda e: self.send_auth())

        self.btn_open = ttk.Button(entry_box, text="Открыть замок", command=self.send_auth, state=tk.DISABLED)
        self.btn_open.pack(side=tk.LEFT, padx=8)

        self.btn_clear = ttk.Button(entry_box, text="Сброс ввода", command=self.reset_input, state=tk.DISABLED)
        self.btn_clear.pack(side=tk.LEFT, padx=4)

        # --- 4. Смена пароля (Запись в EEPROM) ---
        set_frame = ttk.LabelFrame(self, text=" Смена мастер-пароля (Запись в EEPROM MCP79411) ", padding=10)
        set_frame.pack(fill=tk.X, padx=12, pady=6)

        set_box = ttk.Frame(set_frame)
        set_box.pack(fill=tk.X, pady=4)

        ttk.Label(set_box, text="Новый пароль:", font=("Helvetica", 11)).pack(side=tk.LEFT, padx=5)
        self.entry_new_pass = ttk.Entry(set_box, textvariable=self.new_pass_var, font=("Courier", 14), width=16)
        self.entry_new_pass.pack(side=tk.LEFT, padx=5)

        self.btn_save = ttk.Button(set_box, text="Записать в EEPROM", command=self.send_new_password, state=tk.DISABLED)
        self.btn_save.pack(side=tk.LEFT, padx=8)

        # --- 5. Журнал UART сообщений ---
        log_frame = ttk.LabelFrame(self, text=f" Журнал UART обмена ({LOG_FILE}) ", padding=8)
        log_frame.pack(fill=tk.BOTH, expand=True, padx=12, pady=6)

        self.txt_log = scrolledtext.ScrolledText(log_frame, font=("Courier", 10), height=10, bg="#1E1E1E", fg="#D4D4D4")
        self.txt_log.pack(fill=tk.BOTH, expand=True)

    def log_ui(self, direction: str, msg: str):
        full_line = log_message(direction, msg)
        self.txt_log.insert(tk.END, full_line)
        self.txt_log.see(tk.END)

    def toggle_connection(self):
        if self.connected:
            self.disconnect_serial()
        else:
            self.connect_serial()

    def connect_serial(self):
        port = self.port_name.get().strip()
        if not port:
            messagebox.showwarning("Предупреждение", "Укажите COM-порт!")
            return

        try:
            self.ser = serial.Serial(port, 9600, timeout=0.2)
            self.connected = True
            self.btn_connect.config(text="Отключиться")
            self.btn_ping.config(state=tk.NORMAL)
            self.btn_open.config(state=tk.NORMAL)
            self.btn_clear.config(state=tk.NORMAL)
            self.btn_save.config(state=tk.NORMAL)

            self.status_var.set("ПОДКЛЮЧЕНО (Опрос...)")
            self.lbl_status.config(bg="#0055AA", fg="#FFFFFF")
            self.log_ui("SYS", f"Подключено к порту {port}")

            self.running = True
            self.rx_thread = threading.Thread(target=self.rx_loop, daemon=True)
            self.rx_thread.start()

            self.after(200, self.send_ping)

        except Exception as e:
            messagebox.showerror("Ошибка подключения", str(e))
            self.log_ui("ERR", f"Не удалось открыть порт {port}: {e}")

    def disconnect_serial(self):
        self.running = False
        if self.ser and self.ser.is_open:
            self.ser.close()
        self.connected = False
        self.btn_connect.config(text="Подключиться")
        self.btn_ping.config(state=tk.DISABLED)
        self.btn_open.config(state=tk.DISABLED)
        self.btn_clear.config(state=tk.DISABLED)
        self.btn_save.config(state=tk.DISABLED)
        self.status_var.set("ОТКЛЮЧЕНО")
        self.lbl_status.config(bg="#333333", fg="#FFFFFF")
        self.log_ui("SYS", "Отключено от порта")

    def send_raw(self, cmd: str):
        if not self.ser or not self.ser.is_open:
            return
        msg = cmd.strip() + "\r\n"
        self.ser.write(msg.encode("utf-8"))
        self.log_ui("TX", msg)

    def send_ping(self):
        self.send_raw("PING")

    def on_key_type(self, event):
        val = self.password_var.get()
        if event.char and event.char.isalnum():
            self.send_raw(event.char)

    def send_auth(self):
        passw = self.password_var.get().strip()
        if not passw:
            messagebox.showwarning("Внимание", "Введите пароль!")
            return
        self.send_raw(f"AUTH:{passw}")
        self.password_var.set("")

    def reset_input(self):
        self.password_var.set("")
        self.send_raw("\r\n")

    def send_new_password(self):
        new_p = self.new_pass_var.get().strip()
        if len(new_p) < 1 or len(new_p) > 16:
            messagebox.showwarning("Внимание", "Длина пароля должна быть от 1 до 16 символов!")
            return
        if not messagebox.askyesno("Подтверждение", f"Записать новый пароль '{new_p}' в энергонезависимую память EEPROM?"):
            return
        self.send_raw(f"SET:{new_p}")
        self.new_pass_var.set("")

    def rx_loop(self):
        while self.running and self.ser and self.ser.is_open:
            try:
                line = self.ser.readline()
                if line:
                    decoded = line.decode("utf-8", errors="replace").strip()
                    if decoded:
                        self.log_ui("RX", decoded)
                        self.parse_response(decoded)
            except Exception as e:
                self.log_ui("ERR", f"Ошибка чтения: {e}")
                break

    def parse_response(self, text: str):
        if text.startswith("STATUS:"):
            status = text[7:]
            if status == "OK":
                self.status_var.set("[ДОСТУП РАЗРЕШЕН] ЗАМОК ОТКРЫТ")
                self.lbl_status.config(bg="#007700", fg="#FFFFFF")
            elif status == "ERR":
                self.status_var.set("[ОШИБКА] НЕВЕРНЫЙ ПАРОЛЬ")
                self.lbl_status.config(bg="#AA0000", fg="#FFFFFF")
            elif status == "BLOCKED":
                self.status_var.set("[БЛОКИРОВКА] СИСТЕМА ВРЕМЕННО ЗАБЛОКИРОВАНА")
                self.lbl_status.config(bg="#880000", fg="#FFFFFF")
            elif status == "LOCKED":
                self.status_var.set("ЗАМОК ЗАКРЫТ (ОЖИДАНИЕ ВВОДА)")
                self.lbl_status.config(bg="#333333", fg="#FFFFFF")
            elif status == "SAVED":
                self.status_var.set("[EEPROM] НОВЫЙ ПАРОЛЬ УСПЕШНО СОХРАНЕН")
                self.lbl_status.config(bg="#0055AA", fg="#FFFFFF")
                messagebox.showinfo("EEPROM", "Новый пароль успешно сохранён в энергонезависимую память!")

        elif text.startswith("DISPLAY:"):
            disp = text[8:]
            self.oled_display_var.set(disp)

        elif text == "PONG":
            self.status_var.set("[СВЯЗЬ УСТАНОВЛЕНА] СТЕНД ГОТОВ К РАБОТЕ")
            self.lbl_status.config(bg="#0055AA", fg="#FFFFFF")

    def on_closing(self):
        self.disconnect_serial()
        self.destroy()

if __name__ == "__main__":
    port = sys.argv[1] if len(sys.argv) > 1 else None
    app = LockClientGUI(port_arg=port)
    app.protocol("WM_DELETE_WINDOW", app.on_closing)
    app.mainloop()
