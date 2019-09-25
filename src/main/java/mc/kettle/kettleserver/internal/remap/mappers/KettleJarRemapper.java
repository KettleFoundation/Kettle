package mc.kettle.kettleserver.internal.remap.mappers;

import net.md_5.specialsource.CustomRemapper;

public class KettleJarRemapper extends CustomRemapper {
    public final KettleJarMapping jarMapping;

    @Override
    public String mapSignature(String signature, boolean typeSignature) {
        // todo
        return signature;
    }

    public KettleJarRemapper(KettleJarMapping mapping) {
        jarMapping = mapping;
    }


}
