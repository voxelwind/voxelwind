package com.voxelwind.server.game.entities.visitor;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;

import javax.annotation.Nullable;
import java.util.Optional;

import static org.objectweb.asm.Opcodes.ASM5;

public class EntityClassVisitor extends ClassVisitor {

    private static final String SPAWNABLE_ANNOTATION = "Lcom/voxelwind/server/game/entities/Spawnable;";

    private String entityInterface;
    private boolean hasSpawnableAnnotation;

    public EntityClassVisitor() {
        super(ASM5);
    }

    @Override
    public void visit(int var1, int var2, String var3, String var4, String var5, String[] var6) {
        for (String s : var6) {
            if (s.startsWith("com/voxelwind/api/game/entities/")) {
                this.entityInterface = s.replaceAll("/", ".");
            }
        }
    }

    @Override @Nullable
    public AnnotationVisitor visitAnnotation(String name, boolean visible) {
        if (visible && SPAWNABLE_ANNOTATION.equals(name)) {
            this.hasSpawnableAnnotation = true;
        }

        return null;
    }

    public Optional<String> getEntityClass() {
        return !this.hasSpawnableAnnotation ? Optional.empty() : Optional.ofNullable(this.entityInterface);
    }

}
