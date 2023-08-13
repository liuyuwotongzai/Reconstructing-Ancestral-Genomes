package miller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils  // 文本文件
{
    // 写入文件(文件路径， 写入的内容， 是否在原有内容上追加)
    public static void WriteToFile(String filePath, String content, boolean append)
    {
        FileWriter fileWriter;
        try
        {
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // 删除文件 或者 清除文件夹下的所有文件
    public static void deleteFile(String filePath)
    {
        File file = new File(filePath);
        if (file.isFile())  //判断是否为文件，是，则删除
        {
            file.delete();
        }
        else //不为文件，则为文件夹
        {
            String[] childFilePath = file.list();//获取文件夹下所有文件相对路径
            for (String path:childFilePath)
            {
                deleteFile(file.getAbsoluteFile()+"/"+path);//递归，对每个都进行判断
            }
            //file.delete(); // 如果不保留文件夹本身 则执行此行代码
        }
    }

    // 新建文件夹
    public static void makeNewDir(String path)
    {
        File dir = new File(path);
        if(!dir.exists())
            dir.mkdirs();
        else
            return;
    }

    //从文件中读数据，每行读取为一个String 存在List<String>中
    public static List<String> readFile(String filePath)
    {
        List<String> sourceList = new ArrayList<String>();
        File file = new File(filePath);
        String encoding = "utf-8";
        String line;
        try//-----------------------------------------------------------对于文件中的数据(每条数据为一行)以字符串的形式存在List中
        {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, encoding);
            BufferedReader br = new BufferedReader(isr);
            int i = 0;
            while(  (line=(br.readLine()))!=null)
            {
                sourceList.add(line);
            }
            br.close();  isr.close();  fis.close();
        }
        catch(UnsupportedEncodingException e) { e.printStackTrace();}
        catch(FileNotFoundException e) { e.printStackTrace();}
        catch(IOException e) { e.printStackTrace();}
        return sourceList;
    }

    // 判断一个路径对应的状况
    public static int fileExists(String path)
    {
        File file = new File(path);
        if(file.isFile())
            return 1; // 是文件
        else if(file.isDirectory())
        {
            if(file.list().length>0)
                return 2;   // 非空文件夹
            else
                return 3;   // 空文件夹
        }
        return 0; // 路径不存在
    }

    // 修改文件名
    public static boolean reName(String absolutePathName, String newAbsoluteName)
    {
        File file = new File(absolutePathName);
        if (file == null)
            return false;
        if (!file.exists())
        {
            System.out.println("file " + absolutePathName + " doesn't exits!");
            return false;
        }
        else
        {
            file.renameTo(new File(newAbsoluteName));
            return true;
        }
    }
}
