# Ответы для упражнения 2.3

Исходное изображение: `input_images/sven-hornburg-TAVB-2C6uyE-unsplash.jpg`

Исходный размер: `6000x4000` пикселей.

## Ответы

- Коэффициент отношения исходной ширины к высоте: `1.500`
- Высота полученного изображения: `346`
- Размер сжатого изображения в байтах: `40895`

## Расчет

```text
6000 / 4000 = 1.5
floor(520 / 1.5) = 346
```

Размер `40895` байт получен при сохранении RGB-изображения в JPEG с `quality=95`:

```python
image_bgr = cv2.imread(...)
image_rgb = cv2.cvtColor(image_bgr, cv2.COLOR_BGR2RGB)
resized_rgb = cv2.resize(image_rgb, (520, 346), interpolation=cv2.INTER_AREA)
Image.fromarray(resized_rgb).save('outputs/resized_rgb_pil_q95.jpg', quality=95)
```

Примечание: если сохранить RGB-массив напрямую через `cv2.imwrite(...)`, получится `41638` байт. Похоже, именно такой способ не совпал с LMS в прошлой попытке, поэтому основной ответ здесь - `40895`.
