package ru.practicum.ewm.main.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.main.compilation.Compilation;
import ru.practicum.ewm.main.tools.factories.CompilationFactory;

public class CompilationMatcher implements ArgumentMatcher<Compilation> {
    private final Compilation compilation;

    private CompilationMatcher(Compilation compilation) {
        this.compilation = compilation;
    }

    public static CompilationMatcher equalTo(Compilation compilation) {
        return new CompilationMatcher(compilation);
    }

    @Override
    public boolean matches(Compilation compilation) {
        return CompilationFactory.equals(this.compilation, compilation);
    }
}