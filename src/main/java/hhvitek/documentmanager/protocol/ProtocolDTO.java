package hhvitek.documentmanager.protocol;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProtocolDTO {
	private String createdBy;
	private Instant createdTime;
	private ProtocolState state;

	private List<Integer> documents;

	/**
	 * Creates protocol without id and documents. If some attributes are missing inside this DTO, default values are used instead to create Protocol.
	 *
	 * For example. No createdTime value -> uses current time instead...
	 */
	public Protocol toProtocolWithDefaultValues(String createdBy) {
		Protocol protocol = new Protocol(createdBy, Instant.now(), ProtocolState.NEW);
		modifyProtocol(protocol);
		return protocol;
	}

	public boolean hasAnyDocuments() {
		return documents != null && !documents.isEmpty();
	}

	public void modifyProtocol(Protocol protocolToModify) {
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
