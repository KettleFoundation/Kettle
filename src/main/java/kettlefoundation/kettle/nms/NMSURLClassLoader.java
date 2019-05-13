package kettlefoundation.kettle.nms;

import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;
import kettlefoundation.kettle.nms.ClassInheritanceProvider;
import kettlefoundation.kettle.nms.MappingLoader;
import kettlefoundation.kettle.nms.reflections.ReflectionTransformer;
import kettlefoundation.kettle.nms.utils.RemapUtils;
import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.JarRemapper;
import net.md_5.specialsource.provider.ClassLoaderProvider;
import net.md_5.specialsource.provider.JointProvider;
import net.md_5.specialsource.repo.RuntimeRepo;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 * NMSURLClassLoader
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 08/05/2019 - 09:20 AM
 */
public class NMSURLClassLoader extends URLClassLoader {

  private JarMapping jarMapping;
  private JarRemapper remapper;
  private final Map<String, Class<?>> classes;

  {
    this.jarMapping = MappingLoader.loadMappings();
    final JointProvider provider = new JointProvider();
    provider.add(new ClassInheritanceProvider());
    provider.add(new ClassLoaderProvider(this));
    this.jarMapping.setFallbackInheritanceProvider(provider);
    this.remapper = new JarRemapper(this.jarMapping);
    classes = new HashMap<>();
  }

  public NMSURLClassLoader(URL[] urls, ClassLoader parent) {
    super(urls, parent);
  }

  public NMSURLClassLoader(URL[] urls) {
    super(urls);
  }

  public NMSURLClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
    super(urls, parent, factory);
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    return this.findClass(name, true);
  }

  private Class<?> findClass(final String name, final boolean checkGlobal) throws ClassNotFoundException {
    if (name.startsWith("net.minecraft.server." + RemapUtils.NMS_VERSION)) {
      final String remappedClass = this.jarMapping.classes.get(name.replaceAll("\\.", "\\/"));
      final Class<?> clazz = ((LaunchClassLoader) FMLCommonHandler.instance().getMinecraftServerInstance().getClass().getClassLoader()).findClass(remappedClass);
      return clazz;
    }

    Class<?> result = this.classes.get(name);
    synchronized (name.intern()) {
      if (result == null) {
        result = this.remappedFindClass(name);
        if (result != null) {
          this.setClass(name, result);
        }
        if (result == null) {
          try {
            result = super.findClass(name);
          } catch (ClassNotFoundException e) {
            result = ((LaunchClassLoader) FMLCommonHandler.instance().getMinecraftServerInstance().getClass().getClassLoader()).findClass(name);
          }
        }
        if (result == null) {
          throw new ClassNotFoundException(name);
        }
        this.classes.put(name, result);
      }
    }
    return result;
  }

  private void setClass(String name, Class<?> clazz) {
    if(!this.classes.containsKey(name)){
      this.classes.put(name,clazz);
      if(ConfigurationSerializable.class.isAssignableFrom(clazz)){
        final Class<? extends ConfigurationSerializable> serializable = clazz.asSubclass(ConfigurationSerializable.class);
        ConfigurationSerialization.registerClass(serializable);
      }
    }
  }

  private Class<?> remappedFindClass(String name) throws ClassNotFoundException {
    Class<?> result = null;
    try{
      final String path = name.replace('.', '/').concat(".class");
      final URL url = this.findResource(path);
      if(url != null){
        final InputStream stream = url.openStream();
        if(stream != null){
          byte[] bytecode;
          bytecode = this.remapper.remapClassFile(stream, RuntimeRepo.getInstance());
          bytecode = ReflectionTransformer.transform(bytecode);
          final JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
          final URL jarUrl = jarURLConnection.getJarFileURL();
          final CodeSource codeSource = new CodeSource(jarUrl, new CodeSigner[0]);
          result = this.defineClass(name, bytecode, 0, bytecode.length, codeSource);
          if(result != null){
            this.resolveClass(result);
          }
        }
      }
    }catch (Throwable t){
      throw new ClassNotFoundException("Failed to remap class "+ name, t);
    }
    return result;
  }
}
