package hhvitek.documentmanager.document;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import hhvitek.documentmanager.configuration.error.ApiException;


@Service
public class DocumentService {
	private final DocumentRepository documentRepository;

	public DocumentService(DocumentRepository documentRepository) {
		this.documentRepository = documentRepository;
	}

	public List<Document> getAll() {
		return documentRepository.findAll();
	}

	/**
	 * Tries to retrieve document by id. If no such document exists throws ApiException.
	 */
	public Document getOne(Integer id) {
		return documentRepository.findById(id)
				.orElseThrow(() -> new ApiException("No such document found with id=" + id));
	}

	public Document create(MultipartFile file, String createdBy) throws IOException {
		Document document = createDocumentFrom(file);
		document.setCreatedBy(createdBy);
		document.setCreatedTime(Instant.now());
		return documentRepository.save(document);
	}

	private Document createDocumentFrom(MultipartFile file) throws IOException {
		String name = file.getOriginalFilename();
		if (StringUtils.isBlank(name)) {
			name = "unknown-file-name";
		}

		String contentType = file.getContentType();
		byte[] fileContent = file.getBytes();

		return new Document(name, contentType, fileContent);
	}

	public Document update(Integer id, MultipartFile file) throws IOException {
		Document oldDocument = getOne(id);
		Document document = createDocumentFrom(file);
		oldDocument.modifyFrom(document);

		return documentRepository.save(oldDocument);
	}

	public Document updateMetadataOnly(Integer id, Document metadataDocument) {
		Document oldDocument = getOne(id);

		metadataDocument.clearAllNonMetadata(); // only metadata should be modified. For example let's not allow document id modification.
		oldDocument.modifyFrom(metadataDocument);

		return documentRepository.save(oldDocument);
	}

	public void delete(Integer id) {
		Document document = getOne(id);
		if (document.isPartOfAnyProtocol()) {
			throw new ApiException("Document id=" + id + " is a member of an existing protocol. Cannot delete this document.");
		}

		documentRepository.deleteById(id);
	}
}
