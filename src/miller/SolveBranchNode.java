package miller;

import java.io.IOException;
import java.util.*;

public class SolveBranchNode//解决一个分支节点的问题
{
    public static TreeNode currentNode = null;//当前的节点
    public static String outPath = "";//重建出节点的输出路径
    public static int nodeNum = 0;//节点的编号

    public static List<List<List<PairAdjsWithStateProba>>> classifiedPawsps = new ArrayList<>();

    public static List<PairAdjsWithStateProba> pawsps = new ArrayList<>();
    public static PairAdjsWithStateProba[] array_pawsps;
    public static int length_array_pawsps = 0;

    public static List<int[]> orginAndNew = new ArrayList<>();

//    public static int minChromoNum = 0;
//    public static int maxChromoNum = 0;

    public SolveBranchNode() { }

    public static void construct(TreeNode the_currentNode, String the_outPath, int the_nodeNum)//三个参数的传递
    {
        currentNode = the_currentNode;
        outPath = the_outPath;
        nodeNum = the_nodeNum;
    }

    public static void solve1() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("The branch node " + currentNode.name + " is being reconstructed!" + "\n");
        getCurrentAdjs();//得到当前节点的所有邻接
       // modifyCurrentAdjs(currentNode.adjsOfNode);
        getPawsps();//得到当前节点所有邻接对的所有状态的初始概率
        getEndPawsps();//得到最终的每个邻接对的每种状态的概率
        getPathFromPawsps();//找路径，这里已经将路径转化为基因的顺序，并写入文件了
        sb.append("Finished" + "\n");
        FileUtils.WriteToFile(outPath+"process", sb.toString(), true);
    }
    //函数，根据内容推断的结果修改当前节点的邻接集合
    public static void modifyCurrentAdjs(List<AdjOrTelomere> before) throws IOException {
         String[] allGenes  = encode.findAllGenes();//拿到所有的基因
         int [] allState = findAndAnalysis.allStates();//拿到当前节点各个基因的状态
         List<AdjOrTelomere> allAdjOfNode  = before;//当前节点的所有邻接
        for(int i =0;i<allGenes.length;i++)
        {
            for(int j = 0;j<allAdjOfNode.size();j++)
            {
                if(allState[i]==0)//如果此基因对应的状态为0
                {
                    if(allAdjOfNode.get(j).right==Integer.parseInt(allGenes[i])||(allAdjOfNode.get(j).left==Integer.parseInt(allGenes[i])))//如果邻接中含有此基因
                    {
                        allAdjOfNode.remove(j);
                    }
                }

            }
        }



    }
    public static void getCurrentPre(TreeNode treeNode)//得到当前节点的pre，要递归的找遍所有孩子的pre
    {
        if(treeNode==null)
            return;
        if(!(treeNode instanceof LeafNode))
        {
            getCurrentPre(treeNode.children.get(0));
            //***********************************************************

            //***********************************************************

           // System.out.println(treeNode.children.get(0).name+"*");
            getCurrentPre(treeNode.children.get(1));}
            //treeNode.pre.addAll(dealSeqs(treeNode.children.get(0).pre, treeNode.children.get(1).pre));
            treeNode.pre.addAll(treeNode.children.get(0).pre);
            if(treeNode.children.size()>1){
            treeNode.pre.addAll(treeNode.children.get(1).pre);}
            removeDuplicateAdjs(treeNode.pre);


    }


    public static void getCurrentSuc(TreeNode treeNode)
    {
        if(treeNode==null)
            return;
        if(!(treeNode instanceof LeafNode))
        {
            getCurrentSuc(treeNode.children.get(0));
            if(treeNode.children.size()>1){
            getCurrentSuc(treeNode.children.get(1));}
            //treeNode.suc.addAll(dealSeqs(treeNode.children.get(0).suc, treeNode.children.get(1).suc));
            treeNode.suc.addAll(treeNode.children.get(0).suc);
            if(treeNode.children.size()>1){
            treeNode.suc.addAll(treeNode.children.get(1).suc);}
            removeDuplicateAdjs(treeNode.suc);
        }
        else
            return;
    }

    public static void getCurrentAdjs()
    {
        getCurrentPre(Tree.tree.get(0));//得到根节点的pre
        getCurrentSuc(Tree.tree.get(0));//得到根节点的suc
        //currentNode.adjsOfNode.addAll(dealSeqs(currentNode.pre, currentNode.suc));
        currentNode.adjsOfNode.addAll(currentNode.pre);
        currentNode.adjsOfNode.addAll(currentNode.suc);
        removeDuplicateAdjs(currentNode.adjsOfNode);//去除相同的邻接

        int num = 0;//计算共有多少个邻接？也没用啊
        for(AdjOrTelomere current : currentNode.adjsOfNode)
        {
            current.num = num;
            num++;
        }
    }

    public static void getPawsps()//得到所有邻接对的初始概率
    {
        String nn = "0,0", ny = "0,1", yn = "1,0", yy = "1,1", yyd = "1,1d";
        AdjOrTelomere adj1, adj2;
        int m=0, n=0, count=0;

        List<String> stateList = new ArrayList<>();//所有邻接对的状态链表？
        int count0;//每种状态的个数？
        int count1;
        int count2;
        int count3;
        int count4;
        double p_00;//每种邻接对状态的概率？
        double p_01;
        double p_10;
        double p_11;
        double p_11d;
        PairAdjsWithStateProba proba;//带有概率的邻接对
        int size_adjsOfCurrentNode = currentNode.adjsOfNode.size();//当前节点的邻接数量
        for(int i=0; i<size_adjsOfCurrentNode-1; i++)
        {
            for(int j=i+1; j<size_adjsOfCurrentNode; j++)
            {
                adj1 = currentNode.adjsOfNode.get(i);//分别取邻接进行组合
                adj2 = currentNode.adjsOfNode.get(j);
                getStateList(stateList, adj1, adj2, m, n, count);//每一种组合的状态链表。每一个元素：邻接对的状态（0，0）等
                count0 = 0;//每一种状态的数量
                count1 = 0;
                count2 = 0;
                count3 = 0;
                count4 = 0;
                for(String current : stateList)//统计每一种状态的个数
                {
                    if(current.equals(nn))
                        count0++;
                    else if(current.equals(ny))
                        count1++;
                    else if(current.equals(yn))
                        count2++;
                    else if(current.equals(yy))
                        count3++;
                    else if(current.equals(yyd))
                        count4++;
                }
                int size = stateList.size();
                p_00 = (double)count0/size;//以每一种状态的个数 除以 所有邻接对状态链表的大小
                p_01 = (double)count1/size;//得到每种状态的初始概率
                p_10 = (double)count2/size;
                p_11 = (double)count3/size;
                p_11d = (double)count4/size;
//                if(p_11<0.1 && p_11d<0.1)
//                    continue;

                    proba = new PairAdjsWithStateProba(adj1, adj2);//邻接对是每一种状态的概率各是多少
                    proba.p_00 = p_00;//这个邻接对是00的概率是多少
                    proba.p_01 = p_01;//
                    proba.p_10 = p_10;
                    proba.p_11 = p_11;
                    proba.p_11d = p_11d;
                    pawsps.add(proba);//将此已经知道各种状态对应概率的邻接对，放入到带有概率的邻接对链表中

            }
        }
        stateList.clear();
        array_pawsps = new PairAdjsWithStateProba[pawsps.size()];
        pawsps.toArray(array_pawsps);
        length_array_pawsps = array_pawsps.length;
        pawsps.clear();
    }

    public static void getEndPawsps()
    {
        String nn = "0,0", ny = "0,1", yn = "1,0", yy = "1,1", yyd = "1,1d";

        int m=0, n=0, count=0;

        // get_P_state_Dx 可重用的变量
        double frequencyOfState = 0, numerator = 0, denominator = 0;
        // get_P_state_Dx 可重用的变量

        // ---------------------------------将该问题中涉及到的所有基因收集起来,并初始化 classifiedPawsps
        int size_adjsOfCurrentNode = currentNode.adjsOfNode.size();
        for(int i=0; i<size_adjsOfCurrentNode; i++)
        {
            List<PairAdjsWithStateProba> p11 = new ArrayList<>();
            List<PairAdjsWithStateProba> p11d = new ArrayList<>();
            List<List<PairAdjsWithStateProba>> current = new ArrayList<>();
            current.add(p11);
            current.add(p11d);
            classifiedPawsps.add(current);
        }

        double[] f = new double[5];
        double p_11;
        double p_11d;
        AdjOrTelomere adj1;
        AdjOrTelomere adj2;
        for(PairAdjsWithStateProba pawsp : array_pawsps)
        {
            f[0] = pawsp.p_00;
            f[1] = pawsp.p_01;
            f[2] = pawsp.p_10;
            f[3] = pawsp.p_11;
            f[4] = pawsp.p_11d;
            p_11 = get_P_state_Dx(currentNode, pawsp, yy, f, frequencyOfState, numerator, denominator, m, n, count, nn, ny, yn, yy, yyd);
            p_11d = get_P_state_Dx(currentNode, pawsp, yyd, f, frequencyOfState, numerator, denominator, m, n, count, nn, ny, yn, yy, yyd);
            pawsp.p_11 = p_11;
            pawsp.p_11d = p_11d;
            adj1 = pawsp.adj_1;
            adj2 = pawsp.adj_2;
            if(p_11>=0.01)
            {
                classifiedPawsps.get(adj1.num).get(0).add(pawsp);
                classifiedPawsps.get(adj2.num).get(0).add(pawsp);
            }
            if(p_11d>=0.01)
            {
                classifiedPawsps.get(adj1.num).get(1).add(pawsp);
                classifiedPawsps.get(adj2.num).get(1).add(pawsp);
            }
            pawsps.add(pawsp);
        }
        array_pawsps = new PairAdjsWithStateProba[pawsps.size()];
        pawsps.toArray(array_pawsps);
        length_array_pawsps = array_pawsps.length;
    }

    public static double getLS(TreeNode currentNode, PairAdjsWithStateProba pawsp, String state, double[] frequency,
                               int m, int n, int count,
                               String nn, String ny, String yn, String yy, String yyd)
    {
        if(currentNode instanceof LeafNode)  // 如果当前节点是叶子节点
        {
            if(state.equals(getStateAtNode(currentNode, pawsp.adj_1, pawsp.adj_2, m, n, count)))
                return 1;
            else
                return 0;
        }
        else                           // 当前节点是分支节点
        {
            if(currentNode.children.size()>1) {//自己加的
                TreeNode Child0 = currentNode.children.get(0);
                double BranchLength0 = Child0.branchLength;
                double value0 = get_P_transform(state, nn, BranchLength0, nn, ny, yn, yy, yyd) * getLS(Child0, pawsp, nn, frequency, m, n, count, nn, ny, yn, yy, yyd)
                        + get_P_transform(state, ny, BranchLength0, nn, ny, yn, yy, yyd) * getLS(Child0, pawsp, ny, frequency, m, n, count, nn, ny, yn, yy, yyd)
                        + get_P_transform(state, yn, BranchLength0, nn, ny, yn, yy, yyd) * getLS(Child0, pawsp, yn, frequency, m, n, count, nn, ny, yn, yy, yyd)
                        + get_P_transform(state, yy, BranchLength0, nn, ny, yn, yy, yyd) * getLS(Child0, pawsp, yy, frequency, m, n, count, nn, ny, yn, yy, yyd)
                        + get_P_transform(state, yyd, BranchLength0, nn, ny, yn, yy, yyd) * getLS(Child0, pawsp, yyd, frequency, m, n, count, nn, ny, yn, yy, yyd);

                TreeNode Child1 = currentNode.children.get(1);
                double BranchLength1 = Child1.branchLength;
                double value1 = get_P_transform(state, nn, BranchLength1, nn, ny, yn, yy, yyd) * getLS(Child1, pawsp, nn, frequency, m, n, count, nn, ny, yn, yy, yyd)
                        + get_P_transform(state, ny, BranchLength1, nn, ny, yn, yy, yyd) * getLS(Child1, pawsp, ny, frequency, m, n, count, nn, ny, yn, yy, yyd)
                        + get_P_transform(state, yn, BranchLength1, nn, ny, yn, yy, yyd) * getLS(Child1, pawsp, yn, frequency, m, n, count, nn, ny, yn, yy, yyd)
                        + get_P_transform(state, yy, BranchLength1, nn, ny, yn, yy, yyd) * getLS(Child1, pawsp, yy, frequency, m, n, count, nn, ny, yn, yy, yyd)
                        + get_P_transform(state, yyd, BranchLength1, nn, ny, yn, yy, yyd) * getLS(Child1, pawsp, yyd, frequency, m, n, count, nn, ny, yn, yy, yyd);
                return value0 * value1;
            }
            else{//自己加的
                TreeNode Child0 = currentNode.children.get(0);
                double BranchLength0 = Child0.branchLength;
                double value0 = get_P_transform(state, nn, BranchLength0, nn, ny, yn, yy, yyd) * getLS(Child0, pawsp, nn, frequency, m, n, count, nn, ny, yn, yy, yyd)
                        + get_P_transform(state, ny, BranchLength0, nn, ny, yn, yy, yyd) * getLS(Child0, pawsp, ny, frequency, m, n, count, nn, ny, yn, yy, yyd)
                        + get_P_transform(state, yn, BranchLength0, nn, ny, yn, yy, yyd) * getLS(Child0, pawsp, yn, frequency, m, n, count, nn, ny, yn, yy, yyd)
                        + get_P_transform(state, yy, BranchLength0, nn, ny, yn, yy, yyd) * getLS(Child0, pawsp, yy, frequency, m, n, count, nn, ny, yn, yy, yyd)
                        + get_P_transform(state, yyd, BranchLength0, nn, ny, yn, yy, yyd) * getLS(Child0, pawsp, yyd, frequency, m, n, count, nn, ny, yn, yy, yyd);
                return value0;
            }
        }
    }

    public static double get_P_transform(String oldState, String newState, double t, String nn, String ny, String yn, String yy, String yyd)
    {
        Constant.setT(t);
        Constant.setAlpha(1);
        Constant.setBeta(2);
        Constant.getP();
        // ----------------------------------------------------
        if((nn).equals(oldState))
        {
            if(newState.equals(nn))
                return Constant.p[0][0];
            if(newState.equals(ny))
                return Constant.p[0][1];
            if(newState.equals(yn))
                return Constant.p[0][2];
            if(newState.equals(yy))
                return Constant.p[0][3];
            if(newState.equals(yyd))
                return Constant.p[0][4];
        }

        if((ny).equals(oldState))
        {
            if(newState.equals(nn))
                return Constant.p[1][0];
            if(newState.equals(ny))
                return Constant.p[1][1];
            if(newState.equals(yn))
                return Constant.p[1][2];
            if(newState.equals(yy))
                return Constant.p[1][3];
            if(newState.equals(yyd))
                return Constant.p[1][4];
        }

        if((yn).equals(oldState))
        {
            if(newState.equals(nn))
                return Constant.p[2][0];
            if(newState.equals(ny))
                return Constant.p[2][1];
            if(newState.equals(yn))
                return Constant.p[2][2];
            if(newState.equals(yy))
                return Constant.p[2][3];
            if(newState.equals(yyd))
                return Constant.p[2][4];
        }

        if((yy).equals(oldState))
        {
            if(newState.equals(nn))
                return Constant.p[3][0];
            if(newState.equals(ny))
                return Constant.p[3][1];
            if(newState.equals(yn))
                return Constant.p[3][2];
            if(newState.equals(yy))
                return Constant.p[3][3];
            if(newState.equals(yyd))
                return Constant.p[3][4];
        }

        if((yyd).equals(oldState))
        {
            if(newState.equals(nn))
                return Constant.p[4][0];
            if(newState.equals(ny))
                return Constant.p[4][1];
            if(newState.equals(yn))
                return Constant.p[4][2];
            if(newState.equals(yy))
                return Constant.p[4][3];
            if(newState.equals(yyd))
                return Constant.p[4][4];
        }

        else
        {
            System.out.println("Warning : Something is wrong!!!");
        }

        return 0;
    }

    public static double get_P_state_Dx(TreeNode currentNode , PairAdjsWithStateProba pawsp,
                                        String state, double frequency[], double frequencyOfState,
                                        double numerator, double denominator,
                                         int m, int n, int count,
                                        String nn, String ny, String yn, String yy, String yyd)
    {
        frequencyOfState = 0;
        numerator = 0;
        denominator = 0;
        if((nn).equals(state))
            frequencyOfState = frequency[0];
        else if((ny).equals(state))
            frequencyOfState = frequency[1];
        else if((yn).equals(state))
            frequencyOfState = frequency[2];
        else if((yy).equals(state))
            frequencyOfState = frequency[3];
        else if((yyd).equals(state))
            frequencyOfState = frequency[4];
        if(frequencyOfState<0.001) // ==0
            return 0;
        else
            numerator = frequencyOfState * getLS(currentNode, pawsp, state, frequency, m, n, count, nn, ny, yn, yy, yyd);

        if(frequency[0]>0.001)
            denominator += frequency[0] * getLS(currentNode, pawsp, nn, frequency, m, n, count, nn, ny, yn, yy, yyd);
        if(frequency[1]>0.001)
            denominator += frequency[1] * getLS(currentNode, pawsp, ny, frequency, m, n, count, nn, ny, yn, yy, yyd);
        if(frequency[2]>0.001)
            denominator += frequency[2] * getLS(currentNode, pawsp, yn, frequency, m, n, count, nn, ny, yn, yy, yyd);
        if(frequency[3]>0.001)
            denominator += frequency[3] * getLS(currentNode, pawsp, yy, frequency, m, n, count, nn, ny, yn, yy, yyd);
        if(frequency[4]>0.001)
            denominator += frequency[4] * getLS(currentNode, pawsp, yyd, frequency,  m, n, count, nn, ny, yn, yy, yyd);
        return numerator / denominator;
    }

    /*
    public static void getMinMaxChromoNum()
    {
        List<Integer> temp = new ArrayList<>();

        TreeNode[] array_Tree_tree = new TreeNode[Tree.tree.size()];
        Tree.tree.toArray(array_Tree_tree);
        int length_array_Tree_tree = array_Tree_tree.length;

        TreeNode treeNode = null;
        for(int i=0; i<length_array_Tree_tree; i++)
        {
            treeNode = array_Tree_tree[i];
            if(treeNode instanceof LeafNode)
            {
                temp.add(treeNode.chromosomes.length);
            }
        }
        minChromoNum = Collections.min(temp);
        maxChromoNum = Collections.max(temp);
    }
     */

    public static void getPathFromPawsps()//从邻接对中选择邻接组成最终的路径
    {
        List<List<Integer>> allpaths = new ArrayList<>();//存放所有的路径，内层链表的每一个元素都是一个邻接，用编号表示
        //getMinMaxChromoNum();

        List<AdjOrTelomere> before_starts = new ArrayList<>();//所有的开端
        List<int[]> infoOfStarts = new ArrayList<>();//每一个开端可能存在的次数
        List<AdjOrTelomere> starts = new ArrayList<>();//存放所有可能的开端（筛选后的）

        for(TreeNode node : Tree.tree)//收集所有的端粒
        {
            if(node instanceof LeafNode)//如果是叶子节点
            {
                int current_size = node.adjsOfNode.size();//叶子节点所有邻接的数量
                for(int i=0; i<current_size; i++)
                {
                    AdjOrTelomere current_adj = node.adjsOfNode.get(i);//拿到每一个邻接
                    if(current_adj.left==0)//如果这个邻接的左边是0，它很可能是开端（是否是编码过程中两个边缘都添加了0）
                        before_starts.add(current_adj);//将其存放到开端链表中
                    else if(current_adj.right==0)//右边为0，同样可能是开端
                    {
                        before_starts.add(current_adj);
                    }
                }
            }
        }
        int left;
        int right;//统计每一个端粒的信息，1如果在头步存为（0，1），尾同理
        for(AdjOrTelomere current : before_starts)//对于每一个可能是开端的节点
        {
            left = current.left;
            right = current.right;
            if(left==0)//考虑完了左边是0的
            {
                int i;
                for(i=0; i<infoOfStarts.size(); i++)
                {
                    if(infoOfStarts.get(i)[0]== Math.abs(right))//第0个元素存的是开头的基因
                    {
                        if(right>0)
                        {
                            infoOfStarts.get(i)[1]++;//作为开端的次数
                            break;
                        }
                        if(right<0)
                        {
                            infoOfStarts.get(i)[2]++;//作为结尾的次数
                            break;
                        }
                    }
                }
                if(i==infoOfStarts.size())//在infoOFstart中没有找到哪个元素是和right是相等的
                {
                    int[] anew = new int[5];
                    anew[0] = Math.abs(right);
                    anew[1]=0; anew[2]=0; anew[3]=0; anew[4]=0;
                    if(right>0)
                        anew[1] = 1;
                    else
                        anew[2] = 1;
                    infoOfStarts.add(anew);
                }
            }
            if(right==0)//考虑右边是0的
            {
                int i;
                for(i=0; i<infoOfStarts.size(); i++)
                {
                    if(infoOfStarts.get(i)[0]==Math.abs(left))
                    {
                        if(left>0)
                        {
                            infoOfStarts.get(i)[3]++;
                            break;
                        }
                        else
                        {
                            infoOfStarts.get(i)[4]++;
                            break;
                        }
                    }
                }
                if(i==infoOfStarts.size())
                {
                    int[] anew = new int[5];
                    anew[0] = Math.abs(left);
                    anew[1] = 0; anew[2] = 0; anew[3] = 0; anew[4] = 0;
                    if(left>0)
                        anew[3] = 1;
                    else
                        anew[4] = 1;
                    infoOfStarts.add(anew);
                }
            }
        }
        for(int i=0; i<infoOfStarts.size(); i++)//对最初的开端集进行筛选，再看一看
        {
            int[] current = infoOfStarts.get(i);
            int maxIndex1 = 0, maxIndex2 = 0, maxIndex = 0;
            if(current[1]>current[2])
                maxIndex1 = 1;
            else
                maxIndex1 = 2;
            if(current[3]>current[4])
                maxIndex2 = 3;
            else
                maxIndex2 = 4;
            if(current[maxIndex1]>current[maxIndex2])
                maxIndex = maxIndex1;
            else
                maxIndex = maxIndex2;
            //有条件的加东西
            if(maxIndex==1 || maxIndex==4)//
                starts.add(new AdjOrTelomere(0, current[0], ""));
            else if(maxIndex==2 || maxIndex==3)
                starts.add(new AdjOrTelomere(0, -current[0], ""));
        }

        before_starts.clear();  before_starts = null; // free
        infoOfStarts.clear();   infoOfStarts = null;  // free


        List<List<Integer>> paths = new ArrayList<>();
        List<Integer> first = new ArrayList<>();
        List<Integer> nexts = new ArrayList<>();
        int size_nexts;
        List<List<Integer>> newAdded = new ArrayList<>();
        List<Integer> willBeRemoved = new ArrayList<>();
        List<List<Integer>> tempPaths = new ArrayList<>();
        int currentSizeOfPaths;
        boolean expanded;
        List<List<Integer>> mostGenesPaths;
        List<Integer> theMostValues;
        int count = 0;
        int index;

        AdjOrTelomere endforGetNexts = null;
        int left_for_getNexts = 0;
        int right_for_getNexts = 0;
        int left1_for_getNexts = 0;
        int left2_for_getNexts = 0;
        int right1_for_getNexts = 0;
        int right2_for_getNexts = 0;
        List<PairAdjsWithStateProba> listOfPawsps_forGetNexts = null;


        int count_forGeneInPath = 0;
        int left_forGeneInPath = 0;
        int right_forGeneInPath = 0;


        List<Integer> genes = new ArrayList<>();
        int maxGeneNum = 0;
        List<Integer> indexsOfMaxGeneNumPath = new ArrayList<>();
        int count_forGetTheMostGensPaths = 0;
        int geneNumOfPath = 0;
        int size_indexsOfMaxGeneNumPath = 0;

        AdjOrTelomere former = null;
        AdjOrTelomere later = null;
        int path_size = 0;
        double result = 0;

        for(AdjOrTelomere currentStart : starts)//拿到每一个开端，从每一个开端开始找路径
        {
            first.add(currentStart.num);
            paths.add(first);//当前start出发的所有路径集合。
            expanded = true;
            while(expanded)//不断的扩展路径
            {
                expanded = false;
                index = -1;
                willBeRemoved.clear();
                newAdded.clear();
                tempPaths.clear();
                for(List<Integer> path : paths)
                {
                    index++;
                    nexts.clear();
                    endforGetNexts = null;
                    left_for_getNexts = 0;
                    right_for_getNexts = 0;
                    left1_for_getNexts = 0;
                    left2_for_getNexts = 0;
                    right1_for_getNexts = 0;
                    right2_for_getNexts = 0;
                    listOfPawsps_forGetNexts = null;
                    getNexts(path, nexts,
                            endforGetNexts, left_for_getNexts, right_for_getNexts, left1_for_getNexts, left2_for_getNexts, right1_for_getNexts, right2_for_getNexts,
                            listOfPawsps_forGetNexts,
                            count_forGeneInPath, left_forGeneInPath, right_forGeneInPath);
                    size_nexts = nexts.size();
                    if(size_nexts==0)//如果下一可连接的邻接对个数为0
                    {
                        willBeRemoved.add(index);
                        continue;
                    }
                    else if(size_nexts==1)//如果下一个可连接的邻接对个数为1
                    {
                        path.add(nexts.get(0));
                        expanded = true;
                    }
                    else // (nexts.size()>1)//有很多可以连接的邻接对
                    {
                        for(int k=1; k<size_nexts; k++)
                        {
                            List<Integer> temp = new ArrayList<>();
                            temp.addAll(path);
                            temp.add(nexts.get(k));
                            newAdded.add(temp);
                        }
                        path.add(nexts.get(0));
                        expanded = true;
                    }
                }
                // 一轮扩展结束
                if(expanded)
                {
                    if(willBeRemoved.size()>0)
                    {
                        currentSizeOfPaths = paths.size();
                        for(int i=0; i<currentSizeOfPaths; i++)
                        {
                            if(!willBeRemoved.contains(i))
                                tempPaths.add(paths.get(i));
                        }
                        paths.clear();
                        paths.addAll(tempPaths);
                    }
                    if(newAdded.size()>0)
                    {
                        paths.addAll(newAdded);
                    }
                }
            }//从当前start出发找的所有路径，已经完成
//654-668善后工作
            mostGenesPaths = getTheMostGensPaths(paths, genes, maxGeneNum, indexsOfMaxGeneNumPath, count_forGetTheMostGensPaths, geneNumOfPath, size_indexsOfMaxGeneNumPath);//从paths选出的最多的1或几条路径
            theMostValues = getTheMostValuePath(mostGenesPaths, former, later, path_size, result);//返回一条
            //transfromAndWrite(theMostValues);

            allpaths.add(theMostValues);//allpaths里存的是：从每一个开端出发的最优路径
            mostGenesPaths.clear();
            paths.clear();
            first.clear();
            count++;
        }
        starts.clear(); starts=null;
        nexts.clear();  nexts=null;
        newAdded.clear(); newAdded=null;
        willBeRemoved.clear();  willBeRemoved=null;
        tempPaths.clear();  tempPaths=null;

        mostGenesPaths = getTheMostGensPaths(allpaths, genes, maxGeneNum, indexsOfMaxGeneNumPath, count_forGetTheMostGensPaths, geneNumOfPath, size_indexsOfMaxGeneNumPath);  // 获得基因数最多的路径(一条或多条)
        theMostValues = getTheMostValuePath(mostGenesPaths, former, later, path_size, result);   // 获得累积概率最多的一条路径
        transfromAndWrite(theMostValues);//将路径转化为基因组，写道文件中
        allpaths.clear(); allpaths = null;
        mostGenesPaths.clear();; mostGenesPaths = null;
        theMostValues.clear(); theMostValues = null;
    }

    public static void tranformPath(List<Integer> intPath, List<AdjOrTelomere> adjPath)
    {
        for(int current : intPath)
        {
            adjPath.add(currentNode.adjsOfNode.get(current));
        }
    }

    public static void transfromAndWrite(List<Integer> finalPathInt)
    {
        StringBuilder sb = new StringBuilder();
        List<AdjOrTelomere> finalPathAdj = new ArrayList<>();
        tranformPath(finalPathInt, finalPathAdj);
        sb.append(">" + currentNode.name + "\n");

        AdjOrTelomere[] array_finalPath = new AdjOrTelomere[finalPathAdj.size()];
        finalPathAdj.toArray(array_finalPath);
        int length_array_finalPath = array_finalPath.length;
        AdjOrTelomere currentPoint = null;
        for(int i=0; i<length_array_finalPath; i++)
        {
            currentPoint = array_finalPath[i];
            if(currentPoint.right!=0)
            {
                sb.append(currentPoint.right + " ");
                if(i==length_array_finalPath-1)
                    sb.append("$");
            }
            else
                sb.append("$" + "\n");
        }
        FileUtils.WriteToFile(outPath + "/RAGPM_Reconstructed_" + nodeNum , sb.toString(), false);
    }

    public static List<List<Integer>> getTheMostGensPaths(List<List<Integer>> paths, List<Integer> genes,
                                                                int maxGeneNum, List<Integer> indexsOfMaxGeneNumPath,
                                                                int count, int geneNumOfPath,
                                                                int size_indexsOfMaxGeneNumPath)
    {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        maxGeneNum = 0;
        indexsOfMaxGeneNumPath.clear();
        count = 0;
        for(List<Integer> path : paths)
        {
            geneNumOfPath = path.size();
            if(geneNumOfPath>maxGeneNum)
            {
                indexsOfMaxGeneNumPath.clear();
                indexsOfMaxGeneNumPath.add(count);
            }
            else if(geneNumOfPath==maxGeneNum)
            {
                indexsOfMaxGeneNumPath.add(count);
            }
            count++;
        }
        size_indexsOfMaxGeneNumPath = indexsOfMaxGeneNumPath.size();
        for(int i=0; i<size_indexsOfMaxGeneNumPath; i++)
        {
            result.add(paths.get(indexsOfMaxGeneNumPath.get(i)));
        }
        return result;
    }

    public static List<Integer> getTheMostValuePath(List<List<Integer>> paths, AdjOrTelomere former, AdjOrTelomere later, int path_size, double result)
    {
        int size_paths = paths.size();
        int maxValueIndex = 0;
        double maxValue = 0;
        double currentValue;
        for(int i=0; i<size_paths; i++)
        {
            currentValue = getValuesInPath(paths.get(i), former, later, path_size, result);
            if(currentValue>maxValue)
            {
                maxValueIndex = i;
                maxValue = currentValue;
            }
        }
        //System.out.println("\n" + "maxValue : " + maxValue + "\n");
        return paths.get(maxValueIndex);
    }

    public static void getNexts(List<Integer> path, List<Integer> nexts,
                                AdjOrTelomere end, int left, int right, int left1, int left2, int right1, int right2,
                                List<PairAdjsWithStateProba> listOfPawsps,
                                int count_forGeneInPath, int left_forGeneInPath, int right_forGeneInPath)
    {
       // System.out.println(currentNode.name);//测试1到3出现问题，测试2到2出现问题
        end = currentNode.adjsOfNode.get(path.get(path.size()-1));//当前节点的所有邻接的其中一个
        left = end.left;
        right = end.right;
        if(left==0)
        {
            if(end.num>=classifiedPawsps.size())
            {
                for(int[] a : orginAndNew)
                {
                    if(a[1]==end.num)
                    {
                        listOfPawsps = classifiedPawsps.get(a[0]).get(0);
                        break;
                    }
                }
            }
            else
                listOfPawsps = classifiedPawsps.get(end.num).get(0);
            for(PairAdjsWithStateProba pawsp :  listOfPawsps)
            {
//                if((pawsp.p_11 + pawsp.p_11d)<=0.2)
//                    continue;
                if(isEqual(pawsp.adj_1, end))
                {
                    left2 = pawsp.adj_2.left;
                    right2 = pawsp.adj_2.right;
                    if(left2==right)
                    {
                        if(!geneInPath(path, right2, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath))
                        {
                            nexts.add(pawsp.adj_2.num);
                            continue;
                        }
                    }
                    else if(right2==-right)
                    {
                        if(!geneInPath(path, left2, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath))
                        {
                            AdjOrTelomere aNew = new AdjOrTelomere(right, -left2, "");
                            aNew.num = currentNode.adjsOfNode.size();
                            currentNode.adjsOfNode.add(aNew);
                            nexts.add(aNew.num);
                            orginAndNew.add(new int[]{pawsp.adj_2.num, aNew.num});
                            continue;
                        }
                    }
                }
                else if(isEqual(pawsp.adj_2, end))
                {
                    left1 = pawsp.adj_1.left;
                    right1 = pawsp.adj_1.right;
                    if(left1==right)
                    {
                        if(!geneInPath(path, right1, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath))
                        {
                            nexts.add(pawsp.adj_1.num);
                            continue;
                        }
                    }
                    else if(right1==-right)
                    {
                        if(!geneInPath(path, left1, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath))
                        {
                            AdjOrTelomere aNew = new AdjOrTelomere(right, -left1, "");
                            aNew.num = currentNode.adjsOfNode.size();
                            currentNode.adjsOfNode.add(aNew);
                            nexts.add(aNew.num);
                            orginAndNew.add(new int[]{pawsp.adj_1.num, aNew.num});
                            continue;
                        }
                    }
                }
            }
            removeDuplicate(nexts);
        }
        else if(right==0)
        {
            if(end.num>=classifiedPawsps.size())
            {
                for(int[] a : orginAndNew)
                {
                    if(a[1]==end.num)
                    {
                        listOfPawsps = classifiedPawsps.get(a[0]).get(1);
                        break;
                    }
                }
            }
            else
                listOfPawsps = classifiedPawsps.get(end.num).get(1);
            for(PairAdjsWithStateProba pawsp :  listOfPawsps)
            {
//                if((pawsp.p_11 + pawsp.p_11d)<=0.2)
//                    continue;
                if(isEqual(pawsp.adj_1, end))
                {
                    left2 = pawsp.adj_2.left;
                    right2 = pawsp.adj_2.right;
                    if(left2==0)
                    {
                        if(!geneInPath(path, right2,  count_forGeneInPath, left_forGeneInPath, right_forGeneInPath))
                        {
                            nexts.add(pawsp.adj_2.num);
                            continue;
                        }
                    }
                    else if(right2==0)
                    {
                        if(!geneInPath(path, left2, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath))
                        {
                            AdjOrTelomere aNew = new AdjOrTelomere(0, -left2, "");
                            aNew.num = currentNode.adjsOfNode.size();
                            currentNode.adjsOfNode.add(aNew);
                            nexts.add(aNew.num);
                            orginAndNew.add(new int[]{pawsp.adj_2.num, aNew.num});
                            continue;
                        }
                    }
                }
                else if(isEqual(pawsp.adj_2, end))
                {
                    left1 = pawsp.adj_1.left;
                    right1 = pawsp.adj_1.right;
                    if(left1==0)
                    {
                        if(!geneInPath(path, right1, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath))
                        {
                            nexts.add(pawsp.adj_1.num);
                            continue;
                        }
                    }
                    else if(right1==0)
                    {
                        if(!geneInPath(path, left1,  count_forGeneInPath, left_forGeneInPath, right_forGeneInPath))
                        {
                            AdjOrTelomere aNew = new AdjOrTelomere(0, -left1, "");
                            aNew.num = currentNode.adjsOfNode.size();
                            currentNode.adjsOfNode.add(aNew);
                            nexts.add(aNew.num);
                            orginAndNew.add(new int[]{pawsp.adj_1.num, aNew.num});
                            continue;
                        }
                    }
                }
            }
            removeDuplicate(nexts);
        }
        else
        {
            if(end.num>=classifiedPawsps.size())
            {
                for(int[] a : orginAndNew)
                {
                    if(a[1]==end.num)
                    {
                        listOfPawsps = classifiedPawsps.get(a[0]).get(0);
                        break;
                    }
                }
            }
            else
                listOfPawsps = classifiedPawsps.get(end.num).get(0);
            for(PairAdjsWithStateProba pawsp :  listOfPawsps)
            {
//                if((pawsp.p_11 + pawsp.p_11d)<=0.2)
//                    continue;
                if(isEqual(pawsp.adj_1, end))
                {
                    left2 = pawsp.adj_2.left;
                    right2 = pawsp.adj_2.right;
                    if(left2==right)
                    {
                        if(!geneInPath(path, right2, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath))
                        {
                            nexts.add(pawsp.adj_2.num);
                            continue;
                        }
                    }
                    else if(right2==-right)
                    {
                        if(!geneInPath(path, left2, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath))
                        {
                            AdjOrTelomere aNew = new AdjOrTelomere(right, -left2, "");
                            aNew.num = currentNode.adjsOfNode.size();
                            currentNode.adjsOfNode.add(aNew);
                            nexts.add(aNew.num);
                            orginAndNew.add(new int[]{pawsp.adj_2.num, aNew.num});
                            continue;
                        }
                    }
                }
                else if(isEqual(pawsp.adj_2, end))
                {
                    left1 = pawsp.adj_1.left;
                    right1 = pawsp.adj_1.right;
                    if(left1==right)
                    {
                        if(!geneInPath(path, right1, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath))
                        {
                            nexts.add(pawsp.adj_1.num);
                            continue;
                        }
                    }
                    else if(right1==-right)
                    {
                        if(!geneInPath(path, left1, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath))
                        {
                            AdjOrTelomere aNew = new AdjOrTelomere(right, -left1, "");
                            aNew.num = currentNode.adjsOfNode.size();
                            currentNode.adjsOfNode.add(aNew);
                            nexts.add(aNew.num);
                            orginAndNew.add(new int[]{pawsp.adj_1.num, aNew.num});
                            continue;
                        }
                    }
                }
            }
            removeDuplicate(nexts);
        }
    }

    public static int getChromoNumInPath(List<AdjOrTelomere> path)
    {
        int preResult = 0;

        AdjOrTelomere[] pathArray = new AdjOrTelomere[path.size()];
        path.toArray(pathArray);

        for(AdjOrTelomere current : pathArray)
        {
            if(current.left==0)
            {
                preResult++;
            }
            if(current.right==0)
            {
                preResult++;
            }
        }
        return preResult/2;
    }

    public static double getValuesInPath(List<Integer> path, AdjOrTelomere former, AdjOrTelomere latter, int path_size, double result)
    {
        result = 0;
        path_size = path.size();
        boolean b1, b2;
        for(int i=0; i<path_size-1; i++)
        {
            former = currentNode.adjsOfNode.get(path.get(i));
            latter = currentNode.adjsOfNode.get(path.get(i+1));
            for(PairAdjsWithStateProba current : array_pawsps)
            {
                b1 = ((isEqual(former, current.adj_1)) && (isEqual(latter, current.adj_2)));
                b2 = ((isEqual(former, current.adj_2)) && (isEqual(latter, current.adj_1)));
                if(b1||b2)
                {
                    result += (current.p_11>0 ? current.p_11 : current.p_11d);
                    break;
                }
            }
        }
        return result;
    }

    public static boolean geneInPath(List<Integer> path, int gene, int count, int left, int right)
    {
        if(gene==0)
            return false;
        count = 0;
        for(int current : path)
        {
            AdjOrTelomere adj = currentNode.adjsOfNode.get(current);
            left = adj.left;
            right = adj.right;
            if(left==gene)
                break;
            else if(left==-gene)
                break;
            else if(right==gene)
                break;
            else if(right==-gene)
                break;
            else
                count++;
        }
        if(count==path.size())
            return false;
        else
            return true;
    }

    public static void getStateList(List<String> stateList, AdjOrTelomere adj1, AdjOrTelomere adj2, int m, int n, int count)
    {
        stateList.clear();
        for(TreeNode current : Tree.tree)
        {
            if(current.children.size()==0)
            {
                stateList.add(getStateAtNode(current, adj1, adj2, m, n, count));
            }
        }
    }

    public static String getStateAtNode(TreeNode treeNode, AdjOrTelomere adj_1, AdjOrTelomere adj_2, int m, int n, int count)
    {
        m=-1;
        n=-1;
        count=0;
        for(AdjOrTelomere current : treeNode.adjsOfNode)
        {
            if(isEqual(adj_1, current))
                m = count;
            if(isEqual(adj_2, current))
                n = count;
            if(m!=-1 && n!=-1)
                break;
            count++;
        }
        if(m==-1 && n==-1)
            return "0,0";
        else if(m==-1 && n!=-1)
            return "0,1";
        else if(m!=-1 && n==-1)
            return "1,0";
        else if(treeNode.adjsOfNode.get(m).fromWhere.equals(treeNode.adjsOfNode.get(n).fromWhere))
            return "1,1";
        else
            return "1,1d";
    }

    public static List<AdjOrTelomere> dealSeqs(List<AdjOrTelomere> seq1, List<AdjOrTelomere> seq2)
    {
        List<AdjOrTelomere> result = new ArrayList<>();
        List<Integer> allRight = new ArrayList<>();

        AdjOrTelomere[] array_seq1 = new AdjOrTelomere[seq1.size()];
        seq1.toArray(array_seq1);
        AdjOrTelomere[] array_seq2 = new AdjOrTelomere[seq2.size()];
        seq2.toArray(array_seq2);
        int length_array_seq1 = array_seq1.length;
        int length_array_seq2 = array_seq2.length;

        for(int i=0; i<length_array_seq1; i++)
        {
            int currentRight = array_seq1[i].right;
            if(!allRight.contains(currentRight))
            {
                allRight.add(currentRight);
            }
        }
        for(int i=0; i<length_array_seq2; i++)
        {
            int currentRight = array_seq2[i].right;
            if(!allRight.contains(currentRight))
            {
                allRight.add(currentRight);
            }
        }
        int size_allRight = allRight.size();
        for(int i=0; i<size_allRight; i++)
        {
            int currentRight = allRight.get(i);
            List<Integer> leftsOfPre1 = new ArrayList<>();
            for(int j=0; j<length_array_seq1; j++)
            {
                AdjOrTelomere current = array_seq1[j];
                if(current.right==currentRight)
                {
                    if(!leftsOfPre1.contains(current.left))
                        leftsOfPre1.add(current.left);
                }
            }
            List<Integer> leftsOfPre2 = new ArrayList<>();
            for(int j=0; j<length_array_seq2; j++)
            {
                AdjOrTelomere current = array_seq2[j];
                if(current.right==currentRight)
                {
                    if(!leftsOfPre2.contains(current.left))
                        leftsOfPre2.add(current.left);
                }
            }
            List<Integer> intersection = getIntersection(leftsOfPre1, leftsOfPre2);
            if(intersection==null)
            {
                intersection = new ArrayList<Integer>();
                intersection.addAll(leftsOfPre1);
                intersection.addAll(leftsOfPre2);
            }
            int size_intersection = intersection.size();
            for(int j=0; j<size_intersection; j++)
            {
                result.add(new AdjOrTelomere(intersection.get(j), currentRight, ""));
            }
        }
        return result;
    }

    public static List<AdjOrTelomere> dealPreAndSuc(List<AdjOrTelomere> pre, List<AdjOrTelomere> suc)
    {
        List<AdjOrTelomere> result = new ArrayList<>();
        List<Integer> allRight = new ArrayList<>();

        AdjOrTelomere[] array_pre = new AdjOrTelomere[pre.size()];
        AdjOrTelomere[] array_suc = new AdjOrTelomere[suc.size()];
        int length_array_pre = array_pre.length;
        int length_array_suc = array_suc.length;

        for(int i=0; i<length_array_pre; i++)
        {
            int currentRight = array_pre[i].right;
            if(!allRight.contains(currentRight))
            {
                allRight.add(currentRight);
            }
        }
        for(int i=0; i<length_array_suc; i++)
        {
            int currentRight = array_suc[i].right;
            if(!allRight.contains(currentRight))
            {
                allRight.add(currentRight);
            }
        }
        int size_allRight = allRight.size();
        for(int i=0; i<size_allRight; i++)
        {
            int currentRight = allRight.get(i);
            List<Integer> leftsOfPre = new ArrayList<>();
            for(int j=0; j<length_array_pre; j++)
            {
                AdjOrTelomere current = array_pre[j];
                if(current.right==currentRight)
                {
                    if(!leftsOfPre.contains(current.left))
                        leftsOfPre.add(current.left);
                }
            }
            List<Integer> leftsOfSuc = new ArrayList<>();
            for(int j=0; j<length_array_suc; j++)
            {
                AdjOrTelomere current = array_suc[j];
                if(current.right==currentRight)
                {
                    if(!leftsOfSuc.contains(current.left))
                        leftsOfSuc.add(current.left);
                }
            }
            // 接下来取 leftOfPre 和 leftOfSuc 的 (完全)相交集
            List<Integer> intersection = getIntersection(leftsOfPre, leftsOfSuc);
            if(intersection==null)
            {
                continue;
            }
            else
            {
                int size_intersection = intersection.size();
                for(int j=0; j<size_intersection; j++)
                {
                    result.add(new AdjOrTelomere(intersection.get(j), currentRight, ""));
                }
            }
        }
        return result;
    }

    public static List<Integer> getIntersection(List<Integer> ints1, List<Integer> ints2)
    {
        Integer[] array_ints1 = new Integer[ints1.size()];
        ints1.toArray(array_ints1);
        Integer[] array_ints2 = new Integer[ints2.size()];
        ints2.toArray(array_ints2);
        int length_array_ints1 = array_ints1.length;
        int length_array_ints2 = array_ints2.length;

        if(length_array_ints1==0 || length_array_ints2==0)
        {
            return null;
        }
        List<Integer> intersection = new ArrayList<>();
        for(int i=0; i<length_array_ints1; i++)
        {
            int current1 = array_ints1[i];
            for(int j=0; j<length_array_ints2; j++)
            {
                int current2 = array_ints2[j];
                if(current1==current2)
                {
                    intersection.add(current1);
                    break;
                }
            }
        }
        if(intersection.size()!=0)
            return intersection;
        else
            return null;
    }

    public static void removeDuplicate(List<Integer> before)
    {
        List<Integer> after = new ArrayList<>();

        for(int current1 : before)
        {
            int count = 0;
            for(int current2 : after)
            {
                if(current1==current2)
                    break;
                count++;
            }
            if(count==after.size())
                after.add(current1);
        }
        before.clear();
        before.addAll(after);
        after.clear();
    }

    public static void removeDuplicateAdjs(List<AdjOrTelomere> before)
    {
        List<AdjOrTelomere> after = new ArrayList<>();

        AdjOrTelomere[] array_before = new AdjOrTelomere[before.size()];
        before.toArray(array_before);
        for(AdjOrTelomere current1 : array_before)
        {
            int count = 0;
            for(AdjOrTelomere current2 : after)
            {
                if(isEqual(current1, current2))
                    break;
                count++;
            }
            if(count==after.size())
                after.add(current1);
        }
        before.clear();
        before.addAll(after);
        after.clear();
    }

    public static boolean isEqual(AdjOrTelomere adj1, AdjOrTelomere adj2)
    {
        if(adj1.toString().equals(adj2.toString()))
            return true;
        else if((adj1.left==(-adj2.right)) && (adj1.right==(-adj2.left)))
            return true;
        else
            return false;
    }

    public static void back()
    {
        currentNode = null;
        nodeNum = 0;
        outPath = "";
        pawsps.clear();
        classifiedPawsps.clear();
        array_pawsps = null;
        length_array_pawsps = 0;
        orginAndNew.clear();
//        minChromoNum = 0;
//        maxChromoNum = 0;
    }
}
