

import org.objectweb.asm.tree.*;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * @description:
 * @author: AckerRun
 * @time: 2022/9/10
 */
public class GotoPlusTransformer extends Transformer {

    private static int encrypt1 = 40;
    private static String[] encrypt2;

    @Override
    public void transform(Deobfuscator deobfuscator) throws Exception {
//        deString1("1","2");
        for (ClassNode cn : deobfuscator.classes()) {
            cn.signature = null;
            for (MethodNode method : cn.methods) {
                method.signature = null;
/*                InsnList insnList = new InsnList();
                insnList.add(new FieldInsnNode(GETSTATIC,"java/lang/System","out","Ljava/io/PrintStream;"));
                insnList.add(new LdcInsnNode(method.name));
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL,"java/io/PrintStream","println","(Ljava/lang/String;)V"));
                method.instructions.insert(method.instructions.getFirst(),insnList);*/
                if (method.localVariables != null) {
                    for (int i = 0; i < method.localVariables.size(); i++) {
                        LocalVariableNode localVariable = method.localVariables.get(i);
                        if (localVariable.name.equals("this")) continue;
                        localVariable.name = "var" + i;
                    }
                }
            }
            for (FieldNode field : cn.fields) {
                field.signature = null;
            }
//            removeFlow3(cn);
        }
    }

    private static String deString1(String data, String key) {
        try {
            byte[] sb = MessageDigest.getInstance("MD5").digest(key.getBytes(StandardCharsets.UTF_8));
            String mode = "DES";
            SecretKeySpec secretKeySpec = new SecretKeySpec(Arrays.copyOf(sb, 8), mode);
            Cipher cipher = Cipher.getInstance(mode);
            cipher.init(2,secretKeySpec);
            byte[] bytes = cipher.doFinal(Base64.getDecoder().decode(data.getBytes(StandardCharsets.UTF_8)));
            return new String(bytes,StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void removeFlow3(ClassNode cn) {
        for (MethodNode method : cn.methods) {
            int length = method.instructions.toArray().length;
            List<AbstractInsnNode> removeList = new ArrayList<>();
            int isGOTO = 0;
            boolean canAdd = false;
            for (int i = 0; i < length; i++) {
                AbstractInsnNode insn = method.instructions.toArray()[i];
                if (insn.getOpcode() == POP) {
                    if (i + 12 < length) {
                        if (method.instructions.get(i + 12).getOpcode() == GOTO) {
                            canAdd = true;
                        }
                    }
                }
                if (insn.getOpcode() == GOTO) {
                    isGOTO++;
                    if (isGOTO == 2) {
                        canAdd = false;
                        isGOTO = 0;
                        removeList.add(insn);
                    }
                }
                if (canAdd) {
                    if (!(insn.getOpcode() >= 182 && insn.getOpcode() <= 185)) {
                        removeList.add(insn);
                    }
                }
            }
            for (AbstractInsnNode abstractInsnNode : removeList) {
                method.instructions.remove(abstractInsnNode);
            }
        }
    }

    private void removeFlow2(ClassNode cn) {
        for (MethodNode method : cn.methods) {
            int length = method.instructions.toArray().length;
            List<AbstractInsnNode> removeList = new ArrayList<>();
            int ithorow = 0;
            boolean canAdd = false;

            for (int i = 0; i < length; i++) {
                AbstractInsnNode insn = method.instructions.toArray()[i];
                if (insn.getOpcode() == GETSTATIC) {
                    if (i + 23 < length) {
                        if (method.instructions.get(i + 23).getOpcode() == ATHROW) {
                            System.out.println("WWW");
                            canAdd = true;
                        }
                    }
                }
                if (insn.getOpcode() == ATHROW) {
                    ithorow++;
                    if (ithorow == 3) {
                        canAdd = false;
                        ithorow = 0;
                        removeList.add(insn);
                    }
                }
                if (canAdd) {
                    if (!(insn.getOpcode() >= 182 && insn.getOpcode() <= 185)) {
                        removeList.add(insn);
                    }
                }
//                System.out.print(insn);
//                System.out.println("                    " + insn.getOpcode());
            }
            for (AbstractInsnNode abstractInsnNode : removeList) {
                method.instructions.remove(abstractInsnNode);
            }
//            System.out.println(cn.name);
//            System.out.println(method.name);
        }
    }

    private void removeFlow(ClassNode cn) {
        cn.signature = null;
        for (MethodNode method : cn.methods) {
            method.signature = null;
            int length = method.instructions.toArray().length;
            List<AbstractInsnNode> removeList = new ArrayList<>();
            int isGoto = 0;
            boolean canAdd = false;
            for (int i = 0; i < length; i++) {
                AbstractInsnNode insn = method.instructions.toArray()[i];
                if (insn.getOpcode() == GOTO) {
                    isGoto++;
                    if (i + 15 < length) {
                        if (method.instructions.get(i + 15).getOpcode() == GOTO && method.instructions.get(i + 12).getOpcode() == GOTO) {
                            if (isGoto == 1) {
                                canAdd = true;
                            }
                        }
                    }
                    if (isGoto == 3) {
                        isGoto = 0;
                        canAdd = false;
                        removeList.add(insn);
                    }
                }
                if (canAdd) {
                    if (!(insn.getOpcode() >= 182 && insn.getOpcode() <= 185)) {
                        removeList.add(insn);
                    }
                }
            }
            for (AbstractInsnNode abstractInsnNode : removeList) {
                method.instructions.remove(abstractInsnNode);
            }
        }
    }
}
