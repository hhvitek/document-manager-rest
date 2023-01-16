package hhvitek.documentmanager.protocol;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import hhvitek.documentmanager.document.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "protocol")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Protocol {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String createdBy;
	private LocalDateTime createdTime;
	private ProtocolState state;

	@ManyToMany
	@JoinTable(
			name = "protocol_to_documents",
			joinColumns = @JoinColumn(name = "protocol_id"),
			inverseJoinColumns = @JoinColumn(name = "document_id")
	)
	private List<Document> documents = new ArrayList<>();

	public List<Integer> getDocumentIds() {
		return documents.stream()
				.map(Document::getId)
				.collect(Collectors.toList());
	}


}
