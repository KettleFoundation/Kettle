package kettlefoundation.kettle.nms;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import kettlefoundation.kettle.nms.reflections.ReflectionTransformer;
import kettlefoundation.kettle.nms.utils.RemapUtils;
import net.md_5.specialsource.provider.InheritanceProvider;

/**
 * ClassInheritanceProvider
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 08/05/2019 - 09:03 AM
 */
public class ClassInheritanceProvider implements InheritanceProvider {

  @Override
  public Collection<String> getParents(String className) {
    className = ReflectionTransformer.remapper.map(className);

    try {
      Collection<String> parent = new HashSet<>();
      Class<?> reference = Class.forName(className.replace('/', '.').replace('$', '.'), false, this.getClass().getClassLoader());
      Class<?> extend = reference.getSuperclass();
      if (extend != null) {
        parent.add(RemapUtils.reverseMap(extend));
      }

      Arrays.stream(reference.getInterfaces()).filter(Objects::nonNull).map(RemapUtils::reverseMap).forEach(parent::add);

      return parent;
    } catch (Exception e) {
    }
    return null;
  }
}
