package hhvitek.documentmanager.protocol;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import hhvitek.documentmanager.ApiException;
import hhvitek.documentmanager.document.DocumentRepository;

@Component
public class ProtocolValidator {
	private final DocumentRepository documentRepository;

	public ProtocolValidator(DocumentRepository documentRepository) {
		this.documentRepository = documentRepository;
	}

	public void validateDocumentIds(ProtocolDTO protocol) throws ApiException {
		List<Integer> documentIds = protocol.getDocumentIds();
		if (CollectionUtils.isEmpty(documentIds)) {
			throw new ApiException("Protocol must contain at least one document!");
		}

		if (!documentRepository.existsAllByIdIn(documentIds)) {
			throw new ApiException("Invalid documents. All must exist");
		}
	}
}
