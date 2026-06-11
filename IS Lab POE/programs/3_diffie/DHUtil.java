public class DHUtil {
    public static final long P = 23;
    public static final long Q = 5;

    public static long generatePublicValue(long privateKey){
        return (long) (Math.pow(Q, privateKey) % P);
    }

    public static long computeSharedSecret(long publicValue,long privateKey){
        return (long)(Math.pow(publicValue, privateKey) % P);
    }
}
