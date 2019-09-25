package mc.kettle.kettleserver.internal.remap.mappers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import java.util.Map;

public class ClassMapping {
    private String nativeSrcName, nativeSimpleName, nativeName, mcpSrcName, mcpSimpleName, mcpName;
    private final BiMap<String, String> fieldMapping = HashBiMap.create();
    private final Map<String, Map<String, String>> methodMapping = Maps.newHashMap();
    private final Map<String, Map<String, String>> inverseMethodMapping = Maps.newHashMap();
    private final Map<String, Map<String, String>> sourceMethodMapping = Maps.newHashMap();
    private final Map<String, Map<String, String>> inverseSourceMethodMapping = Maps.newHashMap();

    public void setNativeSourceName(String name) {
        name = name.intern();
        this.nativeSrcName = name;
        Integer dot = nativeSrcName.lastIndexOf('$');
        if (dot > 0) {
            nativeSrcName = nativeSrcName.substring(dot + 1).intern();
        } else {
            nativeSrcName = nativeSrcName.substring(nativeSrcName.lastIndexOf('/') + 1).intern();
        }

        this.nativeName = nativeSrcName.replace('/', '.').intern();
    }

    public void setMCPSourceName(String name) {
        mcpSrcName = mcpSrcName.intern();
        mcpSrcName = name;
        Integer dot = mcpSrcName.lastIndexOf('$');
        if (dot > 0) {
            mcpSimpleName = mcpSrcName.substring(dot + 1).intern();
        } else {
            mcpSimpleName = mcpSrcName.substring(mcpSrcName.lastIndexOf('/') + 1).intern();
        }

        mcpName = mcpSrcName.replace('/', '.').intern();
    }

    public Map<String, Map<String, String>> getSourceMethodMapping() {
        return sourceMethodMapping;
    }

    public Map<String, Map<String, String>> getInverseSourceMethodMapping() {
        return inverseSourceMethodMapping;
    }

    public String getNativeSourceName() {
        return nativeSrcName;
    }

    public String getNativeSimpleName() {
        return nativeSimpleName;
    }

    public String getNativeName() {
        return nativeName;
    }

    public String getMCPSourceName() {
        return mcpSrcName;
    }

    public String getMCPName() {
        return mcpName;
    }

    public String getMCPSimpleName() {
        return mcpSimpleName;
    }

    public BiMap<String, String> getFieldMapping() {
        return fieldMapping;
    }

    public Map<String, Map<String, String>> getMethodMapping() {
        return methodMapping;
    }

    public Map<String, Map<String, String>> getInverseMethodMapping() {
        return inverseMethodMapping;
    }
}