INSERT INTO document (id, name, created_by, created_time, file) VALUES (1, 'first', 'firstUser', '1671747742000', 'random bytes');
INSERT INTO document (id, name, created_by, created_time, file) VALUES (2, 'second', 'secondUser', '1671747742000', 'random bytes');

INSERT INTO protocol (id, created_by, created_time, state) VALUES (1, 'user1', '1671747742000', 'NEW');

INSERT INTO protocol_to_documents (protocol_id, document_id) VALUES (1, 1);