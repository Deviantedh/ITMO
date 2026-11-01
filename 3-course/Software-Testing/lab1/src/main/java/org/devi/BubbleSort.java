package org.devi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BubbleSort {

    private BubbleSort() {
    }

    /** Стандартная сортировка пузырьком без трассировки */
    public static int[] sort(int[] input) {
        return sortWithTrace(input).sorted();
    }

    /** Сортировка пузырьком с записью последовательности посещения характерных точек */
    public static SortTraceResult sortWithTrace(int[] input) {
        if (input == null) {
            throw new IllegalArgumentException("input must not be null");
        }

        int[] arr = Arrays.copyOf(input, input.length);
        List<String> trace = new ArrayList<>();
        trace.add("T0_START");

        for (int i = 0; i < arr.length - 1; i++) {
            trace.add("T1_OUTER_LOOP");
            boolean swapped = false;

            for (int j = 0; j < arr.length - 1 - i; j++) {
                trace.add("T2_INNER_LOOP");
                if (arr[j] > arr[j + 1]) {
                    trace.add("T3_SWAP");
                    int tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                    swapped = true;
                } else {
                    trace.add("T4_NO_SWAP");
                }
            }

            if (!swapped) {
                trace.add("T5_EARLY_EXIT");
                break;
            }
        }

        trace.add("T6_FINISH");
        return new SortTraceResult(arr, trace);
    }

    public record SortTraceResult(int[] sorted, List<String> trace) {
        public SortTraceResult {
            sorted = Arrays.copyOf(sorted, sorted.length);
            trace = Collections.unmodifiableList(new ArrayList<>(trace));
        }
    }
}
