package inha.git.project.api.service.patent;

import inha.git.common.exceptions.BaseException;
import inha.git.project.api.controller.dto.request.CreatePatentRequest;
import inha.git.project.api.controller.dto.response.PatentResponse;
import inha.git.project.api.controller.dto.response.SearchInventorResponse;
import inha.git.project.api.controller.dto.response.SearchPatentResponse;
import inha.git.project.api.mapper.ProjectMapper;
import inha.git.project.domain.Project;
import inha.git.project.domain.ProjectPatent;
import inha.git.project.domain.ProjectPatentInventor;
import inha.git.utils.file.FilePath;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.project.domain.repository.ProjectPatentInventorJpaRepository;
import inha.git.project.domain.repository.ProjectPatentJpaRepository;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.Constant.*;
import static inha.git.common.code.status.ErrorStatus.*;

/**
 * ProjectPatentServiceImpl은 프로젝트 특허 관련 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjectPatentServiceImpl implements ProjectPatentService {

    private final ProjectPatentJpaRepository projectPatentJpaRepository;
    private final ProjectPatentInventorJpaRepository projectPatentInventorJpaRepository;
    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectMapper projectMapper;

    @Override
    public PatentResponse createPatent(User user, Integer projectIdx, CreatePatentRequest createPatentRequest, MultipartFile file) {
        Project project = projectJpaRepository.findByIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
        if(user.getId() != project.getUser().getId()) {
            throw new BaseException(USER_NOT_PROJECT_OWNER);
        }
        if(project.getProjectPatentId() != null) {
            throw new BaseException(ALREADY_REGISTERED_PATENT);
        }
        ProjectPatent savePatent = projectPatentJpaRepository.save(projectMapper.toProjectPatent(createPatentRequest, FilePath.storeFile(file, PATENT)));
        List<ProjectPatentInventor> inventors = projectMapper.toPatentInventor(createPatentRequest.inventors(), savePatent);
        inventors.forEach(projectPatentInventorJpaRepository::save);
        project.setProjectPatentId(savePatent.getId());
        return projectMapper.toPatentResponse(savePatent);
    }

    /**
     * 특허 삭제 메서드
     *
     * @param user 로그인한 사용자 정보
     * @param projectIdx 프로젝트의 식별자
     * @return PatentResponse 특허 정보
     */
    @Override
    public PatentResponse deletePatent(User user, Integer projectIdx) {
        Project project = projectJpaRepository.findByIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
        if(user.getId() != project.getUser().getId() && user.getRole() != Role.ADMIN) {
            throw new BaseException(USER_NOT_PROJECT_OWNER);
        }
        ProjectPatent projectPatent = projectPatentJpaRepository.findByIdAndState(project.getProjectPatentId(), ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_PATENT));
        project.setProjectPatentId(null);
        return projectMapper.toPatentResponse(projectPatent);
    }
}
