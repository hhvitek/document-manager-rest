package hhvitek.documentmanager.document;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import hhvitek.documentmanager.error.ApiException;


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
		return documentRepository.save(document);
	}

	private Document createDocumentFrom(MultipartFile file) throws IOException {
		String name = StringUtils.cleanPath(file.getOriginalFilename());
		String contentType = file.getContentType();
		byte[] fileContent = file.getBytes();

		return new Document(name, contentType, fileContent);
	}

	public Document update(Integer id, MultipartFile file) throws IOException {
		Document oldDocument = getOne(id);
		Document document = createDocumentFrom(file);
		oldDocument.modifyUsing(document);

		return documentRepository.save(document);
	}

	public Document updateMetadataOnly(Integer id, Document metadataDocument) {
		Document oldDocument = getOne(id);
		oldDocument.modifyUsing(metadataDocument);
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
