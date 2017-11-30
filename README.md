# Sorting Assignment for ICS4U

Goal: To compare quicksort and timsort, written in Java

## Quicksort

A commonly used sort, which has average time complexity O(N log N), and worst case O(N^2).
The implementation used here picks the right-most element as the pivot, and then calls quicksort on the two partitions.
This is implemented with the use of a stack to simulate recursion, as `syncronized` won't work with recursive calls.

## Timsort

A varient on mergesort, with best case O(N), and worst case O(N log N).
This sort is based on the idea of mergesort, where you merge two adjacent sorted subarrays.
The implementation here uses an FIFO data structure to keep track of the runs.
Each time two runs are merged, they form a new larger run, which is then pushed to the back of the FIFO data structure.
