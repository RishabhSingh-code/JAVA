import java.util.Scanner;

public class SwapNumbers {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        System.out.print("Enter first number: ");
        int a = s.nextInt();
        System.out.print("Enter second number: ");
        int b = s.nextInt();

        System.out.println("\n With 3rd variable");
        System.out.println("Before: a=" + a + " b=" + b);

        int temp = a;
        a = b;
        b = temp;
        System.out.println("After: a=" + a + " b=" + b);

        System.out.println("\n Without 3rd variable");
        System.out.println("Before: a=" + a + " b=" + b);
        a = a + b;
        b = a - b;
        a = a - b;
        System.out.println("After: a=" + a + " b=" + b);

        s.close();
    }
}
