package ru.practicum.ewm.main.compilation.dto;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import ru.practicum.ewm.main.compilation.Compilation;
import ru.practicum.ewm.main.event.dto.EventMapper;
import ru.practicum.ewm.main.event.dto.EventShortDto;
import ru.practicum.ewm.main.event.model.Event;
import ru.practicum.ewm.main.stat.StatService;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CompilationMapper {
   @Autowired
   private EventMapper eventMapper;

   @Autowired
   private StatService statService;

   @Mapping(target = "events", ignore = true)
   public abstract Compilation toEntity(NewCompilationDto dto);

   @Mapping(target = "events", ignore = true)
   @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
   public abstract void update(UpdateCompilationRequest dto, @MappingTarget Compilation compilation);

   @Mapping(target = "events", ignore = true)
   public abstract CompilationDto toDto(Compilation compilation);

   public CompilationDto toDto(Compilation compilation, boolean sortEventsByViews) {
      CompilationDto dto = toDto(compilation);

      dto.setEvents(eventMapper.toShortDto(compilation.getEvents(), sortEventsByViews));

      return dto;
   }

   public List<CompilationDto> toDto(List<Compilation> compilations, boolean sortEventsByViews) {
      if (compilations.size() == 0) {
         return List.of();
      }

      Set<Long> allEventIds = new HashSet<>();
      compilations.forEach(c -> allEventIds.addAll(c.getEvents().stream().map(Event::getId)
              .collect(Collectors.toSet())));

      if (allEventIds.size() == 0) {
         return toDto(compilations, Map.of());
      }

      if (!sortEventsByViews) {
         return toDto(compilations, statService.getViews(new LinkedList<>(allEventIds)));
      }

      return toDto(compilations, statService.getViewsSortedByCount(new LinkedList<>(allEventIds)));
   }

   private List<CompilationDto> toDto(List<Compilation> compilations, List<Pair<Long, Long>> views) {
      List<CompilationDto> result = new LinkedList<>();

      compilations.forEach(comp -> {
         CompilationDto dto = toDto(comp);

         if (comp.getEvents() != null && comp.getEvents().size() != 0) {
            List<EventShortDto> eventsDtoList = new LinkedList<>();

            final Map<Long, Event> mappedEvents = new TreeMap<>(Long::compareTo);
            mappedEvents.putAll(comp.getEvents().stream().collect(Collectors.toMap(Event::getId, e -> e)));

            views.forEach(p -> {
               Event event = mappedEvents.getOrDefault(p.getFirst(), null);

               if (event != null) {
                  eventsDtoList.add(eventMapper.toShortDto(event, p.getSecond()));
               }
            });

            dto.setEvents(eventsDtoList);
         }

         result.add(dto);
      });

      return result;
   }

   private List<CompilationDto> toDto(List<Compilation> compilations, Map<Long, Long> views) {
      List<CompilationDto> result = new LinkedList<>();

      compilations.forEach(comp -> {
         CompilationDto dto = toDto(comp);

         if (comp.getEvents() != null && comp.getEvents().size() != 0) {
            dto.setEvents(comp.getEvents()
                    .stream()
                    .map(e -> eventMapper.toShortDto(e, views.getOrDefault(e.getId(), 0L)))
                    .collect(Collectors.toList()));
         }

         result.add(dto);
      });

      return result;
   }
}