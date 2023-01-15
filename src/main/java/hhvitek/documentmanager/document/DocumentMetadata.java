package hhvitek.documentmanager.document;

import java.time.LocalDateTime;

import javax.persistence.Embeddable;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentMetadata {
	private String name;

	private String createdBy;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime createdTime = LocalDateTime.now();

	private String contentType;

	public DocumentMetadata(String name, String contentType) {
		this.name = name;
		this.contentType = contentType;
		this.createdTime = LocalDateTime.now();
	}

	public void modifyUsing(DocumentMetadata another) {
		if (another.name != null) {
			this.name = another.name;
		}
		if (another.contentType != null) {
			this.contentType = another.contentType;
		}
		if (another.createdBy != null) {
			this.createdBy = another.createdBy;
		}
		if (another.createdTime != null) {
			this.createdTime = another.createdTime;
		}
	}
}
