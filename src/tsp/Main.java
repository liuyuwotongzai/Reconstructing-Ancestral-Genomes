package tsp;

public class Main
{

    public static void main(String[] args)
    {
        GeneticAlgorithm GA = new GeneticAlgorithm();  //创建遗传算法驱动对象
        SpeciesPopulation speciesPopulation = new SpeciesPopulation();  //创建初始种群
        SpeciesIndividual bestRate=GA.run(speciesPopulation); //开始遗传算法（选择算子、交叉算子、变异算子）
        bestRate.printRate();//打印路径与最短距离
    }
}
