package inha.git.field.api.service;

import inha.git.field.api.controller.dto.request.CreateFieldRequest;
import inha.git.field.api.controller.dto.request.UpdateFieldRequest;
import inha.git.field.api.controller.dto.response.SearchFieldResponse;
import inha.git.user.domain.User;

import java.util.List;

public interface FieldService {
    List<SearchFieldResponse> getFields();
    String createField(User admin, CreateFieldRequest createFieldRequest);
    String updateField(User admin, Integer fieldIdx, UpdateFieldRequest updateFieldRequest);
    String deleteField(User admin, Integer fieldIdx);
}
