package hhvitek.documentmanager.protocol;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/protocols")
public class ProtocolController {
	private final ProtocolService protocolService;

	public ProtocolController(ProtocolService protocolService) {
		this.protocolService = protocolService;
	}

	@GetMapping
	public List<ProtocolDTO> listAll() {
		return ProtocolDTO.createList(protocolService.getAll());
	}

	@GetMapping("/{id}")
	public ProtocolDTO listOne(@PathVariable("id") Integer id) {
		return ProtocolDTO.create(protocolService.getOne(id));
	}

	/**
	 * Create a new protocol. Authenticated user is logged in createdBy filed.
	 */
	@PostMapping
	public ProtocolDTO create(@RequestBody ProtocolDTO dto, Authentication authentication) {
		dto.setCreatedBy(authentication.getName());
		return ProtocolDTO.create(protocolService.create(dto));
	}

	/**
	 * Edit everything / Replace under specific id. May change createdBy field as requested
	 */
	@PutMapping("/{id}")
	public ProtocolDTO edit(@PathVariable("id") Integer id, @RequestBody ProtocolDTO dto) {
		return ProtocolDTO.create(protocolService.update(id, dto));
	}

	/**
	 * To only modify protocol's state
	 */
	@PutMapping("/{id}/editState/{newState}")
	public ProtocolDTO editState(@PathVariable("id") Integer id, @PathVariable("newState") ProtocolState newState) {
		Protocol protocol = protocolService.updateState(id, newState);
		return ProtocolDTO.create(protocol);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable("id") Integer id) {
		protocolService.delete(id);
	}
}
