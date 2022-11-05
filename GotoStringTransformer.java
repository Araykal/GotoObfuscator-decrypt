package uwu.narumi.deobfuscator.transformer.impl.gotoplus;

import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;
import uwu.narumi.deobfuscator.Deobfuscator;
import uwu.narumi.deobfuscator.analysis.stack.ConstantValue;
import uwu.narumi.deobfuscator.analysis.stack.IConstantReferenceHandler;
import uwu.narumi.deobfuscator.asm.Access;
import uwu.narumi.deobfuscator.asm.InstructionModifier;
import uwu.narumi.deobfuscator.asm.Instructions;
import uwu.narumi.deobfuscator.asm.References;
import uwu.narumi.deobfuscator.transformer.composed.GotoPlus;
import uwu.narumi.deobfuscator.util.format.Strings;
import uwu.narumi.deobfuscator.vm.IVMReferenceHandler;
import uwu.narumi.deobfuscator.vm.Sandbox;
import uwu.narumi.deobfuscator.vm.VM;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.StreamSupport;

public class GotoStringTransformer extends GotoPlus implements IVMReferenceHandler, IConstantReferenceHandler {

    private static final String ALLOWED_CALLS = "(java/lang/String).*";
    private FieldInsnNode decryptedArrayField;
    private String[] decryptedFieldValue;

    @Override
    public void transform(Deobfuscator deobfuscator) throws Exception {
        for (ClassNode cn : deobfuscator.classes()) {

            if (hasGotoBlock(cn)) {
                MethodNode clinit = getStaticInitializer(cn);
                if (clinit == null) {
                    return;
                }
                if (clinit.instructions.size() > 2000) {
                    return;
                }
                ClassNode proxyClass = Sandbox.createClassProxy("ProxyClass");
                MethodNode callMethod = Sandbox.copyMethod(clinit);
                callMethod.name = "clinitProxy";
                callMethod.access = ACC_PUBLIC | ACC_STATIC;

                proxyClass.methods.add(callMethod);

                // add decryption methods、
                cn.methods.stream().filter(m -> m.desc.equals("(Ljava/lang/String;I)V")
                        || (m.instructions.size() < 4
                        && (m.desc.equals("(Ljava/lang/String;)I")
                        || m.desc.equals("(Ljava/lang/String;)[C")
                        || m.desc.equals("()[Ljava/lang/String;")
                        || m.desc.equals("(Ljava/lang/String;)Ljava/lang/String;")
                        || m.desc.equals("([Ljava/lang/String;)V")))).forEach(m -> {
                    InsnList insnList = new InsnList();
                    m.instructions.insert(insnList);
                    MethodNode copy = Sandbox.copyMethod(m);
                    proxyClass.methods.add(copy);
                });

                /*.filter(m -> m.desc.equals("[Ljava/lang/String;") || m.desc.equals("Ljava/lang/String;"))*/
                cn.fields.stream().filter(f -> f.desc.equals("[Ljava/lang/String;") || f.desc.equals("J") || f.desc.equals("C") || f.desc.equals("B") || f.desc.equals("Z") || f.desc.equals("D") || f.desc.equals("F") || f.desc.equals("I")).forEach(f -> {
                    proxyClass.fields.add(f);
                });
                Map<String, String> singleMap = Collections.singletonMap(cn.name, proxyClass.name);
                proxyClass.methods.stream().map(m -> m.instructions.toArray()).flatMap(Arrays::stream)
                        .forEach(ain -> References.remapClassRefs(singleMap, ain));
                try {
                    invokeVMAndReplace(proxyClass, cn);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void invokeVMAndReplace(ClassNode proxy, ClassNode realClass) throws Throwable {
        VM vm = VM.constructNonInitializingVM(this);
        vm.explicitlyPreload(proxy, true);
        vm.explicitlyPreload(realClass, true);
        Class<?> callProxy = vm.loadClass("ProxyClass");
        try {
            callProxy.getMethod("clinitProxy").invoke(null); // invoke cut clinit, fiel ds
            // in original class in vm get set
        } catch (Throwable e) {
            //e.printStackTrace();
        }
        realClass.methods.forEach(m -> {
            decryptedArrayField = null;
            m.instructions.forEach(ain -> {
                if (isLocalField(realClass, ain) && ((FieldInsnNode) ain).desc.equals("[Ljava/lang/String;")) {
                    decryptedArrayField = (FieldInsnNode) ain;
                    try {
                        decryptedFieldValue = (String[]) callProxy.getField(((FieldInsnNode) ain).name).get(null);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            });

            InstructionModifier modifier = new InstructionModifier();
            loopConstantFrames(realClass, m, this, (ain, frame) -> {
                tryReplaceFieldLoads(realClass, callProxy, m, ain, frame, modifier);
            });

            modifier.apply(m);
        });
    }

    /**
     * Replace decrypted String[] and String fields in the
     * code. This is the hardest part
     */
    private void tryReplaceFieldLoads(ClassNode cn, Class<?> callProxy, MethodNode m, AbstractInsnNode ain,
                                      Frame<ConstantValue> frame, InstructionModifier modifier) {
        try {

            if (ain.getOpcode() == INVOKESTATIC) {
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.desc.equals("()[Ljava/lang/String;")) {
                    for (Field declaredField : callProxy.getDeclaredFields()) {
                        if (declaredField.get(null) instanceof String[]) {
                            String[] deStrs = (String[]) declaredField.get(null);
                            String decrypedString = deStrs[getNumber(ain.getNext()).intValue()];
                            modifier.remove(ain.getNext().getNext());
                            modifier.remove(ain.getNext());
                            System.out.println(decrypedString);
                            modifier.replace(ain, new LdcInsnNode(decrypedString));
                        }
                    }
                }
            }

            if (ain.getOpcode() == GETSTATIC) {
                FieldInsnNode fin = (FieldInsnNode) ain;
                if (isLocalField(cn, fin) && fin.desc.equals("Ljava/lang/String;")) {
                    String decrypedString = (String) callProxy.getDeclaredField(fin.name).get(null);
                    if (decrypedString != null) {
                        // i don't know why we need NOP, but it only
                        // works that way :confusion:
                        modifier.replace(ain, new LdcInsnNode(decrypedString), new InsnNode(NOP));
                    }
                }
            } else if (ain.getOpcode() == AALOAD) {
                ConstantValue previous = frame.getStack(frame.getStackSize() - 1);
                ConstantValue prePrevious = frame.getStack(frame.getStackSize() - 2);
                if (previous.getValue() != null) {
                    int arrayIndex = previous.getAsInteger();
                    Object reference = prePrevious.getValue();
                    if (reference instanceof String[]) {
                        String[] ref = (String[]) reference;
                        String decryptedString = ref[arrayIndex];
                        Strings.isHighUTF(decryptedString);
                        modifier.replace(ain, new InsnNode(POP2), new LdcInsnNode(decryptedString));
                    }
                }
            }
        } catch (Throwable t) {
//            t.printStackTrace();
        }
    }

    private boolean isLocalField(ClassNode cn, AbstractInsnNode ain) {
        if (ain.getOpcode() != GETSTATIC)
            return false;
        FieldInsnNode fin = ((FieldInsnNode) ain);
        // could be either array or normal string field, two
        // cases
        return fin.owner.equals(cn.name);
    }


    private boolean hasGotoBlock(ClassNode cn) {
        if (Access.isInterface(cn.access)) // TODO maybe
            // interfaces get string encrypted too, but proxy
            // would not be
            // working because static methods in interfaces are
            // not allowed
            return false;
        MethodNode mn = getStaticInitializer(cn);
        if (mn == null)
            return false;
        return StreamSupport.stream(mn.instructions.spliterator(), false).anyMatch(
                ain -> ain.getType() == AbstractInsnNode.LDC_INSN &&
                        Strings.isHighUTF(((LdcInsnNode) ain).cst.toString()));
    }

    @Override
    public Object getFieldValueOrNull(BasicValue v, String owner, String name, String desc) {
        return decryptedArrayField != null && decryptedArrayField.owner.equals(owner) &&
                decryptedArrayField.name.equals(name) && decryptedArrayField.desc.equals(desc) ? decryptedFieldValue : null;
    }

    @Override
    public Object getMethodReturnOrNull(BasicValue v, String owner, String name, String desc, List<? extends ConstantValue> values) {
        return null;
    }

    @Override
    public ClassNode tryClassLoad(String name) {
        return null;
    }
}
