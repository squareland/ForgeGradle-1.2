package net.minecraftforge.gradle;

import org.gradle.api.tasks.compile.AbstractCompile;

import java.io.File;
import java.lang.reflect.Method;

public class CompileTaskHelper {
    private static final BuildDirGetter VERSION_GETTERS = GradleVersionUtils.choose("6.1",
            OldBuildDirGetter::new,
            NewBuildDirGetter::new
    );

    public static void setDestinationDir(AbstractCompile task, File file) {
        VERSION_GETTERS.setDestinationDir(task, file);
    }

    public interface BuildDirGetter {
        void setDestinationDir(AbstractCompile task, File file);
    }

    private static class OldBuildDirGetter implements BuildDirGetter {
        private static final Method setDestinationDir;

        static {
            try {
                setDestinationDir = AbstractCompile.class.getMethod("setDestinationDir", File.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setDestinationDir(AbstractCompile task, File file) {
            ReflectionHelper.call(setDestinationDir, task, file);
        }
    }

    private static class NewBuildDirGetter implements BuildDirGetter {
        @Override
        public void setDestinationDir(AbstractCompile task, File file) {
            task.getDestinationDirectory().set(file);
        }
    }

}
