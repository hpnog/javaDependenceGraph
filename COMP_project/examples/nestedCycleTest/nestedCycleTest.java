public class test3{
	int main() {
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