package com.falconsag.nqueen.solvers;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;
import java.util.stream.IntStream;

public class BackTrackingSolver implements NQueenSolver {

    public static ImmutableSet<Integer> NUMBERS;

    public List<Integer> getCustom(int size, ImmutableSet<Integer> possibleSet) {
        NUMBERS = possibleSet;
        Deque<Integer> q = new LinkedList();
        //TODO use Guava Table
        TreeMultimap<Integer, Integer> threatPositions = TreeMultimap.create();
        List<List<Integer>> solutions = new ArrayList<>();
        permutate(q, size, threatPositions, solutions);
        List<Integer> solution = solutions.get(0);
        System.out.println("Size: " + solution.size());
        System.out.println("Number of total solutions: "+solutions.size());
        System.out.println(solution);
        return null;
    }


    public Deque<Integer> permutate(Deque<Integer> deque, int size, TreeMultimap<Integer, Integer> threatPositions, List<List<Integer>> solutions) {
//        if (solutions.size() > 0) {
//            return null;
//        }
        int colPos = deque.size() + 1;
        if (deque.size() == size) {
            solutions.add(new ArrayList<>(deque));
//            System.out.println(deque);
            deque.pollLast();
            threatPositions.removeAll(colPos);
            return deque;
        }

        NavigableSet<Integer> forbiddenPositions = threatPositions.get(colPos);
        Sets.SetView<Integer> possibles = Sets.difference(NUMBERS, forbiddenPositions);
        for (Integer possible : possibles) {
            deque.addLast(possible);
            updateThreatPositions(size, colPos, possible, threatPositions, false);
            permutate(deque, size, threatPositions, solutions);
        }

        Integer last = deque.pollLast();
        TreeMultimap<Integer, Integer> newThreatMap = TreeMultimap.create();
        int i = 0;
        for (Integer integer : deque) {
            i++;
            updateThreatPositions(size, i, integer, newThreatMap, false);
        }
        threatPositions.clear();
        threatPositions.putAll(newThreatMap);

        return deque;
    }

    private void updateThreatPositions(int size, int colPos, int rowPos, Multimap<Integer, Integer> threatPositions, boolean delete) {
        int maxNeededIterations = size - colPos;
        IntStream.rangeClosed(1, maxNeededIterations)
                 .forEach(increment -> {
                     int targetColPos = colPos + increment;
                     if (delete) {
                         threatPositions.remove(targetColPos, rowPos);
                     } else {
                         threatPositions.put(targetColPos, rowPos);
                     }
                     if (rowPos + increment <= size) {
                         if (delete) {
                             threatPositions.remove(targetColPos, rowPos + increment);
                         } else {
                             threatPositions.put(targetColPos, rowPos + increment);
                         }
                     }
                     if (rowPos - increment > 0) {
                         if (delete) {
                             threatPositions.remove(targetColPos, rowPos - increment);
                         } else {
                             threatPositions.put(targetColPos, rowPos - increment);
                         }
                     }
                 });
    }


    @Override
    public List<Integer> getSolutionForSize(int size) {
        return null;
    }
}
