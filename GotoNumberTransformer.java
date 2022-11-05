package uwu.narumi.deobfuscator.transformer.impl.gotoplus;

import org.objectweb.asm.tree.*;
import uwu.narumi.deobfuscator.Deobfuscator;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class GotoNumberTransformer extends GotoPlusTransformer {

    @Override
    public void transform(Deobfuscator deobfuscator) throws Exception {
        for (ClassNode cl : deobfuscator.classes()) {
            cl.signature = null;
            for (MethodNode method : cl.methods) {
                method.signature = null;
            }
            for (FieldNode field : cl.fields) {
                field.signature = null;
            }
        }
        for (ClassNode classNode : deobfuscator.classes()) {
            for (MethodNode method : classNode.methods) {
                ArrayList<AbstractInsnNode> removeNodes = new ArrayList<>();
                for (int i = 0; i < method.instructions.toArray().length; i++) {
                    if (method.instructions.toArray().length > i + 8) {
                        AbstractInsnNode insn = method.instructions.get(i);
                        AbstractInsnNode getStatic = method.instructions.get(i + 2);
                        AbstractInsnNode jump = method.instructions.get(i + 3);
                        AbstractInsnNode print = method.instructions.get(i + 4);
                        AbstractInsnNode l = method.instructions.get(i + 5);
                        AbstractInsnNode aconst_Null = method.instructions.get(i + 7);
                        AbstractInsnNode athrow = method.instructions.get(i + 8);
                        if (athrow.getOpcode() == ATHROW
                                && aconst_Null.getOpcode() == ACONST_NULL
                                && jump instanceof JumpInsnNode
                                && print.getOpcode() == GETSTATIC
                                && l.getOpcode() == LDC
                                && getStatic.getOpcode() == GETSTATIC
                                && insn instanceof VarInsnNode
                                && insn.getNext() instanceof VarInsnNode) {
                            for (int i1 = 0; i1 <= 8; i1++) {
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

            /*    //             *//*for (int i = 0; i < method.instructions.toArray().length; i++) {
                   //*   AbstractInsnNode insn = method.instructions.get(i);
                    if (method.instructions.toArray().length > i + 2) {
                        if (isInteger(insn)) {
                            AbstractInsnNode ldc = method.instructions.get(i + 1);
                            AbstractInsnNode ixor = method.instructions.get(i + 2);
                            if (isInteger(ldc) && ixor.getOpcode() == IXOR) {
                                for (int i1 = 1; i1 < 3; i1++) {
                                    removeNodes.add(method.instructions.get(i + i1));
                                }
                     //           int num = getInteger(insn) ^ getInteger(ldc);
                                method.instructions.set(insn, getNumber(num));
                            }//
                        }//
                    }//
                } //*
                      if (method.instructions.toArray().length > i + 2) {
                        if (isInteger(insn)) {
                            AbstractInsnNode iconst_m1 = method.instructions.get(i + 1);
                            AbstractInsnNode ixor = method.instructions.get(i + 2);
                            if (iconst_m1.getOpcode() == ICONST_M1 && ixor.getOpcode() == IXOR) {
                                for (int i1 = 1; i1 < 3; i1++) {
                                    removeNodes.add(method.instructions.get(i + i1));
                                }
                                int num = ~getInteger(insn);
                                method.instructions.set(insn, getNumber(num));
                            }
                        }
                    }
                    *//*
 //*
            //num解密1
//*                    if (method.instructions.toArray().length >= i + 5) {
                   /*     if (isInteger(insn)) {
                            if (isInteger(insn.getNext().getNext())
                                    && insn.getNext().getNext().getNext().getNext().getOpcode() == LXOR) {
                                AbstractInsnNode l2i = method.instructions.get(i + 5);
                                if (l2i.getOpcode() == L2I) {
                                    for (int i1 = 1; i1 < 6; i1++) {
                                        removeNodes.add(method.instructions.get(i + i1));
                                    }
                                    int num = getInteger(insn) ^ getInteger(insn.getNext().getNext());
                                    method.instructions.set(insn, getNumber(num));
                                }
                            }
                        }
                    }
                    *//*


            //删除傻逼的sout
    *//*                for (int i = 0; i < method.instructions.toArray().length; i++) {
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
                }*//*
        }*/
        }
    }
}
