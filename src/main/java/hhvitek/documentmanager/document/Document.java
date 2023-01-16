package hhvitek.documentmanager.document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import hhvitek.documentmanager.protocol.Protocol;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "document")
@Data
@NoArgsConstructor
public class Document {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String name;
	private String createdBy;
	private LocalDateTime createdTime = LocalDateTime.now();
	private String contentType;

	private byte[] file;

	/**
	 * This document is part of the following protocols
	 */
	@ManyToMany(mappedBy = "documents")
	private List<Protocol> protocols = new ArrayList<>();

	public Document(String name, String contentType, byte[] file) {
		this.name = name;
		this.contentType = contentType;
		this.createdTime = LocalDateTime.now();
		this.file = file;
	}

	/**
	 * Modify by copying only non-null attributes of another Document.
	 */
	public void modifyUsing(Document another) {
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
		if (another.file != null) {
			this.file = another.file;
		}
	}

	public boolean isPartOfAnyProtocol() {
		return !protocols.isEmpty();
	}

	public List<Integer> getProtocolIds() {
		return protocols.stream()
				.map(Protocol::getId)
				.collect(Collectors.toList());
	}
}
