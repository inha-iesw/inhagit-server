package inha.git.project.api.controller.dto.response;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SearchFileDetailResponse.class, name = "file"),
        @JsonSubTypes.Type(value = SearchDirectoryResponse.class, name = "directory")
})
public interface SearchFileResponse {
    String getName();
    String getType();
}