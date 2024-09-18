package net.minecraftforge.gradle;

import org.gradle.api.Project;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProjectBuildDirHelper {
    private static final BuildDirGetter VERSION_GETTERS = GradleVersionUtils.choose("4.1",
            OldBuildDirGetter::new,
            NewBuildDirGetter::new
    );

    public static File getBuildDir(Project project) {
        return VERSION_GETTERS.getBuildDir(project);
    }

    public interface BuildDirGetter {
        File getBuildDir(Project project);
    }

    private static class OldBuildDirGetter implements BuildDirGetter {
        private static final Method getBuildDir;

        static {
            try {
                getBuildDir = Project.class.getMethod("getBuildDir");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public File getBuildDir(Project project) {
            return call(getBuildDir, project);
        }
    }

    private static class NewBuildDirGetter implements BuildDirGetter {
        @Override
        public File getBuildDir(Project project) {
            return project.getLayout().getBuildDirectory().get().getAsFile();
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T call(Method method, Object self, Object... args) {
        try {
            return (T) method.invoke(self, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
