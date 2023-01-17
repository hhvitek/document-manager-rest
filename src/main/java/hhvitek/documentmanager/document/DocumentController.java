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
 * GET /api/documents/{id}/download - download a single document by id
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
	public List<Document> listAll() {
		return documentService.getAll();
	}

	@GetMapping("/{id}")
	public Document listOne(@PathVariable("id") Integer id) {
		return documentService.getOne(id);
	}

	/*
	 * https://www.devglan.com/spring-boot/spring-boot-file-upload-download
	 */
	@GetMapping("/{id}/download")
	public ResponseEntity<byte[]> download(@PathVariable("id") Integer id) {
		Document document = documentService.getOne(id);

		String contentType = document.getContentType();
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getName() + "\"")
				.body(document.getFile());
	}

	/**
	 * Create/upload a new document, logged user is marked as the creator of an uploaded file.
	 */
	@PostMapping
	public Document upload(@RequestParam("document") MultipartFile file, Authentication authentication) throws IOException {
		String loggedUser = authentication.getName();
		return documentService.create(file, loggedUser);
	}

	/**
	 * Re-upload/replace document and its metadata under specific id.
	 */
	@PutMapping("/{id}")
	public Document edit(@PathVariable("id") Integer id, @RequestParam("document") MultipartFile file) throws IOException {
		return documentService.update(id, file);
	}

	/**
	 * Modifies just some document's metadata (name, contentType, createdBy & createdTime) while ignoring id and file itself...
	 */
	@PutMapping("/{id}/editMetadata")
	public Document editMetadata(@PathVariable("id") Integer id, @RequestBody Document metadataDocument) {
		return documentService.updateMetadataOnly(id, metadataDocument);
	}

	/**
	 * Try to delete document. Will throw if document is part of any protocol. Protocol must be modified first (by removing document from protocol)
	 */
	@DeleteMapping("/{id}")
	public void delete(@PathVariable("id") Integer id) {
		documentService.delete(id);
	}
}
