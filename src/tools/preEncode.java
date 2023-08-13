package tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class preEncode {
    public static void main(String[] args) throws IOException {
        System.out.println(Arrays.toString(findAllGenes()));

    }


    //找所有的基因，包括重复的,但是都是正的
    public static String[] findAllGenes() throws IOException
    {
        String leafGenomes_path ="./data/leafGenomes_for_RAGPM";
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
        String[] shuzu = new String[list.size()];//存放所有基因的数组
        int suisuibianbian=0;
        for(String ss:list)
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

        return shuzu;
}

//对每一个基因都赋予一个不同的编码
    public static  String[] uniqueEncodeGenes() throws IOException {
        String[] shuzu = findAllGenes();
        int lenth = shuzu.length;
        for(int i = 0;i<lenth-1 ;i++)
        {
            for(int j=i+1;j<lenth;j++)
            {
                if(shuzu[j] == shuzu[i]){
                    shuzu[j] = shuzu[j]+"'";

                }
            }
        }
        return shuzu;
    }

}
