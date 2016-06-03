
public class test {
	int main() {
		int n = 0;
		int i = 1;
		int sum = 0;
		while(i <= n){
			sum = 0;
			int j = 1;
			while(j <= i){
				sum = sum + j;
				j = j + 1;
			}
			System.out.println(sum + i);
			i = i + 1;
		}
		System.out.println(sum + i);
		return i;
	}
}
