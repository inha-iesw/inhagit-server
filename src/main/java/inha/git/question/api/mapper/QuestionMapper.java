package inha.git.question.api.mapper;

import inha.git.field.domain.Field;
import inha.git.mapping.domain.QuestionField;
import inha.git.mapping.domain.id.QuestionFieldId;
import inha.git.project.api.controller.dto.request.UpdateProjectRequest;
import inha.git.project.domain.Project;
import inha.git.question.api.controller.dto.request.CreateQuestionRequest;
import inha.git.question.api.controller.dto.request.UpdateQuestionRequest;
import inha.git.question.api.controller.dto.response.QuestionResponse;
import inha.git.question.domain.Question;
import inha.git.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * QuestionMapper는 Question 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface QuestionMapper {

    /**
     * CreateQuestionRequest를 Question으로 변환합니다.
     *
     * @param createQuestionRequest CreateQuestionRequest
     * @param user                  User
     * @return Question
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subjectName", source = "createQuestionRequest.subject")
    @Mapping(target = "user", source = "user")
    Question createQuestionRequestToQuestion(CreateQuestionRequest createQuestionRequest, User user);

    /**
     * Question을 QuestionResponse로 변환합니다.
     *
     * @param question Question
     * @return QuestionResponse
     */
    @Mapping(target = "idx", source = "question.id")
    QuestionResponse questionToQuestionResponse(Question question);


    /**
     * QuestionField를 생성합니다.
     *
     * @param question Question
     * @param field    Field
     * @return QuestionField
     */
    default QuestionField createQuestionField(Question question, Field field) {
        return new QuestionField(new QuestionFieldId(question.getId(), field.getId()), question, field);
    }

    /**
     * CreateQuestionRequest를 Question으로 업데이트합니다.
     *
     * @param updateQuestionRequest UpdateQuestionRequest
     * @param question              Question
     */
    @Mapping(target = "subjectName", source = "updateQuestionRequest.subject")
    @Mapping(target = "title", source = "updateQuestionRequest.title")
    @Mapping(target = "contents", source = "updateQuestionRequest.contents")
    void updateQuestionRequestToQuestion(UpdateQuestionRequest updateQuestionRequest, @MappingTarget Question question);

}
