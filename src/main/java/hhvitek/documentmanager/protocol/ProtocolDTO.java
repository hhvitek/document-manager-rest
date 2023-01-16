package hhvitek.documentmanager.protocol;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProtocolDTO {
	private Integer id;

	private String createdBy;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime createdTime;
	private ProtocolState state;

	private List<Integer> documentIds;

	public ProtocolDTO(Protocol protocol) {
		this.id = protocol.getId();
		this.createdBy = protocol.getCreatedBy();
		this.createdTime = protocol.getCreatedTime();
		this.state = protocol.getState();
		this.documentIds = protocol.getDocumentIds();
	}

	/**
	 * Creates protocol without id and documents.
	 */
	public Protocol toProtocolWithoutDocuments() {
		Protocol protocol = new Protocol();
		protocol.setCreatedBy(createdBy);
		protocol.setCreatedTime(createdTime);
		protocol.setState(state);
		return protocol;
	}

	public static ProtocolDTO create(Protocol protocol) {
		return new ProtocolDTO(protocol);
	}

	public static List<ProtocolDTO> createList(Collection<Protocol> protocols) {
		return protocols.stream()
				.map(ProtocolDTO::new)
				.collect(Collectors.toList());
	}

	public boolean hasAnyDocumentIds() {
		return documentIds != null && !documentIds.isEmpty();
	}

	public void modify(Protocol protocolToModify) {
		if (createdBy != null) {
			protocolToModify.setCreatedBy(createdBy);
		}
		if (createdTime != null) {
			protocolToModify.setCreatedTime(createdTime);
		}
		if (state != null) {
			protocolToModify.setState(state);
		}
	}
}
