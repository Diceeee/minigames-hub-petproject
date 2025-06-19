package com.dice.tasks;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FizzBuzz {

    public static void main(String[] args) {
        List<Integer> numbers = IntStream.rangeClosed(1, 100).boxed().toList();
        List<String> strings = numbers.stream()
                .flatMap(number -> {
                    if (number % 3 == 0 && number % 5 == 0) {
                        return Stream.of("Fizz", "Buzz");
                    } else if (number % 3 == 0) {
                        return Stream.of("Fizz");
                    } else if (number % 5 == 0) {
                        return Stream.of("Buzz");
                    } else {
                        return Stream.of(Integer.toString(number));
                    }
                })
                .toList();

        System.out.println(strings);
        System.out.println(String.join("", strings));

        int[] ints = IntStream.rangeClosed(1, 100).toArray();
        StringBuilder builder = new StringBuilder();
        for (int value : ints) {
            if (value % 3 == 0 && value % 5 == 0) {
                builder.append("FizzBuzz");
            } else if (value % 3 == 0) {
                builder.append("Fizz");
            } else if (value % 5 == 0) {
                builder.append("Buzz");
            } else {
                builder.append(value);
            }
        }

        System.out.println(builder);
    }
}
