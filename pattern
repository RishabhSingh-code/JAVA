import java.util.Scanner;

public class pattern {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter three numbers: ");
        int a = input.nextInt();
        int b = input.nextInt();
        int c = input.nextInt();

        if (a != b && b != c && a != c) {
            if (a < b && b < c) {
                System.out.println("Strictly Increasing");
            } else if (a > b && b > c) {
                System.out.println("Strictly Decreasing");
            } else if (b > a && b > c) {
                System.out.println("Middle number is largest");
            } else {
                System.out.println("No specific pattern");
            }
        }
    }
}
