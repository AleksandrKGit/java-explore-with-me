package ru.practicum.ewm.main.event.dto;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import ru.practicum.ewm.main.category.Category;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.event.model.EventState;
import ru.practicum.ewm.main.stat.StatService;
import ru.practicum.ewm.main.user.User;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import static ru.practicum.ewm.common.support.DateFactory.DATE_FORMAT;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class EventMapper {
    @Autowired
    private StatService statService;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "createdOn", source = "createdOn")
    @Mapping(target = "eventDate", dateFormat = DATE_FORMAT)
    @Mapping(target = "state", source = "state")
    public abstract Event toEntity(NewEventDto dto, User initiator, Category category, LocalDateTime createdOn,
                                   EventState state);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "eventDate", dateFormat = DATE_FORMAT)
    @Mapping(target = "state", source = "state")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateFromUser(UpdateEventUserRequest dto, @MappingTarget Event event, EventState state);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "publishedOn", source = "publishedOn")
    @Mapping(target = "eventDate", dateFormat = DATE_FORMAT)
    @Mapping(target = "state", source = "state")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateFromAdmin(UpdateEventAdminRequest dto, @MappingTarget Event event, EventState state,
                                         LocalDateTime publishedOn);

    @Mapping(target = "createdOn", dateFormat = DATE_FORMAT)
    @Mapping(target = "publishedOn", dateFormat = DATE_FORMAT)
    @Mapping(target = "eventDate", dateFormat = DATE_FORMAT)
    public abstract EventFullDto toFullDto(Event event, Long views);

    public EventFullDto toFullDto(Event event) {
        return toDto(event, this::toFullDto);
    }

    public List<EventFullDto> toFullDto(Collection<Event> events, boolean sortByViews) {
        return toDto(events, sortByViews, this::toFullDto);
    }

    @Mapping(target = "eventDate", dateFormat = DATE_FORMAT)
    public abstract EventShortDto toShortDto(Event event, Long views);

    public List<EventShortDto> toShortDto(Collection<Event> events, boolean sortByViews) {
        return toDto(events, sortByViews, this::toShortDto);
    }

    @FunctionalInterface
    private interface ToDto<T> {
        T apply(Event event, Long views);
    }

    private <T> List<T> toDto(Collection<Event> events, boolean sortByViews, ToDto<T> mapper) {
        if (events.isEmpty()) {
            return List.of();
        }

        List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());

        if (sortByViews) {
            List<Pair<Long, Long>> views = statService.getViewsSortedByCount(ids);

            Map<Long, Event> mappedEvents = new TreeMap<>(Long::compareTo);
            mappedEvents.putAll(events.stream().collect(Collectors.toMap(Event::getId, e -> e)));

            return views.stream()
                    .map(p -> mapper.apply(mappedEvents.get(p.getFirst()), p.getSecond()))
                    .collect(Collectors.toList());
        } else {
            Map<Long, Long> views = statService.getViews(ids);

            return events.stream()
                    .map(event -> mapper.apply(event, views.get(event.getId())))
                    .collect(Collectors.toList());
        }
    }

    private <T> T toDto(Event event, ToDto<T> mapper) {
        Long views = 0L;

        if (event.getId() != 0) {
            views = statService.getViews(List.of(event.getId())).get(event.getId());
        }

        return mapper.apply(event, views);
    }
}