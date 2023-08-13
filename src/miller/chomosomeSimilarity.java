package miller;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

// 两个文件中分别存有两个基因组数据，分别将其读出来,转化为染色体数量
public class chomosomeSimilarity
{
    private static int realNumber;
    private static int predictNumber;


    public static double similarity = 0;

    // 构造方法
    public chomosomeSimilarity() {}

    public static int different(String path1,String path2)
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(path1), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            while((line=br.readLine())!=null)
            {
                if(line.length()>0 && !line.equals("\r") && !line.equals("\n"))
                    realNumber++;
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
        //预测出来的染色体数量
        try
        {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(path2), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            while((line=br.readLine())!=null)
            {
                if(line.length()>0 && !line.equals("\r") && !line.equals("\n"))
                    predictNumber++;
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

//        System.out.println(realNumber+"*");
//        System.out.println(predictNumber+"*");
        return Math.abs(realNumber-predictNumber);

    }

    // 将静态变量的值还原
    public static void back()
    {

        realNumber=0;
        predictNumber=0;
    }
}
