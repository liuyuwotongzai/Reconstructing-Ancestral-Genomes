package reinplement;

import miller.FileUtils;
import miller.LeafNode;
import miller.Tree;
import miller.TreeNode;

import java.util.*;

public class CTree {
    public String tree_str = "";
    public ArrayList<TreeNode> tree = new ArrayList<TreeNode>();
    public Map<String, TreeNode> record = new HashMap<String, TreeNode>();
    public List<List<String>> genomes = new ArrayList<List<String>>();


    public CTree(String the_treeStr, List<List<String>> the_genomes) {
        this.tree_str = the_treeStr;
        this.genomes.addAll(the_genomes);
        this.tree.add(new TreeNode());
        this.mkNod(
                this.tree.get(0),
                this.tree_str.substring(0, this.tree_str.length() - 1)
        );//根据结构字符串创建树结构
        this.getPrepared(this.tree.get(0));//给节点赋予分支长度
    }

    // 该方法传入的参数分别是 树(网络)的根节点、表示树(网络)的 newick格式的字符串。(习惯将二叉树称为树，而将多分枝且父子关系比较复杂的树称为网络)
    // 我此处用到的是二叉树
    public void mkNod(TreeNode node, String nodeFlag)//根节点，树结构字符串
    {
        node.nodeFlag = nodeFlag;
        if (this.record.containsKey(nodeFlag)) {
            TreeNode newNode = record.get(nodeFlag);
            for (int i = 0; i < newNode.parents.size(); i++) {
                newNode.parents.get(i).children.remove(record.get(nodeFlag));
            }
            newNode.parents.addAll(node.parents);
            for (int i = 0; i < newNode.parents.size(); i++) {
                newNode.parents.get(i).children.add(newNode);
                newNode.parents.get(i).children.remove(node);
            }
            this.tree.remove(this.record.get(nodeFlag));
            this.tree.remove(node);
            this.tree.add(newNode);
            this.record.put(nodeFlag, newNode);
        }
        if ((!(record.containsKey(nodeFlag))) && (!(nodeFlag.contains("("))) && (!(nodeFlag.contains(","))) && (!(nodeFlag.contains(")")))) {

            LeafNode newLeafNode = new LeafNode();
            newLeafNode.parents.addAll(node.parents);
            newLeafNode.nodeFlag = nodeFlag;
            for (int i = 0; i < newLeafNode.parents.size(); i++) {
                newLeafNode.parents.get(i).children.add(newLeafNode);
                newLeafNode.parents.get(i).children.remove(node);
            }
            tree.remove(node);
            tree.add(newLeafNode);
        }
        if (nodeFlag.startsWith("(") && nodeFlag.endsWith(")")) {
            Stack<Character> stk = new Stack<Character>();
            ArrayList<Integer> comma = new ArrayList<Integer>();
            for (int i = 0; i < nodeFlag.length(); i++) {
                if (nodeFlag.charAt(i) == '(') {
                    stk.push(nodeFlag.charAt(i));
                }
                if (nodeFlag.charAt(i) == ')') {
                    stk.pop();
                }
                if (nodeFlag.charAt(i) == ',' && stk.size() == 1) {
                    comma.add(i);
                }
            }
            comma.add(0, 0);
            comma.add((nodeFlag.length() - 1));
            for (int j = 0; j < (comma.size() - 1); j++) {
                tree.add(new TreeNode());
                tree.get(tree.size() - 1).nodeFlag = nodeFlag.substring(comma.get(j) + 1, comma.get(j + 1));
                node.children.add(tree.get(tree.size() - 1));
                tree.get(tree.size() - 1).parents.add(node);
                mkNod(tree.get(tree.size() - 1), tree.get(tree.size() - 1).nodeFlag);
            }
        }
        if (nodeFlag.startsWith("(") && nodeFlag.contains(")") && (!(nodeFlag.endsWith(")")))) {
            Stack<Character> stk = new Stack<Character>();
            ArrayList<Integer> comma = new ArrayList<Integer>();
            for (int i = 0; i < nodeFlag.length(); i++) {
                if (nodeFlag.charAt(i) == '(') {
                    stk.push(nodeFlag.charAt(i));
                }
                if (nodeFlag.charAt(i) == ')') {
                    if (stk.size() == 1) {
                        comma.add(i);
                    }
                    stk.pop();
                }
                if (nodeFlag.charAt(i) == ',' && stk.size() == 1) {
                    comma.add(i);
                }
            }
            comma.add(0, 0);

            record.put(nodeFlag.substring(comma.get(comma.size() - 1) + 1, nodeFlag.length()), node);
            for (int j = 0; j < (comma.size() - 1); j++) {
                tree.add(new TreeNode());
                tree.get(tree.size() - 1).nodeFlag = nodeFlag.substring(comma.get(j) + 1, comma.get(j + 1));
                node.children.add(tree.get(tree.size() - 1));
                tree.get(tree.size() - 1).parents.add(node);
                mkNod(tree.get(tree.size() - 1), tree.get(tree.size() - 1).nodeFlag);
            }
            node.nodeFlag = nodeFlag.substring(0, comma.get(comma.size() - 1) + 1);
        }
    }

    public void getPrepared() {
        int tree_size = tree.size();
        for (int i = 0; i < tree_size; i++) {
            TreeNode tn = tree.get(i);
            if (!(tn instanceof LeafNode)) {
                tn.name = tn.nodeFlag;
                if (tn.parents.size() != 0) {
                    int start = tree_str.indexOf(tn.nodeFlag) + tn.nodeFlag.length() + 1;
                    StringBuilder sb = new StringBuilder();
                    for (int j = start; j < tree_str.length(); j++) {
                        char temp = tree_str.charAt(j);
                        if (temp == ',' || temp == ')')
                            break;
                        else
                            sb.append(temp);
                    }
                    tn.branchLength = Double.valueOf(sb.toString());
                }
            } else {
                tn.name = tn.nodeFlag.substring(0, tn.nodeFlag.indexOf(":"));
                String flag = ">" + tn.name;
                for (List<String> temp : genomes) {
                    if (temp.get(0).equals(flag)) {
                        tn.genome = new String[temp.size()];
                        temp.toArray(tn.genome);
                        break;
                    }
                }
                tn.getChromosomes();
                tn.getAdjsOfNode();
                tn.getPreAndSuc();
            }
        }
    }

    public void getPrepared(TreeNode root) {
        if (root == null)
            return;

        for (TreeNode child : root.children)
            getPrepared(child);

        if (root.children.size() == 0) {
            String[] temp = root.nodeFlag.split(":");
            root.name = temp[0];
            root.branchLength = Double.valueOf(temp[1]);

            String flag = ">" + root.name;
            for (List<String> temp2 : genomes) {
                if (temp2.get(0).equals(flag)) {
                    root.genome = new String[temp2.size()];
                    temp2.toArray(root.genome);
                    break;
                }
            }
            root.getChromosomes();
            root.getAdjsOfNode();
            root.getPreAndSuc();
        } else {
            if (root.parents.size() == 0) {
                root.name = root.nodeFlag;
                return;
            }
            String parentFlag = root.parents.get(0).nodeFlag;
            String rootFlag = root.nodeFlag;
            root.name = root.nodeFlag;
            StringBuffer stringBuffer = new StringBuffer();
            int start = parentFlag.indexOf(rootFlag) + rootFlag.length() + 1;
            for (int i = start; i < parentFlag.length(); i++) {
                char temp = parentFlag.charAt(i);
                if (temp == ',' || temp == ')')
                    break;
                else
                    stringBuffer.append(temp);
            }
            root.branchLength = Double.valueOf(stringBuffer.toString());
        }
    }

    public void showTheTree(String path) {
        StringBuilder sb = new StringBuilder();
        sb.append("The information of the tree and leaf genomes > " + "\n");
        for (int i = 0; i < tree.size(); i++) {
            //System.out.println(tree.get(i).name+"8");树节点链表中存放的顺序是左序遍历树（根左右）
            TreeNode tn = tree.get(i);
            boolean leafOrNot = (tn instanceof LeafNode);
            sb.append(i + "  " + tn.name + "  " + tn.branchLength + "   " + (leafOrNot == true ? "Leaf" : "Branch") + "\n");
            if (leafOrNot)//如果是叶子节点
            {
                for (String s : tn.genome) {
                    sb.append(s + "\n");
                }
            }
        }
        if (path.equals(""))
            System.out.println(sb.toString());
        else {
            FileUtils.WriteToFile(path + "process", sb.toString(), false);
        }
    }
    public void back()
    {
        tree_str = "";
        tree.clear();
        record.clear();
        genomes.clear();
    }
    //**********************************************************************************
    //以下内容都是新加的7.21
    //层序遍历树
    public void levelOrder() {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(tree.get(0));
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
//            System.out.println(node.name + " " + node.branchLength + " " + node.adjsOfNode.toString());
            if(!(node instanceof LeafNode))
            {
                System.out.println(node.name + " " );
            }
            else
            {
                System.out.println(node.name + " " + node.branchLength);
            }
            //System.out.println(node.name);
            //System.out.println("(Weight:" + node.getWeight() + ", Name:" + node.getName() + ", Leaf:" + node.isLeaf() + ")");
            List<TreeNode> children = node.getChildren();
            for (TreeNode child : children) {
                queue.offer(child);
            }
        }
    }
    //层序遍历，用于检查树的结构是否正确
    public void levelorder2(TreeNode node1)
    {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(node1);
        while(!queue.isEmpty()){
            TreeNode node = queue.poll();
            System.out.println(node.name+" "+node.branchLength+" "+node.adjsOfNode.toString());//测试节点顺序正确，分支长度正确，节点孩子正确
            // System.out.println(node.name+" "+node.branchLength);//测试节点顺序正确，分支长度正确，节点孩子正确
            //System.out.println("(Weight:" + node.getWeight() + ", Name:" + node.getName() + ", Leaf:" + node.isLeaf() + ")");
            List<TreeNode> children = node.getChildren();
            //System.out.println(children.size());
            for (TreeNode child : children) {
                queue.offer(child);
            }
        }
    }
    //使每个节点知道其父亲是谁
    public void knowFather() {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(tree.get(0));
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            List<TreeNode> children = node.getChildren();
            for (TreeNode child : children) {
                child.setFather(node);
                // System.out.println(child.getFather().name);//6个，根节点无父亲
                queue.offer(child);
            }
        }
    }
    //重新根植这棵树
    public void reconstruct(TreeNode node) {
        TreeNode rootNode = node;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(tree.get(1));
        while (!queue.isEmpty()) {
            TreeNode node1 = queue.poll();
            //System.out.println(node.name);
            //System.out.println("(Weight:" + node.getWeight() + ", Name:" + node.getName() + ", Leaf:" + node.isLeaf() + ")");
            List<TreeNode> children = node1.getChildren();
            for (TreeNode child : children) {
                queue.offer(child);
            }
        }

    }
    public LinkedList<TreeNode> allNotleaf(TreeNode node) {//获取此节点到根节点所有经过的中间节点
        TreeNode pointer;
        LinkedList<TreeNode> allzhongjian = new LinkedList<TreeNode>();
        for (pointer = node; pointer != null; pointer = pointer.father) {

            if (!(pointer instanceof LeafNode)) {
                //System.out.println(pointer.name + "*");
                allzhongjian.add(pointer);
            }
        }
        return allzhongjian;
    }
    //二叉树的前序遍历.通过前序遍历去改变存放树节点的链表
    public void preOrder(TreeNode node, Queue<TreeNode> queue){
        int childNumber = node.children.size();
        if(childNumber == 0){
            queue.offer(node);
            return;
        }
        else if(childNumber == 1){
            queue.offer(node);
            TreeNode child = node.children.get(0);
            preOrder(child, queue);
        }
        else if(childNumber == 2){
            queue.offer(node);
            TreeNode lchild =  node.children.get(0);
            preOrder(lchild,queue);
            TreeNode rchild =  node.children.get(1);
            preOrder(rchild, queue);
        }
    }
    //返回根据新的树结构生成的新的树的节点的链表
    public ArrayList<TreeNode> getNewNodeList(TreeNode tree){
        Queue<TreeNode> queue = new LinkedList<>();
        preOrder(tree, queue);
        ArrayList<TreeNode> arrayList = new ArrayList<>();
        while(!queue.isEmpty()){
            arrayList.add(queue.poll());
        }
        return arrayList;

    }




}
