package com.dice.tasks;

import java.util.Arrays;

public class QuickSort {

    public static class QuickSorter {

        public static void main(String[] args) {
            int[] array = {3, 5, 3};

            System.out.println(Arrays.toString(array));
            QuickSorter.quickSort(array);
            System.out.println(Arrays.toString(array));
        }

        public static void quickSort(int[] array) {
            quickSort(array, 0, array.length - 1);
        }

        private static void quickSort(int[] array, int lowIndex, int highIndex) {
            if (lowIndex >= highIndex) return;

            int pivotIndex = lowIndex + (highIndex - lowIndex) / 2;
            int pivotElement = array[pivotIndex];

            int lowIndexBound = lowIndex;
            int highIndexBound = highIndex;
            int iterator = lowIndex;

            while (iterator <= highIndexBound) {
                int currentElement = array[iterator];
                if (currentElement < pivotElement) {
                    swap(array, iterator, lowIndexBound);
                    iterator++;
                    lowIndexBound++;
                } else if (currentElement > pivotElement) {
                    swap(array, iterator, highIndexBound);
                    highIndexBound--;
                } else {
                    iterator++;
                }
            }

            quickSort(array, lowIndex, lowIndexBound - 1);
            quickSort(array, highIndexBound + 1, highIndex);
        }

        private static void swap(int[] array, int aIndex, int bIndex) {
            if(aIndex == bIndex) return;

            int temp = array[aIndex];
            array[aIndex] = array[bIndex];
            array[bIndex] = temp;
        }
    }
}
