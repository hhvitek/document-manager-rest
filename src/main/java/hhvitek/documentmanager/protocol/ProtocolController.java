package hhvitek.documentmanager.protocol;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	@PostMapping
	public ProtocolDTO create(@RequestBody ProtocolDTO protocol) {
		return ProtocolDTO.create(protocolService.create(protocol));
	}

	/**
	 * Edit everything / Replace
	 */
	@PutMapping("/{id}")
	public ProtocolDTO edit(@PathVariable("id") Integer id, @RequestBody ProtocolDTO protocol) {
		return ProtocolDTO.create(protocolService.update(id, protocol));
	}

	/**
	 * Modifies just some document's metadata while ignoring id and file itself...
	 */
	@PutMapping("/{id}/editState")
	public ProtocolDTO editState(@PathVariable("id") Integer id, @RequestParam("state") ProtocolState state) {
		Protocol protocol = protocolService.updateState(id, state);
		return ProtocolDTO.create(protocol);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable("id") Integer id) {
		protocolService.delete(id);
	}
}
