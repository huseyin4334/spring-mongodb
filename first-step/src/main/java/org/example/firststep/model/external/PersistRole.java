package org.example.firststep.model.external;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class PersistRole {
    private String name;
    private String description;
    private List<PersistPermission> permissions;
}
