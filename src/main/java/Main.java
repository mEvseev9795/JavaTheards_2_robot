import java.util.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();


    public static void main(String[] args) {
        String LETTERS = "RLRFR";
        int LENGTH = 100;
        int THREAD_COUNT = 1000;
        char R = 'R';

        for (int i = 0; i < THREAD_COUNT; i++) {
            new Thread(() -> {
                synchronized (sizeToFreq) {  //стр. 15-25. Неправильно в этот блок затаскивать и логику подсчета букв,
                    int count = (int) generateRoute(LETTERS, LENGTH).chars() //стр. 16-18. так как получается что нет выиграша от многопоточности, 
                            .filter(ch -> ch == R) //так как всю нужную логику может выполнять только один поток за раз,
                            .count(); //все остальные будут ждать, когда монитор у sizeToFreq освободится.
//В блок synchronized надо заносить только то, что критично к работе в многопоточном режиме, то есть к тому, что может сломаться, если с этим работают многие потоки одновременно(разделяемые объекты)
                    if (sizeToFreq.containsKey(count)) {
                        sizeToFreq.put(count, sizeToFreq.get(count) + 1);
                    } else {
                        sizeToFreq.put(count, 1);
                    }
                }
            }).start();
        }

        print();
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static void print(){
        int maxRepeat = Collections.max(sizeToFreq.values());
        synchronized (sizeToFreq){
            sizeToFreq.forEach((k, v) -> {
                if (v.equals(maxRepeat)){
                    System.out.println("Самое частое кол-во повторений " + k + " - встретилось " + maxRepeat + " раз(а)");
                }
            });

            System.out.println("Другие размеры:");
            sizeToFreq.forEach((k, v) -> {
                if (!v.equals(maxRepeat)){
                    System.out.println(k + " - " + v + " раз(а)");
                }
            });
        }
    }
}
