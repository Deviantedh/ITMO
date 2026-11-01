# Ответы для упражнения 5.1

Исходное изображение: `input_images/st-petersburg-3714288_1280.jpg`

## Ответы

- Максимальное значение `h(n)` для гистограммы `cA`: `1950`
- `c_167_st-petersburg-3714288_1280.jpg`: `cV`
- `c_471_st-petersburg-3714288_1280.jpg`: `cA`
- `c_807_st-petersburg-3714288_1280.jpg`: `cD`
- `c_863_st-petersburg-3714288_1280.jpg`: `cH`

## Как считалось

```python
gray = cv2.cvtColor(image_bgr, cv2.COLOR_BGR2GRAY)
cA, (cH, cV, cD) = pywt.dwt2(gray, 'haar')
hist, bin_edges = np.histogram(cA.ravel(), bins=256)
max_h = hist.max()
```

Сопоставление изображений выполнено сравнением кандидатов с `np.clip(coef, 0, 255).astype(np.uint8)`.

Если LMS не примет `1950`, возможная причина - другой способ построения гистограммы. Проверенные альтернативы:

- `np.histogram(cA.ravel(), bins=256, range=(0, 256))`: `1018`
- `np.bincount(np.rint(cA.ravel()).astype(int)).max()`: `1070`
- `np.histogram(cA.ravel(), bins=256, range=(0, 512))`: `1908`
