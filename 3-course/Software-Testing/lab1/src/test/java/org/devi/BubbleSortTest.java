package org.devi;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BubbleSortTest {

    // Проверяет валидацию входных данных: null-массив недопустим.
    @Test
    void nullInputMustThrow() {
        assertThrows(IllegalArgumentException.class, () -> BubbleSort.sortWithTrace(null));
    }

    // Проверяет пустой массив: сортировка не выполняется, только старт и финиш.
    @Test
    void emptyArrayTraceMatchesEtalon() {
        BubbleSort.SortTraceResult result = BubbleSort.sortWithTrace(new int[]{});

        assertArrayEquals(new int[]{}, result.sorted());
        assertEquals(
                List.of("T0_START", "T6_FINISH"),
                result.trace()
        );
    }

    // Проверяет массив из одного элемента: изменений и проходов циклов быть не должно.
    @Test
    void singleElementArray() {
        BubbleSort.SortTraceResult result = BubbleSort.sortWithTrace(new int[]{42});

        assertArrayEquals(new int[]{42}, result.sorted());
        assertEquals(
                List.of("T0_START", "T6_FINISH"),
                result.trace()
        );
    }

    // Проверяет минимальный размер для сортировки (2 элемента) и выполнение ветки обмена.
    @Test
    void twoElementsMinimalSortableSize() {
        BubbleSort.SortTraceResult result = BubbleSort.sortWithTrace(new int[]{2, 1});

        assertArrayEquals(new int[]{1, 2}, result.sorted());
        assertEquals(
                List.of(
                        "T0_START",
                        "T1_OUTER_LOOP",
                        "T2_INNER_LOOP", "T3_SWAP",
                        "T6_FINISH"
                ),
                result.trace()
        );
    }

    // Проверяет случай с одинаковыми элементами и ранний выход без обменов.
    @Test
    void allElementsEqual() {
        BubbleSort.SortTraceResult result = BubbleSort.sortWithTrace(new int[]{7, 7, 7});

        assertArrayEquals(new int[]{7, 7, 7}, result.sorted());
        assertEquals(
                List.of(
                        "T0_START",
                        "T1_OUTER_LOOP",
                        "T2_INNER_LOOP", "T4_NO_SWAP",
                        "T2_INNER_LOOP", "T4_NO_SWAP",
                        "T5_EARLY_EXIT",
                        "T6_FINISH"
                ),
                result.trace()
        );
    }

    // Проверяет корректную сортировку с отрицательными и нулевыми значениями.
    @Test
    void negativeNumbersArray() {
        BubbleSort.SortTraceResult result = BubbleSort.sortWithTrace(new int[]{-1, -3, 2, 0});

        assertArrayEquals(new int[]{-3, -1, 0, 2}, result.sorted());
        assertEquals(
                List.of(
                        "T0_START",
                        "T1_OUTER_LOOP",
                        "T2_INNER_LOOP", "T3_SWAP",
                        "T2_INNER_LOOP", "T4_NO_SWAP",
                        "T2_INNER_LOOP", "T3_SWAP",
                        "T1_OUTER_LOOP",
                        "T2_INNER_LOOP", "T4_NO_SWAP",
                        "T2_INNER_LOOP", "T4_NO_SWAP",
                        "T5_EARLY_EXIT",
                        "T6_FINISH"
                ),
                result.trace()
        );
    }

    // Проверяет базовый случай среднего размера с несколькими проходами и ранним завершением.
    @Test
    void mediumBaseCase() {
        BubbleSort.SortTraceResult result = BubbleSort.sortWithTrace(new int[]{4, 1, 3, 2, 5});

        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, result.sorted());
        assertEquals(
                List.of(
                        "T0_START",
                        "T1_OUTER_LOOP",
                        "T2_INNER_LOOP", "T3_SWAP",
                        "T2_INNER_LOOP", "T3_SWAP",
                        "T2_INNER_LOOP", "T3_SWAP",
                        "T2_INNER_LOOP", "T4_NO_SWAP",
                        "T1_OUTER_LOOP",
                        "T2_INNER_LOOP", "T4_NO_SWAP",
                        "T2_INNER_LOOP", "T3_SWAP",
                        "T2_INNER_LOOP", "T4_NO_SWAP",
                        "T1_OUTER_LOOP",
                        "T2_INNER_LOOP", "T4_NO_SWAP",
                        "T2_INNER_LOOP", "T4_NO_SWAP",
                        "T5_EARLY_EXIT",
                        "T6_FINISH"
                ),
                result.trace()
        );
    }

    // Проверяет необычный случай на экстремальных значениях int.
    @Test
    void unusualCaseWithExtremeIntValues() {
        BubbleSort.SortTraceResult result = BubbleSort.sortWithTrace(
                new int[]{Integer.MAX_VALUE, 0, Integer.MIN_VALUE}
        );

        assertArrayEquals(new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE}, result.sorted());
        assertEquals(
                List.of(
                        "T0_START",
                        "T1_OUTER_LOOP",
                        "T2_INNER_LOOP", "T3_SWAP",
                        "T2_INNER_LOOP", "T3_SWAP",
                        "T1_OUTER_LOOP",
                        "T2_INNER_LOOP", "T3_SWAP",
                        "T6_FINISH"
                ),
                result.trace()
        );
    }

    // Проверяет уже отсортированный массив: отсутствие обменов и ранний выход.
    @Test
    void alreadySortedArrayHasEarlyExit() {
        BubbleSort.SortTraceResult result = BubbleSort.sortWithTrace(new int[]{1, 2, 3});

        assertArrayEquals(new int[]{1, 2, 3}, result.sorted());
        assertEquals(
                List.of(
                        "T0_START",
                        "T1_OUTER_LOOP",
                        "T2_INNER_LOOP", "T4_NO_SWAP",
                        "T2_INNER_LOOP", "T4_NO_SWAP",
                        "T5_EARLY_EXIT",
                        "T6_FINISH"
                ),
                result.trace()
        );
    }

    // Проверяет массив в обратном порядке: максимальное число обменов для данного размера.
    @Test
    void reverseArrayUsesSwapBranch() {
        BubbleSort.SortTraceResult result = BubbleSort.sortWithTrace(new int[]{3, 2, 1});

        assertArrayEquals(new int[]{1, 2, 3}, result.sorted());
        assertEquals(
                List.of(
                        "T0_START",
                        "T1_OUTER_LOOP",
                        "T2_INNER_LOOP", "T3_SWAP",
                        "T2_INNER_LOOP", "T3_SWAP",
                        "T1_OUTER_LOOP",
                        "T2_INNER_LOOP", "T3_SWAP",
                        "T6_FINISH"
                ),
                result.trace()
        );
    }

    // Проверяет смешанный случай, где покрываются обе ветки: и с обменом, и без обмена.
    @Test
    void mixedArrayCoversBothSwapAndNoSwap() {
        BubbleSort.SortTraceResult result = BubbleSort.sortWithTrace(new int[]{2, 1, 2});

        assertArrayEquals(new int[]{1, 2, 2}, result.sorted());
        assertEquals(
                List.of(
                        "T0_START",
                        "T1_OUTER_LOOP",
                        "T2_INNER_LOOP", "T3_SWAP",
                        "T2_INNER_LOOP", "T4_NO_SWAP",
                        "T1_OUTER_LOOP",
                        "T2_INNER_LOOP", "T4_NO_SWAP",
                        "T5_EARLY_EXIT",
                        "T6_FINISH"
                ),
                result.trace()
        );
    }
}
