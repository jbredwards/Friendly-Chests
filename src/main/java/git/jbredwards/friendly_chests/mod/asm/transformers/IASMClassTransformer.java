package git.jbredwards.friendly_chests.mod.asm.transformers;

import com.google.common.collect.Lists;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * A version of IClassTransformer that has some basic utility functions.
 * <p>This class exists so I can easily move this mod away from requiring Fluidlogged API.
 * @author jbred
 *
 */
@FunctionalInterface
public interface IASMClassTransformer extends IClassTransformer, Opcodes
{
    boolean DEOBFUSCATED = FMLLaunchHandler.isDeobfuscatedEnvironment();
    void transform(@Nonnull final ClassNode classNode);

    @Nonnull
    @Override
    default byte[] transform(@Nonnull final String name, @Nonnull final String transformedName, @Nonnull final byte[] basicClass) {
        @Nonnull final ClassNode classNode = new ClassNode();
        new ClassReader(basicClass).accept(classNode, 0);
        transform(classNode);

        // writes the changes
        @Nonnull final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    default void overwriteMethod(@Nonnull final ClassNode classNode, @Nonnull final String deobfName, @Nonnull final String obfName, @Nonnull final String desc, @Nonnull final Consumer<GeneratorAdapter> generator) {
        @Nonnull final MethodNode method = new MethodNode(ACC_PUBLIC, DEOBFUSCATED ? deobfName : obfName, desc, null, null);
        classNode.methods.removeIf(candidate -> {
            if(candidate.name.equals(method.name) && candidate.desc.equals(method.desc)) {
                method.access = candidate.access;
                return true;
            }

            return false;
        });

        @Nonnull final LinkedList<Type> generatedHookDescriptor = Lists.newLinkedList();
        @Nonnull final ArrayList<Type> mappedArguments = (method.access & ACC_STATIC) != 0 ? Lists.newArrayList() : Lists.newArrayList(Type.getObjectType(classNode.name));
        for(@Nonnull final Type type : Type.getArgumentTypes(desc)) for(int i = type.getSize(); i > 0; i--) mappedArguments.add(type);
        @Nonnull final GeneratorAdapter adapter = new GeneratorAdapter(new MethodVisitor(ASM5, method) {
            @Override
            public void visitVarInsn(final int opcode, final int var) {
                generatedHookDescriptor.add(mappedArguments.get(var));
                super.visitVarInsn(opcode, var);
            }

            @Override
            public void visitMethodInsn(final int opcode, @Nonnull final String owner, @Nonnull final String name, @Nonnull final String desc, final boolean itf) {
                super.visitMethodInsn(opcode, owner, name, desc, itf);

                final int size = Type.getArgumentTypes(desc).length;
                for(int i = 0; i < size; i++) generatedHookDescriptor.removeLast();
                if(opcode != INVOKESTATIC) generatedHookDescriptor.removeLast();

                @Nonnull final Type returnType = Type.getReturnType(desc);
                if(returnType != Type.VOID_TYPE) generatedHookDescriptor.add(returnType);
            }
        }, method.access, method.name, method.desc);

        generator.accept(adapter);
        adapter.invokeStatic(Type.getObjectType(getClass().getName().replace('.', '/') + "$Hooks"), new Method(deobfName, Type.getReturnType(desc), generatedHookDescriptor.toArray(new Type[0])));
        adapter.returnValue();
        classNode.methods.add(method);
    }
}