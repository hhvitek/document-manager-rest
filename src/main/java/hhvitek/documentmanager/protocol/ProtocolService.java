package hhvitek.documentmanager.protocol;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hhvitek.documentmanager.document.Document;
import hhvitek.documentmanager.document.DocumentRepository;

@Service
public class ProtocolService {
	private final ProtocolValidator protocolValidator;
	private final ProtocolRepository protocolRepository;
	private final DocumentRepository documentRepository;

	public ProtocolService(ProtocolValidator protocolValidator, ProtocolRepository protocolRepository, DocumentRepository documentRepository) {
		this.protocolValidator = protocolValidator;
		this.protocolRepository = protocolRepository;
		this.documentRepository = documentRepository;
	}


	public List<Protocol> getAll() {
		return protocolRepository.findAll();
	}

	public Protocol getOne(Integer id) {
		return protocolRepository.findById(id).orElseThrow();
	}

	@Transactional
	public Protocol create(ProtocolDTO protocol) {
		protocolValidator.validateDocumentIds(protocol); // all documents exists now
		List<Document> documents = documentRepository.findAllById(protocol.getDocumentIds());

		Protocol newProtocol = protocol.toProtocol();
		newProtocol.setDocuments(documents);

		return protocolRepository.save(newProtocol);
	}

	@Transactional
	public Protocol update(Integer id, ProtocolDTO protocol) {
		Protocol oldProtocol = getOne(id); // it exists

		if (protocol.hasAnyDocumentIds()) {
			protocolValidator.validateDocumentIds(protocol); // lets validate protocol ids and set them
			List<Document> documents = documentRepository.findAllById(protocol.getDocumentIds());
			oldProtocol.setDocuments(documents);
		}

		protocol.modify(oldProtocol);

		return protocolRepository.save(oldProtocol);
	}

	@Transactional
	public Protocol updateState(Integer id, ProtocolState state) {
		Protocol protocol = getOne(id);
		protocol.setState(state);
		return protocolRepository.save(protocol);
	}

	public void delete(Integer id) {
		protocolRepository.deleteById(id);
	}


}
