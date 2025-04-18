package org.example.firststep.model.external;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PersistAddress {
    private String name;
    private String description;

    private double[] coordinates;
}
