package hhvitek.documentmanager.document;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {
	boolean existsAllByIdIn(Collection<Integer> ids);
}
