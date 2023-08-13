package miller;

public class PairAdjsWithStateProba
{
    // 邻接对的左邻接
    public AdjOrTelomere adj_1;
    // 邻接对的由邻接
    public AdjOrTelomere adj_2;

    // 左邻接不存在，右邻接不存在的概率
    public double p_00;
    // 左邻接不存在，右邻接存在的概率
    public double p_01;
    // 左邻接存在，右邻接不存在的概率
    public double p_10;
    // 左邻接存在，右邻接存在的概率
    public double p_11;
    // 左邻接存在，右邻接存在的概率，不在同一条染色体
    public double p_11d;  

    public PairAdjsWithStateProba(AdjOrTelomere adj_1, AdjOrTelomere adj_2)
    {
        this.adj_1 = adj_1;
        this.adj_2 = adj_2;
        p_00 = 0;
        p_01 = 0;
        p_10 = 0;
        p_11 = 0;
        p_11d = 0;
    }
}
