Gradle 4.1.1, Java 8 (`1.8.0_66`) with **incremental** compilation.

Running `./gradlew compileJava`produces:
```
FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':compileJava'.
> java.lang.ArrayIndexOutOfBoundsException (no error message)

* Try:
Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output. Run with --scan to get full insights.

* Get more help at https://help.gradle.org

BUILD FAILED in 1s
1 actionable task: 1 executed
```

We tried to upgrade our big project to Gradle 4.4.1 (from 3.3), but incremental compilation got broken (full exception below).

After some googling I found https://gitlab.ow2.org/asm/asm/issues/317789, which looks like a plausible explanation. Their test case is included in this project and it causes the same exception during incremental compilation.

```
Caused by: java.lang.ArrayIndexOutOfBoundsException: 117
        at org.objectweb.asm.ClassReader.readLabel(ClassReader.java:2257)
        at org.objectweb.asm.ClassReader.readTypeAnnotations(ClassReader.java:1684)
        at org.objectweb.asm.ClassReader.readCode(ClassReader.java:1264)
        at org.objectweb.asm.ClassReader.readMethod(ClassReader.java:1090)
        at org.objectweb.asm.ClassReader.accept(ClassReader.java:700)
        at org.objectweb.asm.ClassReader.accept(ClassReader.java:505)
        at org.gradle.api.internal.tasks.compile.incremental.asm.ClassDependenciesVisitor.analyze(ClassDependenciesVisitor.java:75)
        at org.gradle.api.internal.tasks.compile.incremental.analyzer.DefaultClassDependenciesAnalyzer.getClassAnalysis(DefaultClassDependenciesAnalyzer.java:34)
        at org.gradle.api.internal.tasks.compile.incremental.analyzer.DefaultClassDependenciesAnalyzer.getClassAnalysis(DefaultClassDependenciesAnalyzer.java:42)
        at org.gradle.api.internal.tasks.compile.incremental.analyzer.CachingClassDependenciesAnalyzer$1.create(CachingClassDependenciesAnalyzer.java:37)
        at org.gradle.api.internal.tasks.compile.incremental.analyzer.CachingClassDependenciesAnalyzer$1.create(CachingClassDependenciesAnalyzer.java:35)
        at org.gradle.cache.internal.MinimalPersistentCache.get(MinimalPersistentCache.java:36)
        at org.gradle.api.internal.tasks.compile.incremental.analyzer.CachingClassDependenciesAnalyzer.getClassAnalysis(CachingClassDependenciesAnalyzer.java:35)
        at org.gradle.api.internal.tasks.compile.incremental.analyzer.ClassFilesAnalyzer.visitFile(ClassFilesAnalyzer.java:52)
        at org.gradle.api.internal.file.collections.jdk7.Jdk7DirectoryWalker$1.visitFile(Jdk7DirectoryWalker.java:86)
        at org.gradle.api.internal.file.collections.jdk7.Jdk7DirectoryWalker$1.visitFile(Jdk7DirectoryWalker.java:59)
        at org.gradle.api.internal.file.collections.jdk7.Jdk7DirectoryWalker.walkDir(Jdk7DirectoryWalker.java:59)
        at org.gradle.api.internal.file.collections.DirectoryFileTree.walkDir(DirectoryFileTree.java:155)
        at org.gradle.api.internal.file.collections.DirectoryFileTree.visitFrom(DirectoryFileTree.java:133)
        at org.gradle.api.internal.file.collections.DirectoryFileTree.visit(DirectoryFileTree.java:118)
        at org.gradle.api.internal.file.collections.FileTreeAdapter.visit(FileTreeAdapter.java:110)
        at org.gradle.api.internal.file.CompositeFileTree.visit(CompositeFileTree.java:87)
        at org.gradle.api.internal.tasks.compile.incremental.ClassSetAnalysisUpdater.updateAnalysis(ClassSetAnalysisUpdater.java:66)
        at org.gradle.api.internal.tasks.compile.incremental.IncrementalCompilationFinalizer.execute(IncrementalCompilationFinalizer.java:44)
        at org.gradle.api.internal.tasks.compile.incremental.IncrementalCompilationFinalizer.execute(IncrementalCompilationFinalizer.java:24)
        at org.gradle.api.tasks.compile.JavaCompile.performCompilation(JavaCompile.java:207)
        at org.gradle.api.tasks.compile.JavaCompile.compile(JavaCompile.java:133)
        at org.gradle.internal.reflect.JavaMethod.invoke(JavaMethod.java:73)
        at org.gradle.api.internal.project.taskfactory.IncrementalTaskAction.doExecute(IncrementalTaskAction.java:46)
        at org.gradle.api.internal.project.taskfactory.StandardTaskAction.execute(StandardTaskAction.java:39)
        at org.gradle.api.internal.project.taskfactory.StandardTaskAction.execute(StandardTaskAction.java:26)
        at org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter$1.run(ExecuteActionsTaskExecuter.java:121)
        at org.gradle.internal.progress.DefaultBuildOperationExecutor$RunnableBuildOperationWorker.execute(DefaultBuildOperationExecutor.java:336)
        at org.gradle.internal.progress.DefaultBuildOperationExecutor$RunnableBuildOperationWorker.execute(DefaultBuildOperationExecutor.java:328)
        at org.gradle.internal.progress.DefaultBuildOperationExecutor.execute(DefaultBuildOperationExecutor.java:199)
        at org.gradle.internal.progress.DefaultBuildOperationExecutor.run(DefaultBuildOperationExecutor.java:110)
        at org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter.executeAction(ExecuteActionsTaskExecuter.java:110)
        at org.gradle.api.internal.tasks.execution.ExecuteActionsTaskExecuter.executeActions(ExecuteActionsTaskExecuter.java:92)
        ... 31 more
```

