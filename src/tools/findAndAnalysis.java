package tools;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
// allStates();为程序的最终接口
public class findAndAnalysis {
//
//遍历文件夹下的所有文件，找到所有要使用的文件
public static void findAllFile() throws IOException {

    String path  = "./data/newick_Tree_pastml";//结果文件夹的路径

    //将此路径下的文件存放在String数组中
    File file = new File(path);
    File[] fn = file.listFiles();//所有文件的列表
    String [] fileName = new String[fn.length];//将包含文件的名字存放在此数组中
    for(int i = 0;i<fn.length; i++)
    {
        fileName[i] = fn[i].getName();
    }

    //遍历数组，找到以marginal开头的文件，这是我们需要的文件
    List<String> theFileName = new LinkedList<>();//存放要找的文件名字
    for(int i = 0; i< fileName.length;i++)
    {
        if(fileName[i].startsWith("m"))
        {
            theFileName.add(fileName[i]);
        }
    }
    //去拿整棵进化树中的全部基因
    String[] allGenes = encode.findAllGenes();
    System.out.println(Arrays.toString(theFileName.toArray()));//测试成功
}

//遍历文件夹下的所有文件，找到指定名称的文件,返回他的路径
public static String findFilePath(String a) throws IOException {//参数为找基因a存在文件的路径

    String path  = "./data/newick_Tree_pastml";//结果文件夹的路径

    //将此路径下的文件存放在String数组中
    File file = new File(path);
    File[] fn = file.listFiles();//所有文件的列表


    //遍历数组，找到特定名称的文件，这是我们需要的文件
    String pathName="没有找到指定的文件a";
    for(int i = 0; i< fn.length;i++)
    {
        if(fn[i].getName().equals("marginal_probabilities.character_"+a+".model_F81.tab"))
        {
             pathName = fn[i].getPath();
        }
    }
   return pathName;
}

//函数，返回数组中最大值的下标
public   static  int  returnMaxIndex(Double[] a)
{
    double aar_Max = a[0];
    int    aar_index=0;
    if(a.length>0){
        for(int i=0;i<a.length;i++){
            if(a[i]>aar_Max){//比较后赋值
                aar_Max=a[i];
                aar_index = i;
            }
        }
    }
    return aar_index;
}

//函数，返回某一个基因在祖先中的状态
public static  int analysisState(String a) throws IOException//测试成功
{
    String filePath = findFilePath(a);

    //接下来就是去读此文件
    FileInputStream inputStream = new FileInputStream(filePath);
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    String str = null;
    List<String> firstLine  = new LinkedList<>();//第一行初node以外的内容，已测试均成功拿到
    List<String> secondLine = new LinkedList<>();//第二行初root以外的内容
    while((str = bufferedReader.readLine()) != null)
    {
        if(str.startsWith("node"))
        {
            String[] first = str.split("\\s+");
            for(String ss : first)
            {
                if(ss.equals("node")==false)
                {
                    firstLine.add(ss);
                }
            }
        }
        if(str.startsWith("root"))
        {
            String[] second= str.split("\\s+");
            for(String ss : second)
            {
                if(ss.equals("root")==false)
                {
                    secondLine.add(ss);
                }
            }
        }
    }
//    System.out.println(Arrays.toString(firstLine.toArray()));
//    System.out.println(Arrays.toString(secondLine.toArray()));//至此为止测试均正确
    //将链表中的元素转存到数组里
    String[] firstLine_Array  = encode.listToShuzu(firstLine);
    String[] secondLine_Array = encode.listToShuzu(secondLine);

    //将字符串数组转化为Double类型的数组
    //Double[] first_Array = new Double[firstLine_Array.length];
    Double[] second_Array = new Double[secondLine_Array.length];
    for(int i= 0;i<second_Array.length;i++)
    {
        second_Array[i] = Double.parseDouble(secondLine_Array[i]);
    }
    //System.out.println(Arrays.toString(second_Array));//测试成功，没有丢失精度
    int xiabiao = returnMaxIndex(second_Array);//拥有最大值的下标
    return Integer.valueOf(firstLine_Array[xiabiao]).intValue();
}
//某一个基因在祖先中的状态清楚了，下面就是所有基因在祖先中的状态
    //求所有基因在祖先中的状态
public static int[] allStates() throws IOException {
    String[] allGenes = encode.findAllGenes();//拿到所有的基因

    List<Integer> state = new LinkedList<>();//存储所有基因的状态

    for(int i=0;i<allGenes.length;i++)
    {
        state.add(analysisState(allGenes[i]));
    }
    //将链表中状态进行转存
   int[] states = new int[state.size()];
    for(int i=0;i<states.length;i++)
    {
        states[i] = state.get(i);
    }
    System.out.println("所有基因： "+Arrays.toString(allGenes));
    System.out.println("此节点中所有基因的对应状态： "+Arrays.toString(states));//测试成功，最终返回的所有状态均没有问题
    return states;
}


//



}
