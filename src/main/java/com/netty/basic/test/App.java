package netty.test;

public class App {
    public static void main(String[] args) {
        System.out.println(new ChildrenClass() instanceof FatherClass);
        System.out.println(new ChildrenClass() instanceof FatherClass2);
    }
}
