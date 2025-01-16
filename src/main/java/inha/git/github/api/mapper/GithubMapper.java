package inha.git.github.api.mapper;

import inha.git.github.api.controller.dto.response.GithubRepositoryResponse;
import org.kohsuke.github.GHRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GithubMapper {

    @Mapping(target = "archiveUrl", expression = "java(getArchiveUrl(repo))")
    GithubRepositoryResponse toDto(GHRepository repo);

    default String getArchiveUrl(GHRepository repo) {
        return "https://api.github.com/repos/" + repo.getFullName() + "/zipball/" + repo.getDefaultBranch();
    }
}
