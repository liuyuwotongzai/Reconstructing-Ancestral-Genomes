package miller;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

// 两个文件中分别存有两个基因组数据，分别将其读出来，转换成 List<AdjOrTelomere>,记为 adjs1, adjs2, 计算 (adjs1 ∩ adjs2) / (adjs1 ∪ adjs2) 作为 两个基因组的相似度
public class genesSimilarity
{
    public static List<String> genome = new ArrayList<String>();
    public static List<String> chromosomes = new ArrayList<String>();
    public static List<AdjOrTelomere> adjs_real = new ArrayList<AdjOrTelomere>();
    public static List<AdjOrTelomere> adjs_predict = new ArrayList<AdjOrTelomere>();
    public static double similarity = 0;

    // 构造方法
    public genesSimilarity() {}

    // 将中间方法封装 作为外界调用的入口
    public static void deal(String path_real, String path_predict)
    {

        getGenome(path_real);
        List<String> realGenes = getGenes(genome);//真实祖先基因组中的所有基因



        genome.clear();


        getGenome(path_predict);
        List<String> predictGenes = getGenes(genome);//真实祖先基因组中的所有基因


        getSimilarity(realGenes,predictGenes);
    }

    public static void getGenome(String path_real)
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(path_real), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            while((line=br.readLine())!=null)
            {
                if(line.length()>0 && !line.equals("\r") && !line.equals("\n") && line.startsWith(">")==false)
                    genome.add(line);
            }
            br.close();
            isr.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // 从节点genome(的基因组数据)中提取出节点对应的染色体(chromosomes)
    public static void getChromosomes()
    {
        int size_genome = genome.size();
        for(int i=1; i<size_genome; i++)  // 0号单元存的是 ">nodeName"
        {
            String element = genome.get(i);
            int elementLength = element.length();
            if(element.length()>1) {
                chromosomes.add(element.substring(0, elementLength - 2)); // 因为染色体的表示形如 "1 -3 4 $"
            }
        }
    }

    // 从genome转换为Adjs(每个Adj表示一个邻接 邻接形如"1，2")的方法
    public static List<AdjOrTelomere> getAdjs()
    {
        List<AdjOrTelomere> result = new ArrayList<AdjOrTelomere>();
        int chromosomes_size = chromosomes.size();
        for(int i=0; i<chromosomes_size; i++) // 依次处理每一条染色体
        {
            String currentChromosome = chromosomes.get(i);        // 取得当前染色体
            String[] genesInCurrentChromosome = currentChromosome.split(" "); //将当前染色体上的基因提出来
            int len = genesInCurrentChromosome.length;            // 当前染色体包含的基因数
            result.add(new AdjOrTelomere(0, Integer.valueOf(genesInCurrentChromosome[0]), ""));
            for (int j = 0; j < len-1; j++)
            {
                result.add(new AdjOrTelomere(Integer.valueOf(genesInCurrentChromosome[j]), Integer.valueOf(genesInCurrentChromosome[j+1]), ""));
            }
            result.add(new AdjOrTelomere(Integer.valueOf(genesInCurrentChromosome[len-1]), 0, ""));
        }
        return result;
    }

    // 计算两个基因组(List<AdjOrTelomere> 之间的相似度)
    public static void getSimilarity(List<String> a,List<String> b)
    {
        //交集、并集中元素个数
        int jiaoji = 0;
        int bingji = 0;

        int temp = 0;

        List<String> realGenes = a;
        List<String> predictGenes = b;

        //找两个链表中都存在的元素
        for (int i = 0; i < realGenes.size(); i++) {
            if(predictGenes.contains(realGenes.get(i))==true)
            {
                jiaoji++;
            }
        }


        //找只存在于一个链表中的元素
        for (int i = 0; i < realGenes.size(); i++) {
            if(predictGenes.contains(realGenes.get(i))==false)
            {
                temp++;
            }
        }
        for (int i = 0; i < predictGenes.size(); i++) {
             if(realGenes.contains(predictGenes.get(i))==false)
           {
            temp++;
           }
        }

        bingji=temp+jiaoji;



        similarity = (double)jiaoji/bingji;


    }
    //得到基因组中的基因
    public static List<String> getGenes(List<String> Genome)
    {
        List<String> genes = new LinkedList<>();
        for (int i = 0; i <Genome.size() ; i++) {
            String[] temp = Genome.get(i).split(" ");
            for (int j = 0; j < temp.length-1; j++) {
                if(Integer.parseInt(temp[j])<0){temp[j] = Integer.toString(-Integer.parseInt(temp[j]));}
                //System.out.print(temp[j]+ " ");
                genes.addAll(Collections.singleton(temp[j]));
            }
        }

//        for (int i = 0; i < genes.size(); i++) {
//            System.out.print(genes.get(i)+" ");
//        }

        return genes;
    }

    // 判断两个邻接是否等效
    public static boolean isEqual(AdjOrTelomere adj1, AdjOrTelomere adj2)
    {
        if(adj1.toString().equals(adj2.toString()))
            return true;
        else if((adj1.left==(-adj2.right)) && (adj1.right==(-adj2.left)))
            return true;
        else if(Math.abs(adj1.left)==Math.abs(adj2.left)&&Math.abs(adj1.right)==Math.abs(adj2.right))
            return true;
        else
            return false;
    }

    // 将静态变量的值还原
    public static void back()
    {
        genome.clear();
        chromosomes.clear();
        adjs_real.clear();
        adjs_predict.clear();
        similarity = 0;
    }
}
