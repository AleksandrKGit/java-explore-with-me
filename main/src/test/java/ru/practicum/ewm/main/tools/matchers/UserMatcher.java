package ru.practicum.ewm.main.tools.matchers;

import org.mockito.ArgumentMatcher;
import ru.practicum.ewm.main.user.User;
import ru.practicum.ewm.main.tools.factories.UserFactory;

public class UserMatcher implements ArgumentMatcher<User> {
    private final User user;

    private UserMatcher(User user) {
        this.user = user;
    }

    public static UserMatcher equalTo(User user) {
        return new UserMatcher(user);
    }

    @Override
    public boolean matches(User user) {
        return UserFactory.equals(this.user, user);
    }
}