package reinplement;
import miller.*;

import java.io.*;
import java.util.*;

public class CSolveBranchNode//解决一个分支节点的问题
{

    public TreeNode currentNode = null;//当前的节点
    public String outPath = "";//重建出节点的输出路径
    public int nodeNum = 0;//节点的编号
    public String cinPath = "";//输入叶子节点文件的路径

    public List<List<List<PairAdjsWithStateProba>>> classifiedPawsps = new ArrayList<>();

    public List<PairAdjsWithStateProba> pawsps = new ArrayList<>();
    public PairAdjsWithStateProba[] array_pawsps;
    public int length_array_pawsps = 0;

    public List<int[]> orginAndNew = new ArrayList<>();


    public CSolveBranchNode(TreeNode the_currentNode, String the_outPath, int the_nodeNum,String cin) throws IOException//三个参数的传递
    {
        currentNode = the_currentNode;
        outPath = the_outPath;
        nodeNum = the_nodeNum;
        cinPath = cin;
    }

    //判断某个邻接是否含有指定的基因
    public static int whetherHaveAGenes(int gene,AdjOrTelomere adj)
    {
        if(Math.abs(adj.left)==Math.abs(gene)||Math.abs(adj.right)==Math.abs(gene))
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    //判断两个邻接是否可连接
    public static int  whetherConnectable(AdjOrTelomere adj1,AdjOrTelomere adj2)
    {
        if((whetherHaveAGenes(adj1.left,adj2)==1)||(whetherHaveAGenes(adj1.right,adj2)==1))
        {
            return 1;
        }
        return 0;
    }

    public void solve1(CTree Tree) throws IOException {

        //System.out.println("节点的编号"+nodeNum);


        final List<AdjOrTelomere> allAdjs = currentNode.adjsOfNode;


        if(currentNode.name.equals("newRoot") == false)//如果是树的最初的根节点
        {
            getCurrentAdjs(Tree);//得到当前节点的所有邻接

            writeTreeStructure_0();


           // modifyCurrentAdjs(currentNode.adjsOfNode);//确实将指定的邻接进行了删除

            //System.out.println("当前节点的名字"+currentNode.name);
            //System.out.println("邻接链表的长度是"+allAdjs.size());
        }
        else
        {
            Tree.tree = Tree.getNewNodeList(currentNode);//新的树节点链表

            //得到当前节点的内容推断结果
            getCurrentAdjs(Tree);//得到当前节点的所有邻接
            writeTreeStructure();

           // modifyCurrentAdjs(currentNode.adjsOfNode);//确实将指定的邻接进行了删除,也会改变邻接链表的长度


            // String[] allgenes = encode.findAllGenes();
            //int [] allstate =Main.nishi();
        }
        //System.out.println("当前节点的所有邻接"+Arrays.toString(new List[]{allAdjs}).toString() + "*");

        getPawsps(Tree);//得到当前节点所有邻接对的所有状态的初始概率

        //System.out.println("邻接对链表的长度："+ pawsps.size());

        getEndPawsps();//得到最终的每个邻接对的每种状态的概率

        // long time1= System.currentTimeMillis();
        getPathFromPawsps(Tree);//找路径，这里已经将路径转化为基因的顺序，并写入文件了
//        long time2 = System.currentTimeMillis();
//        System.out.println(time2-time1);



    }
//    long time_02 = System.currentTimeMillis();
//    System.out.println(time_02-time_01+"&");

    //函数，根据内容推断的结果修改当前节点的邻接集合
    public void modifyCurrentAdjs(List<AdjOrTelomere> before) throws IOException {
        String[] allgenes = encode.findAllGenes(cinPath);//获取当前进化树的所有基因

        encode.encodeAllandWrite(cinPath,outPath+"pastml.csv");

        //String commands = "pastml --tree E:\\研究生论文\\program\\RAGPM\\data\\newick_Tree_"+nodeNum+" --data E:\\研究生论文\\program\\RAGPM\\data\\pastml.csv  --columns  --html_compressed D:\\Albanian_map.html  --data_sep ,";
        String commands = "pastml --tree "+outPath +"newick_Tree_"+nodeNum+" --data "+ outPath +"pastml.csv  --columns  --html_compressed D:\\Albanian_map.html  --data_sep ,";
        //System.out.println(commands);

        ExecutionOfcommands.doOneCommands(commands);

        int []allstate  = findAndAnalysis.allStates(nodeNum,cinPath,outPath);//传入一个参数指代要找的文件夹编号,第二个参数代表叶子基因组存放路径,第三个执行pastml输出结果的路径

//        System.out.println(Arrays.toString(allgenes));
//        System.out.println(Arrays.toString(allstate));
        //int [] allstate =Main.nishi();//获取当前进化树根节点的基因对应的状态列表
        List<Integer> gene = new LinkedList<>();
        for (int i = 0; i < allgenes.length; i++)
        {
            if(allstate[i]==0){gene.add(Integer.parseInt(allgenes[i]));}//如果找到状态为0的基因，将这个基因记录下来。
        }

        //打印一下状态链表
//        for (int i = 0; i < gene.size(); i++) {
//            System.out.println(gene.get(i));
//        }
        //遍历整个邻接链表，将状态为0的基因删除
//        for (int i= before.size()-1; i>=0; i--) {
//            for(int j=0;j<gene.size();j++)
//            {
//                if(whetherHaveAGenes(gene.get(j),before.get(i))==1)
//                {
//                    System.out.println(Arrays.toString(before.toArray()));
//                    System.out.println(before.get(i));
//
//                    before.remove(i);
//                    System.out.println(before.size()+"*");
//                }
//            }
//        }
//遍历整个邻接链表，将状态为0的基因删除
        for (int i = 0; i < gene.size() ; i++) {
            for (int j = before.size()-1; j >=0 ; j--) {
                if(whetherHaveAGenes(gene.get(i),before.get(j))==1)
                {
                    before.remove(j);
                }
            }
        }


        int num = 0;//不然啊，这个是邻接的编号，终于懂了
        for(AdjOrTelomere current : currentNode.adjsOfNode)
        {
            current.num = num;
            num++;
        }

    }

    //  long time_01 = System.currentTimeMillis();
    public void getCurrentPre(TreeNode treeNode)//得到当前节点的pre，要递归的找遍所有孩子的pre
    {
        //System.out.println(treeNode.name+"***");所有节点，包括叶子节点

        if (treeNode == null)
            return;
        if (!(treeNode instanceof LeafNode))
        {
            //System.out.println(treeNode.children.size()+"**");
            getCurrentPre(treeNode.children.get(0));
            getCurrentPre(treeNode.children.get(1));
            treeNode.pre.addAll(treeNode.children.get(0).pre);
            treeNode.pre.addAll(treeNode.children.get(1).pre);
            removeDuplicateAdjs(treeNode.pre);

        }
        else return;
    }

    public  void getCurrentSuc(TreeNode treeNode)
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

    public  void getCurrentAdjs(CTree Tree)
    {
        getCurrentPre(Tree.tree.get(0));//得到根节点的pre（除了尾邻接之外的邻接）
        getCurrentSuc(Tree.tree.get(0));//得到根节点的suc（除了头邻接之外的邻接）
        //currentNode.adjsOfNode.addAll(dealSeqs(currentNode.pre, currentNode.suc));
        currentNode.adjsOfNode.addAll(currentNode.pre);
        currentNode.adjsOfNode.addAll(currentNode.suc);
        removeDuplicateAdjs(currentNode.adjsOfNode);//去除相同的邻接


        int num = 0;//不然啊，这个是邻接的编号，终于懂了
        for(AdjOrTelomere current : currentNode.adjsOfNode)
        {
            current.num = num;
            num++;
        }
    }
//得到每个邻接对每种状态的最终概率

    public void getAdjs(CTree Tree) throws IOException {
        String leafGenomes_path ="./data//leafGenomes_for_RAGPM";
        

        List<AdjOrTelomere> list  = new LinkedList<>();//存放所有邻接的链表


        FileInputStream inputStream = new FileInputStream(leafGenomes_path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String str = null;
        while((str = bufferedReader.readLine()) != null)
        {
            if(str.startsWith(">")) //如果是叶子节点的名字，则将名字存储在数组中
            {
                continue;
            }
            else
            {

            }
        }
        inputStream.close();
        bufferedReader.close();
    }

    public  void getPawsps(CTree Tree)//得到所有邻接对的初始概率
    {
//        System.out.println("得到临界对时的树结构");
//        Tree.levelorder2(currentNode);
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
            adj1 = currentNode.adjsOfNode.get(i);//分别取邻接进行组合
            for(int j=i+1; j<size_adjsOfCurrentNode; j++) {
                if(whetherConnectable(adj1,currentNode.adjsOfNode.get(j))==1)
                {

                    adj2 = currentNode.adjsOfNode.get(j);

                    getStateList(stateList, adj1, adj2, m, n, count, Tree);//每个邻接对的状态链表。每一个元素：邻接对的状态（0，0）等

                    //每个邻接对的状态链表。每一个元素：邻接对的状态（0，0）等
//                    Long time2 = System.currentTimeMillis();
//                } else continue;
                    count0 = 0;//每一种状态的数量
                    count1 = 0;
                    count2 = 0;
                    count3 = 0;
                    count4 = 0;
                    for (String current : stateList)//统计每一种状态的个数
                    {
                        if (current.equals(nn))
                            count0++;
                        else if (current.equals(ny))
                            count1++;
                        else if (current.equals(yn))
                            count2++;
                        else if (current.equals(yy))
                            count3++;
                        else if (current.equals(yyd))
                            count4++;
                    }
                    int size = stateList.size();
                    p_00 = (double) count0 / size;//以每一种状态的个数 除以 所有邻接对状态链表的大小
                    p_01 = (double) count1 / size;//得到每种状态的初始概率
                    p_10 = (double) count2 / size;
                    p_11 = (double) count3 / size;
                    p_11d = (double) count4 / size;
//           if(p_11<0.1 && p_11d<0.1)
//              continue;

                    proba = new PairAdjsWithStateProba(adj1, adj2);//邻接对是每一种状态的概率各是多少
                    proba.p_00 = p_00;//这个邻接对是00的概率是多少
                    proba.p_01 = p_01;//
                    proba.p_10 = p_10;
                    proba.p_11 = p_11;
                    proba.p_11d = p_11d;
                    pawsps.add(proba);//将此已经知道各种状态对应概率的邻接对，放入到带有概率的邻接对链表中
                }
                else continue;
            }
        }
        //System.out.println(pawsps.size());//长度全部为1111
        stateList.clear();
        //System.out.println("邻接对链表的长度："+ pawsps.size());
        array_pawsps = new PairAdjsWithStateProba[pawsps.size()];
        pawsps.toArray(array_pawsps);
        length_array_pawsps = array_pawsps.length;
        pawsps.clear();
    }

    public  void getEndPawsps()//邻接对太多了，每种邻接又有五种状态
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
            List<PairAdjsWithStateProba> p11d = new ArrayList<>();//每一个元素都是带有状态概率的邻接对,所有的元素即为所有的带有状态概率的邻接对
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


        for(PairAdjsWithStateProba pawsp : array_pawsps)//得到初始概率函数的最后，得到的带有状态概率的邻接对链表
        {
            f[0] = pawsp.p_00;
            f[1] = pawsp.p_01;
            f[2] = pawsp.p_10;
            f[3] = pawsp.p_11;
            f[4] = pawsp.p_11d;

            //时间基本都反映在这两个函数中
            p_11 = get_P_state_Dx(currentNode, pawsp, yy, f, frequencyOfState, numerator, denominator, m, n, count, nn, ny, yn, yy, yyd);
            p_11d = get_P_state_Dx(currentNode, pawsp, yyd, f, frequencyOfState, numerator, denominator, m, n, count, nn, ny, yn, yy, yyd);
            pawsp.p_11 = p_11;
            pawsp.p_11d = p_11d;



            adj1 = pawsp.adj_1;//邻接对包含的两个邻接
            adj2 = pawsp.adj_2;
            if(p_11>=0.01)//如果邻接对为<1，1>状态的概率大于0.01
            {
                classifiedPawsps.get(adj1.num).get(0).add(pawsp);//测试之后果然嵌套链表的第二层长度始终为2
                classifiedPawsps.get(adj2.num).get(0).add(pawsp);
            }
            if(p_11d>=0.01)//邻接对状态为<1，1>‘的概率大于0.01
            {
                classifiedPawsps.get(adj1.num).get(1).add(pawsp);
                classifiedPawsps.get(adj2.num).get(1).add(pawsp);
            }
            pawsps.add(pawsp);//得到的是带有最终概率的邻接对链表（长度与array_pawsps相同）

        }
        array_pawsps = new PairAdjsWithStateProba[pawsps.size()];
        pawsps.toArray(array_pawsps);
        length_array_pawsps = array_pawsps.length;

        //打印邻接对概率
//        for(int i=0;i<length_array_pawsps;i++)
//        {
//            if(pawsps.get(i).p_11>0.01||pawsps.get(i).p_11d>0.01)
//            System.out.println("<"+"("+pawsps.get(i).adj_1.left+","+pawsps.get(i).adj_1.right+")"+
//                            "("+pawsps.get(i).adj_2.left+","+pawsps.get(i).adj_2.right+")"+ ">"+
//                    pawsps.get(i).p_11+"*"+pawsps.get(i).p_11d);
//        }
    }

    public  double getLS(TreeNode currentNode, PairAdjsWithStateProba pawsp, String state, double[] frequency,
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
        else                  // 当前节点是分支节点
        {
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
    }

   /*
   public  void getMinMaxChromoNum()
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

    public  double get_P_transform(String oldState, String newState, double t, String nn, String ny, String yn, String yy, String yyd)
    {//t参数代表的是分支长度
        Constant.setT(t);//设置分支长度
        Constant.setAlpha(1);//设置阿尔法值
        Constant.setBeta(2);//设置贝塔值
        Constant.getP();//设置转换矩阵
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

    public  double get_P_state_Dx(TreeNode currentNode , PairAdjsWithStateProba pawsp,
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
        if(frequencyOfState<0.0001) // ==0
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

    public  void getPathFromPawsps(CTree Tree) throws IOException//从邻接对中选择邻接组成最终的路径
    {
        int size = array_pawsps.length; //对的数量
//        for(int i=0;i<size;i++)
//        {
//            System.out.println("邻接对"+"<"+pawsps.get(i).adj_1.left+","+pawsps.get(i).adj_1.right+">"+
//                    "<"+pawsps.get(i).adj_2.left+","+pawsps.get(i).adj_2.right+">    "
//                     +pawsps.get(i).p_11+"&");
//        }

//        int [] allStates = findAndAnalysis.allStates(nodeNum);//获取当前节点所有基因的状态
//        String[] allgenes = encode.findAllGenes();//获取当前进化树的所有基因
//        int gene_length = allgenes.length;
//        int state_length = allStates.length;
//        List<String> overOnegenes = new LinkedList<>();//存放基因状态大于1的基因
//        //将大于基因状态大于1的基因取出来
//        for (int i = 0; i <gene_length ; i++) {
//            if(allStates[i]>1)
//                overOnegenes.add(allgenes[i]);
//        }
        //gai


        List<List<Integer>> allpaths = new ArrayList<>();//存放所有的路径，内层链表的每一个元素都是一个邻接，用编号表示
        //getMinMaxChromoNum();

        List<AdjOrTelomere> before_starts = new ArrayList<>();//所有的开端
        List<int[]> infoOfStarts = new ArrayList<>();//每一个开端可能存在的次数(每一个数组：right，（0，r）次数，（0，-r）次数，（r，0）次数，（-r,0）次数)
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
                        before_starts.add(current_adj);//存储的是所有的开端，下一步要对所有的开端进行筛选
                    }
                }
            }
        }
//        System.out.println("***********************");
//        for(AdjOrTelomere t: before_starts){
//            System.out.println(t.left + ":" + t.right + "-" + t.num);
//        }
        int left;
        int right;//统计每一个端粒的信息，1如果在头步存为（0，1），尾同理
        for(AdjOrTelomere current : before_starts)//对于每一个可能是开端的节点
        {
            left = current.left;
            right = current.right;
            if(left==0)//考虑完了左边是0的
            {
                int i;
                int size_01 = infoOfStarts.size();//改
                for(i=0; i<size_01; i++)
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
                if(i==size_01)//在infoOFstart中没有找到哪个元素是和right是相等的
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
                int size_01 = infoOfStarts.size();//改
                for(i=0; i<size_01; i++)
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
                if(i==size_01)
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

//打印初始邻接对集合
//        System.out.println("AAAAAAAAAAAAAAAAAAAAAA");
//        for(int[] i: infoOfStarts){
//            for(int j = 0; j < i.length; j++){
//                System.out.print(i[j] + " ");
//            }
//            System.out.println("\n");
//        }


        int size_01 = infoOfStarts.size();//改
        for(int i=0; i<size_01; i++)//对最初的开端集进行筛选，再看一看
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
            {
                int tempNum = 0;
                for (AdjOrTelomere adj : currentNode.adjsOfNode) {
                    if (adj.left == 0 && adj.right == current[0]) {
                        tempNum = adj.num;
                    }
                    else if(adj.right==0&&adj.left==-current[0])
                    {
                        tempNum = adj.num;
                    }
                }

                starts.add(new AdjOrTelomere(0, current[0], "",tempNum));
            }
            else if(maxIndex==2 || maxIndex==3) {
                int tempNum = 0;
                for (AdjOrTelomere adj : currentNode.adjsOfNode) {
                    if (adj.left == 0 && adj.right == -current[0]) {
                        tempNum = adj.num;
                    }
                    else if(adj.right==0&&adj.left==current[0])
                    {
                        tempNum = adj.num;
                    }
                }
                starts.add(new AdjOrTelomere(0, -current[0], "",tempNum));//筛选后的开端
            }
        }
//打印开端
//        for(AdjOrTelomere j :starts)
//        {
//            System.out.println(j.left+","+j.right+"   "+j.num);
//        }

        before_starts.clear();  before_starts = null; // free
        infoOfStarts.clear();  infoOfStarts = null;  // free


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
        int jishu = 0;


long time1 = System.currentTimeMillis();
        for(AdjOrTelomere currentStart : starts)//拿到每一个开端，从每一个开端开始找路径
        {

            first.add(currentStart.num);
            paths.add(first);//当前start出发的所有路径集合。
            expanded = true;
            while (expanded)//不断的扩展路径
            {
                expanded = false;
                index = -1;
                willBeRemoved.clear();
                newAdded.clear();
                tempPaths.clear();
                for (List<Integer> path : paths)//对于路径集合中的每一条路径
                {
                    index++;
                    nexts.clear();//int类型数组链表
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

                    //System.out.println(time2-time1);
                    //System.out.println(nexts.size()+"*");
                    size_nexts = nexts.size();
                    if (size_nexts == 0)//如果下一可连接的邻接对个数为0
                    {
                        willBeRemoved.add(index);
                        continue;
                    } else if (size_nexts == 1)//如果下一个可连接的邻接对个数为1
                    {

                        path.add(nexts.get(0));//直接将此邻接追加到路径上
                        expanded = true;
                    }
                    //这里是否需要选择两个概率最大的邻接对
                    else // (nexts.size()>1)//有很多可以连接的邻接对,测试才知道绝大多数都是只有两个可连接的邻接对
                    {
                        //System.out.println(nexts.size()+"***********");
                        //System.out.println(pawsps.get(nexts.get(0)).p_11);//是不是因为我将这个邻接对变少了导致的
//                        for (int k = 1; k < nexts.size(); k++) {
//                            List<Integer> temp = new ArrayList<>();
//                            temp.addAll(path);
//                            temp.add(nexts.get(k));
//                            newAdded.add(temp);
//                        }
//                         path.add(nexts.get(0));

//改动部分********************************************************************************************
                        //输出下一个邻接链表中，所有的邻接对的概率看一看



                        int max_index = 0;
                        double max_prob = 0;
                        int maxGeneContenet = 0;

                        for (int k = 0; k < nexts.size(); k++) {

                            int index_1 = path.get(path.size() - 1);
                            int index_2 = nexts.get(k);
                            AdjOrTelomere adj_1 = currentNode.adjsOfNode.get(index_1);
                            AdjOrTelomere adj_2 = currentNode.adjsOfNode.get(index_2);
                            for(PairAdjsWithStateProba p: array_pawsps) {
//                                System.out.println(currentNode.adjsOfNode.get(p.adj_1.num)+" "
//                                        +currentNode.adjsOfNode.get(adj_1.num)+" "+currentNode.adjsOfNode.get(p.adj_2.num)+" "
//                                        + currentNode.adjsOfNode.get(adj_2.num)+" AAAAAAAAAAAAAAAA");
//                                System.out.println(currentNode.adjsOfNode.get(p.adj_1.num)+" "
//                                        +currentNode.adjsOfNode.get(adj_1.num)+" "+currentNode.adjsOfNode.get(p.adj_2.num)+" "
//                                        + currentNode.adjsOfNode.get(adj_2.num)+" BBBBBBBBBBBBB");
                                if (p.adj_1.num == adj_1.num && p.adj_2.num == adj_2.num) {
//                                    System.out.println(path);
//                                    System.out.println(nexts);
//                                    System.out.println(currentNode.adjsOfNode.get(p.adj_1.num)+" "
//                                            +currentNode.adjsOfNode.get(adj_1.num)+" "+currentNode.adjsOfNode.get(p.adj_2.num)+" "
//                                            + currentNode.adjsOfNode.get(adj_2.num)+" BBBBBBBBBBBBB");
//                                    for(int i:nexts){
//                                        System.out.println("CASE1:"+currentNode.adjsOfNode.get(i));
//                                    }
                                    double prob = p.p_11;
                                    if (prob > max_prob) {
                                        max_prob = prob;
                                        max_index = k;
                                    }
                                }
                                else if(p.adj_1.num == adj_2.num && p.adj_2.num == adj_1.num)
                                {
//                                    System.out.println(currentNode.adjsOfNode.get(p.adj_1.num)+" "
//                                            +currentNode.adjsOfNode.get(adj_1.num)+" "+currentNode.adjsOfNode.get(p.adj_2.num)+" "
//                                            + currentNode.adjsOfNode.get(adj_2.num)+" BBBBBBBBBBBBB");
//                                    for(int i:nexts){
//                                        System.out.println("CASE2:"+currentNode.adjsOfNode.get(i) + "-" + i + "-" + p.adj_2.num);
//                                    }
//                                    for(AdjOrTelomere tem2adj:currentNode.adjsOfNode){
//                                        System.out.println(tem2adj.left+ "="+tem2adj.right+"="+tem2adj.num);
//                                    }
                                    double prob = p.p_11;
                                    if (prob > max_prob) {
                                        max_prob = prob;
                                        max_index = k;
                                    }
                                }
                                else if(isEqual(p.adj_1,adj_1)&&isEqual(p.adj_2,adj_2))
                                {
//                                    System.out.println(currentNode.adjsOfNode.get(p.adj_1.num)+" "
//                                            +currentNode.adjsOfNode.get(adj_1.num)+" "+currentNode.adjsOfNode.get(p.adj_2.num)+" "
//                                            + currentNode.adjsOfNode.get(adj_2.num)+" BBBBBBBBBBBBB");
//                                    for(int i:nexts){
//                                        System.out.println("CASE3:"+currentNode.adjsOfNode.get(i) + "-" + i + "-" + p.adj_2.num);
//                                    }
                                    double prob = p.p_11;
                                    if (prob > max_prob) {
                                        max_prob = prob;
                                        max_index = k;
                                    }
                                }
                                else if(isEqual(p.adj_1,adj_2)&&isEqual(p.adj_2,adj_1))
                                {
//                                    System.out.println(currentNode.adjsOfNode.get(p.adj_1.num)+" "
//                                            +currentNode.adjsOfNode.get(adj_1.num)+" "+currentNode.adjsOfNode.get(p.adj_2.num)+" "
//                                            + currentNode.adjsOfNode.get(adj_2.num)+" BBBBBBBBBBBBB");
//                                    for(int i:nexts){
//                                        System.out.println("CASE4:"+currentNode.adjsOfNode.get(i) + "-" + i + "-" + p.adj_2.num);
//                                    }
                                    double prob = p.p_11;
                                    if (prob > max_prob) {
                                        max_prob = prob;
                                        max_index = k;
                                    }
                                }
                            }
                        }
                        path.add(nexts.get(max_index));
//改动部分********************************************************************************

                        expanded = true;
                    }
                }
                // 一轮扩展结束
                if (expanded) {
                    if (willBeRemoved.size() > 0) {
                        currentSizeOfPaths = paths.size();
                        for (int i = 0; i < currentSizeOfPaths; i++) {
                            if (!willBeRemoved.contains(i))
                                tempPaths.add(paths.get(i));
                        }
                        paths.clear();
                        paths.addAll(tempPaths);
                    }
                    if (newAdded.size() > 0) {
                        paths.addAll(newAdded);
                    }
                }
            }//从当前start出发找的所有路径，已经完成
//654-668善后工作
            //打印路径
//            System.out.println("一共有多少条路径"+paths.size());
//            for (int i = 0; i < paths.size(); i++) {
//                System.out.println("+++++++++++++++++++++++++++++++");
//                for (int j = 0; j < paths.get(i).size(); j++) {
//                    System.out.print(currentNode.adjsOfNode.get(paths.get(i).get(j))+" ");
//                }
//                System.out.println(paths.get(i).size());
//            }
            //mostGenesPaths = getTheMostGensPaths(paths, genes, maxGeneNum, indexsOfMaxGeneNumPath, count_forGetTheMostGensPaths, geneNumOfPath, size_indexsOfMaxGeneNumPath);//从paths选出的最多的1或几条路径
            mostGenesPaths  = getTheMostGensPathsPro(paths);
            List<Integer> tempMV = new LinkedList<>();
            theMostValues = getTheMostValuePath(mostGenesPaths, former, later, path_size, result);//只有一条
            for(int i: theMostValues){
                tempMV.add(i);
            }
//            System.out.println("一个开端得到的概率之和最大的路径：");
//            for (int i = 0; i < tempMV.size(); i++) {
//                System.out.print(currentNode.adjsOfNode.get(tempMV.get(i))+" ");
//            }
            transfromAndWrite(tempMV);

            allpaths.add(tempMV);//allpaths里存的是：从每一个开端出发的最优路径
//            //每一次都打印一下aiallpaths里面的东西
//            for (int i = 0; i < allpaths.size(); i++) {
//                System.out.println(allpaths.get(i).size());
//                for (int j = 0; j < allpaths.get(i).size(); j++) {
//                    System.out.print(currentNode.adjsOfNode.get(allpaths.get(i).get(j))+" ");
//                }
//                System.out.println('\n');
//            }
            mostGenesPaths.clear();
            paths.clear();
            first.clear();
            count++;

        }

        long time2 = System.currentTimeMillis();
//        System.out.println("路径组装所用时间");
//        System.out.println(time2-time1);



        starts.clear(); starts=null;
        nexts.clear();  nexts=null;
        newAdded.clear(); newAdded=null;
        willBeRemoved.clear();  willBeRemoved=null;
        tempPaths.clear();  tempPaths=null;
//        System.out.println("一共有多少条路径"+allpaths.size());
//        for (int i = 0; i < allpaths.size(); i++) {
//            System.out.println("+++++++++++++++++++++++++++++++");
//            for (int j = 0; j < allpaths.get(i).size(); j++) {
//                System.out.print(currentNode.adjsOfNode.get(allpaths.get(i).get(j))+" ");
//            }
//            System.out.println(allpaths.get(i).size());
//        }
        //mostGenesPaths = getTheMostGensPaths(allpaths, genes, maxGeneNum, indexsOfMaxGeneNumPath, count_forGetTheMostGensPaths, geneNumOfPath, size_indexsOfMaxGeneNumPath);  // 获得基因数最多的路径(一条或多条)
        mostGenesPaths  = getTheMostGensPathsPro(allpaths);
        theMostValues = getTheMostValuePath(mostGenesPaths, former, later, path_size, result);  // 获得累积概率最多的一条路径

        //来看一下为什么会产生$这个符号
//        for (int i=0;i<theMostValues.size();i++) {
//            System.out.print(currentNode.adjsOfNode.get(theMostValues.get(i)));
//        }
//        System.out.println(" ");
        //
//        System.out.println("概率之和最大的路径：");
//        for (int i = 0; i < theMostValues.size(); i++) {
//            System.out.print(currentNode.adjsOfNode.get(theMostValues.get(i))+" ");
//        }
        transfromAndWrite(theMostValues);//将路径转化为基因组，写道文件中
        allpaths.clear(); allpaths = null;
        mostGenesPaths.clear();; mostGenesPaths = null;
        theMostValues.clear(); theMostValues = null;

    }

    //将邻接的编号列表变化为邻接的列表
    public  void tranformPath(List<Integer> intPath, List<AdjOrTelomere> adjPath)//参数1：邻接编号的列表
    {
        for(int current : intPath)
        {
            adjPath.add(currentNode.adjsOfNode.get(current));
        }
    }

    public  void transfromAndWrite(List<Integer> finalPathInt)
    {
        StringBuilder sb = new StringBuilder();
        List<AdjOrTelomere> finalPathAdj = new ArrayList<>();
        tranformPath(finalPathInt, finalPathAdj);

        //-37，0.可以正常转化为邻接
        sb.append(">" + currentNode.children.get(0).name + "\n");

        AdjOrTelomere[] array_finalPath = new AdjOrTelomere[finalPathAdj.size()];
        finalPathAdj.toArray(array_finalPath);
        int length_array_finalPath = array_finalPath.length;
        AdjOrTelomere currentPoint = null;
        for(int i=0; i<length_array_finalPath; i++)
        {
            currentPoint = array_finalPath[i];


            //改，自己加的，为了解决
            if(i==0&&currentPoint.right==0)
            {
               // sb.append(-currentPoint.left+" ");
                continue;
            }
            //
            if(currentPoint.right!=0)
            {
                sb.append(currentPoint.right + " ");
                if(i==length_array_finalPath-1)
                    sb.append("$");
            }
            else
                sb.append("$" + "\n");
        }


        // System.out.println(nodeNum+"*");//重建节点的顺序正确
        FileUtils.WriteToFile(outPath + "/RAGPM_Reconstructed_" + nodeNum, sb.toString(), false);

    }

    public  List<List<Integer>> getTheMostGensPaths(List<List<Integer>> paths, List<Integer> genes,
                                                    int maxGeneNum, List<Integer> indexsOfMaxGeneNumPath,
                                                    int count, int geneNumOfPath,
                                                    int size_indexsOfMaxGeneNumPath)
    {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        maxGeneNum = 0;
        indexsOfMaxGeneNumPath.clear();
        count = 0;

//********************************************************************//问题出现在这个区域之内
        for(List<Integer> path : paths)
        {
            geneNumOfPath = path.size();//三个11一个0
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
//*************************************************************************************8

        for(int i=0; i<size_indexsOfMaxGeneNumPath; i++)
        {
            result.add(paths.get(indexsOfMaxGeneNumPath.get(i)));
        }
        return result;
    }
    //修改后的得到包含基因最多的路径
    public  List<List<Integer>> getTheMostGensPathsPro(List<List<Integer>> paths)
    {
        List<List<Integer>> result = new LinkedList<>();
        int maxGeneNumber = 0;
        int count = 0;
        List<Integer> indexOfPath  = new LinkedList<>();
        int size_indexsOfMaxGeneNumPath = 0;

        for(List<Integer> path : paths)
        {
            if(path.size()>=maxGeneNumber)
            {
                indexOfPath.add(count);
                maxGeneNumber = path.size();
            }
            count++;
        }
        size_indexsOfMaxGeneNumPath = indexOfPath.size();

        for(int i=0; i<size_indexsOfMaxGeneNumPath; i++)
        {
            result.add(paths.get(indexOfPath.get(i)));
        }
        return  result;

    }
    //返回最大value值路径的下标
    public  List<Integer> getTheMostValuePath(List<List<Integer>> paths, AdjOrTelomere former, AdjOrTelomere later, int path_size, double result)
    {
        int size_paths = paths.size();
        int maxValueIndex = 0;
        double maxValue = 0;
        double currentValue;
        for(int i=0; i<size_paths; i++)//对于每一条路径
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

    //找下一个邻接对有多少？
    public  void getNexts(
            List<Integer> path,
            List<Integer> nexts,
            AdjOrTelomere end,
            int left,
            int right,
            int left1,
            int left2,
            int right1,
            int right2,
            List<PairAdjsWithStateProba> listOfPawsps,
            int count_forGeneInPath,
            int left_forGeneInPath,
            int right_forGeneInPath
    ) throws IOException {
        /**
         * path 当前的路径编码，具体是邻接下标
         * nexts 下一个邻接的下标
         * listOfPawsps 邻接对链表
         * count_forGeneInPath 路径中的基因个数
         *
         */


        // System.out.println(currentNode.name);//测试1到3出现问题，测试2到2出现问题
        end = currentNode.adjsOfNode.get(path.get(path.size()-1));//当前路径最后的一个邻接
        left = end.left;//最后一个邻接的左元素
        right = end.right;//最后一个邻接的右元素
        if(left==0)//是首部的端粒
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


            for(PairAdjsWithStateProba pawsp :  listOfPawsps)//对于每一个知道概率的邻接对
            {
//测试每一个基因的含量是可以正确得到的
//           if((pawsp.p_11 + pawsp.p_11d)<=0.2)
//              continue;
                if(isEqual(pawsp.adj_1, end))//如果邻接对的第一个邻接与当前路径的最后一个邻接相等//定位
                {
                    left2 = pawsp.adj_2.left;
                    right2 = pawsp.adj_2.right;
                    if(left2==right)//对应画的第一种情况
                    {
//                        int zhuangtai = 0;
//                        if(right2!=0)//对于0元素可以无线多
//                            zhuangtai = findAndAnalysis.analysisState(Integer.toString(Math.abs(right2)),nodeNum,outPath);
//                        else zhuangtai = 100;

                        //System.out.println(geneNumberInPath(path, 4, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath));

                        if(geneNumberInPath(path, right2, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath)<1)
                        {
                            nexts.add(pawsp.adj_2.num);
                            continue;
                        }
                    }
                    else if(right2==-right)
                    {
//                        int zhuangtai = 0;
//                        if(left2!=0)
//                            zhuangtai = findAndAnalysis.analysisState(Integer.toString(Math.abs(left2)),nodeNum,outPath);
//                        else zhuangtai = 100;

                        //System.out.println(geneNumberInPath(path, 4, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath));


                        if(geneNumberInPath(path, left2, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath)<1)
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
                else if(isEqual(pawsp.adj_2, end))//邻接对的右边邻接与当前路径最后一个邻接相同
                {
                    left1 = pawsp.adj_1.left;
                    right1 = pawsp.adj_1.right;
                    if(left1==right)
                    {
//                        int zhuangtai = 0;
//                        if(right1!=0)
//                            zhuangtai = findAndAnalysis.analysisState(Integer.toString(Math.abs(right1)),nodeNum,outPath);
//                        else zhuangtai = 100;

                        //System.out.println(geneNumberInPath(path, 4, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath));

                        if(geneNumberInPath(path, right1, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath)<1)
                        {
                            nexts.add(pawsp.adj_1.num);
                            continue;
                        }
                    }
                    else if(right1==-right)
                    {
//                        int zhuangtai = 0;
//                        if(left1!=0)
//                            zhuangtai = findAndAnalysis.analysisState(Integer.toString(Math.abs(left1)),nodeNum,outPath);
//                        else zhuangtai = 100;

                        //System.out.println(geneNumberInPath(path, 4, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath));

                        if(geneNumberInPath(path, left1, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath)<1)
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
        else if(right==0)//是尾部的端粒
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
//           if((pawsp.p_11 + pawsp.p_11d)<=0.2)
//              continue;
                if(isEqual(pawsp.adj_1, end))
                {
                    left2 = pawsp.adj_2.left;
                    right2 = pawsp.adj_2.right;
                    if(left2==0)
                    {
//                        int zhuangtai = 0;
//                        if(right2!=0)
//                            zhuangtai = findAndAnalysis.analysisState(Integer.toString(Math.abs(right2)),nodeNum,outPath);
//                        else zhuangtai = 100;

                        //System.out.println(geneNumberInPath(path, 4, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath));

                        if(geneNumberInPath(path, right2,  count_forGeneInPath, left_forGeneInPath, right_forGeneInPath)<1)
                        {
                            nexts.add(pawsp.adj_2.num);
                            continue;
                        }
                    }
                    else if(right2==0)
                    {
//                        int zhuangtai = 0;
//                        if(left2!=0)
//                            zhuangtai = findAndAnalysis.analysisState(Integer.toString(Math.abs(left2)),nodeNum,outPath);
//                        else zhuangtai = 100;

                        //System.out.println(geneNumberInPath(path, 4, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath));

                        if(geneNumberInPath(path, left2, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath)<1)
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
//                        int zhuangtai = 0;
//                        if(right1!=0)
//                            zhuangtai = findAndAnalysis.analysisState(Integer.toString(Math.abs(right1)),nodeNum,outPath);
//                        else zhuangtai = 100;

                        //System.out.println(geneNumberInPath(path, 4, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath));

                        if(geneNumberInPath(path, right1, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath)<1)
                        {
                            nexts.add(pawsp.adj_1.num);
                            continue;
                        }
                    }
                    else if(right1==0)
                    {
//                        int zhuangtai = 0;
//                        if(left1!=0)
//                            zhuangtai = findAndAnalysis.analysisState(Integer.toString(Math.abs(left1)),nodeNum,outPath);
//                        else zhuangtai = 100;

                        // System.out.println(geneNumberInPath(path, 4, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath));

                        if(geneNumberInPath(path, left1,  count_forGeneInPath, left_forGeneInPath, right_forGeneInPath)<1)
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
        else//既不是首部的端粒，也不是尾部的端粒
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
//           if((pawsp.p_11 + pawsp.p_11d)<=0.2)
//              continue;
                if(isEqual(pawsp.adj_1, end))
                {
                    left2 = pawsp.adj_2.left;
                    right2 = pawsp.adj_2.right;
                    if(left2==right)
                    {
//                        int zhuangtai = 0;
//                        if(right2!=0)
//                            zhuangtai = findAndAnalysis.analysisState(Integer.toString(Math.abs(right2)),nodeNum,outPath);
//                        else zhuangtai = 100;

                        // System.out.println(geneNumberInPath(path, 4, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath));

                        if(geneNumberInPath(path, right2, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath)<1)
                        {
                            nexts.add(pawsp.adj_2.num);
                            continue;
                        }
                    }
                    else if(right2==-right)
                    {
//                        int zhuangtai = 0;
//                        if(left2!=0)
//                            zhuangtai = findAndAnalysis.analysisState(Integer.toString(Math.abs(left2)),nodeNum,outPath);
//                        else zhuangtai = 100;

                        //System.out.println(geneNumberInPath(path, 4, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath));

                        if(geneNumberInPath(path, left2, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath)<1)
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
//                        int zhuangtai = 0;
//                        if(right1!=0)
//                            zhuangtai = findAndAnalysis.analysisState(Integer.toString(Math.abs(right1)),nodeNum,outPath);
//                        else zhuangtai = 100;

                        //System.out.println(geneNumberInPath(path, 4, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath));

                        if(geneNumberInPath(path, right1, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath)<1)
                        {
                            nexts.add(pawsp.adj_1.num);
                            continue;
                        }
                    }
                    else if(right1==-right)
                    {
//                        int zhuangtai = 0;
//                        if(left1!=0)
//                            zhuangtai = findAndAnalysis.analysisState(Integer.toString(Math.abs(left1)),nodeNum,outPath);
//                        else zhuangtai = 100;

                        //System.out.println(geneNumberInPath(path, 4, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath));

                        if(geneNumberInPath(path, left1, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath)<1)
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

//    public List<PairAdjsWithStateProba> getListOfPawsps(AdjOrTelomere end, List<PairAdjsWithStateProba> listOfPawsps, int pos){
//        if(end.num >= classifiedPawsps.size()) {
//            for(int[] a : orginAndNew) {
//                if(a[1]==end.num) {
//                    listOfPawsps = classifiedPawsps.get(a[0]).get(pos);
//                    break;
//                }
//            }
//        }
//        else listOfPawsps = classifiedPawsps.get(end.num).get(pos);
//        return listOfPawsps;
//    }
//
//    public int getGeneNumber(int gene) throws IOException {
//        /**
//         * 获取基因的数量，在基因组（所有的染色体）
//         */
//        int number = 0;
//        if(gene != 0)
//            number = findAndAnalysis.analysisState(Integer.toString(Math.abs(gene)),nodeNum,outPath);
//        else number = 100;
//        return number;
//    }
////
////    public int getGeneNumberInPath(){
////
////    }
////
//    public void addNext(AdjOrTelomere a, AdjOrTelomere b){
//        int left_1 = b.left;
//        int right_1 = b.right;
//        int left_2 = a.left;
//        int right_2 = a.right;
//        if(left_2 == right_1) { //对应画的第一种情况
//            int number = this.getGeneNumber(right2);
//            if(geneNumberInPath(path, right_2, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath) < number)
//            {
//                nexts.add(pawsp.adj_2.num);
//                continue;
//            }
//        }
//        else if(right_2==-right_1) {
//            int zhuangtai = this.getGeneNumber(left2);
//
//            //System.out.println(geneNumberInPath(path, 4, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath));
//            if(geneNumberInPath(path, left2, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath)<1)
//            {
//                AdjOrTelomere aNew = new AdjOrTelomere(right, -left2, "");
//                aNew.num = currentNode.adjsOfNode.size();
//                currentNode.adjsOfNode.add(aNew);
//                nexts.add(aNew.num);
//                orginAndNew.add(new int[]{pawsp.adj_2.num, aNew.num});
//            }
//        }
//    }
//
//    public List<Integer> getNextLeft(AdjOrTelomere end, List<PairAdjsWithStateProba> listOfPawsps) throws IOException {
//        // 获得所有符合条件的邻接对
//        List<Integer> nexts = new ArrayList<>();
//        listOfPawsps = this.getListOfPawsps(end, listOfPawsps, 0);
//        for(PairAdjsWithStateProba pawsp :  listOfPawsps) { //对于每一个知道概率的邻接对
//            if(isEqual(pawsp.adj_1, end)) { //如果邻接对的第一个邻接与当前路径的最后一个邻接相等//定位
//                addNext(pawsp.adj_1, end);
//            }
//            else if(isEqual(pawsp.adj_2, end))//邻接对的右边邻接与当前路径最后一个邻接相同
//            {
//                addNext(pawsp.adj_2, end);
//            }
//        }
//        removeDuplicate(nexts);
//        return nexts;
//    }
//
//    public void getNextRight(AdjOrTelomere end, List<PairAdjsWithStateProba> listOfPawsps){
//        // 获得所有符合条件的邻接对
//        listOfPawsps = this.getListOfPawsps(end, listOfPawsps, 1);
//        for(PairAdjsWithStateProba pawsp :  listOfPawsps)//对于每一个知道概率的邻接对
//        {
////测试每一个基因的含量是可以正确得到的
////           if((pawsp.p_11 + pawsp.p_11d)<=0.2)
////              continue;
//            if(isEqual(pawsp.adj_1, end))//如果邻接对的第一个邻接与当前路径的最后一个邻接相等//定位
//            {
//                left2 = pawsp.adj_2.left;
//                right2 = pawsp.adj_2.right;
//                if(left2==right)//对应画的第一种情况
//                {
//                    int zhuangtai = 0;
//                    if(right2!=0)//对于0元素可以无线多
//                        zhuangtai = findAndAnalysis.analysisState(Integer.toString(Math.abs(right2)),nodeNum,outPath);
//                    else zhuangtai = 100;
//
//                    //System.out.println(geneNumberInPath(path, 4, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath));
//
//                    if(geneNumberInPath(path, right2, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath)<1)
//                    {
//                        nexts.add(pawsp.adj_2.num);
//                        continue;
//                    }
//                }
//                else if(right2==-right)
//                {
//                    int zhuangtai = 0;
//                    if(left2!=0)
//                        zhuangtai = findAndAnalysis.analysisState(Integer.toString(Math.abs(left2)),nodeNum,outPath);
//                    else zhuangtai = 100;
//
//                    //System.out.println(geneNumberInPath(path, 4, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath));
//
//
//                    if(geneNumberInPath(path, left2, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath)<1)
//                    {
//                        AdjOrTelomere aNew = new AdjOrTelomere(right, -left2, "");
//                        aNew.num = currentNode.adjsOfNode.size();
//                        currentNode.adjsOfNode.add(aNew);
//                        nexts.add(aNew.num);
//                        orginAndNew.add(new int[]{pawsp.adj_2.num, aNew.num});
//                        continue;
//                    }
//                }
//            }
//            else if(isEqual(pawsp.adj_2, end))//邻接对的右边邻接与当前路径最后一个邻接相同
//            {
//                left1 = pawsp.adj_1.left;
//                right1 = pawsp.adj_1.right;
//                if(left1==right)
//                {
//                    int zhuangtai = 0;
//                    if(right1!=0)
//                        zhuangtai = findAndAnalysis.analysisState(Integer.toString(Math.abs(right1)),nodeNum,outPath);
//                    else zhuangtai = 100;
//
//                    //System.out.println(geneNumberInPath(path, 4, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath));
//
//                    if(geneNumberInPath(path, right1, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath)<1)
//                    {
//                        nexts.add(pawsp.adj_1.num);
//                        continue;
//                    }
//                }
//                else if(right1==-right)
//                {
//                    int zhuangtai = 0;
//                    if(left1!=0)
//                        zhuangtai = findAndAnalysis.analysisState(Integer.toString(Math.abs(left1)),nodeNum,outPath);
//                    else zhuangtai = 100;
//
//                    //System.out.println(geneNumberInPath(path, 4, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath));
//
//                    if(geneNumberInPath(path, left1, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath)<1)
//                    {
//                        AdjOrTelomere aNew = new AdjOrTelomere(right, -left1, "");
//                        aNew.num = currentNode.adjsOfNode.size();
//                        currentNode.adjsOfNode.add(aNew);
//                        nexts.add(aNew.num);
//                        orginAndNew.add(new int[]{pawsp.adj_1.num, aNew.num});
//                        continue;
//                    }
//                }
//            }
//        }
//        removeDuplicate(nexts);
//    }
//
//    public void getNextOther(AdjOrTelomere end, List<PairAdjsWithStateProba> listOfPawsps){
//        // 获得所有符合条件的邻接对
//        listOfPawsps = this.getListOfPawsps(end, listOfPawsps, 0);
//        for(PairAdjsWithStateProba pawsp :  listOfPawsps)//对于每一个知道概率的邻接对
//        {
////测试每一个基因的含量是可以正确得到的
////           if((pawsp.p_11 + pawsp.p_11d)<=0.2)
////              continue;
//            if(isEqual(pawsp.adj_1, end))//如果邻接对的第一个邻接与当前路径的最后一个邻接相等//定位
//            {
//                left2 = pawsp.adj_2.left;
//                right2 = pawsp.adj_2.right;
//                if(left2==right)//对应画的第一种情况
//                {
//                    int zhuangtai = 0;
//                    if(right2!=0)//对于0元素可以无线多
//                        zhuangtai = findAndAnalysis.analysisState(Integer.toString(Math.abs(right2)),nodeNum,outPath);
//                    else zhuangtai = 100;
//
//                    //System.out.println(geneNumberInPath(path, 4, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath));
//
//                    if(geneNumberInPath(path, right2, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath)<1)
//                    {
//                        nexts.add(pawsp.adj_2.num);
//                        continue;
//                    }
//                }
//                else if(right2==-right)
//                {
//                    int zhuangtai = 0;
//                    if(left2!=0)
//                        zhuangtai = findAndAnalysis.analysisState(Integer.toString(Math.abs(left2)),nodeNum,outPath);
//                    else zhuangtai = 100;
//
//                    //System.out.println(geneNumberInPath(path, 4, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath));
//
//
//                    if(geneNumberInPath(path, left2, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath)<1)
//                    {
//                        AdjOrTelomere aNew = new AdjOrTelomere(right, -left2, "");
//                        aNew.num = currentNode.adjsOfNode.size();
//                        currentNode.adjsOfNode.add(aNew);
//                        nexts.add(aNew.num);
//                        orginAndNew.add(new int[]{pawsp.adj_2.num, aNew.num});
//                        continue;
//                    }
//                }
//            }
//            else if(isEqual(pawsp.adj_2, end))//邻接对的右边邻接与当前路径最后一个邻接相同
//            {
//                left1 = pawsp.adj_1.left;
//                right1 = pawsp.adj_1.right;
//                if(left1==right)
//                {
//                    int zhuangtai = 0;
//                    if(right1!=0)
//                        zhuangtai = findAndAnalysis.analysisState(Integer.toString(Math.abs(right1)),nodeNum,outPath);
//                    else zhuangtai = 100;
//
//                    //System.out.println(geneNumberInPath(path, 4, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath));
//
//                    if(geneNumberInPath(path, right1, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath)<1)
//                    {
//                        nexts.add(pawsp.adj_1.num);
//                        continue;
//                    }
//                }
//                else if(right1==-right)
//                {
//                    int zhuangtai = 0;
//                    if(left1!=0)
//                        zhuangtai = findAndAnalysis.analysisState(Integer.toString(Math.abs(left1)),nodeNum,outPath);
//                    else zhuangtai = 100;
//
//                    //System.out.println(geneNumberInPath(path, 4, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath));
//
//                    if(geneNumberInPath(path, left1, count_forGeneInPath, left_forGeneInPath, right_forGeneInPath)<1)
//                    {
//                        AdjOrTelomere aNew = new AdjOrTelomere(right, -left1, "");
//                        aNew.num = currentNode.adjsOfNode.size();
//                        currentNode.adjsOfNode.add(aNew);
//                        nexts.add(aNew.num);
//                        orginAndNew.add(new int[]{pawsp.adj_1.num, aNew.num});
//                        continue;
//                    }
//                }
//            }
//        }
//        removeDuplicate(nexts);
//    }

    public  int getChromoNumInPath(List<AdjOrTelomere> path)
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

    public  double getValuesInPath(List<Integer> path, AdjOrTelomere former, AdjOrTelomere latter, int path_size, double result)
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

    public  boolean geneInPath(List<Integer> path, int gene, int count, int left, int right)//判断某一个基因是否已经存在于路径中
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
                count++;//记录找到了哪一个邻接
        }
        if(count==path.size())//找到了路径上最后一个，邻接也没有找到
            return false;
        else
            return true;
    }

    //判断路径中有多少个某个基因
    public int geneNumberInPath(List<Integer> path, int gene, int count, int left, int right)//判断一个路径中有多少这个基因
    {
        if(gene==0)
            return 0;//对于0基因的特殊处理
        count = 0;//用于计数，路径上的第几个基因
        int jishu = 0;
        for(int current : path)
        {
            AdjOrTelomere adj = currentNode.adjsOfNode.get(current);
            left = adj.left;
            right = adj.right;
            if(left==gene){jishu++;}

            else if(left==-gene) {jishu++;}
            else if(right==gene){jishu++;}
            else if(right==-gene){jishu++;}
            else count++;//记录找到了哪一个邻接
        }
        return jishu;
    }

    public  void getStateList(List<String> stateList, AdjOrTelomere adj1, AdjOrTelomere adj2, int m, int n, int count, CTree Tree)
    {
        stateList.clear();
        for(TreeNode current : Tree.tree)
        {
            if(current.children.size()==0)//对于树中的每个节点，只要他是叶子节点
            {
                stateList.add(getStateAtNode(current, adj1, adj2, m, n, count));
            }
        }
    }

    public  String getStateAtNode(TreeNode treeNode, AdjOrTelomere adj_1, AdjOrTelomere adj_2, int m, int n, int count)
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
        if(m==-1 && n==-1)//叶子节点中哪个邻接都没有
            return "0,0";
        else if(m==-1 && n!=-1)//叶子节点中有后面的这个邻接
            return "0,1";
        else if(m!=-1 && n==-1)//叶子节点有前面的这个邻接
            return "1,0";
        else if(treeNode.adjsOfNode.get(m).fromWhere.equals(treeNode.adjsOfNode.get(n).fromWhere))//都有，并且来自于同一条染色体
            return "1,1";
        else
            return "1,1d";//都有，但是来自于 不同的染色体
    }

    public  List<AdjOrTelomere> dealSeqs(List<AdjOrTelomere> seq1, List<AdjOrTelomere> seq2)
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

    public  List<AdjOrTelomere> dealPreAndSuc(List<AdjOrTelomere> pre, List<AdjOrTelomere> suc)
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

    public  List<Integer> getIntersection(List<Integer> ints1, List<Integer> ints2)
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

    public void removeDuplicate(List<Integer> before)
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

    public  void removeDuplicateAdjs(List<AdjOrTelomere> before)
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

    public  boolean isEqual(AdjOrTelomere adj1, AdjOrTelomere adj2)
    {
        if(adj1.toString().equals(adj2.toString()))
            return true;
        else if((adj1.left==(-adj2.right)) && (adj1.right==(-adj2.left)))
            return true;
        else
            return false;
    }

    public  void back()
    {
        currentNode = null;
        nodeNum = 0;
        outPath = "";
        pawsps.clear();
        classifiedPawsps.clear();
        array_pawsps = null;
        length_array_pawsps = 0;
        orginAndNew.clear();
//      minChromoNum = 0;
//      maxChromoNum = 0;
    }

    //设置一个缓冲池，将当前节点的左孩子的中包含的所有邻接对的概率都存储下来
//    public  static List<PairAdjsWithStateProba> bufferPool()
//    {
//
//
//    }
    //将每一个重建节点的树结构都写入文件
    public  void writeTreeStructure()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(currentNode.children.get(0).name+";");
        FileUtils.WriteToFile(outPath + "/newick_Tree_" + nodeNum, sb.toString(), false);

    }
    //特殊节点的处理
    public  void writeTreeStructure_0()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(currentNode.name+";");
        FileUtils.WriteToFile(outPath + "/newick_Tree_" + nodeNum, sb.toString(), false);

    }
    //筛选邻接的函数
    public void seletion(AdjOrTelomere adj1,AdjOrTelomere adj2) {
        if (adj2.right == adj1.right || adj2.right == adj1.left
                || adj2.left == adj1.left || adj2.left == adj1.right) {
            if (((adj1.left == 0 && adj2.left == 0) == false) && (adj1.right == 0 && adj2.right == 0) == false) {
                if (((adj1.left == 0 && adj2.right == 0) == false) && (adj1.right == 0 && adj2.left == 0) == false) {
//                            System.out.println(currentNode.adjsOfNode.get(i) + "*");
//                            System.out.println(currentNode.adjsOfNode.get(j) + "*");
                    adj2 = adj2;
                }
            }
        }
    }
}
