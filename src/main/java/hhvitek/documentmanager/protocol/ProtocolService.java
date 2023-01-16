package hhvitek.documentmanager.protocol;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hhvitek.documentmanager.document.Document;
import hhvitek.documentmanager.document.DocumentRepository;
import hhvitek.documentmanager.error.ApiException;

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

	public Protocol getOne(Integer id) {
		return protocolRepository.findById(id).orElseThrow(() -> new ApiException("No such protocol found with id=" + id));
	}

	@Transactional
	public Protocol create(ProtocolDTO dto) {
		Protocol newProtocol = dto.toProtocolWithoutDocuments();

		if (CollectionUtils.isEmpty(dto.getDocumentIds())) {
			throw new ApiException("Protocol must contain at least one document!");
		}

		setDocumentsIfNotFoundThrow(newProtocol, dto);

		return protocolRepository.save(newProtocol);
	}

	private void setDocumentsIfNotFoundThrow(Protocol protocol, ProtocolDTO dto) {
		List<Document> documents = documentRepository.findAllById(dto.getDocumentIds());
		if (documents.size() != dto.getDocumentIds().size()) {
			throw new ApiException("Invalid documents. At least one document was not found.");
		}
		protocol.setDocuments(documents);
	}

	@Transactional
	public Protocol update(Integer id, ProtocolDTO dto) {
		Protocol oldProtocol = getOne(id); // it exists

		if (dto.hasAnyDocumentIds()) { // if document ids are part of passed DTO, otherwise retain old values
			setDocumentsIfNotFoundThrow(oldProtocol, dto);
		}

		dto.modify(oldProtocol);

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
