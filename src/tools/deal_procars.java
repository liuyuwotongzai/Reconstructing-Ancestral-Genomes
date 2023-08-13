package tools;

import java.util.ArrayList;
import java.util.List;

public class deal_procars {
    public static void main(String[] args) {
        String path1 = "D:\\idea\\ideaproject\\test_01\\data\\ProCars.txt";
        String path2 ="D:\\idea\\ideaproject\\test_01\\data\\ProCarsOver.txt";
        procarsjinyibu(path1,path2);

    }
    public static void procarsjinyibu(String one,String two)
    {
        List<String> temp = FileUtils.readFile(one);
        StringBuilder sb = new StringBuilder(">O" + "\n");
        int flag=0;
        List<String> dan = new ArrayList<>();
        for(String current : temp)
        {
            System.out.println(current.length());

            if(current.length()==4)
            {
                dan.add(current.substring(0, current.length()-2)+" ");
            }
            else
            {
                if(current.length()>2)
                sb.append(current.substring(0, current.length()-2)+" $"+'\n');
            }
        }
        sb.append(listToShuzu(dan)+"$");
        FileUtils.WriteToFile(two, sb.toString(), false);

    }
    public static String listToShuzu(List<String> stateEncode)
    {
        String stateEncode_shuzu = "";//最终编码存储的数组（过程不必看重）
        int suisuibianbian=0;
        for(String ss:stateEncode)
        {
            stateEncode_shuzu =stateEncode_shuzu + ss;
            suisuibianbian++;
        }
        return stateEncode_shuzu;
    }
}
