package inha.git.project.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.project.api.controller.dto.request.CreatePatentRequest;
import inha.git.project.api.controller.dto.response.PatentResponse;
import inha.git.project.api.controller.dto.response.SearchInventorResponse;
import inha.git.project.api.controller.dto.response.SearchPatentResponse;
import inha.git.project.api.mapper.ProjectMapper;
import inha.git.project.domain.Project;
import inha.git.project.domain.ProjectPatent;
import inha.git.project.domain.ProjectPatentInventor;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.project.domain.repository.ProjectPatentInventorJpaRepository;
import inha.git.project.domain.repository.ProjectPatentJpaRepository;
import inha.git.user.domain.Company;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.utils.file.FilePath;
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

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjectPatentServiceImpl implements ProjectPatentService {

    private final ProjectPatentJpaRepository projectPatentJpaRepository;
    private final ProjectPatentInventorJpaRepository projectPatentInventorJpaRepository;
    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectMapper projectMapper;

    @Value("${kipris.access-key}")
    private String key;

    @Value("${kipris.inventor-url}")
    private String inventorUrlString;

    @Value("${kipris.applicant-url}")
    private String applicantUrlString;

    @Value("${kipris.basic-info-url}")
    private String basicInfoUrlString;

    /**
     * 특허 조회 메서드
     *
     * @param user 로그인한 사용자 정보
     * @param projectIdx 프로젝트의 식별자
     * @return SearchPatentResponse 특허 정보
     */
    @Override
    public SearchPatentResponse getProjectPatent(User user, Integer projectIdx) {
        Project project = projectJpaRepository.findByIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
        ProjectPatent projectPatent = projectPatentJpaRepository.findByIdAndState(project.getProjectPatentId(), ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_PATENT));
        List<SearchInventorResponse> inventors = projectPatentInventorJpaRepository.findByProjectPatentId(projectPatent.getId());
        List<ProjectPatentInventor> patentInventors = projectMapper.toPatentInventor(inventors, projectPatent);
        return projectMapper.toSearchPatentResponse(projectPatent, patentInventors);
    }

    /**
     * 특허 검색 메서드
     *
     * @param user 로그인한 사용자 정보
     * @param applicationNumber 특허 출원번호
     * @param projectIdx 프로젝트의 식별자
     * @return SearchPatentResponse 특허 정보
     */
    @Override
    public SearchPatentResponse searchProjectPatent(User user, String applicationNumber, Integer projectIdx) {
        validApplicationNumber(applicationNumber);

        Project project = projectJpaRepository.findByIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));

        Optional<ProjectPatent> patentOptional = projectPatentJpaRepository.findByApplicationNumberAndState(applicationNumber, ACTIVE);
        if (patentOptional.isPresent()) {
            ProjectPatent existingPatent = patentOptional.get();
            List<SearchInventorResponse> inventors = projectPatentInventorJpaRepository.findByProjectPatentId(existingPatent.getId());
            List<ProjectPatentInventor> patentInventors = projectMapper.toPatentInventor(inventors, existingPatent);
            return projectMapper.toSearchPatentResponse(existingPatent, patentInventors);
        } else {
            inventorUrlString += SEARCH_PATENT + applicationNumber + ACCESS_KEY + key;
            applicantUrlString += SEARCH_PATENT + applicationNumber + ACCESS_KEY + key;
            basicInfoUrlString += SEARCH_PATENT + applicationNumber + SERVICE_KEY + key;
            List<SearchInventorResponse> inventors = fetchInventorInfo(inventorUrlString);
            SearchPatentResponse applicantInfo = fetchApplicantInfo(applicantUrlString);
            SearchPatentResponse basicInfo = fetchBasicInfo(basicInfoUrlString);

            ProjectPatent projectPatent = projectMapper.toProjectPatent(applicationNumber, basicInfo.applicationDate(), basicInfo.inventionTitle(), basicInfo.inventionTitleEnglish(), applicantInfo.applicantName(), applicantInfo.applicantEnglishName());
            ProjectPatent savePatent = projectPatentJpaRepository.save(projectPatent);

            projectMapper.toPatentInventor(inventors, savePatent).forEach(projectPatentInventorJpaRepository::save);
            projectJpaRepository.save(project);
            return projectMapper.toSearchPatentResponse(applicationNumber, basicInfo.applicationDate(), basicInfo.inventionTitle(), basicInfo.inventionTitleEnglish(), applicantInfo.applicantName(), applicantInfo.applicantEnglishName(), inventors);
        }
    }

    /**
     * 특허 등록 메서드
     *
     *
     * @param user 로그인한 사용자 정보
     * @param applicationNumber 특허 출원번호
     * @param projectIdx 프로젝트의 식별자
     * @return PatentResponse 특허 정보
     */
    @Override
    public PatentResponse registerPatent(User user, String applicationNumber, Integer projectIdx) {
        validApplicationNumber(applicationNumber);
        Project project = projectJpaRepository.findByIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
        if(user.getId() != project.getUser().getId()) {
            throw new BaseException(USER_NOT_PROJECT_OWNER);
        }
        if(project.getProjectPatentId() != null) {
            throw new BaseException(ALREADY_REGISTERED_PATENT);
        }
        ProjectPatent projectPatent = projectPatentJpaRepository.findByApplicationNumberAndState(applicationNumber, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_PATENT));

        if (projectPatent.getProjectPatentInventors().stream()
                .noneMatch(inventor -> inventor.getName().equals(user.getName())))
            throw new BaseException(USER_NOT_INVENTORY);
        project.setProjectPatentId(projectPatent.getId());
        return projectMapper.toPatentResponse(projectPatent);
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

    @Override
    public PatentResponse registerManualPatent(User user, Integer projectIdx, CreatePatentRequest createPatentRequest, MultipartFile file) {
//        Project project = projectJpaRepository.findByIdAndState(projectIdx, ACTIVE)
//                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
//        if(user.getId() != project.getUser().getId()) {
//            throw new BaseException(USER_NOT_PROJECT_OWNER);
//        }
//        if(project.getProjectPatentId() != null) {
//            throw new BaseException(ALREADY_REGISTERED_PATENT);
//        }
//        ProjectPatent savePatent = projectPatentJpaRepository.save(projectMapper.toProjectPatent(createPatentRequest, FilePath.storeFile(file, EVIDENCE)));
//        List<ProjectPatentInventor> inventors = projectMapper.createToPatentInventor(createPatentRequest.inventors(), savePatent);
//        inventors.forEach(projectPatentInventorJpaRepository::save);
//        project.setProjectPatentId(savePatent.getId());
//        return projectMapper.toPatentResponse(savePatent);
        return null;
    }

    private static void validApplicationNumber(String applicationNumber) {
        if (applicationNumber == null || !applicationNumber.matches("\\d{13}")) {
            throw new BaseException(INVALID_APPLICATION_NUMBER);
        }
    }

    private List<SearchInventorResponse> fetchInventorInfo(String urlString) {
        List<SearchInventorResponse> inventors = new ArrayList<>();
        Document doc = fetchDocument(urlString);
        NodeList inventorList = doc.getElementsByTagName("patentInventorInfo");
        if(inventorList.getLength() == 0) {
            throw new BaseException(NOT_EXIST_PATENT);
        }
        try {
            for (int i = 0; i < inventorList.getLength(); i++) {
                Node inventorNode = inventorList.item(i);

                if (inventorNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element inventorElement = (Element) inventorNode;
                    String inventorName = inventorElement.getElementsByTagName("InventorName").item(0).getTextContent();
                    String inventorEnglishName = inventorElement.getElementsByTagName("InventorEnglishsentenceName").item(0).getTextContent();
                    inventors.add(new SearchInventorResponse(inventorName, inventorEnglishName));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(PATENT_API_IO_ERROR);
        }
        return inventors;
    }

    private SearchPatentResponse fetchApplicantInfo(String urlString) {
        Document doc = fetchDocument(urlString);
        NodeList applicantList = doc.getElementsByTagName("patentApplicantInfo");

        if(applicantList.getLength() == 0) {
            throw new BaseException(NOT_EXIST_PATENT);
        }
        try {
            if (applicantList.getLength() > 0) {
                Node applicantNode = applicantList.item(0);
                if (applicantNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element applicantElement = (Element) applicantNode;
                    String applicantName = applicantElement.getElementsByTagName("ApplicantName").item(0).getTextContent();
                    String applicantEnglishName = applicantElement.getElementsByTagName("ApplicantEnglishsentenceName").item(0).getTextContent();
                    return projectMapper.toSearchPatentResponse(applicantName, applicantEnglishName);                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(PATENT_API_IO_ERROR);
        }
        return null;
    }

    private SearchPatentResponse fetchBasicInfo(String urlString) {
        Document doc = fetchDocument(urlString);
        NodeList itemList = doc.getElementsByTagName("item");
        if(itemList.getLength() == 0) {
            throw new BaseException(NOT_EXIST_PATENT);
        }
        try {
            if (itemList.getLength() > 0) {
                Node itemNode = itemList.item(0);

                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element itemElement = (Element) itemNode;
                    String applicationDate = itemElement.getElementsByTagName("applicationDate").item(0).getTextContent();
                    String inventionTitle = itemElement.getElementsByTagName("inventionTitle").item(0).getTextContent();
                    String inventionTitleEng = itemElement.getElementsByTagName("inventionTitleEng").item(0).getTextContent();
                    return projectMapper.toSearchPatentResponse(applicationDate, inventionTitle, inventionTitleEng);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(PATENT_API_IO_ERROR);
        }
        return null;
    }

    private Document fetchDocument(String urlString) {
        Document doc = null;
        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder docb = dbf.newDocumentBuilder();
            doc = docb.parse(is);
            doc.getDocumentElement().normalize();

            is.close();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
            throw new BaseException(PATENT_API_IO_ERROR);
        }
        return doc;
    }
}
