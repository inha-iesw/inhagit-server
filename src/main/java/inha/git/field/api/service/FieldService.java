package inha.git.field.api.service;

import inha.git.field.api.controller.request.CreateFieldRequest;
import inha.git.field.api.controller.request.UpdateFieldRequest;
import inha.git.field.api.controller.response.SearchFieldResponse;

import java.util.List;

public interface FieldService {
    List<SearchFieldResponse> getFields();
    String createField(CreateFieldRequest createFieldRequest);

    String updateField(Integer fieldIdx, UpdateFieldRequest updateFieldRequest);


    String deleteField(Integer fieldIdx);
}
