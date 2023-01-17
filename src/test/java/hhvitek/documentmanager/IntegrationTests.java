package hhvitek.documentmanager;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional // restore db state after each test
class IntegrationTests {

	private static final String API_DOCUMENTS = "/api/documents";
	private static final String API_PROTOCOLS = "/api/protocols";

	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser
	public void initDbAsExpectedTwoDocumentsAlreadyExistsTest() throws Exception {
		mockMvc.perform(
						get(API_DOCUMENTS)
								.accept(MediaType.APPLICATION_JSON_VALUE)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.*", hasSize(2)))
				.andExpect(jsonPath("$.[0].*", hasSize(7))) // no surprise new field in json
				.andExpect(jsonPath("$.[0].id", is(1)))
				.andExpect(jsonPath("$.[0].name", is("first")))
				.andExpect(jsonPath("$.[0].created_by", is("firstUser")))
				.andExpect(jsonPath("$.[0].created_time", is("2022-12-22T22:22:22Z")))
				.andExpect(jsonPath("$.[0].content_type", is(nullValue())))
				.andExpect(jsonPath("$.[0].file", is(not(nullValue()))))
				.andExpect(jsonPath("$.[0].protocols", hasSize(1)))
				.andExpect(content().string(containsString("\"id\":2,\"name\":\"second\"")));
	}

	@Test
	@WithMockUser
	public void initDbAsExpectedSingleProtocolAlreadyExistsTest() throws Exception {
		mockMvc.perform(
						get(API_PROTOCOLS)
								.accept(MediaType.APPLICATION_JSON_VALUE)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.*", hasSize(1)))
				.andExpect(jsonPath("$.[0].*", hasSize(5)))
				.andExpect(jsonPath("$.[0].id", is(1)))
				.andExpect(jsonPath("$.[0].created_by", is("user1")))
				.andExpect(jsonPath("$.[0].created_time", is("2022-12-22T22:22:22Z")))
				.andExpect(jsonPath("$.[0].state", is("NEW")))
				.andExpect(jsonPath("$.[0].documents", hasSize(1)))
				.andExpect(jsonPath("$.[0].documents[0]", is(1)));
	}

	@Test
	@WithMockUser
	public void editMetadataChangingOnlyNameTest() throws Exception {
		mockMvc.perform(
						put(API_DOCUMENTS + "/1/editMetadata")
								.accept(MediaType.APPLICATION_JSON_VALUE)
								.contentType(MediaType.APPLICATION_JSON_VALUE)
								.content("{ \"name\" : \"anotherName\"}") // changing only name, no other field should be modified
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("name", is("anotherName")))
				.andExpect(jsonPath("created_by", is("firstUser")))
				.andExpect(jsonPath("created_time", is("2022-12-22T22:22:22Z"))); // not changed


		mockMvc.perform(
						get(API_DOCUMENTS)
								.accept(MediaType.APPLICATION_JSON_VALUE)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.*", hasSize(2)))
				.andExpect(jsonPath("$.[0].id", is(1)))
				.andExpect(jsonPath("$.[0].name", is("anotherName")))
				.andExpect(jsonPath("$.[0].created_by", is("firstUser")))

				.andExpect(jsonPath("$.[1].id", is(2)))
				.andExpect(jsonPath("$.[1].name", is("second"))) // modified
				.andExpect(jsonPath("$.[1].created_by", is("secondUser"))); // retained old
	}

	@Test
	@WithMockUser
	public void uploadNewDocumentTest() throws Exception {
		MockMultipartFile document = new MockMultipartFile(
				"document", // must be document see controller @RequestParam
				"file.txt",
				"text/plain",
				"This is a nice text".getBytes(StandardCharsets.UTF_8));

		mockMvc.perform(
						multipart(HttpMethod.POST, API_DOCUMENTS)
								.file(document)
				)
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("\"id\":3,\"name\":\"file.txt\"")));

		mockMvc.perform(
						get(API_DOCUMENTS)
								.accept(MediaType.APPLICATION_JSON_VALUE)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.*", hasSize(3)))

				.andExpect(jsonPath("$.[2].id", is(3)))
				.andExpect(jsonPath("$.[2].name", is("file.txt")))
				.andExpect(jsonPath("$.[2].content_type", is("text/plain")))
				.andExpect(jsonPath("$.[2].created_by", is("user"))) // test username added properly
				.andExpect(jsonPath("$.[2].created_time", is(not(nullValue())))); // also createdTime added as well as username
	}

	@Test
	@WithMockUser
	public void createProtocolCasesTest() throws Exception {
		 mockMvc.perform(post(API_PROTOCOLS)
						.accept(MediaType.APPLICATION_JSON_VALUE)
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content("{\"created_by\" : \"user\"}")
				)
				.andExpect(status().isBadRequest())
				.andExpect(status().reason(containsString("Protocol must contain at least one document!")));


		mockMvc.perform(post(API_PROTOCOLS)
						.accept(MediaType.APPLICATION_JSON_VALUE)
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content("{\"created_by\" : \"user\", \"documents\" : []}")
				)
				.andExpect(status().isBadRequest())
				.andExpect(status().reason(containsString("Protocol must contain at least one document!")));

		mockMvc.perform(post(API_PROTOCOLS)
						.accept(MediaType.APPLICATION_JSON_VALUE)
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content("{\"created_by\" : \"user\", \"documents\" : [1]}")
				)
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("\"created_by\":\"user\"")));

		mockMvc.perform(post(API_PROTOCOLS)
						.accept(MediaType.APPLICATION_JSON_VALUE)
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content("{\"created_by\" : \"user\", \"documents\" : [1, 9999]}")
				)
				.andExpect(status().isBadRequest())
				.andExpect(status().reason(containsString("At least one document was not found.")));
	}

	@Test
	@WithMockUser
	public void dontAllowDeleteForDocumentsBelongingToProtocol() throws Exception {
		mockMvc.perform(delete(API_DOCUMENTS + "/1")
						.accept(MediaType.APPLICATION_JSON_VALUE)
				)
				.andExpect(status().isBadRequest())
				.andExpect(status().reason(containsString("Cannot delete this document.")));

		mockMvc.perform(delete(API_DOCUMENTS + "/2")
						.accept(MediaType.APPLICATION_JSON_VALUE)
				)
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	public void testUserCreatedByStoredProperly() throws Exception {
		MockMultipartFile document = new MockMultipartFile(
				"document", // must be document see controller @RequestParam
				"file.txt",
				"text/plain",
				"This is a nice text".getBytes(StandardCharsets.UTF_8));

		mockMvc.perform(
						multipart(HttpMethod.POST, API_DOCUMENTS)
								.file(document)
								.accept(MediaType.APPLICATION_JSON_VALUE)
				)
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("\"id\":3,\"name\":\"file.txt\"")))
				.andExpect(jsonPath("created_by", is("user")));
	}

	@Test
	@WithMockUser
	public void editExistingDocumentDoesNotChangeCreatedByAndCreatedTime() throws Exception {
		MockMultipartFile document = new MockMultipartFile(
				"document", // must be document see controller @RequestParam
				"file.txt",
				"text/plain",
				"This is a nice text".getBytes(StandardCharsets.UTF_8));

		mockMvc.perform(
						multipart(HttpMethod.PUT, API_DOCUMENTS + "/1")
								.file(document)
								.accept(MediaType.APPLICATION_JSON_VALUE)
				)
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("\"id\":1,\"name\":\"file.txt\"")))
				.andExpect(jsonPath("created_by", is("firstUser")))
				.andExpect(jsonPath("created_time", is("2022-12-22T22:22:22Z")))
				.andExpect(jsonPath("content_type", is("text/plain")));
	}

	@Test
	@WithMockUser
	public void editProtocolStatesTest() throws Exception {
		mockMvc.perform(get(API_PROTOCOLS + "/1")
						.accept(MediaType.APPLICATION_JSON_VALUE)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("id", is(1)))
				.andExpect(jsonPath("state", is("NEW")));

		mockMvc.perform(put(API_PROTOCOLS + "/1/editState/UNKNOWN")
					.accept(MediaType.APPLICATION_JSON_VALUE)
				)
				.andExpect(status().isBadRequest());

		mockMvc.perform(put(API_PROTOCOLS + "/1/editState/CANCELLED")
						.accept(MediaType.APPLICATION_JSON_VALUE)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("id", is(1)))
				.andExpect(jsonPath("state", is("CANCELLED")));


		mockMvc.perform(get(API_PROTOCOLS + "/1")
						.accept(MediaType.APPLICATION_JSON_VALUE)
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("id", is(1)))
				.andExpect(jsonPath("state", is("CANCELLED")));
	}

}
