package com.voxelwind.server.plugin.loader.java;

import org.objectweb.asm.AnnotationVisitor;

import static org.objectweb.asm.Opcodes.ASM5;

public class PluginAnnotationVisitor extends AnnotationVisitor {
    private final String className;
    private Type type = Type.INFORMATION;
    private final PluginInformation information;

    public PluginAnnotationVisitor(String className) {
        super(ASM5);
        this.className = className;
        this.information = new PluginInformation(className);
    }

    @Override
    public void visit(String name, Object value) {
        switch (type) {
            case INFORMATION:
                switch (name) {
                    case "id":
                        information.setId((String) value);
                        break;
                    case "author":
                        information.setAuthor((String) value);
                        break;
                    case "version":
                        information.setVersion((String) value);
                        break;
                    case "website":
                        information.setWebsite((String) value);
                        break;
                }
                break;
            case DEPENDENCIES:
                information.getDependencies().add((String) value);
                break;
            case SOFT_DEPENDENCIES:
                information.getSoftDependencies().add((String) value);
                break;
        }

        super.visit(name, value);
    }

    @Override
    public AnnotationVisitor visitArray(String s) {
        switch (s) {
            case "dependencies":
                type = Type.DEPENDENCIES;
                break;
            case "softDependencies":
                type = Type.SOFT_DEPENDENCIES;
                break;
        }
        return super.visitArray(s);
    }

    public String getClassName() {
        return className;
    }

    public Type getType() {
        return type;
    }

    public PluginInformation getInformation() {
        return information;
    }

    private enum Type {
        INFORMATION,
        DEPENDENCIES,
        SOFT_DEPENDENCIES
    }
}
