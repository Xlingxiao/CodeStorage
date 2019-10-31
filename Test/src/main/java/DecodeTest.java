import utils.DecodeUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DecodeTest implements Runnable {
    private DecodeUtil decodeUtil;
    private static ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();

    private DecodeTest(DecodeUtil decodeUtil) {
        this.decodeUtil = decodeUtil;
    }

    public static void main(String[] args) throws InterruptedException {

        DecodeUtil util = new DecodeUtil();
        List<Thread> decodeThread = new LinkedList<>();
        for (int i = 0; i < 2; i++) {
            DecodeTest test = new DecodeTest(util);
            Thread thread = new Thread(test);
            decodeThread.add(thread);
            thread.start();
        }

        for (Thread thread : decodeThread) {
            thread.join();
        }
        System.out.println(String.format("count--->%s", queue.size()));
        System.out.println("End！！！");
    }
    @Override
    public void run() {
//        String appId = "wx4f4bc4dec97d474b";
        String sessionKey = "tiihtNczf5v6AKRyjwEUhQ==";
        String encryptedData = "CiyLU1Aw2KjvrjMdj8YKliAjtP4gsMZMQmRzooG2xrDcvSnxIMXFufNstNGTyaGS9uT5geRa0W4oTOb1WT7fJlAC+oNPdbB+3hVbJSRgv+4lGOETKUQz6OYStslQ142dNCuabNPGBzlooOmB231qMM85d2/fV6ChevvXvQP8Hkue1poOFtnEtpyxVLW1zAo6/1Xx1COxFvrc2d7UL/lmHInNlxuacJXwu0fjpXfz/YqYzBIBzD6WUfTIF9GRHpOn/Hz7saL8xz+W//FRAUid1OksQaQx4CMs8LOddcQhULW4ucetDf96JcR3g0gfRK4PC7E/r7Z6xNrXd2UIeorGj5Ef7b1pJAYB6Y5anaHqZ9J6nKEBvB4DnNLIVWSgARns/8wR2SiRS7MNACwTyrGvt9ts8p12PKFdlqYTopNHR1Vf7XjfhQlVsAJdNiKdYmYVoKlaRv85IfVunYzO0IKXsyl7JCUjCpoG20f0a04COwfneQAGGwd5oa+T8yO5hzuyDb/XcxxmK01EpqOyuxINew==";
        String iv = "r7BXXKkLb8qrSNn05n0qiA==";
        for (int i = 0; i < 1; i++) {
            try {
                String string = decodeUtil.decrypt(sessionKey, iv, encryptedData);
                queue.add(string);
            } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | NoSuchPaddingException | IOException | BadPaddingException | IllegalBlockSizeException | InvalidParameterSpecException e) {
                e.printStackTrace();
            }
        }
        System.out.println(String.format("End--->%s", Thread.currentThread().getName()));
    }
}
