

public class test {

//    private Configuration configuration = null;
//    private Connection connection = null;
//
//    private static HbaseUtil instance = null;
//    public static synchronized HbaseUtil getInstance(){
//        if(instance == null){
//            instance = new HbaseUtil();
//        }
//        return instance;
//    }

    private int a = 0;

    public static test instance = null;

    public static test getInstance(){
        if(instance == null){
            instance = new test();
        }
        return instance;
//        instance = new test();
//        return instance;
    }

    public void setA(int a){this.a=a;}


    public static void main(String[] args){
        test q = test.getInstance();
        System.out.println(q.a);
        q.setA(2);
        System.out.println(q.a);

        test qq = test.getInstance();
        System.out.println(qq.a);

    }

}
