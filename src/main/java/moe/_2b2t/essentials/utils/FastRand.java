package moe._2b2t.essentials.utils;

public class FastRand
{
    private long seed;

    public FastRand()
    {
        seed = System.currentTimeMillis();
    }

    //xorShift算法
    public int nextInt(int bound)
    {
        seed ^= seed << 13;
        seed ^= seed >>> 17;
        seed ^= seed << 5;
        return (int) Math.abs(seed % bound);
    }

    public long nextLong()
    {
        seed ^= seed << 13;
        seed ^= seed >>> 17;
        seed ^= seed << 5;
        return seed;
    }
}
