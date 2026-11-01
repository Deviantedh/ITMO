#!/usr/bin/env python3
from pathlib import Path

import matplotlib.pyplot as plt


OUT_DIR = Path(__file__).resolve().parent / "lab4_graphs"
OUT_DIR.mkdir(exist_ok=True)


load_mac = [
    {"config": "config=1", "cost": 2700, "avg": 960, "max": 3596, "throughput": 3.8 * 60},
    {"config": "config=2", "cost": 3000, "avg": 654, "max": 2924, "throughput": 4.0 * 60},
    {"config": "config=3", "cost": 3000, "avg": 474, "max": 1840, "throughput": 3.9 * 60},
]

load_helios = [
    {"config": "config=1", "cost": 2700, "avg": 3278, "max": 4945, "throughput": 3.00 * 60},
    {"config": "config=2", "cost": 3000, "avg": 3187, "max": 4429, "throughput": 3.08 * 60},
    {"config": "config=3", "cost": 3000, "avg": 381, "max": 2128, "throughput": 3.97 * 60},
]

stress = [
    {"users": 12, "target": 240, "avg": 392, "throughput": 4.02 * 60},
    {"users": 15, "target": 300, "avg": 544, "throughput": 5.00 * 60},
    {"users": 16, "target": 320, "avg": 799, "throughput": 5.28 * 60},
    {"users": 18, "target": 360, "avg": 2737, "throughput": 3.65 * 60},
]


def savefig(name: str) -> None:
    plt.tight_layout()
    plt.savefig(OUT_DIR / name, dpi=180)
    plt.close()


def plot_load_throughput(data, title, filename):
    configs = [row["config"] for row in data]
    values = [row["throughput"] for row in data]

    plt.figure(figsize=(8, 4.8))
    bars = plt.bar(configs, values, color=["#9ca3af", "#60a5fa", "#34d399"])
    plt.axhline(240, color="#dc2626", linestyle="--", linewidth=2, label="Требуемая нагрузка: 240 req/min")
    plt.title(title)
    plt.ylabel("Пропускная способность, запросов/мин")
    plt.xlabel("Конфигурация")
    plt.ylim(0, max(270, max(values) * 1.25))
    plt.grid(axis="y", alpha=0.25)
    plt.legend()

    for bar, value in zip(bars, values):
        plt.text(
            bar.get_x() + bar.get_width() / 2,
            bar.get_height() + 4,
            f"{value:.0f}",
            ha="center",
            va="bottom",
        )

    savefig(filename)


def plot_load_avg_response(data, title, filename):
    configs = [row["config"] for row in data]
    avg_values = [row["avg"] for row in data]

    plt.figure(figsize=(8, 4.8))
    bars = plt.bar(configs, avg_values, color=["#9ca3af", "#60a5fa", "#34d399"])
    plt.axhline(570, color="#dc2626", linestyle="--", linewidth=2, label="Порог: 570 мс")
    plt.title(title)
    plt.ylabel("Среднее время отклика, мс")
    plt.xlabel("Конфигурация")
    plt.ylim(0, max(avg_values) * 1.2)
    plt.grid(axis="y", alpha=0.25)
    plt.legend()

    for bar, value in zip(bars, avg_values):
        plt.text(
            bar.get_x() + bar.get_width() / 2,
            bar.get_height() + 25,
            f"{value:.0f}",
            ha="center",
            va="bottom",
        )

    savefig(filename)


def plot_stress_avg_response():
    users = [row["users"] for row in stress]
    avg_values = [row["avg"] for row in stress]

    plt.figure(figsize=(8, 4.8))
    plt.plot(users, avg_values, marker="o", linewidth=2.5, color="#2563eb")
    plt.axhline(570, color="#dc2626", linestyle="--", linewidth=2, label="Порог: 570 мс")
    plt.title("Стресс-тест config=3: время отклика от нагрузки")
    plt.ylabel("Среднее время отклика, мс")
    plt.xlabel("Количество пользователей")
    plt.xticks(users)
    plt.grid(alpha=0.25)
    plt.legend()

    for x, y in zip(users, avg_values):
        plt.text(x, y + 55, f"{y} мс", ha="center")

    savefig("stress_avg_response_config3.png")


def plot_stress_throughput():
    users = [row["users"] for row in stress]
    actual = [row["throughput"] for row in stress]
    target = [row["target"] for row in stress]

    plt.figure(figsize=(8, 4.8))
    plt.plot(users, target, marker="o", linewidth=2.5, color="#16a34a", label="Целевая нагрузка")
    plt.plot(users, actual, marker="o", linewidth=2.5, color="#f97316", label="Фактическая пропускная способность")
    plt.title("Стресс-тест config=3: целевая и фактическая пропускная способность")
    plt.ylabel("Запросов/мин")
    plt.xlabel("Количество пользователей")
    plt.xticks(users)
    plt.grid(alpha=0.25)
    plt.legend()

    savefig("stress_throughput_config3.png")


def main():
    plot_load_throughput(
        load_mac,
        "Нагрузочное тестирование через SSH-туннель: пропускная способность",
        "load_throughput_mac.png",
    )
    plot_load_throughput(
        load_helios,
        "Нагрузочное тестирование на Helios: пропускная способность",
        "load_throughput_helios.png",
    )
    plot_load_avg_response(
        load_mac,
        "Нагрузочное тестирование через SSH-туннель: среднее время отклика",
        "load_avg_response_mac.png",
    )
    plot_load_avg_response(
        load_helios,
        "Нагрузочное тестирование на Helios: среднее время отклика",
        "load_avg_response_helios.png",
    )
    plot_stress_avg_response()
    plot_stress_throughput()

    print(f"Graphs saved to: {OUT_DIR}")


if __name__ == "__main__":
    main()
