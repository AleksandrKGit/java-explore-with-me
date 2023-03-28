package ru.practicum.ewm.main.user.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.practicum.ewm.main.tools.configuration.AppTestConfiguration;

import static ru.practicum.ewm.main.tools.factories.UserFactory.*;

@JsonTest
@SpringJUnitConfig({AppTestConfiguration.class})
class UserDtoTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void toJson_withNullFields_shouldReturnJsonStringWithNullFields() throws JsonProcessingException, JSONException {
        UserDto source = createUserDto(null, null, null);
        String expected = "{\"id\":null, \"name\":null, \"email\":null}";

        String actual = objectMapper.writeValueAsString(source);

        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }

    @Test
    void toJson_withNotNullFields_shouldReturnCorrectJsonString() throws JsonProcessingException, JSONException {
        UserDto source = createUserDto(1L,"John", "Smith");
        String expected = "{\"id\":1, \"name\":\"John\", \"email\":\"Smith\"}";

        String actual = objectMapper.writeValueAsString(source);

        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }
}