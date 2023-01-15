package hhvitek.documentmanager.document;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import hhvitek.documentmanager.protocol.Protocol;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Delegate;

@Entity(name = "document")
@Data
@NoArgsConstructor
public class Document {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Embedded
	@Delegate
	private DocumentMetadata metadata;

	private byte[] file;

	/**
	 * This document is part of the following protocols
	 */
	@ManyToMany(mappedBy = "documents")
	private List<Protocol> protocols = new ArrayList<>();

	public Document(String name, String contentType, byte[] file) {
		this.metadata = new DocumentMetadata(name, contentType);
		this.file = file;
	}

	public void modifyMetadataUsing(DocumentMetadata anotherMetadata) {
		metadata.modifyUsing(anotherMetadata);
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
