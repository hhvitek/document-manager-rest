package hhvitek.documentmanager.document;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DocumentDTO {
	private Integer id;

	private String name;

	private String createdBy;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime createdTime;

	private String contentType;

	private List<Integer> protocolIds;

	public DocumentDTO(Document document) {
		this.id = document.getId();
		this.name = document.getName();
		this.createdBy = document.getCreatedBy();
		this.createdTime = document.getCreatedTime();
		this.contentType = document.getContentType();

		this.protocolIds = document.getProtocolIds();
	}

	public static List<DocumentDTO> createList(Collection<Document> documents) {
		return documents.stream()
				.map(DocumentDTO::new)
				.collect(Collectors.toList());
	}

	public static DocumentDTO create(Document documents) {
		return new DocumentDTO(documents);
	}

	public DocumentMetadata toMetadata() {
		DocumentMetadata metadata = new DocumentMetadata();
		metadata.setName(name);
		metadata.setCreatedBy(createdBy);
		metadata.setCreatedTime(createdTime);
		metadata.setContentType(contentType);

		return metadata;
	}
}
