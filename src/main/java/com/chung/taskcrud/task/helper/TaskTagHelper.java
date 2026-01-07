package com.chung.taskcrud.task.helper;

import com.chung.taskcrud.task.entity.Tag;
import com.chung.taskcrud.task.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskTagHelper {

    private final TagRepository tagRepository;

    public Set<Tag> resolveTags(List<String> tagNames) {
        if(tagNames == null) {
            return new HashSet<>();
        }

        return tagNames.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(this::upsertTag)
                .collect(Collectors.toSet());
    }

    private Tag upsertTag(String name) {
        return tagRepository.findByName(name)
                .orElseGet(() -> tagRepository.save(Tag.builder()
                                .name(name)
                        .build()));
    }
}
