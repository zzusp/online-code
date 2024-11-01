package sqlrunner;

public class Main {


    public static void main(String[] args) {
//        int[] arr = {3,2,2,3};
        int[] arr = {0,1,2,2,3,0,4,2};
        changeAndRemove(arr, 2);
        for (int i : arr) {
            System.out.println(i);
        }
    }

    public static void changeAndRemove(int[] arr, int val) {
        int temp;
        int i = 0;
        int len = 0;
        while (i < arr.length) {
            if (arr[i] == val) {
                int j = i + 1;
                while (j < arr.length && arr[j] == val) {
                    j++;
                }
                if (j < arr.length) {
                    temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                }
            }
            if (arr[i] != val) {
                len++;
            }
            i++;
        }
    }
}
