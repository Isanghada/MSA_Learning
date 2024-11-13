package com.example.orderservice.dto;

import java.util.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Schema {
    private String type;
    private List<Field> fields;
    private boolean optional;
    private String name;
}
