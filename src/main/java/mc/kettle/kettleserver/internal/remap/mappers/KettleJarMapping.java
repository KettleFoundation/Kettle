package mc.kettle.kettleserver.internal.remap.mappers;

import scala.collection.Map;
import scala.collection.mutable.HashMap;

public class KettleJarMapping implements ClassRemapperSupplier {
    public final Map<String, ClassMapping> byInternalName = new HashMap<>();


}
