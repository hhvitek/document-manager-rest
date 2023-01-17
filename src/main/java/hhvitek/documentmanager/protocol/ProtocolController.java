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

/**
 * GET /api/protocols - list all with metadata
 * GET /api/protocols/{id} - list single protocol with metadata by id
 *
 * POST /api/protocols (JSON) - create a new protocol by JSON representation.
 * PUT /api/protocols/{id} (JSON) - edit existing protocol
 * PUT /api/protocols/{id}/editState/{string} - edit only protocol's state to {string} value
 * DELETE /api/protocols/{id} - delete existing
 */
@RestController
@RequestMapping("/api/protocols")
public class ProtocolController {
	private final ProtocolService protocolService;

	public ProtocolController(ProtocolService protocolService) {
		this.protocolService = protocolService;
	}

	@GetMapping
	public List<Protocol> listAll() {
		return protocolService.getAll();
	}

	@GetMapping("/{id}")
	public Protocol listOne(@PathVariable("id") Integer id) {
		return protocolService.getOne(id);
	}

	/**
	 * Create a new protocol. Authenticated user is logged in createdBy field, if incoming JSON does not contain any createdBy value.
	 */
	@PostMapping
	public Protocol create(@RequestBody ProtocolDTO dto, Authentication authentication) {
		return protocolService.create(dto, authentication);
	}

	/**
	 * Edit everything / replace under specific id. May change createdBy field as requested.
	 */
	@PutMapping("/{id}")
	public Protocol edit(@PathVariable("id") Integer id, @RequestBody ProtocolDTO dto) {
		return protocolService.update(id, dto);
	}

	/**
	 * Endpoint to only modify protocol's state.
	 */
	@PutMapping("/{id}/editState/{newState}")
	public Protocol editState(@PathVariable("id") Integer id, @PathVariable("newState") ProtocolState newState) {
		return protocolService.updateState(id, newState);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable("id") Integer id) {
		protocolService.delete(id);
	}
}
