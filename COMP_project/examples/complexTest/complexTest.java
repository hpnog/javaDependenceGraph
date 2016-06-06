public class test_1 {
    int funcao() {
        int sum = 0;
        sum = sum + 1;
        System.out.println(sum);
        return 0;
    }
}
public class test_2 {
	int outra_funcao() {
		int sum = 0;
		int i = 1;
		while (i < 11) {
			sum = sum + i;
			int j = 0;
			do {
				j=j+1;
			} while(j < 5);
			i = i + 1;
		}
		System.out.println(sum);
		System.out.println(i);
	}
}
public class test_3{
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