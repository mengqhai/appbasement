package urlclassloader;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarClassInfoLoader {

	List<String> classList = new ArrayList<String>();
	List<String> packageList = new ArrayList<String>();
	Map<String, List<String>> methodMap = new HashMap<String, List<String>>();

	public JarClassInfoLoader(String path) {
		URL url;
		try {
			url = new URL("jar:file:" + path + "!/");
			URLClassLoader classLoader = new URLClassLoader(new URL[] { url });
			JarFile jarFile = new JarFile(path);
			Enumeration<JarEntry> jarEntries = jarFile.entries();
			Set<String> packageSet = new HashSet<String>();
			while (jarEntries.hasMoreElements()) {
				JarEntry entry = jarEntries.nextElement();
				String name = entry.getName();
				if (!entry.isDirectory() && name.endsWith(".class")) {
					name = name.replace("/", ".").substring(0,
							name.length() - 6);
					classList.add(name);
					String packageName = name.substring(0,
							name.lastIndexOf("."));
					packageSet.add(packageName);

					Class<?> clazz = classLoader.loadClass(name);
					Method[] allMethods = clazz.getMethods();
					List<String> mNames = new ArrayList<String>(
							allMethods.length);
					Set<String> mNameSet = new HashSet<String>(
							allMethods.length);
					for (Method m : allMethods) {
						if (Modifier.isPublic(m.getModifiers())) {
							mNameSet.add(m.getName());
						}
					}
					mNames.addAll(mNameSet);
					Collections.sort(mNames);
					methodMap.put(name, mNames);
				}
			}
			jarFile.close();
			classLoader.close();
			// the loaded classes can be garbage collected when the class loader
			// is garbage collected

			packageList.addAll(packageSet);
			Collections.sort(packageList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<String> getClassList() {
		return classList;
	}

	public List<String> getPackageList() {
		return packageList;
	}

	public Map<String, List<String>> getMethodMap() {
		return methodMap;
	}

	public static void main(String[] args) throws Exception {
		JarClassInfoLoader loader = new JarClassInfoLoader(
				"E:/Tools/apache-tomcat-7.0.57/lib/annotations-api.jar");
		System.out.println(loader.getClassList());
		System.out.println(loader.getPackageList());
		System.out.println(loader.getMethodMap());
	}
}
