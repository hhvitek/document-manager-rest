package hhvitek.documentmanager.document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import hhvitek.documentmanager.protocol.Protocol;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity(name = "document")
@Data
@NoArgsConstructor
public class Document {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private String createdBy;
	@Column(nullable = false)
	private Instant createdTime;
	private String contentType;

	@Column(nullable = false)
	private byte[] file;

	/**
	 * This document is part of the following protocols
	 */
	@JsonIdentityReference(alwaysAsId = true) // list contained protocols just as an array of ids
	@ManyToMany(mappedBy = "documents")
	private List<Protocol> protocols = new ArrayList<>();

	public Document(String name, String contentType, byte[] file) {
		this.name = name;
		this.contentType = contentType;
		this.file = file;
	}

	/**
	 * Modify by copying only non-null attributes of another Document.
	 */
	public void modifyFrom(Document another) {
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

	public void clearAllNonMetadata() {
		this.id = null;
		this.file = null;
	}

	@JsonIgnore
	public boolean isPartOfAnyProtocol() {
		return !protocols.isEmpty();
	}
}
