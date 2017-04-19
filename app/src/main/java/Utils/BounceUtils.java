package Utils;

/**
 * Created by admin on 2017/4/19.
 */
/*
* 
* 描    述：阻尼震动工具类
* 作    者：ksheng
* 时    间：2017/4/19$ 21:34$.
*/
public class BounceUtils {

    /**
     * 输入0-1，返回弹簧特效的数字
     * @param length 弹簧最初的步长
     * @param frequency 来回振动的次数
     * @return
     */
    public static float[] getBounceNum(float length, int frequency) {
        float num = 0;
        float[] result = new float[frequency];
        float bounceRate = 0;
        float boucneNum = 0;
        for (int i = 0; i < frequency; i++) {
            bounceRate = (float) Math.exp((-5) * num);
            boucneNum = (float) Math.cos(30 * num);
            result[i] = bounceRate * boucneNum*length;
            num+=(1.0000f/(frequency-1));
        }
        return result;
    }
}
