package hhvitek.documentmanager.document;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

	// https://www.devglan.com/spring-boot/spring-boot-file-upload-download
	@GetMapping("/{id}/download")
	public ResponseEntity<byte[]> download(@PathVariable("id") Integer id) {
		Document document = documentService.getOne(id);

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(document.getContentType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getName() + "\"")
				.body(document.getFile());
	}

	@PostMapping
	public DocumentDTO upload(@RequestParam("document") MultipartFile file) throws IOException {
		Document document = documentService.create(file);
		return DocumentDTO.create(document);
	}

	/**
	 * Re-upload using specific id
	 */
	@PutMapping("/{id}")
	public DocumentDTO edit(@PathVariable("id") Integer id, @RequestParam("document") MultipartFile file) throws IOException {
		Document document = documentService.update(id, file);
		return DocumentDTO.create(document);
	}

	/**
	 * Modifies just some document's metadata while ignoring id and file itself...
	 */
	@PutMapping("/{id}/editMetadata")
	public DocumentDTO editMetadata(@PathVariable("id") Integer id, @RequestBody DocumentDTO metadata) {
		Document document = documentService.updateMetadataOnly(id, metadata.toMetadata());
		return DocumentDTO.create(document);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable("id") Integer id) {
		documentService.delete(id);
	}
}
