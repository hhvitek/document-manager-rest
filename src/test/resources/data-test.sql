INSERT INTO document (id, name, created_by) VALUES (1, 'first', 'firstUser');
INSERT INTO document (id, name, created_by) VALUES (2, 'second', 'secondUser');

INSERT INTO protocol (id, created_by, state) VALUES (1, 'user1', 'NEW');

INSERT INTO protocol_to_documents (protocol_id, document_id) VALUES (1, 1);