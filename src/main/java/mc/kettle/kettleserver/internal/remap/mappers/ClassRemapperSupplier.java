package mc.kettle.kettleserver.internal.remap.mappers;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

public interface ClassRemapperSupplier {
    default ClassRemapper getClassRemapper(ClassVisitor classVisitor) {
        return new ClassRemapper(classVisitor, (Remapper) this);
    }
}