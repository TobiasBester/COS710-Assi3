package com.bester.tobias.tspassi;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IndirectChromosome {

    private int numGenes;
    private Integer currentFitness;

    private List<Boolean> genes;

    IndirectChromosome(int numGenes, Chromosome operand) {
        this.numGenes = numGenes;
        generateGenes();
        evaluate(new Chromosome(operand));
    }

    IndirectChromosome(List<Boolean> genes, Chromosome operand) {
        this.genes = genes;
        this.numGenes = genes.size();
        evaluate(new Chromosome(operand));
    }

    IndirectChromosome(IndirectChromosome copy) {
        this.genes = new ArrayList<>(copy.getGenes());
        this.numGenes = copy.numGenes;
        this.currentFitness = copy.getCurrentFitness();
    }

    public Integer evaluate(Chromosome operand) {
        IntStream.range(0, numGenes)
                .forEach(idx -> {
                    int cityToSwap = idx * 2 + 1;
                    if (genes.get(idx)) {
                        Collections.swap(operand.getCities(), cityToSwap, cityToSwap - 1);
                    }
                });

        int result = operand.evaluate();
        currentFitness = result;
        return result;
    }

    public List<Boolean> getGenes() {
        return genes;
    }

    public int getRandomIndex() {
        return new Random().nextInt(numGenes);
    }

    int[] getTwoRandomIndexes() {
        Random r = new Random();
        int idx1 = getRandomIndex();
        int idx2 = r.nextInt(genes.size());
        while (idx1 == idx2) {
            idx2 = r.nextInt(genes.size());
        }

        int[] result = new int[]{idx1, idx2};
        Arrays.sort(result);

        return result;
    }

    public void switchGeneAtIndex(int index) {
        genes.set(index, !genes.get(index));
    }

    @Override
    public String toString() {
        return "IndirectChromosome{ currentFitness=" + currentFitness + " }";
    }

    Integer getCurrentFitness() {
        return currentFitness;
    }

    private void generateGenes() {
        Random rand = new Random();
        genes = IntStream.range(0, numGenes)
                .mapToObj(idx -> rand.nextBoolean())
                .collect(Collectors.toList());
    }
}
