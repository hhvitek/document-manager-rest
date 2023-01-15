package hhvitek.documentmanager.document;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import hhvitek.documentmanager.ApiException;


@Service
public class DocumentService {
	private final DocumentRepository documentRepository;

	public DocumentService(DocumentRepository documentRepository) {
		this.documentRepository = documentRepository;
	}

	public List<Document> getAll() {
		return documentRepository.findAll();
	}

	public Document getOne(Integer id) {
		return documentRepository.findById(id).orElseThrow();
	}

	public Document create(MultipartFile file) throws IOException {
		Document document = createDocumentFrom(file);
		return documentRepository.save(document);
	}

	private Document createDocumentFrom(MultipartFile file) throws IOException {
		String name = StringUtils.cleanPath(file.getOriginalFilename());
		String contentType = file.getContentType();
		byte[] fileContent = file.getBytes();

		return new Document(name, contentType, fileContent);
	}

	public Document update(Integer id, MultipartFile file) throws IOException {
		Document document = createDocumentFrom(file);
		document.setId(id);

		return documentRepository.save(document);
	}

	@Transactional
	public Document updateMetadataOnly(Integer id, DocumentMetadata metadata) {
		Document oldDocument = documentRepository.getReferenceById(id);
		oldDocument.modifyMetadataUsing(metadata);
		return documentRepository.save(oldDocument);
	}

	public void delete(Integer id) {
		Document document = getOne(id);
		if (document.isPartOfAnyProtocol()) {
			throw new ApiException("Document is a member of an existing protocol. Cannot delete this document.");
		}

		documentRepository.deleteById(id);
	}
}
