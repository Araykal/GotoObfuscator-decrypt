package uwu.narumi.deobfuscator.transformer.impl.gotoplus;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import uwu.narumi.deobfuscator.Deobfuscator;
import uwu.narumi.deobfuscator.transformer.Transformer;
import uwu.narumi.deobfuscator.transformer.composed.GotoPlus;

import java.util.ArrayList;


public class GotoCNMTransformer extends Transformer {

    @Override
    public void transform(Deobfuscator deobfuscator) throws Exception {
        for (ClassNode cn : deobfuscator.classes()) {
            for (MethodNode method : cn.methods) {
                ArrayList<AbstractInsnNode> removeNodes = new ArrayList<>();
                for (int i = 0; i < method.instructions.toArray().length; i++) {
                    AbstractInsnNode insn = method.instructions.get(i);
                    if (method.instructions.toArray().length > i + 3) {
                        if (isInteger(insn)) {
                            AbstractInsnNode ldc = method.instructions.get(i + 2);
                            AbstractInsnNode i2l = method.instructions.get(i + 1);
                            AbstractInsnNode ixor = method.instructions.get(i + 3);
                            if (i2l.getOpcode() == I2L && isLong(ldc) && ixor.getOpcode() == LXOR) {
                                for (int i1 = 1; i1 < 4; i1++) {
                                    removeNodes.add(method.instructions.get(i + i1));
                                }
                                long num = (long)getInteger(insn) ^ getLong(ldc);
                                method.instructions.set(insn, getNumber(num));
                            }
                        }
                    }
                    /*                    if (method.instructions.toArray().length > i + 11) {
                        AbstractInsnNode athrow1 = method.instructions.get(i + 4);
                        AbstractInsnNode athrow2 = method.instructions.get(i + 11);
                        if (insn.getOpcode() == NOP && athrow1.getOpcode() == ATHROW && athrow2.getOpcode() == ATHROW) {
                            for (int i1 = 0; i1 < 12; i1++) {
                                AbstractInsnNode abstractInsnNode = method.instructions.get(i + i1);
                                if (!abstractInsnNode.equals(athrow1) && !abstractInsnNode.equals(athrow2)) {
                                    if (abstractInsnNode.getOpcode() != NOP) {
                                        break;
                                    }
                                }
                                removeNodes.add(method.instructions.get(i + i1));
                            }
                        }
                    }*/
/*                    if (method.instructions.toArray().length > i + 5) {
                        AbstractInsnNode ifNode = method.instructions.get(i + 1);
                        AbstractInsnNode pop = method.instructions.get(i + 3);
                        AbstractInsnNode aconst_null = method.instructions.get(i + 4);
                        AbstractInsnNode athrow0 = method.instructions.get(i + 5);
                        if (insn.getOpcode() == GETSTATIC
                                && ifNode instanceof JumpInsnNode
                                && pop instanceof  InsnNode
                                && aconst_null.getOpcode() == ACONST_NULL
                                && athrow0.getOpcode() == ATHROW) {
                            for (int i1 = 0; i1 < 6; i1++) {
                                removeNodes.add(method.instructions.get(i + i1));
                            }
                        }
                    }*/


/*                    if (method.instructions.size() > i + 4) {
                        AbstractInsnNode ifNode = method.instructions.get(i + 1);
                        AbstractInsnNode acont_null = method.instructions.get(i + 2);
                        AbstractInsnNode checkcast = method.instructions.get(i + 3);
                        AbstractInsnNode invoke = method.instructions.get(i + 4);
                        if (ifNode instanceof JumpInsnNode
                                && acont_null.getOpcode() == ACONST_NULL
                                && checkcast.getOpcode() == CHECKCAST) {
                            if (isInteger(insn) && invoke.getOpcode() == INVOKESTATIC) {
                                MethodInsnNode invoke1 = (MethodInsnNode) invoke;
                                for (int i1 = 0; i1 < 5; i1++) {
                                    removeNodes.add(method.instructions.get(i + i1));
                                }
                            } else if (insn.getOpcode() == ACONST_NULL && invoke.getOpcode() == INVOKEDYNAMIC) {
                                for (int i1 = 0; i1 < 5; i1++) {
                                    removeNodes.add(method.instructions.get(i + i1));
                                }
                            }
                        }
                    }*/
                }
                for (AbstractInsnNode insn : method.instructions.toArray()) {
                    if (removeNodes.contains(insn)) {
                        method.instructions.remove(insn);
                    }
                }
            }
        }
    }
}
