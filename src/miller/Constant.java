package miller;
/*
This class provide some constant numbers that used in the whole project.
 */
public class Constant
{
    public static double[][] p = new double[5][5];
    //public static double[][] q = new double[5][5];
    public static double alpha = 0; // α
    public static double beta = 0;  // β
    public static double t = 0;     // t

    public Constant()   {}

    public static void setAlpha(double the_alpha)
    {
        alpha = the_alpha;
    }

    public static void setBeta(double the_beta)
    {
        beta = the_beta;
    }

    public static void setT(double the_t)
    {
        t = the_t;
    }

    public static void getP()
    {
        double tempP1 = (double)1/5 + (double)1/20*(Math.pow(Math.E, -5*beta*t)) + (double)3/4*(Math.pow(Math.E, -4*alpha*t-beta*t));
        double tempP2 = (double)1/5 + (double)1/20*(Math.pow(Math.E, -5*beta*t)) - (double)1/4*(Math.pow(Math.E, -4*alpha*t-beta*t));
        double tempP3 = (double)1/5 - (double)1/5*(Math.pow(Math.E, -5*beta*t));
        double tempP4 = (double)1/5 + (double)4/5*(Math.pow(Math.E, -5*beta*t));

        p[0][0] = tempP1; p[0][1] = tempP2; p[0][2] = tempP2; p[0][3] = tempP2; p[0][4] = tempP3;
        p[1][0] = tempP2; p[1][1] = tempP1; p[1][2] = tempP2; p[1][3] = tempP2; p[1][4] = tempP3;
        p[2][0] = tempP2; p[2][1] = tempP2; p[2][2] = tempP1; p[2][3] = tempP2; p[2][4] = tempP3;
        p[3][0] = tempP2; p[3][1] = tempP2; p[3][2] = tempP2; p[3][3] = tempP1; p[3][4] = tempP3;
        p[4][0] = tempP3; p[4][1] = tempP3; p[4][2] = tempP3; p[4][3] = tempP3; p[4][4] = tempP4;
    }

    public static void back()
    {
        alpha = 0;
        beta = 0;
        t = 0;
    }
}
