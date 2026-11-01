# ЛР4: собранные трассы Wireshark

Дата захвата: 2026-06-18.

Сайт по варианту: `nginx.org`.

Обоснование варианта: Носов Георгий Иванович -> инициалы `N G I`; в домене `nginx.org` буквы `n-g-i` идут по порядку.

Локальные параметры сети:

- IP компьютера: `192.168.0.106`
- Шлюз: `192.168.0.1`
- Интерфейс: `en0` / Wi-Fi
- IP `nginx.org` во время захвата: `3.125.197.172`
- DNS-сервер: `192.168.0.1`

## Файлы

- `lab4_ping.pcapng` - чистый захват ICMP ping.
- `lab4_traceroute.pcapng` - компактный захват `traceroute -I -n -q 1 -m 12 -w 2 nginx.org`.
- `lab4_http.pcapng` - HTTP GET и HTTP Conditional GET.
- `lab4_nslookup.pcapng` - `nslookup nginx.org` и `nslookup -type=NS nginx.org`.
- `lab4_arp_gateway.pcapng` - ARP-запрос и ARP-ответ для шлюза после очистки ARP-записи.

## Ping

Команды:

```sh
ping -c 1 -s 100 nginx.org
ping -c 1 -s 1000 nginx.org
ping -c 1 -s 1500 nginx.org
ping -c 1 -s 2000 nginx.org
ping -c 1 -s 3000 nginx.org
ping -c 1 -s 5000 nginx.org
ping -c 1 -s 8000 nginx.org
ping -c 1 -s 10000 nginx.org
```

На macOS пакет `10000` не был отправлен: `sendto: Message too long`. Поэтому в pcap есть размеры до `8000`.

Наблюдения по исходящим ICMP Echo Request:

| Размер ping payload | IP Identification | Количество фрагментов |
|---:|---|---:|
| 100 | `0x636c` | 1 |
| 1000 | `0xb263` | 1 |
| 1500 | `0xcf1c` | 2 |
| 2000 | `0x9631` | 2 |
| 3000 | `0x9863` | 3 |
| 5000 | `0x8cad` | 4 |
| 8000 | `0x2bec` | 6 |

Поля для скриншотов:

- `Internet Protocol Version 4`
- `Identification`
- `Flags`
- `More fragments`
- `Fragment Offset`
- `Time to live`
- `Internet Control Message Protocol`
- `Data`

## Traceroute

Команда:

```sh
traceroute -I -n -q 1 -m 12 -w 2 nginx.org
```

Ключевые хопы:

- TTL 1 -> ответ `ICMP Time-to-live exceeded` от `192.168.0.1`.
- TTL 2 -> ответ `ICMP Time-to-live exceeded` от `10.181.216.1`.
- TTL 5 -> ответ `ICMP Time-to-live exceeded` от `109.239.139.1`.
- TTL 12 -> ответ `ICMP Echo Reply` от конечного узла `3.125.197.172`.

Часть промежуточных хопов не отвечает, это нормально для traceroute.

## HTTP

Первый запрос:

- `GET / HTTP/1.1`
- ответ `HTTP/1.1 200 OK`
- `Last-Modified: Wed, 17 Jun 2026 16:14:11 GMT`
- `ETag: "6a32c7d3-1d43"`

Условный запрос:

- `GET / HTTP/1.1`
- `If-None-Match: "6a32c7d3-1d43"`
- `If-Modified-Since: Wed, 17 Jun 2026 16:14:11 GMT`
- ответ `HTTP/1.1 304 Not Modified`

## nslookup / DNS

Команды:

```sh
nslookup nginx.org
nslookup -type=NS nginx.org
```

Нужные DNS-ответы:

- `A nginx.org -> 3.125.197.172`
- `NS nginx.org -> ns1.f5clouddns.com`
- `NS nginx.org -> ns2.f5clouddns.com`

В pcap также есть несколько фоновых DNS-запросов macOS. Для отчета фильтровать по:

```text
dns && dns.qry.name == "nginx.org"
```

## ARP

Команды:

```sh
sudo arp -d 192.168.0.1
curl http://nginx.org/
```

Нужные кадры в `lab4_arp_gateway.pcapng`:

| Frame | Смысл | Ethernet src | Ethernet dst | Sender IP/MAC | Target IP/MAC |
|---:|---|---|---|---|---|
| 1 | ARP request: кто имеет `192.168.0.1`? | `f6:2d:e3:c9:00:65` | `ff:ff:ff:ff:ff:ff` | `192.168.0.106` / `f6:2d:e3:c9:00:65` | `192.168.0.1` / `00:00:00:00:00:00` |
| 2 | ARP reply: `192.168.0.1` имеет MAC `cc:d8:43:b8:da:48` | `cc:d8:43:b8:da:48` | `f6:2d:e3:c9:00:65` | `192.168.0.1` / `cc:d8:43:b8:da:48` | `192.168.0.106` / `f6:2d:e3:c9:00:65` |

В HTTP-пакетах из `lab4_http.pcapng` MAC-адреса такие:

- запрос к сайту: `eth.src = f6:2d:e3:c9:00:65`, `eth.dst = cc:d8:43:b8:da:48`;
- ответ сервера: `eth.src = cc:d8:43:b8:da:48`, `eth.dst = f6:2d:e3:c9:00:65`.

Это показывает, что для удаленного сайта MAC-адрес самого сайта не используется на нашем Ethernet/Wi-Fi участке: кадры идут до ближайшего шлюза.
