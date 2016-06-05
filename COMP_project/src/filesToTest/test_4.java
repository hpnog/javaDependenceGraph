public class test3{
    int main() {
        int sum = 0;
        int i = 1;
        while (i < 11) {
            sum = sum + i;
            i = i + 1;
            i++;
        }
        if(i > 0) {
            System.out.println(sum);
            System.out.println(i);
        }
    }
}