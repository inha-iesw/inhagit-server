package inha.git.field.api.service;

import inha.git.field.api.controller.request.CreateFieldRequest;
import inha.git.field.api.controller.request.UpdateFieldRequest;

public interface FieldService {
    String createField(CreateFieldRequest createFieldRequest);

    String updateField(Integer fieldIdx, UpdateFieldRequest updateFieldRequest);
}
