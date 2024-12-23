package org.example.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
@Data
@Document(collection = "trainer_workload")
@CompoundIndex(name = "first_last_name_idx", def = "{'firstname': 1, 'lastname': 1}")
public class Trainer {
    @Id
    private String id;

    @Field("username")
    private String username;

    @Field("firstName")
    private String firstname;

    @Field("lastName")
    private String lastname;

    @Field("status")
    private boolean status;

    @Field("Workload")
    private List<YearSummary> yearsList;
}
