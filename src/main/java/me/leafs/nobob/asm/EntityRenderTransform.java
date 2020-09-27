package me.leafs.nobob.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;

public class EntityRenderTransform implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        // find the entityrenderer class
        if (!transformedName.equals("net.minecraft.client.renderer.EntityRenderer")) {
            return basicClass;
        }

        ClassReader reader = new ClassReader(basicClass);
        ClassNode node = new ClassNode();

        reader.accept(node, 0);

        for (MethodNode method : node.methods) {
            String methodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(node.name, method.name, method.desc);
            if (!methodName.equals("setupCameraTransform") && !methodName.equals("func_78479_a")) {
                continue;
            }

            // go every method instruction if it's in setupCameraTransform
            ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();
            boolean edited = false;

            while (iterator.hasNext()) {
                AbstractInsnNode insn = iterator.next();
                if (!(insn instanceof JumpInsnNode)) {
                    continue;
                }

                AbstractInsnNode previous = insn.getPrevious();
                if (!(previous instanceof FieldInsnNode)) {
                    continue;
                }

                FieldInsnNode prevField = (FieldInsnNode) previous;

                // find bobbing check in camera renderer
                if (prevField.name.equals("viewBobbing") || prevField.name.equals("field_74336_f")) {
                    InsnList falsify = new InsnList();

                    // inject `&& false` into if statement to make sure always false
                    falsify.add(new LdcInsnNode(0));
                    falsify.add(new JumpInsnNode(Opcodes.IFEQ, ((JumpInsnNode) insn).label));

                    method.instructions.insert(insn, falsify);

                    edited = true;
                    break;
                }
            }

            // if the method was edited, break
            if (edited) {
                break;
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        node.accept(writer);

        return writer.toByteArray();
    }
}
