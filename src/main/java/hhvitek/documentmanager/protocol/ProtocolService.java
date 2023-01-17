package hhvitek.documentmanager.protocol;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hhvitek.documentmanager.document.Document;
import hhvitek.documentmanager.document.DocumentRepository;
import hhvitek.documentmanager.configuration.error.ApiException;

@Service
public class ProtocolService {
	private final ProtocolRepository protocolRepository;
	private final DocumentRepository documentRepository;

	public ProtocolService(ProtocolRepository protocolRepository, DocumentRepository documentRepository) {
		this.protocolRepository = protocolRepository;
		this.documentRepository = documentRepository;
	}


	public List<Protocol> getAll() {
		return protocolRepository.findAll();
	}

	/**
	 * Retrieves protocol by id. If no such protocol exists, it throws ApiException.
	 */
	public Protocol getOne(Integer id) {
		return protocolRepository.findById(id)
				.orElseThrow(() -> new ApiException("No such protocol found with id=" + id));
	}

	@Transactional
	public Protocol create(ProtocolDTO dto, Authentication authentication) {
		Protocol newProtocol = dto.toProtocolWithDefaultValues(authentication.getName());

		if (CollectionUtils.isEmpty(dto.getDocuments())) {
			throw new ApiException("Protocol must contain at least one document!");
		}
		setDocumentsIfNotFoundThrow(newProtocol, dto.getDocuments());

		return protocolRepository.save(newProtocol);
	}

	private void setDocumentsIfNotFoundThrow(Protocol protocol, List<Integer> documentIdsToSet) {
		List<Document> documents = documentRepository.findAllById(documentIdsToSet);
		if (documents.size() != documentIdsToSet.size()) {
			throw new ApiException("Invalid documents. At least one document was not found.");
		}
		protocol.setDocuments(documents);
	}

	@Transactional
	public Protocol update(Integer id, ProtocolDTO dto) {
		Protocol oldProtocol = getOne(id); // it exists

		if (dto.hasAnyDocuments()) { // if document ids are part of passed DTO, otherwise retain old values
			setDocumentsIfNotFoundThrow(oldProtocol, dto.getDocuments());
		}

		dto.modifyProtocol(oldProtocol);

		return protocolRepository.save(oldProtocol);
	}


	public Protocol updateState(Integer id, ProtocolState state) {
		Protocol oldProtocol = getOne(id);
		oldProtocol.setState(state);
		return protocolRepository.save(oldProtocol);
	}

	public void delete(Integer id) {
		if (protocolRepository.existsById(id)) {
			protocolRepository.deleteById(id); // may throw EmptyResultDataAccessException if no such id exists
		} else {
			throw new ApiException("Cannot delete protocol. No such protocol found with id=" + id);
		}
	}


}
