import javax.tools.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;

public class CompileTracker {
    public static void main(String[] args) throws Exception {
        System.out.println("=== AutoGrinder Tracker Compiler ===");
        System.out.println("Compiling tracker modules...");

        String srcDir = "src/main/java";
        String buildDir = "build/classes";
        String jarPath = "C:\\Users\\kaden\\AppData\\Roaming\\.minecraft\\mods\\1.8.9\\autogrinder-v6-macrocheck (3).jar";

        // Create build directory
        new File(buildDir).mkdirs();

        // Get Java compiler
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.err.println("ERROR: Java compiler not found! Make sure you have JDK installed.");
            System.exit(1);
        }

        // List source files to compile
        String[] sourceFiles = {
            srcDir + "/org/polyfrost/example/module/modules/StatTracker.java",
            srcDir + "/org/polyfrost/example/ui/StatScoreboard.java",
            srcDir + "/org/polyfrost/example/config/TrackerCommand.java"
        };

        // Check if files exist
        for (String file : sourceFiles) {
            if (!new File(file).exists()) {
                System.err.println("ERROR: Source file not found: " + file);
                System.exit(1);
            }
        }

        // Compile
        System.out.println("\n[1/3] Compiling StatTracker.java...");
        System.out.println("[2/3] Compiling StatScoreboard.java...");
        System.out.println("[3/3] Compiling TrackerCommand.java...");

        // Use existing JAR as classpath
        String classPath = jarPath + File.pathSeparator + srcDir;

        List<String> options = Arrays.asList(
            "-encoding", "UTF-8",
            "-d", buildDir,
            "-cp", classPath
        );

        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> compilationUnits =
            fileManager.getJavaFileObjectsFromStrings(Arrays.asList(sourceFiles));

        java.util.concurrent.Callable<?> task = compiler.getTask(null, fileManager, null, options, null, compilationUnits);
        boolean success = (Boolean) task.call();
        fileManager.close();

        if (!success) {
            System.err.println("\n❌ Compilation FAILED!");
            System.err.println("\nNote: This requires full Forge/Minecraft libraries.");
            System.err.println("For now, here's an alternative:\n");
            System.out.println("Use the online compilation method:");
            System.out.println("1. Go to https://replit.com");
            System.out.println("2. Create a new Java project");
            System.out.println("3. Copy src/main/java files");
            System.out.println("4. Add the original JAR to classpath");
            System.out.println("5. Compile and we'll inject the .class files\n");
            System.exit(1);
        }

        System.out.println("\n✅ Compilation successful!");
        System.out.println("\n[4/3] Merging with original JAR...");

        // Copy original JAR
        Files.copy(
            Paths.get(jarPath),
            Paths.get("build/AutoGrinder-Tracker.jar"),
            java.nio.file.StandardCopyOption.REPLACE_EXISTING
        );

        // Add compiled classes to JAR
        updateJar("build/AutoGrinder-Tracker.jar", buildDir);

        System.out.println("\n✅ SUCCESS!");
        System.out.println("\n📦 JAR Ready: build/AutoGrinder-Tracker.jar");
        System.out.println("📍 Install to: C:\\Users\\kaden\\AppData\\Roaming\\.minecraft\\mods\\1.8.9\\");
        System.out.println("\nThen restart Minecraft and use /tracker commands!");
    }

    static void updateJar(String jarPath, String classesDir) throws Exception {
        File jarFile = new File(jarPath);
        File tmpJar = new File(jarPath + ".tmp");

        try (JarFile originalJar = new JarFile(jarFile);
             JarOutputStream jos = new JarOutputStream(new FileOutputStream(tmpJar))) {

            // Copy existing entries
            Enumeration<JarEntry> entries = originalJar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (!entry.getName().startsWith("org/polyfrost/example/module/modules/StatTracker") &&
                    !entry.getName().startsWith("org/polyfrost/example/ui/StatScoreboard") &&
                    !entry.getName().startsWith("org/polyfrost/example/config/TrackerCommand")) {
                    jos.putNextEntry(new JarEntry(entry.getName()));
                    if (!entry.isDirectory()) {
                        InputStream is = originalJar.getInputStream(entry);
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = is.read(buffer)) > 0) {
                            jos.write(buffer, 0, len);
                        }
                        is.close();
                    }
                    jos.closeEntry();
                }
            }

            // Add new classes
            addClassesToJar(jos, new File(classesDir));
        }

        jarFile.delete();
        tmpJar.renameTo(jarFile);
    }

    static void addClassesToJar(JarOutputStream jos, File dir) throws Exception {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                addClassesToJar(jos, file);
            } else if (file.getName().endsWith(".class")) {
                String entryName = getEntryName(file);
                jos.putNextEntry(new JarEntry(entryName));
                Files.copy(file.toPath(), jos);
                jos.closeEntry();
            }
        }
    }

    static String getEntryName(File file) {
        String path = file.getAbsolutePath();
        String basePath = new File("build/classes").getAbsolutePath();
        return path.substring(basePath.length() + 1).replace(File.separator, "/");
    }
}
