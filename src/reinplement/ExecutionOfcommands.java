package reinplement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExecutionOfcommands {

//函数,执行某一条命令
    public static  void doOneCommands(String s)
    {
        Process p;
        String cmds = s;

        try {
            //执行命令
            p = Runtime.getRuntime().exec(cmds);
            //获取输出流，并包装到BufferedReader中
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            while((line = br.readLine()) != null) {
                System.out.println(line);
            }
            int exitValue = p.waitFor();
            //System.out.println("进程返回值：" + exitValue+"    0:成功；1：失败");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

public static void doCommands(){
    //  两个输入文件的路径
    String treePath = "./data/newick_Tree";
    String dataPath = "./data/pastml.csv";

    //命令行执行的命令
    String commands = "pastml --tree E:\\研究生论文\\program\\RAGPM\\data\\newick_Tree --data E:\\研究生论文\\program\\RAGPM\\data\\pastml.csv  --columns  --html_compressed D:\\Albanian_map.html  --data_sep ,";

    doOneCommands(commands);

    }
}
