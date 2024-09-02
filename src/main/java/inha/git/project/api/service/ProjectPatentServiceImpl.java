package inha.git.project.api.service;

import inha.git.common.exceptions.BaseException;
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
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
     * 특허 검색 메서드
     *
     * <p>이 메서드는 주어진 특허 출원번호(applicationNumber)를 기반으로 특허 정보를 검색하고 반환한다.
     * 사용자가 요청한 특허가 이미 데이터베이스에 존재하는 경우, 해당 특허 정보를 조회하여 반환한다.
     * 만약 데이터베이스에 특허 정보가 존재하지 않는다면, 외부 API를 통해 특허 정보를 조회하고,
     * 조회된 정보를 데이터베이스에 저장한 후 반환한다.</p>
     *
     * @param user 로그인한 사용자 정보
     * @param applicationNumber 특허 출원번호
     * @param projectIdx 프로젝트의 식별자
     * @return SearchPatentResponse 특허 정보
     */
    @Override
    public SearchPatentResponse getPatent(User user, String applicationNumber, Integer projectIdx) {
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
    @Override
    public PatentResponse registerPatent(User user, String applicationNumber) {
        validApplicationNumber(applicationNumber);
        inventorUrlString += SEARCH_PATENT + applicationNumber + ACCESS_KEY + key;
        applicantUrlString += SEARCH_PATENT + applicationNumber + ACCESS_KEY + key;
        basicInfoUrlString += SEARCH_PATENT + applicationNumber + SERVICE_KEY + key;
        List<SearchInventorResponse> inventors = fetchInventorInfo(inventorUrlString);
        SearchPatentResponse applicantInfo = fetchApplicantInfo(applicantUrlString);
        SearchPatentResponse basicInfo = fetchBasicInfo(basicInfoUrlString);
        return null;
    }

    private static void validApplicationNumber(String applicationNumber) {
        if (applicationNumber == null || !applicationNumber.matches("\\d{13}")) {
            throw new BaseException(INVALID_APPLICATION_NUMBER);
        }
    }


    /**
     * 특허 발명자 조회
     *
     * @param urlString 특허 발명자 조회 URL
     * @return List<SearchInventorResponse> 특허 발명자 정보
     */
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

    /**
     * 특허 출원인 조회
     *
     * @param urlString 특허 출원인 조회 URL
     * @return SearchPatentResponse 특허 출원인 정보
     */
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

    /**
     * 특허 기본 정보 조회
     *
     * @param urlString 특허 기본 정보 조회 URL
     * @return SearchPatentResponse 특허 기본 정보
     */
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

    /**
     * 특허 정보 조회
     *
     * @param urlString 특허 정보 조회 URL
     * @return Document 특허 정보
     */
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
