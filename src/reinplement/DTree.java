package reinplement;

import miller.LeafNode;
import miller.Tree;
import miller.TreeNode;

import java.io.*;
import java.util.*;

public class DTree implements Cloneable {
    private String tree_str = "";

    public ArrayList<TreeNode> getTree() {
        return tree;
    }

    public void setTree(ArrayList<TreeNode> tree) {
        this.tree = tree;
    }

    public Map<String, TreeNode> getRecord() {
        return record;
    }

    public void setRecord(Map<String, TreeNode> record) {
        this.record = record;
    }

    public List<List<String>> getGenomes() {
        return genomes;
    }

    public void setGenomes(List<List<String>> genomes) {
        this.genomes = genomes;
    }

    public static ArrayList<TreeNode> tree = new ArrayList<TreeNode>();
    private Map<String, TreeNode> record = new HashMap<String, TreeNode>();
    private List<List<String>> genomes = new ArrayList<List<String>>();

    public DTree(ArrayList<TreeNode> shulianbiao) {
        this.tree = (ArrayList<TreeNode>) shulianbiao.clone();

//        this.tree.addAll(shulianbiao);
//        for(int i = 0;i<shulianbiao.size();i++){
//            this.tree.get(i).name = shulianbiao.get(i).name;
//            this.tree.get(i).children = shulianbiao.get(i).children;
//
//            this.tree.get(i).nodeFlag = shulianbiao.get(i).nodeFlag;
//            this.tree.get(i).chromosomes = shulianbiao.get(i).chromosomes;
//            this.tree.get(i).branchLength = shulianbiao.get(i).branchLength;
//            this.tree.get(i).genome = shulianbiao.get(i).genome;
//            this.tree.get(i).parents.addAll(shulianbiao.get(i).parents);
//            this.tree.get(i).father = shulianbiao.get(i).father;
//        }

        //************************************************************
//        this.tree = Tree.tree;//初始值
//        System.out.println(tree.size()+"0");
//        for(int k = 0;k<tree.size();k++)
//        {
//            this.tree.get(k).father = new TreeNode();
//            this.tree.get(k).father = tree.get(k).father;
//
//            this.tree.get(k).parents = new LinkedList<>();
//            this.tree.get(k).parents.addAll(tree.get(k).parents);
//
//            this.tree.get(k).children = new LinkedList<TreeNode>();
//            this.tree.get(k).children.addAll(tree.get(k).children);
//
//            this.tree.get(k).branchLength = 0.0;
//            this.tree.get(k).branchLength  = tree.get(k).branchLength;
//
//            this.tree.get(k).adjsOfNode = new LinkedList<>();
//            this.tree.get(k).adjsOfNode.addAll(tree.get(k).adjsOfNode);
//
//            this.tree.get(k).nodeFlag = new String();
//            this.tree.get(k).nodeFlag = tree.get(k).nodeFlag;
//
//            this.tree.get(k).name = new String();
//            this.tree.get(k).name = tree.get(k).name;
//
//            this.tree.get(k).genome = new String[100];
//            this.tree.get(k).genome = tree.get(k).genome;
//
//            this.tree.get(k).chromosomes = new String[100];
//            this.tree.get(k).chromosomes = tree.get(k).chromosomes;
//        }

        //************************************************************




//        record.putAll(record);
//    for(int i=0 ; i<genomes.size() ; i++){
//        this.genomes.get(i) = ((List<String>)genomes.get(i).clone());
//    }

//
//        this.record.putAll(record);
////        for(int i=0 ; i<genomes.size() ; i++){
////            List<String> temp =  genomes.get(i);
////            List<String> temp1 = new List
////            for(int j =0 ;j<temp.size() ; j++ ){
////
////            }
////
////            this.genomes.add((List<String>)genomes.get(i).clone());
////        }
//
//    }
    }
//    public Tree copyTree() throws IOException, ClassNotFoundException {//第三种方式实现深拷贝
//        return ;
//    }


    public LinkedList<LinkedList<TreeNode>> neiborIsNoteaf() {//获取相邻的且是邻居节点的节点

        LinkedList<LinkedList<TreeNode>> linju = new LinkedList<>();
        for (int i = 0; i < tree.size(); i++) {
            LinkedList<TreeNode> linjuforevery = new LinkedList<TreeNode>();
//            for(int j =0 ; j<tree.size() ; j++){
//                if(j!=i && !(this.tree.get(j) instanceof LeafNode)){//且是中间节点
//                    if(this.tree.get(j).father!=null&&this.tree.get(j).father.name == this.tree.get(i).name)
//                    {
//                        linjuforevery.add(this.tree.get(j));
//                    }
//                    else  if(this.tree.get(i).father!=null&&this.tree.get(i).father.name == this.tree.get(j).name)
//                    {
//                            linjuforevery.add(this.tree.get(j));
//                    }
//                    //System.out.println(this.tree.get(j).name);
//                }
//            }
            if (this.tree.get(i).father != null && !(this.tree.get(i) instanceof LeafNode)) {
                System.out.println(this.tree.get(i).father.name);
                linjuforevery.add(this.tree.get(i).father);
            }
            for (int j = 0; j < this.tree.get(i).children.size(); j++) {
                if (!(this.tree.get(i).children.get(j) instanceof LeafNode)) {
                    System.out.println(this.tree.get(i).children.get(j).name);
                    linjuforevery.add(this.tree.get(i).children.get(j));
                }
            }
            linju.add(linjuforevery);

        }
        return linju;
    }

    public LinkedList<TreeNode> allNotleaf(TreeNode node) {//获取此节点到根节点所有经过的中间节点
        TreeNode pointer;
        LinkedList<TreeNode> allzhongjian = new LinkedList<TreeNode>();
        for (pointer = node; pointer != null; pointer = pointer.father) {

            if (!(pointer instanceof LeafNode)) {
                //System.out.println(pointer.name + "*");
                allzhongjian.add(pointer);
            }
        }
        return allzhongjian;
    }

    public TreeNode reconstruct(LinkedList<TreeNode> node) {//重新生根
        TreeNode pointer_01 = node.get(0);//当前要重建的节点
        TreeNode pointer_02 = pointer_01.father;//当前重建节点的父亲
        TreeNode pointer_03;
        int i = 0;//
        double temp = 0;//实现存储要重建节点的父节点权值
        double temp1 [] = new double[100];//第二个开始用这个存

        TreeNode newRoot = null;
        while (pointer_02 != null) {
            pointer_03 = pointer_02.father;//当前重建节点的爷爷
            if(i==0){
                newRoot  = new TreeNode();

                pointer_02.father = newRoot;
                temp = pointer_02.branchLength;
                //System.out.println(pointer_02.children.get(0).name);
                pointer_02.branchLength = pointer_02.children.get(0).branchLength;//设置重新生根节点父节点的权值

                pointer_01.father = newRoot;
                pointer_01.branchLength = 0.00;


                newRoot.children.add(pointer_01);
                newRoot.children.add(pointer_02);
                newRoot.name = "newRoot";

                for(int j = 0; j< pointer_02.children.size();j++)
                {
                    if(pointer_02.children.get(j).name == pointer_01.name){
                        pointer_02.children.remove(j);
                    }
                }
                pointer_01 = pointer_02 ;
                pointer_02 = pointer_03;
            }
            else{
                pointer_02.father = pointer_01;

                pointer_01.children.add(pointer_02);

                temp1[i] = pointer_02.branchLength;

                //System.out.println(i+" "+pointer_02.branchLength);
               if(i==1){
                pointer_02.branchLength = temp;}
               else{
                   pointer_02.branchLength = temp1[i-1];
               }


                for(int j = 0; j< pointer_02.children.size();j++)
                {
                    if(pointer_02.children.get(j).name == pointer_01.name){
                        pointer_02.children.remove(j);
                    }
                }
                pointer_01 = pointer_02;
                pointer_02 = pointer_03;

            }
         i++;
        }
        return  newRoot;
    }

    //重新调整树，使得最终每个中间节点都有两个孩子
public TreeNode reconstruct_02(TreeNode newroot)
{
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(newroot);
    while(!queue.isEmpty()){
        TreeNode node = queue.poll();
        if(node.children.size()==1)
        {
            node.children.get(0).branchLength = (node.children.get(0).branchLength+node.branchLength)/2;
            node.father.children.remove(1);
            node.father.children.add(node.children.get(0));

        }
        // System.out.println(node.name+" "+node.branchLength);//测试节点顺序正确，分支长度正确，节点孩子正确
        //System.out.println("(Weight:" + node.getWeight() + ", Name:" + node.getName() + ", Leaf:" + node.isLeaf() + ")");
        List<TreeNode> children = node.getChildren();
        //System.out.println(children.size());
        for (TreeNode child : children) {
            queue.offer(child);
        }
    }
return newroot;
}


    //前序遍历，通过前序遍历改变树链表，因为树的结构变了但是链表始终没有变
    public void preOrder(TreeNode node, Queue<TreeNode> queue){
        int childNumber = node.children.size();
        if(childNumber == 0){
            queue.offer(node);
            return;
        }
        else if(childNumber == 1){
            queue.offer(node);
            TreeNode child = node.children.get(0);
            preOrder(child, queue);
        }
        else if(childNumber == 2){
            queue.offer(node);
            TreeNode lchild =  node.children.get(0);
            preOrder(lchild,queue);
            TreeNode rchild =  node.children.get(1);
            preOrder(rchild, queue);
        }
    }
    public ArrayList<TreeNode> getNewNodeList(LinkedList<TreeNode> tree){
        Queue<TreeNode> queue = new LinkedList<>();
        preOrder(tree.get(0), queue);
        ArrayList<TreeNode> arrayList = new ArrayList<>();
        while(!queue.isEmpty()){
            arrayList.add(queue.poll());
        }
        return arrayList;

    }

    public  void levelOrder(LinkedList<TreeNode> tree){
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(tree.get(0));
        while(!queue.isEmpty()){
            TreeNode node = queue.poll();
            if(node.children.size()==1)
            {
                node.children.add(node.children.get(0));
            }

            //System.out.println("(Weight:" + node.getWeight() + ", Name:" + node.getName() + ", Leaf:" + node.isLeaf() + ")");
            List<TreeNode> children = node.getChildren();
            //System.out.println(children.size());
            for (TreeNode child : children) {
                queue.offer(child);
            }
        }
    }
    //层序遍历，用于检查树的结构是否正确
    public void levelorder2(TreeNode node1)
    {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(node1);
        while(!queue.isEmpty()){
            TreeNode node = queue.poll();
         System.out.println(node.name+" "+node.branchLength+" "+node.adjsOfNode.toString());//测试节点顺序正确，分支长度正确，节点孩子正确
           // System.out.println(node.name+" "+node.branchLength);//测试节点顺序正确，分支长度正确，节点孩子正确
            //System.out.println("(Weight:" + node.getWeight() + ", Name:" + node.getName() + ", Leaf:" + node.isLeaf() + ")");
            List<TreeNode> children = node.getChildren();
            //System.out.println(children.size());
            for (TreeNode child : children) {
                queue.offer(child);
            }
        }
    }

    public void PrintToFile(TreeNode node)
    {
        String outputString = "";
        List<TreeNode> children = node.getChildren();
        TreeNode leftChild, rightChild;
        if(children.size() == 0){
            leftChild = null;
            rightChild = null;
        }
        else if(children.size() == 1){
            leftChild = children.get(0);
        }
        else if(children.size() == 2){
            leftChild = children.get(0);
            rightChild = children.get(1);
        }

    }

}

