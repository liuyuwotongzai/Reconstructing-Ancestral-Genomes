package miller;

import java.io.*;
import java.util.*;

// 两个文件中分别存有两个基因组数据，分别将其读出来,转化为染色体数量
public class geneContentSimilarity
{

    private  static List<String> realElement = new ArrayList<>();
    private  static List<String> predictElement = new ArrayList<>();


    public static double jingdu = 0;

    // 构造方法
    public geneContentSimilarity() {}

    public static double different(String path1,String path2)
    {

        try
        {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(path1), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String line;

            while((line=br.readLine())!=null)
            {

                if(line.endsWith("$") && !line.equals("\r") && !line.equals("\n")) {
                    String s = line.substring(0,line.length()-1);
                    realElement.addAll(Arrays.asList(s.split(" ")));
                }
            }
            br.close();
            isr.close();
            System.out.println(Arrays.toString(realElement.toArray()));
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

                if(line.endsWith("$") && !line.equals("\r") && !line.equals("\n")) {
                    String s = line.substring(0,line.length()-1);
                    predictElement.addAll(Arrays.asList(s.split(" ")));
                }
            }
            br.close();
            isr.close();
            System.out.println(Arrays.toString(predictElement.toArray()));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println(realElement.size());
        System.out.println(predictElement.size());
        jingdu  = (double)predictElement.size() / (double)realElement.size();
        return 1-jingdu;

    }

    // 将静态变量的值还原
    public static void back()
    {

        realElement.clear();
        predictElement.clear();
    }
}
