package reinplement;

import java.io.*;
import java.util.*;

//输入为一棵树和一组叶子基因组
//encodeAllandWrite();//这是一个最终的功能接口
public class encode {

        //System.out.println(Arrays.toString(findAllGenes()));
//        System.out.println(Arrays.toString(encodeNode("A")));
//        System.out.println(Arrays.toString(getGenomeName()));




//将list转化为字符串数组
    public static String[] listToShuzu(List<String> stateEncode)
    {
        String [] stateEncode_shuzu = new String[stateEncode.size()];//最终编码存储的数组（过程不必看重）
        int suisuibianbian=0;
        for(String ss:stateEncode)
        {
            stateEncode_shuzu[suisuibianbian] = ss;
            suisuibianbian++;
        }
        return stateEncode_shuzu;
    }

//找到所有的不同基因
public static String[] findAllGenes(String path) throws IOException
{
     String leafGenomes_path =path;
    //String leafGenomes_path = ;
     List<String> list  = new LinkedList<>();//存放所有基因的链表

     FileInputStream inputStream = new FileInputStream(leafGenomes_path);
     BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
     String str = null;
     while((str = bufferedReader.readLine()) != null)
     {
         //System.out.println(str);
         if(str.startsWith(">")) continue;//如果是叶子节点的名字，则继续
         else//是基因内容
         {
             String [] arr = str.split("\\s+");//读取一行内容
             for(String ss: arr) {
                 if((ss.equals("$"))==false){//只要不是$就向链表中追加
                 list.add(ss);}
             }
         }
     }

    inputStream.close();
    bufferedReader.close();
   // System.out.println(list.toString());

    //去除链表中相同的元素,set会自动去掉重复的数字
    Set<String> set = new TreeSet<>();
    set.addAll(list);
    //System.out.println(set.toString());

    //将set链表中的元素放到数组shuzu中
    String[] shuzu = new String[set.size()];
    int suisuibianbian=0;
    for(String ss:set)
    {
        shuzu[suisuibianbian] = ss;
        suisuibianbian++;
    }

    //将数组中的元素全部变为正数,绝对值相同的是一个基因
    for(int i = 0;i< shuzu.length; i++)
    {
        if(Integer.valueOf(shuzu[i]).intValue()<0) {
            shuzu[i] =Integer.toString(Math.abs(Integer.valueOf(shuzu[i]).intValue()));
        }
    }


    //去除数组中相同的元素
    String[] newShuzu = deleteSameGene(shuzu);
    int length = 0;
    for(int i= 0;i<newShuzu.length;i++)
    {
         if(newShuzu[i]!=null) length++;
    }
    String[] newList = new String[length];//newList为最终数组，因为原来的数组后面会有null元素
    for(int i = 0;i<length;i++)
    {
        newList[i] = newShuzu[i];
    }
   return newList;

}



//数组去重函数
public static String[] deleteSameGene(String[] nums) {
    if (nums.length == 0) {
        return null;
    }
    String[] tmpNums = new String[nums.length];
    tmpNums[0] = nums[0];
    int index = 1;
    for (int i = 1, len = nums.length; i < len; i++) {
        int j = 0;
        for (; j < index; j++) {
            if (tmpNums[j].equals(nums[i])) {
                break;
            }
        }
        if (j == index) {
            tmpNums[index++] = nums[i];
        }
    }
    nums = tmpNums;
    return tmpNums;
}



//遍历每一个叶子节点对叶子节点进行编码。参数：传入一个节点的名字，节点基因组的路径，就可以对此节点进行多状态编码
    public static int[] encodeNode(String name,String path) throws IOException {

        //读此节点的内容，放在链表中,并最终转存在数组中便于操作
        String leafGenomes_path =path;
        List<String> list  = new LinkedList<>();//存放此节点所有基因的链表

        FileInputStream inputStream = new FileInputStream(leafGenomes_path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String str = null;
        int jishu = 0;
        while((str = bufferedReader.readLine()) != null)
        {
            //没找到之前应该作计数，记录到第几行了
            if(str.equals(">"+name))//如果找到了这个节点
            {
                //list.add(name);//将这个节点的名字加入链表
                jishu = 1;
                continue;
            }

            if(str.endsWith("$")==true)//只要是以$结尾的字符串
            {
                if(jishu==1) {//找到了之后才开始追加
                    String[] arr = str.split("\\s+");//读取一行内容
                    for (String ss : arr) {
                        if ((ss.equals("$")) == false) {//只要不是$就向链表中追加
                            list.add(ss);
                        }
                    }
                }
            }
            else
            {
                if(str.startsWith(">")&&jishu==1) break;
            }
        }
        inputStream.close();
        bufferedReader.close();
        //System.out.println(Arrays.toString(list.toArray())+"*");//第二次找B时调用无内容
        //将链表转化为字符串数组
        String[] shuzu_for_encodeNode = new String[list.size()];
        int i_for_encodeNode =0;
        for(String ss:list)
        {
            shuzu_for_encodeNode[i_for_encodeNode] = ss;
            i_for_encodeNode++;
        }
        //为了编码方便，将其全部变为正数
        for(int i = 0;i< shuzu_for_encodeNode.length; i++)
        {
            if(Integer.valueOf(shuzu_for_encodeNode[i]).intValue()<0) {
                shuzu_for_encodeNode[i] =Integer.toString(Math.abs(Integer.valueOf(shuzu_for_encodeNode[i]).intValue()));
            }
        }
        //至此，我们完成了读取特定节点的基因组内容,并将其变为正数，并将其转存到字符串数组shuzu_for_encodeNode中。（测试正确）
        //下面，就要对这个节点的字符串数组进行编码了
        String allGenes[]  = findAllGenes(path);//先拿到整棵树上的所有基因
        //System.out.println("进化树中的所有基因"+Arrays.toString(allGenes));
        //System.out.println("当前基因组中的基因"+Arrays.toString(shuzu_for_encodeNode));
        List<Integer> stateEncode = new LinkedList();//用于存放最终的状态编码

        for(int i = 0;i<allGenes.length; i++)
        {
            stateEncode.add(sumNumber(allGenes[i],shuzu_for_encodeNode));
        }
        //将stateEncode链表中的元素放到数组中
        int [] stateEncode_shuzu = new int[stateEncode.size()];//最终编码存储的数组（过程不必看重）
        int suisuibianbian=0;
        for(int ss:stateEncode)
        {
            stateEncode_shuzu[suisuibianbian] = ss;
            suisuibianbian++;
        }

        return  stateEncode_shuzu;
    }
    //统计某一个元素在数组中存在的次数
public static int sumNumber(String a,String[] list)
{
    int sum= 0;//用于计数
    for(int i = 0;i<list.length ; i++)
    {
        if(list[i].equals(a))
        {
            sum++;
        }
    }
    return sum;
}

//获取所有基因组的名字，存储在数组中
public static String[] getGenomeName(String path) throws IOException {
        String leafGenomes_path =path;
        List<String> list  = new LinkedList<>();//存放所有基因组名称的链表


        FileInputStream inputStream = new FileInputStream(leafGenomes_path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String str = null;
        while((str = bufferedReader.readLine()) != null)
        {
            if(str.startsWith(">")) //如果是叶子节点的名字，则将名字存储在数组中
            {
                list.add(str.substring(str.indexOf(">") + 1));
            }
        }
        inputStream.close();
        bufferedReader.close();
        String shuzuForGenomeName[] = listToShuzu(list);
        return shuzuForGenomeName;
    }

//将字符串数组转化成字符串
    public static String stringShuzuToString(String str[])
    {

        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < str.length; i++){
            sb. append(str[i]);
        }
        String s = sb.toString();
        return s;

    }
//将int型数组转化为String型数组
public static String[] intShuzuToStringShuzu(int [] a)
{
    String[] s = new String[a.length];
    for(int i= 0;i<a.length;i++)
    {
        s[i] = Integer.toString(a[i]);
    }
    return s;
}
//遍历所有的节点名称，为每个节点编码，并写入文件中
    public static void encodeAllandWrite(String path,String outoath) throws IOException {//叶子基因组存放路径，pastml输出路径

        String filepath = outoath;
        String[] allGenomeName = getGenomeName(path);//所有基因组的名字
        String[] allGenes = findAllGenes(path);//进化树中所有的基因

        //在每一个基因组后面都加入一个“,”
        for(int i = 0;i<allGenes.length-1;i++)
        {
            allGenes[i] = allGenes[i]+",";
        }

        //第一行，包括ID与每一个基因
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filepath));
        String content = "ID,"+stringShuzuToString(allGenes)+"\r\n";
        bufferedWriter.write(content);
        //bufferedWriter.close();//文件写入后如果不关闭的话是不会写入成功的

        //接下来是剩下的行
        for(int j=0;j<allGenomeName.length;j++)
        {
            int[] everyHang = encodeNode(allGenomeName[j],path);
            String[] Hang = intShuzuToStringShuzu(everyHang);
            //每一个字符后都加“，”
            for(int i = 0;i<Hang.length-1;i++)
            {
                Hang[i] = Hang[i]+",";
            }
            String content_01 = allGenomeName[j]+","+stringShuzuToString(Hang)+"\r\n";
            bufferedWriter.write(content_01);
        }
        bufferedWriter.close();


    }

}



