package com.bester.tobias.tspassi;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IndirectPopulation extends DirectPopulation {

//    private String problemName;
//    private String comment;
//    private int dimension;
//    private int numChromosomes;
//    private int numIterations;
//    private List<String> coords;
//    private List<City> cities;
    private Chromosome directChromosome;
    private int numGenes;
    private List<IndirectChromosome> indirectChromosomes = new ArrayList<>();

    public IndirectPopulation(String problemName, int numChromosomes, int numIterations) {
        super(problemName, numChromosomes, numIterations);
        directChromosome = new Chromosome(0, cities, true);
        numGenes = cities.size() / 2;
    }

    @Override
    public void createInitialPopulation() {
        IntStream.range(0, numChromosomes).forEach(idx ->
            indirectChromosomes.add(new IndirectChromosome(numGenes, directChromosome))
        );
    }

    @Override
    public void startSearch(int tournamentSize, double mutationProbability) {
        System.out.println("Starting search");
        IntStream.range(0, numIterations).forEach(idx -> {
            System.out.format("Starting iteration %d%n", idx);
            evaluatePopulation();

            List<IndirectChromosome> parents = selectParents(tournamentSize);

            indirectChromosomes = new ArrayList<>(applyOperators(parents, mutationProbability));

            averageFitnessHistory.add(calculateAverageFitness());
            bestFitnessHistory.add(calculateBestFitness());
        });
    }

    @Override
    protected Integer calculateBestFitness() {
        Optional<Integer> lowest = indirectChromosomes.stream()
                .map(IndirectChromosome::getCurrentFitness)
                .min(Integer::compareTo);

        return lowest.orElse(9999);
    }

    @Override
    protected float calculateAverageFitness() {
        Optional<Integer> sum = indirectChromosomes.stream()
                .map(IndirectChromosome::getCurrentFitness)
                .reduce(Integer::sum);

        return sum.orElse(0) / ((float) indirectChromosomes.size());
    }

    @Override
    protected void evaluatePopulation() {
        indirectChromosomes.forEach(indirectChromosome -> {
            indirectChromosome.evaluate(directChromosome);
            System.out.println(indirectChromosome);
        });
    }

    private List<IndirectChromosome> applyOperators(List<IndirectChromosome> chromosomes, double mutationProb) {
        int targetSize = chromosomes.size();
        List<IndirectChromosome> newGen = new ArrayList<>();
        float r;

        while (!chromosomes.isEmpty()) {
            r = new Random().nextFloat();

            if (chromosomes.size() > 1 && r > mutationProb) {
                List<IndirectChromosome> selected = getTwoRandomIndirectChromosomes(chromosomes);
                newGen.addAll(crossover(selected.get(0), selected.get(1)));
            } else {
                IndirectChromosome selected = getRandomIndirectChromosome(chromosomes);
                newGen.add(mutation(selected));
            }
        }

        assert newGen.size() == targetSize;

        return newGen;
    }

    private List<IndirectChromosome> getTwoRandomIndirectChromosomes(List<IndirectChromosome> list) {
        List<IndirectChromosome> result = new ArrayList<>();
        IndirectChromosome chromosome1 = getRandomIndirectChromosome(list);
        IndirectChromosome chromosome2 = getRandomIndirectChromosome(list);

        result.add(chromosome1);
        result.add(chromosome2);
        return result;
    }

    private IndirectChromosome getRandomIndirectChromosome(List<IndirectChromosome> chromosomes) {
        IndirectChromosome result = chromosomes.get(new Random().nextInt(chromosomes.size()));
        chromosomes.remove(result);
        return result;
    }

    private List<IndirectChromosome> crossover(IndirectChromosome chromosome1, IndirectChromosome chromosome2) {
        int[] indexes = chromosome1.getTwoRandomIndexes();
        int idx1 = indexes[0];
        int idx2 = indexes[1];
        IndirectChromosome child1 = new IndirectChromosome(chromosome1);
        IndirectChromosome child2 = new IndirectChromosome(chromosome2);

        List<Boolean> parent1Genes = chromosome1.getGenes();
        List<Boolean> parent2Genes = chromosome2.getGenes();
        List<Boolean> child1Genes = getEmptyListOfGenes(child1.getGenes().size());
        List<Boolean> child2Genes = getEmptyListOfGenes(child2.getGenes().size());

        IntStream.range(idx1, idx2 + 1).forEach(idx -> {
            child1Genes.set(idx, parent1Genes.get(idx));
            child2Genes.set(idx, parent2Genes.get(idx));
        });

        IntStream.range(0, idx1).forEach(idx -> {
            child1Genes.set(idx, parent2Genes.get(idx));
            child2Genes.set(idx, parent1Genes.get(idx));
        });

        IntStream.range(idx2 + 1, parent1Genes.size()).forEach(idx -> {
            child1Genes.set(idx, parent2Genes.get(idx));
            child2Genes.set(idx, parent1Genes.get(idx));
        });

        List<IndirectChromosome> results = new ArrayList<>();
        results.add(new IndirectChromosome(child1Genes, directChromosome));
        results.add(new IndirectChromosome(child2Genes, directChromosome));
        return results;
    }

    private IndirectChromosome mutation(IndirectChromosome chromosome) {
        int index = chromosome.getRandomIndex();
        chromosome.switchGeneAtIndex(index);
        return new IndirectChromosome(chromosome.getGenes(), directChromosome);
    }

    private List<IndirectChromosome> selectParents(int tournamentSize) {
        return IntStream.range(0, indirectChromosomes.size())
                .mapToObj(value -> tournamentSelection(tournamentSize))
                .collect(Collectors.toList());
    }

    private IndirectChromosome tournamentSelection(int tSize) {
        List<IndirectChromosome> tournament = new ArrayList<>(indirectChromosomes);
        Collections.shuffle(tournament);
        tournament = tournament.subList(0, tSize);
        Optional<IndirectChromosome> chromosome = tournament.stream()
                .min(Comparator.comparing(indirectChromosome -> indirectChromosome.evaluate(directChromosome)));
        return chromosome.orElse(indirectChromosomes.get(0));
    }

    private List<Boolean> getEmptyListOfGenes(int size) {
        return new ArrayList<>(Collections.nCopies(size, null));
    }
}
