package com.chung.taskcrud.log.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskLogChangeResponse {
    private String fielName;
    private String oldValue;
    private String newValue;
}
