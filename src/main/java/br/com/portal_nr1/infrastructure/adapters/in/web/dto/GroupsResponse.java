package br.com.portal_nr1.infrastructure.adapters.in.web.dto;

import java.util.ArrayList;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

public record GroupsResponse(
    @ArraySchema(schema = @Schema(implementation = GroupResponseItem.class), arraySchema = @Schema(description = "Lista de grupos de usuarios do portal NR1"))
    ArrayList<GroupResponseItem> groups
) {

}
