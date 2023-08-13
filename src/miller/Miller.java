package miller;

import reinplement.CSolveBranchNode;
import reinplement.CTree;
import reinplement.DTree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Miller
{
    //每次都新建一个原始字符串构建的树
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static CTree CreateStartTree(String TreeStr, List<List<String>> leafGenomes){
        CTree t = new CTree(TreeStr, leafGenomes);
        return t;
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static void main(String[] args) throws IOException {


//相对路径
//        String tree_path = "./newick_Tree";
//        String leafGenomes_path ="./leafGenomes_for_RAGPM";
//        String outPath = "./data3";
//绝对路径
        String tree_path = "E:/git/RAGWRES/newick_Tree";
        String leafGenomes_path ="E:/git/RAGWRES/leafGenomes_for_RAGPM";
        String outPath = "E:/git/RAGWRES/data2";


        List<List<String>> leafGenomes = new ArrayList<List<String>>();
        getLeafGenomes(leafGenomes,leafGenomes_path);//读取叶子节点文件，将叶子节点内容存放在嵌套链表中

        String treeStr = FileUtils.readFile(tree_path).get(0);
//        Tree.construct(treeStr, leafGenomes);//将叶子节点基因组的内容按照结构添加到树中
        CTree t = new CTree(treeStr, leafGenomes);//构造函数中
        CSolveBranchNode cs = null;

        //int treeSize = t.tree.size()/2;
        int treeSize = t.tree.size();

        System.out.println("**************************************************************************************************************************************");
        for(int i=treeSize-1; i>=0; i--)
//        for (int i = 7; i <8 ; i++)
        {
            //System.out.println(t.tree.get(i).name+"*");//顺序是4321ABCDE
            if(i==0){
                //long time1 = System.currentTimeMillis();
                TreeNode tn = t.tree.get(i);
                cs = new CSolveBranchNode(tn, outPath, i,leafGenomes_path);
                //t.levelOrder();
                cs.solve1(t);
                cs.back();
//                System.out.print("运行完一次所用时间");//一次都没走完
//                long time2 = System.currentTimeMillis();
//                System.out.println(time2-time1);

            }

            else //是中间节点
            {

                //long time1 = System.currentTimeMillis();
                //**************************************************************
                CTree temp_t = CreateStartTree(treeStr, leafGenomes);//构建原始树
                ArrayList<TreeNode> c = new ArrayList<TreeNode>();//取出所有节点
                temp_t.knowFather();
                c.addAll(temp_t.tree);
                DTree aa = new DTree(c);//生成重构树
                TreeNode tn  = aa.tree.get(i);
                if(!(tn instanceof LeafNode)) {
                    //System.out.println(tn.name+"*");//重建的节点是正确的
                    //temp_t.levelOrder();


                    TreeNode newRoot1 = aa.reconstruct(aa.allNotleaf(tn));
                    TreeNode newRoot   = aa.reconstruct_02(newRoot1);//新重新生根方法的不同之处

                    //aa.levelorder2(newRoot);

                    //**************************************************************
                    //cs = new CSolveBranchNode(newRoot, outPath, i,leafGenomes_path);//修改
                    cs = new CSolveBranchNode(newRoot, outPath, i,leafGenomes_path);
                    cs.solve1(temp_t);
                    cs.back();


//                    System.out.print("运行完一次所用时间：");//一次都没走完
//                    long time2 = System.currentTimeMillis();
//                    System.out.println(time2-time1);

//                    for(int k = 0;k<Tree.tree.size();k++)
//                    {
//                        if(Tree.tree.get(k).name=="newRoot");
//                        Tree.tree.remove(k);
//                    }
                }
            }
        }
        t.back();


    }

    public static void getLeafGenomes(List<List<String>> leafGenomes, String leafGenomesPath)
    {
        FileReader fileReader;
        BufferedReader bufferedReader;
        String temp;
        try
        {
            fileReader = new FileReader(leafGenomesPath);
            bufferedReader = new BufferedReader(fileReader);
            while((temp=bufferedReader.readLine())!=null)
            {
                if(temp.length()>0 && !temp.equals("\r") && !temp.equals("\n"))
                {
                    if(temp.charAt(0)=='>')
                    {
                        List<String> current = new ArrayList<String>();
                        current.add(temp);
                        leafGenomes.add(current);
                    }
                    else
                    {
                        leafGenomes.get(leafGenomes.size()-1).add(temp);
                    }
                }
            }

            bufferedReader.close();
            fileReader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // 将 tree 中 tree 列表中的 target 节点 调整为根节点
    public static Tree reRootTree(Tree tree, TreeNode target)
    {
        if(target.equals(tree.tree.get(0)))  // 当前根节点
            return tree;
        else
        {
            TreeNode currentRoot = tree.tree.get(0);
            TreeNode currentRootLeft = currentRoot.children.get(0);
            TreeNode currentRootRight = currentRoot.children.get(1);
            if(!(currentRootLeft).equals(target))
            {
                TreeNode temp = currentRootLeft;
                currentRoot.children.set(0, temp.children.get(0));
                temp.children.get(0).parents.set(0, currentRoot);
                temp.children.set(0, temp.children.get(1));
                temp.children.set(1, currentRootRight);
                currentRootRight.parents.set(0, temp);
                currentRoot.children.set(1, temp);
                temp.parents.set(0, currentRoot);
                return reRootTree(tree, target);
            }
            else
            {
                TreeNode temp = new TreeNode(); // 需要在树中新加入一个节点 temp
                temp.setName("temp");
                temp.setBranchLength(0.0);
                temp.children.add(currentRootLeft.children.get(1));
                currentRootLeft.children.get(1).parents.set(0, temp);
                temp.children.add(currentRootRight);
                currentRootRight.parents.set(0, temp);
                currentRootLeft.children.set(1, temp);
                temp.parents.add(currentRootLeft);
                int aPositionForTemp = tree.tree.indexOf(currentRootLeft);
                tree.tree.set(aPositionForTemp,temp);                    // 将temp存放于原来存放 currentRootLeft 的位置
                tree.tree.set(0, currentRootLeft);                      // 把 currentRootLeft 指向的对象置于原来的根节点的位置上(0号位置)
                // 当前已经将目标节点置于根节点的位置，下面将分支节点的名字置为"",
                for(TreeNode node : tree.tree)
                {
                    if(!(node instanceof LeafNode))
                        node.setName("");
                }
                // 然后重新给各内部节点赋名字
                adjustNodeNamesINTree(tree.tree.get(0));
                temp.setName(temp.name + "__temp");
                return tree;
            }
        }
    }

    // 整理树中各节点的名称 (整棵树中只有一个节点名字为temp)
    public static void adjustNodeNamesINTree(TreeNode root)
    {
        if(root==null)
            return;
        if(!(root instanceof LeafNode))
        {
            TreeNode left = root.children.get(0);
            TreeNode right = root.children.get(1);
            if("".equals(left.name))
                adjustNodeNamesINTree(left);
            else if("".equals(right.name))
                adjustNodeNamesINTree(right);
            root.setName("(" + left.name + ":" + left.branchLength + "," + right.name + ":" + right.branchLength + ")");
        }
    }



}
