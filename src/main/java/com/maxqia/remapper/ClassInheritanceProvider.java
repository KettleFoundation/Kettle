package com.maxqia.remapper;

import net.md_5.specialsource.provider.InheritanceProvider;

import java.util.Collection;
import java.util.HashSet;

public class ClassInheritanceProvider implements InheritanceProvider {

    @Override
    public Collection<String> getParents(String className) {
        className = Transformer.remapper.map(className);

        try {
            Collection<String> parents = new HashSet<>();
            Class<?> reference = Class.forName(className.replace('/', '.').replace('$', '.'), false, this.getClass().getClassLoader());
            Class<?> extend = reference.getSuperclass();
            if (extend != null) {
                parents.add(Utils.reverseMap(extend));
            }

            for (Class<?> inter : reference.getInterfaces()) {
                if (inter != null) {
                    parents.add(Utils.reverseMap(inter));
                }
            }

            return parents;
        } catch (Exception ignored) {
        }
        return null;
    }
}