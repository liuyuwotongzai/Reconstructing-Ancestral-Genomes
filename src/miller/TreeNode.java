package miller;

import sun.reflect.generics.tree.Tree;

import java.util.ArrayList;
import java.util.List;

// 树结点实体类
public class  TreeNode implements Cloneable
{
    public String nodeFlag;//树结构字符串
    public String name;
    public double branchLength;
    public String[] genome;
    public String[] chromosomes;
    public List<AdjOrTelomere> adjsOfNode;//节点上所有的邻接和端粒
    public List<AdjOrTelomere> pre;
    public List<AdjOrTelomere> suc;



    public List<TreeNode> parents;
    public List<TreeNode> children;

    //******************************************************************************************************
    public TreeNode father;
    //设置节点的孩子
    public void setChildren(List<TreeNode> children) {this.children = children;}
    //获取节点的孩子
    public List<TreeNode> getChildren() {
        return children;
    }
    //设置节点的父亲
    public void setFather(TreeNode father){this.father = father;}
    //获取节点父亲
    public TreeNode getFather() {return father;}
    //设置节点名字
    public void setName(String name) {this.name = name;}
    //设置分支长度
    public void setBranchLength(double branchLength) {this.branchLength = branchLength;}
    //获取分支长度
    public double getBranchLength() {return branchLength;}
    //*******************************************************************************************************
    public TreeNode()
    {
        parents = new ArrayList<TreeNode>();
        children = new ArrayList<TreeNode>();
        adjsOfNode = new ArrayList<AdjOrTelomere>();
        pre = new ArrayList<AdjOrTelomere>();
        suc = new ArrayList<AdjOrTelomere>();
    }

    public void getChromosomes()
    {
        int length_genome = genome.length;
        chromosomes = new String[length_genome-1];
        String element;
        int elementLength;
        for(int i=1; i<length_genome; i++)
        {
            element = genome[i];
            elementLength = element.length();
            chromosomes[i-1] = element.substring(0, elementLength-2);
        }
    }

    public void getAdjsOfNode()
    {
        int len;
        String[] genesInCurrentChromosome;
        int count = 0;
        for(String currentChromosome : chromosomes)
        {
            genesInCurrentChromosome = currentChromosome.split(" ");
            len = genesInCurrentChromosome.length;
            adjsOfNode.add(new AdjOrTelomere(0, Integer.valueOf(genesInCurrentChromosome[0]), name+count));
            for (int j = 0; j < len-1; j++)
            {
                adjsOfNode.add(new AdjOrTelomere(Integer.valueOf(genesInCurrentChromosome[j]), Integer.valueOf(genesInCurrentChromosome[j+1]), name+count));
            }
            adjsOfNode.add(new AdjOrTelomere(Integer.valueOf(genesInCurrentChromosome[len-1]), 0, name+count));
            count++;
        }
    }

    public void getPreAndSuc()
    {
        for(AdjOrTelomere current : adjsOfNode)
        {
            if(current.left!=0 && current.right!=0)
            {
                pre.add(current);
                suc.add(current);
            }
            else
            {
                if(current.left!=0)
                    suc.add(current);
                if(current.right!=0)
                    pre.add(current);
            }
        }
    }
}
