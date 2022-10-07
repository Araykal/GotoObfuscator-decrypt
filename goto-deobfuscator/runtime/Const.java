

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;


/**
 * @description:
 * @author: AckerRun
 * @time: 2022/9/10
 */
public class Const {
    private static final HashMap map = new HashMap();

    public static Object get(String key) {
        return map.get(key);
    }

    static {
        try {
            InputStream stream = new FileInputStream("E:\\javaobf\\deobf\\Const");
            DataInputStream dis = new DataInputStream(stream);
            Throwable var2 = null;

            try {
                int size = dis.readInt();

                for (int i = 0; i < size; ++i) {
                    String sha = dis.readUTF();
                    int type = dis.readInt();
                    switch (type) {
                        case 0:
                            String s = dis.readUTF();
                            map.put(sha, s);
                            break;
                        case 1:
                            int j = dis.readInt();
                            map.put(sha, j);
                            break;
                        case 2:
                            long l = dis.readLong();
                            map.put(sha, l);
                            break;
                        case 3:
                            float f = dis.readFloat();
                            map.put(sha, f);
                            break;
                        case 4:
                            double d = dis.readDouble();
                            map.put(sha, d);
                    }
                }
            } catch (Throwable var22) {
                var2 = var22;
                throw var22;
            } finally {
                if (dis != null) {
                    if (var2 != null) {
                        try {
                            dis.close();
                        } catch (Throwable var21) {
                            var2.addSuppressed(var21);
                        }
                    } else {
                        dis.close();
                    }
                }

            }
        } catch (Throwable var24) {
            var24.printStackTrace();
        }
    }
}
