package miller;

public class AdjOrTelomere {
    /**
     * 邻接
     * (1, 2), 1,2 分别是两个基因
     */
    public int left;
    public int right;
    public String fromWhere;//来自于哪一条染色体
    public int num;

    public  AdjOrTelomere(int left, int right, String fromWhere)
    {
        this.left = left;
        this.right = right;
        this.fromWhere = fromWhere;

    }
    public  AdjOrTelomere(int left, int right, String fromWhere,int num)
    {
        this.left = left;
        this.right = right;
        this.fromWhere = fromWhere;
        this.num = num;

    }

    public String toString()
    {
        return this.left + " " + this.right;
    }
}