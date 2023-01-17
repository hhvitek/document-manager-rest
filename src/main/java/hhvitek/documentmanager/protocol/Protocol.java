package hhvitek.documentmanager.protocol;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import hhvitek.documentmanager.document.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity(name = "protocol")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Protocol {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false)
	private String createdBy;
	@Column(nullable = false)
	private Instant createdTime;
	@Column(nullable = false)
	private ProtocolState state;

	@JsonIdentityReference(alwaysAsId = true)
	@ManyToMany
	@JoinTable(
			name = "protocol_to_documents",
			joinColumns = @JoinColumn(name = "protocol_id"),
			inverseJoinColumns = @JoinColumn(name = "document_id")
	)
	private List<Document> documents = new ArrayList<>();

	public Protocol(String createdBy, Instant createdTime, ProtocolState state) {
		this.createdBy = createdBy;
		this.createdTime = createdTime;
		this.state = state;
	}
}
