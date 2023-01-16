package hhvitek.documentmanager.document;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * GET /api/documents - list all with metadata
 * GET /api/documents/{id} - list single document with metadata by id
 * GET /api/documents/{id}/download - download single document by id
 *
 * POST /api/documents (MultipartFile) - create a new document by uploading a file to server
 * PUT /api/documents/{id} (MultipartFile) - reupload/replace existing document with a new one
 * PUT /api/documents/{id}/editMetadata (JSON) - edit existing document metadata without reloading a whole new file
 * DELETE /api/documents/{id} - delete existing file
 */
@RestController
@RequestMapping("/api/documents")
public class DocumentController {
	private final DocumentService documentService;

	public DocumentController(DocumentService documentService) {
		this.documentService = documentService;
	}

	@GetMapping
	public List<DocumentDTO> listAll() {
		List<Document> documents = documentService.getAll();
		return DocumentDTO.createList(documents);
	}

	@GetMapping("/{id}")
	public DocumentDTO listOne(@PathVariable("id") Integer id) {
		Document document = documentService.getOne(id);
		return DocumentDTO.create(document);
	}

	/**
	 * https://www.devglan.com/spring-boot/spring-boot-file-upload-download
	 */
	@GetMapping("/{id}/download")
	public ResponseEntity<byte[]> download(@PathVariable("id") Integer id) {
		Document document = documentService.getOne(id);

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(document.getContentType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getName() + "\"")
				.body(document.getFile());
	}

	/**
	 * Create/upload a new document, logged user is marked as the creator of an uploaded file.
	 */
	@PostMapping
	public DocumentDTO upload(@RequestParam("document") MultipartFile file, Authentication authentication) throws IOException {
		String loggedUser = authentication.getName();
		Document document = documentService.create(file, loggedUser);
		return DocumentDTO.create(document);
	}

	/**
	 * Re-upload/replace document and its metadata under specific id.
	 */
	@PutMapping("/{id}")
	public DocumentDTO edit(@PathVariable("id") Integer id, @RequestParam("document") MultipartFile file) throws IOException {
		Document document = documentService.update(id, file);
		return DocumentDTO.create(document);
	}

	/**
	 * Modifies just some document's metadata (name, contentType, createdBy & createdTime) while ignoring id and file itself...
	 */
	@PutMapping("/{id}/editMetadata")
	public DocumentDTO editMetadata(@PathVariable("id") Integer id, @RequestBody DocumentDTO metadata) {
		Document metadataDocument = metadata.toDocument();
		Document document = documentService.updateMetadataOnly(id, metadataDocument);
		return DocumentDTO.create(document);
	}

	/**
	 * Try to delete document. Will throw if document is part of any protocol. Protocol must be modified first (by removing document from protocol)
	 */
	@DeleteMapping("/{id}")
	public void delete(@PathVariable("id") Integer id) {
		documentService.delete(id);
	}
}
