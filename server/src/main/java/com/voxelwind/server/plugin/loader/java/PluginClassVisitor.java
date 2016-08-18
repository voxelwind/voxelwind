package com.voxelwind.server.plugin.loader.java;

import static org.objectweb.asm.Opcodes.ASM5;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;

import javax.annotation.Nullable;
import java.util.Optional;

public class PluginClassVisitor extends ClassVisitor {
    private static final String PLUGIN_DESCRIPTOR = "Lcom/voxelwind/api/plugin/Plugin;";

    private String className;
    private PluginAnnotationVisitor annotationVisitor;

    public PluginClassVisitor() {
        super(ASM5);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name;
    }

    @Override @Nullable
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (visible && desc.equals(PLUGIN_DESCRIPTOR)) {
            return this.annotationVisitor = new PluginAnnotationVisitor(className);
        }

        return null;
    }

    public String getClassName() {
        return className;
    }

    public PluginAnnotationVisitor getAnnotationVisitor() {
        return annotationVisitor;
    }

    public Optional<PluginInformation> information() {
        return annotationVisitor == null ? Optional.empty() : Optional.of(annotationVisitor.getInformation());
    }
}
