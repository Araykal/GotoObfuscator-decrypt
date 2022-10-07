
import org.objectweb.asm.tree.*;

import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * @description:
 * @author: AckerRun
 * @time: 2022/9/10
 */
public class GotoNumberTransformer extends GotoPlusTransformer {

    @Override
    public void transform(Deobfuscator deobfuscator) throws Exception {
        for (ClassNode classNode : deobfuscator.classes()) {
            classNode.signature = null;
            for (FieldNode field : classNode.fields) {
                field.signature = null;
            }
            for (MethodNode method : classNode.methods) {
                method.signature = null;
                ArrayList<AbstractInsnNode> removeNodes = new ArrayList<>();

/*                for (int i = 0; i < method.instructions.toArray().length; i++) {
                    AbstractInsnNode insn = method.instructions.get(i);

                    if (method.instructions.toArray().length >= i + 3) {
                        if (isInteger(insn) && isInteger(insn.getNext()) && insn.getNext().getNext().getOpcode() == IXOR) {
                            for (int i1 = 1; i1 < 3; i1++) {
                                removeNodes.add(method.instructions.get(i + i1));
                            }
                            int num = getInteger(insn) ^ getInteger(insn.getNext());
                            method.instructions.set(insn, new LdcInsnNode(num));
                        }
                    }
                }*/

                for (int i = 0; i < method.instructions.toArray().length; i++) {
                    if (method.instructions.toArray().length > i + 10) {
                        AbstractInsnNode insn = method.instructions.get(i);
                        AbstractInsnNode getStatic = method.instructions.get(i + 2);
                        AbstractInsnNode jump = method.instructions.get(i + 3);
                        AbstractInsnNode print = method.instructions.get(i + 4);
                        AbstractInsnNode l = method.instructions.get(i + 5);
                        AbstractInsnNode aconst_Null = method.instructions.get(i + 9);
                        AbstractInsnNode athrow = method.instructions.get(i + 10);
                        if (athrow.getOpcode() == ATHROW
                                && aconst_Null.getOpcode() == ACONST_NULL
                                && jump instanceof JumpInsnNode
                                && print.getOpcode() == GETSTATIC
                                && l.getOpcode() == LDC
                                && getStatic.getOpcode() == GETSTATIC
                                && insn instanceof VarInsnNode
                                && insn.getNext() instanceof VarInsnNode) {
                            for (int i1 = 0; i1 <= 10; i1++) {
                                removeNodes.add(method.instructions.get(i + i1));
                            }
                        }
                    }
                }

                for (AbstractInsnNode insn : method.instructions.toArray()) {
                    if (removeNodes.contains(insn)) {
                        method.instructions.remove(insn);
                        // System.out.println("remove");
                    }
                }
            }
        }
    }

    //删除num
    /*                    if (method.instructions.toArray().length >= i + 5) {
                        if (isInteger(insn) && isInteger(insn.getNext().getNext()) && insn.getNext().getNext().getNext().getNext().getOpcode() == LXOR) {
                            AbstractInsnNode l2i = method.instructions.get(i + 5);
                            if (l2i.getOpcode() == L2I) {
                                System.out.println("cnmm,");
                                for (int i1 = 1; i1 < 6; i1++) {
                                    removeNodes.add(method.instructions.get(i + i1));
                                }
                                int num = getInteger(insn) ^ getInteger(insn.getNext().getNext());
                                method.instructions.set(insn, new LdcInsnNode(num));
                            }
                        }
                    }*/


    //删除傻逼的sout
    /*                for (int i = 0; i < method.instructions.toArray().length; i++) {
                    if (method.instructions.toArray().length > i + 10) {
                        AbstractInsnNode insn = method.instructions.get(i);
                        AbstractInsnNode getStatic = method.instructions.get(i + 2);
                        AbstractInsnNode jump = method.instructions.get(i + 3);
                        AbstractInsnNode print = method.instructions.get(i + 4);
                        AbstractInsnNode l = method.instructions.get(i + 5);
                        AbstractInsnNode aconst_Null = method.instructions.get(i + 9);
                        AbstractInsnNode athrow = method.instructions.get(i + 10);
                        if (athrow.getOpcode() == ATHROW
                                && aconst_Null.getOpcode() == ACONST_NULL
                                && jump instanceof JumpInsnNode
                                && print.getOpcode() == GETSTATIC
                                && l.getOpcode() == LDC
                                && getStatic.getOpcode() == GETSTATIC
                                && insn instanceof VarInsnNode
                                && insn.getNext() instanceof VarInsnNode) {
                            for (int i1 = 0; i1 <= 10; i1++) {
                                removeNodes.add(method.instructions.get(i + i1));
                            }
                        }
                    }
                }*/
}
