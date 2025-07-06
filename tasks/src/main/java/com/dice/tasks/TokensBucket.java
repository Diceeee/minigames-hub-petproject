package com.dice.tasks;


import java.time.Duration;
import java.time.Instant;

public class TokensBucket {


    public static void main(String[] args) throws InterruptedException {
        ImprovedBucket tokensBucket = new ImprovedBucket(10000, 1000);
        System.out.println("Start: " + Instant.now());
        tokensBucket.consume(5001);
        System.out.println("End: " + Instant.now());
    }

    public static class ImprovedBucket {

        long maxCapacity;
        long currentCapacity;
        long capacityInSecond;
        Instant lastTs;

        public ImprovedBucket(long maxCapacity, long capacityInSecond) {
            this.maxCapacity = maxCapacity;
            this.capacityInSecond = capacityInSecond;
            this.lastTs = Instant.now();
            this.currentCapacity = 0;
        }

        public synchronized void consume(long consumeCapacity) throws InterruptedException {
            if (consumeCapacity > maxCapacity) {
                throw new IllegalArgumentException(String.format("Max capacity is %d, requested capacity %d is out of limits",
                        maxCapacity, consumeCapacity));
            }

            while (currentCapacity < consumeCapacity) {
                awaitCapacity(consumeCapacity);
            }

            currentCapacity -= consumeCapacity;
        }

        private void awaitCapacity(long consumeCapacity) throws InterruptedException {
            Instant now = Instant.now();

            long nanosPassed = Duration.between(lastTs, now).toNanos();
            double capacityInNano = capacityInSecond / 1_000_000_000d;
            double capacityFilled = nanosPassed * capacityInNano;
            double totalCapacity = capacityFilled + currentCapacity;

            if (totalCapacity < consumeCapacity) {
                double leftCapacity = consumeCapacity - totalCapacity;
                long leftToWaitForCapacityInMillis = (long) (leftCapacity / capacityInNano / 1_000_000);
                int leftToWaitForCapacityInNanos = (int) ((leftCapacity / capacityInNano) % 1_000_000);
                Thread.sleep(leftToWaitForCapacityInMillis, leftToWaitForCapacityInNanos);
            } else {
                this.currentCapacity = Math.min(maxCapacity, (long) totalCapacity);
                this.lastTs = now;
            }
        }
    }
}
