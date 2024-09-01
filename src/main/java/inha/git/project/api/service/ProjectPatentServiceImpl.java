package inha.git.project.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.project.api.controller.dto.response.SearchInventorResponse;
import inha.git.project.api.controller.dto.response.SearchPatentResponse;
import inha.git.project.api.mapper.ProjectMapper;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static inha.git.common.code.status.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjectPatentServiceImpl implements ProjectPatentService {

    private final ProjectPatentJpaRepository projectPatentJpaRepository;
    private final ProjectPatentInventorJpaRepository projectPatentInventorJpaRepository;
    private final ProjectMapper projectMapper;

    @Value("${kipris.access-key}")
    private String key;

    @Value("${kipris.inventor-url}")
    private String inventorUrlString;

    @Value("${kipris.applicant-url}")
    private String applicantUrlString;

    @Value("${kipris.basic-info-url}")
    private String basicInfoUrlString;

    @Override
    @Transactional(readOnly = true)
    public SearchPatentResponse getPatent(User user, String applicationNumber) {
        if (applicationNumber == null || !applicationNumber.matches("\\d{13}")) {
            throw new BaseException(INVALID_APPLICATION_NUMBER);
        }

        inventorUrlString += "?applicationNumber=" + applicationNumber + "&accessKey=" + key;
        applicantUrlString += "?applicationNumber=" + applicationNumber + "&accessKey=" + key;
        basicInfoUrlString += "?applicationNumber=" + applicationNumber + "&ServiceKey=" + key;

        // 발명자 정보 가져오기
        List<SearchInventorResponse> inventors = fetchInventorInfo(inventorUrlString);

        // 출원인 정보 가져오기
        SearchPatentResponse applicantInfo = fetchApplicantInfo(applicantUrlString);

        // 특허 기본 정보 가져오기
        SearchPatentResponse basicInfo = fetchBasicInfo(basicInfoUrlString);

        // 정보를 합쳐서 반환
        return new SearchPatentResponse(
                applicationNumber,
                basicInfo.applicationDate(),
                basicInfo.inventionTitle(),
                basicInfo.inventionTitleEnglish(),
                applicantInfo.applicantName(),
                applicantInfo.applicantEnglishName(),
                inventors
        );
    }

    private List<SearchInventorResponse> fetchInventorInfo(String urlString) {
        List<SearchInventorResponse> inventors = new ArrayList<>();
        Document doc = fetchDocument(urlString);
        NodeList inventorList = doc.getElementsByTagName("patentInventorInfo");
        log.info("inventorList.getLength() : " + inventorList.getLength());
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
                    return new SearchPatentResponse(null, null, null, null, applicantName, applicantEnglishName, null);
                }
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
        log.info("itemList.getLength() : " + itemList.getLength());
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

                    return new SearchPatentResponse(
                            null,
                            applicationDate,
                            inventionTitle,
                            inventionTitleEng,
                            null,
                            null,
                            null
                    );
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
